package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.ModelClasses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

@Controller
@RequestMapping(path="/booking")
@Service
public class BookingController {

    private HttpSession session;
    private OBSFascade obs;
    @Autowired
    public BookingController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    private static Logger logger =Logger.getLogger("BookingController");

    @RequestMapping(path = "/cancel/{bookingid}")
    public ModelAndView cancelBooking(
            @PathVariable(name="bookingid") int bookingId,
            RedirectAttributes redirectAttrs){
        User user = LoginController.checkLogin(session);
        //Check if the user is a business owner or a customer
        if(user instanceof BusinessOwner){
            //retrieve the booking from the database
            Booking b = obs.getBooking(user.getId(), bookingId);
            if(b == null){
                ModelAndView mav = new ModelAndView("redirect:/businessowner/dashboard");
                redirectAttrs.addFlashAttribute("Error", "Invalid Booking Reference.");
                return mav;
            } //then if not null, cancel it and return the page
            b.cancelBooking();
            logger.finest("Cancelled Booking");
            return new ModelAndView("redirect:/businessowner/dashboard/");
        }
        if(user instanceof Customer){
            //retrieve the booking from the database
            Booking b = obs.getBookingForCustomer(user.getId(),bookingId);
            if(b == null){
                ModelAndView mav = new ModelAndView("redirect:/customer/dashboard");
                redirectAttrs.addFlashAttribute("Error", "Invalid Booking Reference");
                return mav;
            } //then if not null, cancel it and return the page
            b.cancelBooking();
            logger.finest("Cancelled Booking");
            ModelAndView mav = new ModelAndView("redirect:/customer/dashboard/");
            redirectAttrs.addFlashAttribute("Message", "Cancelled Booking");
            return mav;
        }
        session.invalidate();
        return new ModelAndView("redirect:/");
    }


    @RequestMapping(path = "/add/{workid}", method = RequestMethod.GET)
    public ModelAndView addBookingView(
            @PathVariable(name = "workid", required = false) Integer workid,
            RedirectAttributes redirectAttributes
    ){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        if(workid == null){
            workid = -1;
        }

        return generateBookingForm(user.getId(), workid, redirectAttributes);
    }

    @RequestMapping(path = "/add/{workid}", method = RequestMethod.POST)
    public ModelAndView addBookingUpdate(
            @PathVariable(name = "workid", required = false) Integer workid,
            @RequestParam int customer,
            @RequestParam int employee,
            @RequestParam int service,
            @RequestParam String date,
            @RequestParam int startHour,
            @RequestParam int startMin,
            RedirectAttributes redirectAttributes){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        LocalDate startDate = LocalDate.parse(date);
        LocalTime startTime = LocalTime.of(startHour, startMin);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        Timestamp timestamp = Timestamp.valueOf(startDateTime);

        //Create the booking object
        Booking b = new Booking(
                customer,employee, service, timestamp
        );
        //Validate that the booking doesn't conflict with other bookings
        if(!validateBooking(b)){
            ModelAndView mav = generateBookingForm(user.getId(), workid, redirectAttributes);
            BusinessOwner bo = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
            mav.addObject("owner", bo);
            mav.addObject("Error", "Employee is not free at the picked time for the picked service.");
            return mav;
        }
        else{
            b.save();
            ModelAndView mav = new ModelAndView("redirect:/businessowner/dashboard");
            redirectAttributes.addFlashAttribute("Message", "Booking Added");
            return mav;
        }
    }


