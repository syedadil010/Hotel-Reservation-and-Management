package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.DisplayClasses.TableCell;
import OnlineBookingSystem.DisplayClasses.TableRow;
import OnlineBookingSystem.ModelClasses.*;
import OnlineBookingSystem.OBSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Logger;

@Controller
@Service
@RequestMapping(path = "/work")
public class WorkController {

    private HttpSession session;
    private OBSFascade obs;

    @Autowired
    public WorkController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }


    private static Logger logger = Logger.getLogger("WorkController");

    @RequestMapping(path = "/view/{workid}", method = RequestMethod.GET)
    public ModelAndView viewBookingsForWork(@PathVariable(name="workid") int workId){

        //Make sure the user is a business owner
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        try{
            // Work id represents a particular shift by a particular employee
            // The shift may have multiple bookings
            // Get work object from the database
            Work work = obs.getWorkById(workId);
            if(work == null){
                throw new OBSException("Invaid Work ID");
            }
            // Get all bookings for the rostered employee
            ArrayList<Booking> bookings = obs.getBookingsForEmployee(work.getEmployee().getId());
            // Prepare the table
            ArrayList<TableRow> table = new ArrayList<>();
            // Each row in the table is a booking
            // Iterate over all bookings and find those that are within the shift time
            for(Booking b : bookings){ // <--- row

                // Check if the booking is within the shift
                if(b.within(work.getStart(), work.getEnd())){
                    TableRow row = new TableRow();
                    row.cells = new ArrayList<>();
                    row.id = String.format("%d", b.getId());

                    //Populate Customer Information
                    TableCell customerCell = new TableCell();
                    customerCell.text = String.format("%s %s",
                            b.getCustomer().getFirstName(),
                            b.getCustomer().getLastName()
                    );
                    row.cells.add(customerCell);

                    //Populate Start Time
                    TableCell timeCell = new TableCell();
                    timeCell.text = b.getTimeString();
                    row.cells.add(timeCell);

                    //Populate Duration
                    TableCell durationCell = new TableCell();
                    durationCell.text = String.format("%d", b.getBusinessService().getDuration());
                    row.cells.add(durationCell);

                    //Populate BusinessService
                    TableCell serviceCell = new TableCell();
                    serviceCell.text = b.getBusinessService().getServiceName();
                    row.cells.add(serviceCell);

                    //Add the row
                    table.add(row);
                }
            }
            boolean bookingsExist = true;
            if(table.size() == 0){
                bookingsExist = false;
            }
            ModelAndView mav = new ModelAndView("viewwork");
            mav.addObject("bookingsExist", bookingsExist);
            mav.addObject("table", table);
            mav.addObject("employee", work.getEmployee().getName());
            mav.addObject("date", work.getWorkShift().getDay().getDate().format(DateTimeFormatter.ofPattern
                    ("dd/MM/YYYY")));
            mav.addObject("shift", String.format(
                    "%s %s",
                    work.getWorkShift().getShift().getName(),
                    work.getWorkShift().getShift().getTimes())
            );
            mav.addObject("workId", workId);
            BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
            mav.addObject("owner", b);
            return mav;
        }catch(OBSException obse){
            logger.info(obse.getMessage());
            return new ModelAndView("redirect:/businessowner/dashboard");
        }
    }

}
