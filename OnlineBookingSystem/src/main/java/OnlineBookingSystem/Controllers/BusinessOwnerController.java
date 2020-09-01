package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.DisplayClasses.*;
import OnlineBookingSystem.ModelClasses.*;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping(path="/businessowner")
@Service
public class BusinessOwnerController {

    //Session needed to validate the user when requests come in
    private HttpSession session;
    private static Logger logger =Logger.getLogger("BusinessOwnerController");
    private OBSFascade obs;

    @Autowired
    public BusinessOwnerController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    @RequestMapping(path="/service/add", method=RequestMethod.POST)
    public ModelAndView createNewService(
            @RequestParam String serviceName,
            @RequestParam int duration) {
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        //If the user is authenticated, then create new service object and save to the database
        BusinessService newService = new BusinessService(user.getId(), serviceName, 1, duration);
        BusinessService.saveService(newService);
        return new ModelAndView("redirect:/businessowner/service");
    }

    @RequestMapping(path="/service/add", method=RequestMethod.GET)
    public ModelAndView getServiceForm() {
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        ModelAndView mav = new ModelAndView("service_add");
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        return mav;
    }

    @RequestMapping(path="/service", method=RequestMethod.GET)
    public ModelAndView displayCurrentServices() {
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        //get services
        ArrayList<BusinessService> services = obs.getServices(user.getId());

        //create table
        ArrayList<TableCell> headers = new ArrayList<>();
        ArrayList<TableRow> table = new ArrayList<>();

        //Table row, table header
        TableCell topLeftCell = new TableCell("Service Name");
        TableCell topRightCell = new TableCell("Duration");
        TableCell topCancelCell = new TableCell("");
        headers.add(topLeftCell);
        headers.add(topRightCell);
        headers.add(topCancelCell);

        //Table row, table data
        for(BusinessService s: services) {
            TableRow row = new TableRow();
            row.cells = new ArrayList<>();
            TableCell leftCell = new TableCell(s.getServiceName(), "Service Name");
            TableCell rightCell = new TableCell(Integer.toString(s.getDuration()), "Duration");
            row.cells.add(leftCell);
            row.cells.add(rightCell);
            row.id = Integer.toString(s.getId());
            table.add(row);
        }
        //Build mav
        ModelAndView mav = new ModelAndView("service");
        mav.addObject("tableHeader", headers);
        mav.addObject("table", table);
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        return mav;
    }

    /**
     * Method for cancelling a service.
     * It checks to see if bookings exist for the selected service
     * If there are bookings then the user is notified
     * @param serviceId id of the service to be deleted
     * @return The service page for successful deletions
     */
    @RequestMapping(path = "service/cancel/{serviceId}")
    public ModelAndView cancelService(
            @PathVariable(name="serviceId") int serviceId,
            RedirectAttributes redirectAttrs){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        //create page and model object that will be returned
        ModelAndView mav = new ModelAndView();
        //get the service to be cancelled
        BusinessService s = obs.getServiceById(serviceId);
        //get all bookings for the selected service
        ArrayList<Booking> bookings = obs.getBookingsForService(serviceId);
        if(s == null){ //check the service exists in the database
            mav.setViewName("redirect:/businessowner/dashboard");
            redirectAttrs.addFlashAttribute("Error", "Invalid Service Reference.");
            logger.warning("Could not find service with ID: " + serviceId);
        }
        else if(bookings.isEmpty()){ //then ok to remove service
            obs.deleteSpecialisations(serviceId);
            obs.deleteService(serviceId);
            redirectAttrs.addFlashAttribute("Message", "Service successfully removed.");
            mav.setViewName("redirect:/businessowner/service");
            logger.finest("Cancelled Service: " + s.getServiceName());
        }
        else { //else there are bookings for the selected service that need to be cancelled first
            redirectAttrs.addFlashAttribute("Warning", "" +
                    "Bookings exist for the the service '" +
                    s.getServiceName() +
                    "'. Cancel bookings first before deleting.");
            mav.setViewName("redirect:/businessowner/dashboard");
            logger.finer("Service could not be cancelled because bookings exist for: " + s.getServiceName());
        }
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        return mav;
    }


