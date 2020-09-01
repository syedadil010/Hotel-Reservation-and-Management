package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.DisplayClasses.LinkCell;
import OnlineBookingSystem.DisplayClasses.Option;
import OnlineBookingSystem.DisplayClasses.TableCell;
import OnlineBookingSystem.DisplayClasses.TableRow;
import OnlineBookingSystem.ModelClasses.*;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Logger;

@Controller
@Service
@RequestMapping(path="/availability")
public class AvailabilityController {

    private static Logger logger = Logger.getLogger("AvailabilityController");
    private HttpSession session;
    private OBSFascade obs;

    @Autowired
    public AvailabilityController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    /**
     * Method to handle /booking/availabilities
     * @param businessid the id of the business the customer is trying to look up.
     * @return a ModelAndView object populated with a table of availabilities.
     */
    @RequestMapping(path = "/view", method = RequestMethod.POST)
    public ModelAndView viewAvailability(
            @RequestParam(value = "business") int businessid,
            @RequestParam(value = "selectedEmployee", required = false) String employeeIdString,
            @RequestParam(value = "selectedService", required = false) String serviceIdString){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof Customer)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        //information needed for the form
        int selectedEmployeeId = -1;
        int selectedServiceId = -1;
        try{ //Parse the Ids to integers.
            if(employeeIdString != null)
                selectedEmployeeId = Integer.parseInt(employeeIdString);
            if(serviceIdString != null)
                selectedServiceId = Integer.parseInt(serviceIdString);
        }
        catch(Exception e){
            logger.severe(e.getMessage());
            return new ModelAndView("redirect:/");
        }

        //Build display and add the employees and services
        ModelAndView mav = new ModelAndView("viewavailability");
        addEmployeeOptionsToMav(mav, businessid, selectedEmployeeId, selectedServiceId);
        addServiceOptionsToMav(mav, businessid, selectedServiceId, selectedEmployeeId);
        mav.addObject("business", businessid);
        mav.addObject("selectedEmployeeId", selectedEmployeeId);
        mav.addObject("selectedServiceId", selectedServiceId);

