package OnlineBookingSystem.ModelClasses;

import java.util.ArrayList;

public class OBSModel implements OBSFascade{
    private static OBSModel model;

    public OBSModel(){
    }

    public static OBSModel getModel(){
        if(model == null){
            model = new OBSModel();
        }
        return model;
    }

    @Override
    public BusinessService getServiceById(int id) {
        return BusinessService.getServiceById(id);
    }

    @Override
    public ArrayList<Booking> getBookingsForEmployee(int id) {
        return Booking.getBookingsForEmployee(id);
    }

    @Override
    public ArrayList<Work> getAllWorkForEmployee(int id) {
        return Work.getAllWorkForEmployee(id);
    }

    @Override
    public ArrayList<Employee> getEmployeesByService(int businessId, int serviceid) {
        return Employee.getEmployeesByService(businessId, serviceid);
    }

    @Override
    public ArrayList<BusinessService> getServices(int businessid) {
        return BusinessService.getServices(businessid);
    }

    @Override
    public ArrayList<Day> getDays() {
        return Day.getDays();
    }

    @Override
    public ArrayList<Shift> getShifts(int businessid) {
        return Shift.getShifts(businessid);
    }

    @Override
    public ArrayList<WorkShift> getWorkShifts(int businessid) {
        return WorkShift.getWorkShifts(businessid);
    }

    @Override
    public ArrayList<Work> getAllWork(int businessid) {
        return Work.getAllWork(businessid);
    }

    @Override
    public Booking getBooking(int businessOwnerId, int bookingId) {
        return Booking.getBooking(businessOwnerId, bookingId);
    }

    @Override
    public Booking getBookingForCustomer(int customerId, int bookingId) {
        return Booking.getBookingForCustomer(customerId, bookingId);
    }

    @Override
    public ArrayList<Booking> getBookings(int businessOwnerId) {
        return Booking.getBookings(businessOwnerId);
    }

    @Override
    public ArrayList<Customer> getAllCustomers() {
        return Customer.getAllCustomers();
    }

    @Override
    public ArrayList<Employee> getEmployees(int businessId) {
        return Employee.getEmployees(businessId);
    }

    @Override
    public Work getWorkById(int workId) {
        return Work.getWorkById(workId);
    }

    @Override
    public ArrayList<BusinessService> getSpecialisedServices(int employeeid, int businessId) {
        return BusinessService.getSpecialisedServices(employeeid, businessId);
    }

    @Override
    public Employee getEmployee(int employeeId) {
        return Employee.getEmployee(employeeId);
    }

    @Override
    public ArrayList<Booking> getBookingsForService(int serviceId) {
        return Booking.getBookingsForService(serviceId);
    }

    @Override
    public void deleteSpecialisations(int serviceId) {
        Specialisation.deleteSpecialisations(serviceId);
    }

    @Override
    public void deleteService(int serviceId) {
        BusinessService.deleteService(serviceId);
    }

    @Override
    public void setUpDefaultShifts() {
        Shift.setUpDefaultShifts();
    }

    @Override
    public ArrayList<BusinessOwner> getAllBusinessOwners() {
        return BusinessOwner.getAllBusinessOwners();
    }

    @Override
    public ArrayList<Booking> getUpcomingBookingsForCustomer(int customerId) {
        return Booking.getUpcomingBookingsForCustomer(customerId);
    }

    @Override
    public ArrayList<Booking> getPastBookingsForCustomer(int customerId) {
        return Booking.getPastBookingsForCustomer(customerId);
    }

    @Override
    public BusinessOwner getBusinessOwnerById(int businessOwnerId) {
        return BusinessOwner.getById(businessOwnerId);
    }

    @Override
    public int getAccountStatus() {
        return Setup.getAccountStatus();
    }

    @Override
    public Customer getCustomerByUsername(String username) {
        return Customer.getByUsername(username);
    }

    @Override
    public BusinessOwner getBusinessOwnerByUsername(String username) {
        return BusinessOwner.getByUsername(username);
    }

    @Override
    public Customer getCustomerById(int customerId) {
        return Customer.getById(customerId);
    }

    @Override
    public void saveWorkShifts(ArrayList<WorkShift> toBeAdded) {
        WorkShift.saveWorkShifts(toBeAdded);
    }

    @Override
    public void deleteWorkShifts(ArrayList<WorkShift> toBeDeleted) {
        WorkShift.deleteWorkShifts(toBeDeleted);
    }

    @Override
    public void saveCustomer(Customer newCustomer) {
        Customer.saveCustomer(newCustomer);
    }

    @Override
    public void saveWorks(ArrayList<Work> toBeAdded) {
        Work.saveWorks(toBeAdded);
    }

    @Override
    public void deleteWorks(ArrayList<Work> toBeDeleted) {
        Work.deleteWorkShifts(toBeDeleted);
    }

    @Override
    public void saveEmployee(Employee newEmployee) {
        Employee.saveEmployee(newEmployee);
    }

    @Override
    public int getEmployeeIdByName(String name) {
        return Employee.getEmployeeIdByName(name);
    }

    @Override
    public void removeEmployee(int id) {
        Employee.removeEmployee(id);
    }
}
