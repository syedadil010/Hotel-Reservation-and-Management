package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.DisplayClasses.CheckCell;
import OnlineBookingSystem.DisplayClasses.TableCell;
import OnlineBookingSystem.DisplayClasses.TableRow;
import OnlineBookingSystem.ModelClasses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
@RequestMapping(path="/openhours")
@org.springframework.stereotype.Service
public class OpeningController {

    private HttpSession session;
    private OBSFascade obs;
    @Autowired
    public OpeningController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    @RequestMapping(path="/opening", method = RequestMethod.GET)
    public ModelAndView opening(){
        //Check login
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        return buildOpeningHourView(user.getId());
    }

    @RequestMapping(path="/opening", method = RequestMethod.POST)
    public ModelAndView updateOpening(
            @RequestParam(value= "workShift", required = false) String[] workShift
    ){
        //Check login
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        // Create two arrays
        // one for the workshift parameter in the post request,
        // the other for current database workshifts
        ArrayList<WorkShift> toBeAdded = new ArrayList<>();
        ArrayList<WorkShift> toBeDeleted = obs.getWorkShifts(user.getId());

        if(workShift == null){
            workShift = new String[0];
        }
        // Iterate over the new shifts
        for(String ws : workShift){
            String[] ids = ws.split(":");
            int dayId = Integer.parseInt(ids[0]);
            int shiftId = Integer.parseInt(ids[1]);
            int workShiftId = Integer.parseInt(ids[2]);

            //Populate array where new workshifts needs to be added to the database.
            if(workShiftId == -1){
                toBeAdded.add(new WorkShift(user.getId(), shiftId, dayId));
            }

            //Populate array where old workshifts needs to be deleted from database
            WorkShift currentOld = null;
            for(WorkShift oldShift : toBeDeleted){
                if(oldShift.getDay().getId() == dayId && oldShift.getShift().getId() == shiftId){
                    currentOld = oldShift;
                    break;
                }
            }
            toBeDeleted.remove(currentOld);
        }

        //Check that it's safe to delete the shifts in old
        for(WorkShift w : toBeDeleted){
            Employee e = w.safeToDelete();
            if(e != null){
                ModelAndView error = buildOpeningHourView(user.getId());
                error.addObject(
                        "Error",
                        String.format("Employee: %s is currently rostered to work on a shift you " +
                        "are trying to remove.", e.getName()));
                return error;
            }
        }
        //Actually save the new workshifts
        obs.saveWorkShifts(toBeAdded);

        //Actually delete the old workshifts that are not selected
        obs.deleteWorkShifts(toBeDeleted);

        return new ModelAndView("redirect:/businessowner/dashboard");
    }


    private ModelAndView buildOpeningHourView(int userId){
        //Get the workShifts from database
        ArrayList<Shift> shifts = obs.getShifts(userId);
        ArrayList<Day> days = obs.getDays();
        ArrayList<WorkShift> workShifts = obs.getWorkShifts(userId);


        TableRow tableHeader = new TableRow();
        tableHeader.headers = new ArrayList<>();
        TableCell topLeftCell = new TableCell();
        topLeftCell.text = "Shifts";
        tableHeader.headers.add(topLeftCell);
        for(Day day : days){
            TableCell header = new TableCell();
            header.text = day.getName();
            tableHeader.headers.add(header);
        }

        ArrayList<TableRow> checkTable = new ArrayList<>();
        for(Shift shift : shifts){
            TableRow row = new TableRow();
            row.heading = shift.getName();
            row.cells = new ArrayList<>();
            for(Day day : days){
                CheckCell cell = new CheckCell();
                //Each cell should have the shift time as text.
                cell.text = shift.getTimes();
                //Each cell should have the name set to workShift[dayId][shiftId}
                cell.name = "workShift";
                //Each cell should have checked set to true if the dayId and shiftId combination exists in workshifts
                cell.checked = false;
                cell.value = String.format("%d:%d:%d", day.getId(), shift.getId(), -1);
                for(WorkShift ws : workShifts){
                    if(ws.getDay().getId() == day.getId() && ws.getShift().getId() == shift.getId()){
                        cell.checked = true;
                        cell.value = String.format("%d:%d:%d", day.getId(), shift.getId(), ws.getId());
                        break;
                    }
                }
                row.cells.add(cell);
            }
            checkTable.add(row);
        }

        //Render the view
        ModelAndView mav = new ModelAndView("opening");
        mav.addObject("tableHeader", tableHeader);
        mav.addObject("table", checkTable);
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        return mav;
    }
}
