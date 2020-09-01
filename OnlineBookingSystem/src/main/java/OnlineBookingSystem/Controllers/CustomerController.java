package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.DisplayClasses.TableCell;
import OnlineBookingSystem.DisplayClasses.TableRow;
import OnlineBookingSystem.ModelClasses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Controller
@RequestMapping(path="/customer")
@Service
public class CustomerController{

    private HttpSession session;
    private OBSFascade obs;

    @Autowired
    public CustomerController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    @RequestMapping(path="/dashboard")
    public ModelAndView dashboard(){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof Customer)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        //Get all the businesses currently in system.
        ArrayList<BusinessOwner> businesses = obs.getAllBusinessOwners();

        //Get the new bookings for the customer
        ArrayList<Booking> bookings = obs.getUpcomingBookingsForCustomer(user.getId());

        //build the my bookings table
        ArrayList<TableRow> table = buildTable(bookings);

        ModelAndView mav = new ModelAndView("customerdash");
        Customer c = Customer.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("customer", c);
        mav.addObject("businesses", businesses);
        mav.addObject("table", table);
        return mav;
    }

    @RequestMapping(path="/history")
    public ModelAndView getHistory(){
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof Customer)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        //Get all the businesses currently in system.
        ArrayList<BusinessOwner> businesses = obs.getAllBusinessOwners();

        //Get the past bookings for the customer
        ArrayList<Booking> pastBookings = obs.getPastBookingsForCustomer(user.getId());

        //build the my history table
        ArrayList<TableRow> historyTable = buildTable(pastBookings);

        ModelAndView mav = new ModelAndView("customer_history");
        Customer c = Customer.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("customer", c);
        mav.addObject("businesses", businesses);
        mav.addObject("historyTable", historyTable);
        return mav;
    }

    private ArrayList<TableRow> buildTable(ArrayList<Booking> bookings) {
        ArrayList<TableRow> table = new ArrayList<>();
        //Each row in the table is a booking made by the customer
        for(Booking b : bookings) { // <--- row
            TableRow row = new TableRow();
            row.id = String.format("%d", b.getId());
            TableCell businessCell = new TableCell();
            //Get business owner object from the database
            BusinessOwner bo = obs.getBusinessOwnerById(b.getBusinessService().getBusiness());
            if (bo != null) {
                businessCell.text = bo.getBusinessName();
            } else {
                businessCell.text = "";
            }
            // Each of the following groups is a column in the row.
            //The column name attribute is added for each cell so it can display when
            //the table collapses on a mobile and tablet screens
            businessCell.columnName = "Business";
            row.cells.add(businessCell);

            TableCell dateCell = new TableCell();
            dateCell.text = b.getStartLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/YYYY"));
            dateCell.columnName = "Date";
            row.cells.add(dateCell);

            TableCell employeeCell = new TableCell();
            employeeCell.text = b.getEmployee().getName();
            employeeCell.columnName = "Employee";
            row.cells.add(employeeCell);

            TableCell serviceCell = new TableCell();
            serviceCell.text = b.getBusinessService().getServiceName();
            serviceCell.columnName = "Service";
            row.cells.add(serviceCell);

            TableCell timeCell = new TableCell();
            timeCell.text = b.getTimeString();
            timeCell.columnName = "Time";
            row.cells.add(timeCell);

            TableCell durationCell = new TableCell();
            durationCell.text = String.format("%d minutes", b.getBusinessService().getDuration());
            durationCell.columnName = "Duration";
            row.cells.add(durationCell);

            table.add(row);
        }
        return table;
    }
}
