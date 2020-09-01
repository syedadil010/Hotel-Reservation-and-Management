package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.DisplayClasses.TableCell;
import OnlineBookingSystem.DisplayClasses.TableRow;
import OnlineBookingSystem.ModelClasses.*;
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

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@RequestMapping(path = "businessowner/employee")
@Service
public class EmployeeController {
    @Autowired
    public EmployeeController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    private HttpSession session;
    private OBSFascade obs;
    /**
     * get a employee summary page
     * @return ModelAndView the rendered View.
     */
    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public ModelAndView getEmployeeSummary(RedirectAttributes redirectAttributes){
        //User must be logged in as Business Owner
        User user = LoginController.checkLogin(session);
        if(!(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        //get employees
        ArrayList<Employee> employees = obs.getEmployees(user.getId());
        ArrayList<BusinessService> allServices = obs.getServices(user.getId());
        int servicesTotal = allServices.size();

        //If there are no servivces, redirect to dashboard insisting that at least 1 service needs to be present.
        if(servicesTotal == 0){
            ModelAndView mav = new ModelAndView("redirect:/businessowner/dashboard");
            redirectAttributes.addFlashAttribute("Error", "You must first create services before you can create " +
                    "employees.");
            return mav;
        }


        //create table
        ArrayList<TableCell> headers = new ArrayList<>();
        ArrayList<TableRow> table = new ArrayList<>();

        //Table row, table header
        TableCell topLeftCell = new TableCell("");
        headers.add(topLeftCell);
        for(BusinessService s: allServices) { //column names are services
            TableCell cell = new TableCell(s.getServiceName());
            headers.add(cell);
        }
        headers.add(new TableCell("Actions"));
        //Table row, table data
        for(Employee e: employees) {
            //get the services the employee specialises in
            ArrayList<BusinessService> services = obs.getSpecialisedServices(e.getId(), user.getId());
            //initialise the row
            TableRow row = new TableRow();
            row.heading = e.getName();
            row.id = Integer.toString(e.getId());
            for (int x = 0; x < servicesTotal; ++x) {
                TableCell cell = new TableCell(false);
                row.cells.add(cell);
            }
            //go through each service and decide if the employee specialises
            for(int x = 0; x < servicesTotal; ++x){
                if(arrayContainsService(services, allServices.get(x)))
                    row.cells.get(x).hasValue = true; //set a flag to show this cell as a green tick
            }
            row.id = Integer.toString(e.getId());
            table.add(row);
        }
        //Build mav
        ModelAndView mav = new ModelAndView("employee_list");
        if (session.getAttribute("Error") != null) {
        	mav.addObject("Error", session.getAttribute("Error"));
        	session.removeAttribute("Error");
        }
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        mav.addObject("tableHeader", headers);
        mav.addObject("table", table);
        return mav;
    }

    private boolean arrayContainsService(ArrayList<BusinessService> services, BusinessService s) {
        for(BusinessService service: services) {
            if(s.getId() == service.getId())
                return true;
        }
        return false;
    }

    /**
     * Get Method to handle /Employee/Add and render a view that allow the user to add employees.
     * @return ModelAndView the rendered View.
     */
    @RequestMapping(path = "/add", method = RequestMethod.GET)
    public ModelAndView getEmployeeAdd(){
        //User must be logged in as Business Owner
        User user = LoginController.checkLogin(session);
        if(!(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        ArrayList<BusinessService> services = obs.getServices(user.getId());
        ModelAndView mav = new ModelAndView("employeeadd");
        mav.addObject("services", services);
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        return mav;
    }

    /**
     * Method to handle the POST request coming from the web application.
     * @param name Employee Name
     * @param email Employee Email
     * @param phone Employee Phone
     * @param address Employee Address
     * @return The Model and View Object to be rendered.
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ModelAndView postEmployeeAdd(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam Integer[] service
    ){
        //Check user is logged in.
        User user = LoginController.checkLogin(session);
        //Check user is a business owner
        if(!(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        ModelAndView mav = new ModelAndView("employeeadd");

        //Check that name is alpha beta and space only
        if(!name.matches("^[a-zA-Z ]+$")){
            mav.addObject("Error", "Name must only contain letters and spaces");
            return mav;
        }
        //Check that name is less than 100 characters long
        if(name.length() > 100){
            mav.addObject("Error", "Name must be 100 characters or less (Including spaces)");
            return mav;
        }

        //Check that email is of the correct format
        if(!email.matches("^[0-9A-Za-z.+]+@[0-9A-Za-z]+[.][0-9A-Za-z.]+$")){
            mav.addObject("Error", "Email must be in the correct format (abc@def.xyz)");
            return mav;
        }

        //Check that phone is of the correct format
        if(!phone.matches("^([+]61([ ]?[0-9]{3}){3})|([0-9]{4}([ ]?[0-9]{3}){2})$")){
            mav.addObject("Error", "Phone must be in the correct format.");
            return mav;
        }

        //Check that address is alpha numeral only
        if(!address.matches("^[a-zA-Z0-9, ]+$")){
            mav.addObject("Error", "Address must contain only letters, numbers and spaces.");
            return mav;
        }

        if(service.length == 0){
            mav.addObject("Error", "Each Employee must be able to provide at least one service.");
            return mav;
        }

        int businessId = user.getId();
        //Build the employee object
        Employee newEmployee = new Employee( businessId, name, email, phone, address );
        //Write to DB
        obs.saveEmployee(newEmployee);
        int employeeid = obs.getEmployeeIdByName(name);

        buildSpecialisations(service, employeeid);
        //Redirect to Dashboard.
        return new ModelAndView("redirect:/businessowner/dashboard");
    }
    
    @RequestMapping(path="/edit/{id}", method = RequestMethod.GET)
    public ModelAndView editEmployee(
		@PathVariable(name="id") int id
	) {
    	//Check user is logged in.
        User user = LoginController.checkLogin(session);
        //Check user is a business owner
        if(!(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
    	ModelAndView mav = new ModelAndView("editemployee");
    	
    	//get the employee object and send it along with the view
    	Employee e = Employee.getEmployee(id);
    	ArrayList<BusinessService> bs = BusinessService.getServices(user.getId());
    	mav.addObject("services", bs);
    	mav.addObject("Employee", e);
    	BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
    	return mav;
    }
    
    @RequestMapping(path="/edit", method = RequestMethod.POST)
    public ModelAndView updateEmployee(
		@RequestParam int id,
		@RequestParam String name,
        @RequestParam String email,
        @RequestParam String phone,
        @RequestParam String address,
        @RequestParam Integer[] service
	) {
    	//Check user is logged in.
        User user = LoginController.checkLogin(session);
        //Check user is a business owner
        if(!(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        
        ModelAndView mav = new ModelAndView("redirect:/businessowner/employee/edit/" + id);
        
        //Check that name is alpha beta and space only
        if(!name.matches("^[a-zA-Z ]+$")){
            mav.addObject("Error", "Name must only contain letters and spaces");
            return mav;
        }
        //Check that name is less than 100 characters long
        if(name.length() > 100){
            mav.addObject("Error", "Name must be 100 characters or less (Including spaces)");
            return mav;
        }

        //Check that email is of the correct format
        if(!email.matches("^[0-9A-Za-z.+]+@[0-9A-Za-z]+[.][0-9A-Za-z.]+$")){
            mav.addObject("Error", "Email must be in the correct format (abc@def.xyz)");
            return mav;
        }

        //Check that phone is of the correct format
        if(!phone.matches("^([+]61([ ]{0,1}[0-9]{3}){3})|([0-9]{4}([ ]{0,1}[0-9]{3}){2})$")){
            mav.addObject("Error", "Phone must be in the correct format.");
            return mav;
        }

        //Check that address is alpha numeral only
        if(!address.matches("^[a-zA-Z0-9, ]+$")){
            mav.addObject("Error", "Address must contain only letters, numbers and spaces.");
            return mav;
        }

        if(service.length == 0){
            mav.addObject("Error", "Each Employee must be able to provide at least one service.");
            return mav;
        }
        
    	// create Emplyoee object from posted data
    	Employee e = new Employee( user.getId(), name, email, phone, address);
    	e.setId(id);
    	
    	// update the employee
    	Employee.saveEmployee(e);
    	
    	Specialisation.deleteEmployeeSpecialisations(id);
    	buildSpecialisations(service, e.getId());
    	
    	return new ModelAndView("redirect:/businessowner/employee/list");
    }
    
    @RequestMapping(path = "/remove/{id}", method = RequestMethod.GET)
    public ModelAndView removeEmployee(
    		@PathVariable(name="id") int id
	){
    	ModelAndView mav = new ModelAndView("redirect:/businessowner/employee/list");
    	//Check user is logged in.
        User user = LoginController.checkLogin(session);
        //Check user is a business owner
        if(!(user instanceof BusinessOwner)){
            session.invalidate();
      
        }
        
        // check if there are any affected bookings
        ArrayList<Booking> bookings = obs.getBookingsForEmployee(id);
        
        // determine if there are any bookings in the future
        for (Booking b: bookings) {
        	if (b.getStartLocalDateTime().isAfter(LocalDateTime.now())) {
        		session.setAttribute("Error", "There are bookings under this employee, please cancel the bookings first.");
        		return mav;
        	}
        }

        ArrayList<Work> works = obs.getAllWorkForEmployee(id);
        if(works.size() > 0 ){
            session.setAttribute("Error", "This employee is rostered to work, please unroster the employee first.");
            return mav;
        }

        // if there are no bookings in the future then set deleted flag for employee and all associated bookings
        obs.removeEmployee(id);
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        return mav;
    	
    }

    private void buildSpecialisations(Integer[] services, int employeeid) {
        for(int s : services){
            Specialisation.addSpecialisation(new Specialisation(employeeid, s));
        }
    }

    /**
     * Method to render the detailed view for an employee
     * @param id The employee ID.
     * @return The Model and View object to be rendered
     */
    @RequestMapping(value = "/view/{id}")
    public ModelAndView viewEmployees(@PathVariable("id") int id){
        //Check the user is logged in.
        User user = LoginController.checkLogin(session);
        if(user == null){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
        //Get the employee from Database
        Employee employee = obs.getEmployee(id);
        ModelAndView mav = new ModelAndView("employeeview");
        mav.addObject("employee", employee);
        BusinessOwner b = BusinessOwner.getById(Integer.parseInt(session.getAttribute("id").toString()));
        mav.addObject("owner", b);
        return mav;
    }

}