    @RequestMapping(path="/dashboard")
    public ModelAndView dashboard(){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        //Build the booking display table
        ArrayList<TableCell> headers = new ArrayList<>();
        ArrayList<TableRow> table = new ArrayList<>();

        //Need list of days
        ArrayList<Day> days = obs.getDays();
        //Need list of shifts
        ArrayList<Shift> shifts = obs.getShifts(user.getId());
        //Need list of workshifts
        ArrayList<WorkShift> workShifts = obs.getWorkShifts(user.getId());
        //Need list of Work
        ArrayList<Work> works = obs.getAllWork(user.getId());
        //Need list of Bookings
        ArrayList<Booking> bookings = obs.getBookings(user.getId());

        //Build table headers which is just the Days
        TableCell topLeftCell = new TableCell();
        topLeftCell.text = "Bookings";
        headers.add(topLeftCell);
        for (Day day : days){
            TableCell cell = new TableCell();
            cell.text = day.getName() + "<br/>" + day.getDate().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
            headers.add(cell);
        }

        //Build the actual table
        // Each row is a shift
        for(Shift shift : shifts){ // <--- rows
            TableRow row = new TableRow();
            row.cells = new ArrayList<>();
            row.heading = MessageFormat.format("{0} {1}", shift.getName(), shift.getTimes());
            for(Day day : days){ // <--- columns
                ShiftCell cell = new ShiftCell();
                cell.day = day;
                cell.employees = new ArrayList<>();
                WorkShift ws = null;
                //Get the workshift that represents this cell
                for(WorkShift workShift : workShifts){
                    if(workShift.getDay().getId() == day.getId() && workShift.getShift().getId() == shift.getId()){
                        ws = workShift;
                        break;
                    }
                }
                if(ws == null){ //if there is no opening hours
                    cell.text = "CLOSED";
                    cell.status = ShiftStatus.CLOSED;
                    row.cells.add(cell);
                    continue;
                }
                //Get the works relevant to this shift
                ArrayList<Work> currentWork = new ArrayList<>();
                for(Work w : works){
                    if(w.getWorkShift().getId() == ws.getId()){
                        currentWork.add(w);
                    }
                }
                if(currentWork.size() == 0){ //if nobody is rostered on to work
                    cell.text = "";
                    cell.status = ShiftStatus.NONE;
                    row.cells.add(cell);
                    continue;
                }
                //Get the bookings
                int bookingTotal = 0;
                //For each shift with an on duty employee (called a 'work' object in the db)
                for(Work w: currentWork){
                    ArrayList<Booking> currentBookings = new ArrayList<>();
                    // Iterate over bookings and add to a list all bookings that match the rostered employee
                    for(Booking b: bookings){
                        if(b.getEmployee().getId() == w.getEmployee().getId() && b.within(w.getStart(), w.getEnd())){
                            //As long as it is within the current week
                            if(b.withinNextSunday()){
                                currentBookings.add(b);
                                bookingTotal++;
                            }
                        }
                    }
                    cell.employees.add(new Link(w.getEmployee().getName(),"/work/view/" + w.getId(),
                            currentBookings.size()));
                }

                //Set cell status based on load
                if (bookingTotal >= currentWork.size()){
                    cell.status = ShiftStatus.HIGH;
                }
                else if (bookingTotal != 0){
                    cell.status = ShiftStatus.LOW;
                }
                else{
                    cell.status = ShiftStatus.NONE;
                }
                //Add the cell
                row.cells.add(cell);
            }
            table.add(row);
        }
        ModelAndView mav = new ModelAndView("businessownerdash");
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        mav.addObject("tableHeader", headers);
        mav.addObject("table", table);
        return mav;
    }

}