        // Check if table is required
        BusinessService selectedService = obs.getServiceById(selectedServiceId);
        if(selectedServiceId == -1 || selectedEmployeeId == -1 || selectedService == null) {
            mav.addObject("tableisvisible", false);
            return mav;
        }
        else
            buildTable(mav, businessid, selectedEmployeeId, selectedServiceId, selectedService);
        mav.addObject("tableisvisible", true);
        logger.finest("Rendering Booking View");
        return mav;
    }

    /**
     * method to return if an employee is free for a perticular date, time and duration.
     * @param employeeId the employee to check
     * @param date the date to check
     * @param startTime the start time to check
     * @param duration the duration to check
     * @return true for available, false for not available. Returns false for values before the start of this week.
     */
    static boolean checkIsAvailable(int employeeId, LocalDate date, LocalTime startTime, int duration){

        OBSFascade obs = OBSModel.getModel();
        LocalDate lowerBound = LocalDate.now().with(DayOfWeek.MONDAY);
        if(date.isBefore(lowerBound)){
            return false;
        }

        LocalTime endTime = startTime.plusMinutes(duration);
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        ArrayList<Booking> otherBookings = obs.getBookingsForEmployee(employeeId);
        ArrayList<Work> rosteredShifts = obs.getAllWorkForEmployee(employeeId);
        boolean valid = false;

        //Find out which weekday the booking is on.
        DayOfWeek bookingDay = date.getDayOfWeek();

        //Filter the list of rostered shifts so that only the ones on the correct day of week remains
        rosteredShifts.removeIf(work -> !bookingDay.equals(work.getWorkShift().getDay().getDate()
                .getDayOfWeek()));

        //Check to see that the required time slot fits within a shift or a sequence of consecutive shifts.
        //Work with time only, ignore dates as it has already been filtered
        for(Work w : rosteredShifts){
            LocalTime startWorkTime = w.getStart().toLocalTime();
            LocalTime endWorkTime = w.getEnd().toLocalTime();

            //If the booking started before the employee started, move on to the next work session.
            if(startTime.isBefore(startWorkTime)){
                continue;
            }

            //If the interested timeslot started at the work end time or after this work has ended, move on to the
            // next session.
            if(!startTime.isBefore(endWorkTime)){
                continue;
            }

            //Falling through here means the booking must of started within this shift.
            //If the booking also ends not after the end of this shift (i.e. before or at the end of shift),
            //then from a shift perspective this booking is valid.
            //There is no need to continue checking further shifts.
            if(!endTime.isAfter(endWorkTime)){
                valid = true;
                break;
            }

            //Falling through here means that the booking started within the shift and ends after the end of this shift.
            //We now need to look for consecutive shifts such that the booking ends in the next shift.

            //Look for another shift that is immediately after this one and join it on.
            //Loop through every shift again and look for a shift that is consequtive.
            //Continue searching if there is a new end time found, otherwise exit.
            boolean needToSearchAgain = true;
            while(needToSearchAgain){
                //Assume that there is no subsequent shift found, if one is found, set it to true.
                needToSearchAgain = false;

                for(Work nextShift: rosteredShifts){
                    LocalTime nextShiftStart = nextShift.getStart().toLocalTime();
                    LocalTime nextShiftEnd = nextShift.getEnd().toLocalTime();
                    //If there is a shift that started before or at the end of this shift and ends after this shift
                    //Set the new end work time as the new end time and restart the search.
                    if(!nextShiftStart.isAfter(endWorkTime)){
                        if(nextShiftEnd.isAfter(endWorkTime)){
                            endWorkTime = nextShiftEnd;
                            needToSearchAgain = true;
                            break;
                        }
                    }
                }
            }

            //At the end here We have found the maximum run of shifts starting from the first one where booking started
            //in. If the booking end time is no longer after the end of the new end time, the booking is valid from
            //a shift perspective. Otherwise, reject the booking.
            if(!endTime.isAfter(endWorkTime)){
                valid = true;
                break;
            }
            valid = false;
        }

        //Shortcut exit, no point checking bookings if the booking is not valid just based on working shifts.
        if(!valid){
            return false;
        }

        //Need to check the booking against every other booking for conflicts. If there is a conflict, reject the
        // booking. Otherwise the booking is valid.
        for(Booking b : otherBookings){
            //A booking is in conflict if it does not occupy the same timeslot as every other booking for this
            //employee.
            if(b.within(startDateTime, endDateTime)){
                return false;
            }
        }
        return true;

    }

    private void addEmployeeOptionsToMav(ModelAndView mav, int businessid, int selectedEmployeeId, int serviceid) {
        ArrayList<Employee> employees = obs.getEmployeesByService(businessid, serviceid);
        ArrayList<Option> employeeOptions = new ArrayList<>();

        for(Employee e: employees){
            Option o = new Option();
            o.text = e.getName();
            o.value = String.format("%d", e.getId());
            o.selected = e.getId() == selectedEmployeeId;
            if(o.selected)
                mav.addObject("selectedemployee", e.getName());
            employeeOptions.add(o);
        }
        mav.addObject("employees", employeeOptions);
    }

    private void addServiceOptionsToMav(ModelAndView mav, int businessid, int selectedServiceId, int employeeId) {
        ArrayList<BusinessService> services = obs.getServices(businessid);
        ArrayList<Option> serviceOptions = new ArrayList<>();
        for(BusinessService s: services){
            Option o = new Option();
            o.text = String.format("%s - duration: %d minutes", s.getServiceName(), s.getDuration());
            o.value = String.format("%d", s.getId());
            o.selected = s.getId() == selectedServiceId;
            if(o.selected)
                mav.addObject("selectedservice", s.getServiceName());
            serviceOptions.add(o);
        }
        mav.addObject("services", serviceOptions);
    }

    private void buildTable(
            ModelAndView mav,
            int businessid,
            int selectedEmployeeId,
            int selectedServiceId,
            BusinessService selectedService) {
        ArrayList<Day> days = obs.getDays();
        addHeadersToTable(mav, days);
        addShiftsToTable(mav, days, businessid, selectedEmployeeId, selectedServiceId, selectedService);
    }

    private void addHeadersToTable(ModelAndView mav, ArrayList<Day> days) {
        ArrayList<TableCell> headers = new ArrayList<>();
        TableCell topLeftCell = new TableCell();
        topLeftCell.text = "Start Time";
        headers.add(topLeftCell);
        for (Day day : days){
            TableCell cell = new TableCell();
            //Set up the date object so we know which day we are referring to.
            LocalDate currentDay = day.getDate();
            cell.text = day.getName() + currentDay.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
            headers.add(cell);
        }
        mav.addObject("tableHeader", headers);
    }

    private void addShiftsToTable(
            ModelAndView mav,
            ArrayList<Day> days,
            int businessid,
            int selectedEmployeeId,
            int selectedServiceId,
            BusinessService selectedService) {
        ArrayList<TableRow> table = new ArrayList<>();
        ArrayList<Shift> shifts = obs.getShifts(businessid);
        ArrayList<WorkShift> workShifts = obs.getWorkShifts(businessid);
        ArrayList<Work> works = obs.getAllWork(businessid);

        for(Shift shift : shifts){
            LocalTime currentTime = LocalTime.of(shift.getStartHour(), shift.getStartMin());
            LocalTime end = LocalTime.of(shift.getEndHour(), shift.getEndMin());

            while(currentTime.isBefore(end)){
                int blockSize = 30; //minutes
                TableRow row = new TableRow();
                row.cells = new ArrayList<>();
                row.heading = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                for(Day day : days){
                    //Set up the date object so we know which day we are referring to.
                    LocalDate currentDay = day.getDate();
                    LinkCell cell = new LinkCell();
                    cell.text = "";
                    // get the current workshift
                    WorkShift ws = null;
                    for(WorkShift workShift : workShifts){
                        if(workShift.getDay().getId() == day.getId() && workShift.getShift().getId() == shift.getId()){
                            ws = workShift;
                            break;
                        }
                    }
                    if(ws == null){
                        cell.text = "CLOSED";
                        cell.displayClass = "closed";
                        row.cells.add(cell);
                        continue;
                    }
                    //Get the works relavent to this shift
                    ArrayList<Work> currentWork = new ArrayList<>();
                    for(Work w : works){
                        if(w.getWorkShift().getId() == ws.getId() && w.getEmployee().getId() == selectedEmployeeId)
                            currentWork.add(w);
                    }

                    //If not employees are working, this time is not available.
                    if(currentWork.size() == 0){
                        cell.text = " - ";
                        cell.displayClass = "notavailable";
                        row.cells.add(cell);
                        continue;
                    }

                    //Check to see if at least one employee is free during this time.
                    //Initialise flag to false, and set to true if any one employee is free.
                    boolean isAvailable = false;
                    for(Work w: currentWork){
                        //For each rostered employee, check if they are free
                        boolean isWorkAvaialble = checkIsAvailable(selectedEmployeeId, currentDay, currentTime, selectedService
                                .getDuration());
                        if(isWorkAvaialble){
                            isAvailable = true;
                            break;
                        }
                    }
                    if(isAvailable){
                        cell.text = "Available";
                        cell.href =
                                "/booking/cuaddbooking/"+
                                        businessid + "/" +
                                        day.getDate() + "/" +
                                        currentTime + "/" +
                                        selectedEmployeeId + "/" +
                                        selectedServiceId;
                        cell.displayClass = "available";
                    }
                    else{
                        cell.text = " - ";
                        cell.displayClass = "notavailable";
                    }
                    //Add the cell
                    row.cells.add(cell);
                }
                currentTime = currentTime.plusMinutes(blockSize);
                table.add(row);
            }
        }
        mav.addObject("table", table);
    }
}
