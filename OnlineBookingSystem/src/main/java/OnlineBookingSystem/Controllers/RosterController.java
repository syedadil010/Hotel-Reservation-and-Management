package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.DisplayClasses.CheckCell;
import OnlineBookingSystem.DisplayClasses.TableCell;
import OnlineBookingSystem.DisplayClasses.TableRow;
import OnlineBookingSystem.ModelClasses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
@RequestMapping(path="/roster")
@Service
public class RosterController 
{

    private HttpSession session;
    private OBSFascade obs;

    @Autowired
    public RosterController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    /**
     * Method used to render the rostering view.
     * @return ModelAndView
     */
    @RequestMapping(path="/view", method = RequestMethod.GET)
    public ModelAndView roster(RedirectAttributes redirectAttributes){
        //Check login
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        return buildView(user.getId(), redirectAttributes);
    }


    @RequestMapping(path="/update", method = RequestMethod.POST)
    public ModelAndView updateRoster(
            @RequestParam(value= "work", required = false) String[] work,
            RedirectAttributes redirectAttributes
    ){
        //Check login
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        if(work == null){
            work = new String[0];
        }
        // Create two arrays
        // one for the work parameter in the post request,
        // the other for current database 'work' records
        ArrayList<Work> toBeAdded = new ArrayList<>();
        ArrayList<Work> toBeDeleted = obs.getAllWork(user.getId());

        for(String ws : work){
            String[] ids = ws.split(":");
            int workShiftId = Integer.parseInt(ids[0]);
            int employeeId = Integer.parseInt(ids[1]);
            int workId = Integer.parseInt(ids[2]);

            //Populate array where new workshifts needs to be added to the database.
            if(workId == -1){
                toBeAdded.add(new Work(employeeId,workShiftId));
            }

            //Populate array where old workshifts needs to be deleted from database
            Work currentOld = null;
            for(Work oldWork : toBeDeleted){
                if(oldWork.getEmployee().getId() == employeeId && oldWork.getWorkShift().getId() == workShiftId){
                    currentOld = oldWork;
                    break;
                }
            }
            toBeDeleted.remove(currentOld);
        }

        //Check all the employees to be unrostered does not have bookings
        //Get a list of all the bookings
        ArrayList<Booking> bookings = Booking.getBookings(user.getId());
        for(Work w : toBeDeleted){
            for(Booking b: bookings){
                //For each Work and Each Booking, if there is a booking in the work, error
                if(b.within(w.getStart(), w.getEnd())){
                    ModelAndView error = buildView(user.getId(), redirectAttributes);
                    error.addObject(
                            "Error",
                            String.format("There is a Booking for employee %s that needs to be cancelled first.", w
                                    .getEmployee().getName()));
                    return error;
                }
            }
        }


        //Need to save the work changes
        //Save add
        obs.saveWorks(toBeAdded);
        //Save delete
        obs.deleteWorks(toBeDeleted);

        return new ModelAndView("redirect:/businessowner/dashboard");
    }


    private ModelAndView buildView(int userId, RedirectAttributes redirectAttributes){
        //Get the Employees from database
        ArrayList<Employee> employees = obs.getEmployees(userId);
        //Get the workShifts from database
        ArrayList<WorkShift> workShifts = obs.getWorkShifts(userId);
        //Get the list of shifts that employees are already rostered to work
        ArrayList<Work> works = obs.getAllWork(userId);

        //If there are no employees, redirect to dashboard with error.
        if(employees.size() == 0){
            ModelAndView mav = new ModelAndView("redirect:/businessowner/dashboard");
            redirectAttributes.addFlashAttribute("Error", "You must first create employees before you can roster them" +
                    ".");
            return mav;
        }

        //If there are no opening hours, redirect to dashboard with error.
        if(workShifts.size() == 0){
            ModelAndView mav = new ModelAndView("redirect:/businessowner/dashboard");
            redirectAttributes.addFlashAttribute("Error", "You must first define opening hours before you can roster " +
                    "employees.");
            return mav;
        }



        //Prepare the table header
        TableRow tableHeader = new TableRow();
        tableHeader.headers = new ArrayList<>();
        TableCell topLeftCell = new TableCell();
        topLeftCell.text = "Employee Roster";
        tableHeader.headers.add(topLeftCell);
        for(Employee employee: employees){
            TableCell cell = new TableCell();
            cell.text = employee.getName();
            tableHeader.headers.add(cell);
        }

        //Prepare the table view
        ArrayList<TableRow> table = new ArrayList<>();
        // Workshifts are table rows
        for(WorkShift workShift : workShifts){ // <-- for each row
            TableRow row = new TableRow();
            row.heading = workShift.getDay().getName();
            row.cells = new ArrayList<>();
            // Columns are each employees
            for(Employee employee : employees){ // <--- for each column
                CheckCell cell = new CheckCell();
                cell.name = "work";
                cell.text = workShift.getShift().getTimes();
                cell.value = String.format("%d:%d:%d", workShift.getId(), employee.getId(), -1);
                cell.checked = false;

                // Iterate over the list of all work in the database
                // If it matches the current employee and workshift
                // then populate the cell with info
                for(Work work : works){
                    if(work.getEmployee().getId() == employee.getId() && work.getWorkShift().getId() == workShift.getId()){
                        cell.value = String.format("%d:%d:%d", workShift.getId(), employee.getId(), work.getId());
                        cell.checked = true;
                        break;
                    }
                }

                // Add the cell to the current row
                row.cells.add(cell);
            } // <--- end each column

            // Add the row to the table
            table.add(row);

        } // <--- end each row

        //Render the view
        // Add objects to the model
        ModelAndView mav = new ModelAndView("roster");
        mav.addObject("tableHeader", tableHeader );
        mav.addObject("table", table);
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        return mav;
    }
}