    @RequestMapping(path = "/cuaddbooking/{businessId}/{date}/{time}/{employee}/{service}", method = RequestMethod.GET)
    public ModelAndView customerAddBookingConfirm(
            @PathVariable(name = "businessId") int businessId,
            @PathVariable(name = "date") String dateString,
            @PathVariable(name = "time") String timeString,
            @PathVariable(name = "employee") int employeeId,
            @PathVariable(name = "service") int serviceId
    ){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof Customer)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        //As long as the user is a customer, then generate a confirmation page based on the parameters
        return generateCustomerAddBookingConfirmation(businessId, dateString, timeString, employeeId, serviceId);
    }

    @RequestMapping(path = "/cuaddbooking/{businessId}/{date}/{time}/{employee}/{service}", method = RequestMethod.POST)
    public ModelAndView customerAddBookingUpdate(
            @PathVariable(name = "businessId") int businessId,
            @PathVariable(name = "date") String dateString,
            @PathVariable(name = "time") String timeString,
            @PathVariable(name = "employee") int employeeId,
            @PathVariable(name = "service") int serviceId,
            RedirectAttributes redirectAttributes
    ){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof Customer)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        LocalDate d = LocalDate.parse(dateString);
        LocalTime t = LocalTime.parse(timeString);
        LocalDateTime ldt = LocalDateTime.of(d,t);
        Timestamp startTime = Timestamp.valueOf(ldt);
        Booking b = new Booking(
                user.getId(),
                employeeId,
                serviceId,
                startTime
        );

        if(validateBooking(b)){
            b.save();
            redirectAttributes.addFlashAttribute("Message", "Booking Added");
            return new ModelAndView("redirect:/customer/dashboard");
        }
        else {
            ModelAndView mav = generateCustomerAddBookingConfirmation(
                    businessId,
                    dateString,
                    timeString,
                    employeeId,
                    serviceId);
            mav.addObject("Error", "The Service, Employee combination you have selected is no longer available, " +
                    "please return to your dashboard and try selecting a different slot.");
            return mav;
        }
    }


    /**
     * Generates an add booking view with the employee preselected
     * @param businessId the business owner id
     * @param workId the shift + employee's work id that should be preselected.
     * @return a model and view.
     */
    private ModelAndView generateBookingForm(int businessId, int workId, RedirectAttributes redirectAttributes){
        //Need a list of customers
        ArrayList<Customer> customers = obs.getAllCustomers();
        ArrayList<Employee> employees = obs.getEmployees(businessId);

        //Check that at least one customer exists
        if(customers.size() == 0){
            ModelAndView mav = new ModelAndView("redirect:/businessowner/dashboard");
            redirectAttributes.addFlashAttribute("Error", "There are no customers at present for you to add a booking" +
                    " to.");
            return mav;
        }

        Work w = obs.getWorkById(workId);
        Employee employee = null;
        LocalDateTime startTime = LocalDateTime.now();
        if(w != null){
            startTime = w.getWorkShift().getStartDateTime();
            employee = w.getEmployee();
        }
        String dateString = startTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
        int startHour = startTime.getHour();
        int startMin = startTime.getMinute();
        if(employee == null){
            logger.severe("Error, employee null.");
            return new ModelAndView("redirect:/");
        }
        int employeeid = Employee.getEmployeeIdByName(employee.getName());
        ArrayList<BusinessService> services = obs.getSpecialisedServices(employeeid, businessId);

        ModelAndView mav = new ModelAndView("boaddbooking");

        mav.addObject("customers", customers);
        mav.addObject("services", services);
        mav.addObject("employees", employees);
        mav.addObject("employee", employee);
        mav.addObject("dateString", dateString);
        mav.addObject("startHour", startHour);
        mav.addObject("startMin", startMin);
        return mav;
    }


    private ModelAndView generateCustomerAddBookingConfirmation(
            int businessId,
            String dateString,
            String timeString,
            int employeeId,
            int serviceId) {
        //Get list of employees and services from the database
        Employee employee = obs.getEmployee(employeeId);
        BusinessService service = obs.getServiceById(serviceId);
        //Get data and time objects, and calculate end time
        LocalDate ld = LocalDate.parse(dateString);
        LocalTime ltStart = LocalTime.parse(timeString);
        LocalTime ltEnd = ltStart.plusMinutes(service.getDuration());

        //Format time and date information
        String dateOutput = ld.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
        String timeOutput = String.format("%s - %s",
                ltStart.format(DateTimeFormatter.ofPattern("HH:mm")),
                ltEnd.format(DateTimeFormatter.ofPattern("HH:mm")));

        //Create the page and add objects to the model
        ModelAndView mav = new ModelAndView("cuaddbooking");
        mav.addObject("employee", employee);
        mav.addObject("service", service);
        mav.addObject("date", dateOutput);
        mav.addObject("time", timeOutput);
        mav.addObject("duration", service.getDuration());
        return mav;
    }

    /**
     * Method to validate if a booking is ok
     * @param booking booking to validate
     * @return true if its ok, else false
     */
    static boolean validateBooking(Booking booking){
        int employeeId = booking.getEmployee().getId();
        LocalDate date = booking.getStartLocalDateTime().toLocalDate();
        LocalTime time = booking.getStartLocalDateTime().toLocalTime();
        int duration = booking.getBusinessService().getDuration();

        return AvailabilityController.checkIsAvailable(employeeId, date, time, duration);
    }
}
