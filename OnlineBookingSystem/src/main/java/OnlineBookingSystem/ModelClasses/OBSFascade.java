package OnlineBookingSystem.ModelClasses;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface OBSFascade {
    BusinessService getServiceById(int id);
    ArrayList<Booking> getBookingsForEmployee(int id);
    ArrayList<Work> getAllWorkForEmployee(int id);
    ArrayList<Employee> getEmployeesByService(int businessId, int serviceid);
    ArrayList<BusinessService> getServices(int businessid);
    ArrayList<Day> getDays();
    ArrayList<Shift> getShifts(int businessid);
    ArrayList<WorkShift> getWorkShifts(int businessid);
    ArrayList<Work> getAllWork(int businessid);
    Booking getBooking(int BusinessOwnerId, int bookingId);
    Booking getBookingForCustomer(int customerId,int bookingId);
    ArrayList<Booking> getBookings(int businessOwnerId);
    ArrayList<Customer> getAllCustomers();
    ArrayList<Employee> getEmployees(int businessId);
    Work getWorkById(int workId);
    ArrayList<BusinessService> getSpecialisedServices(int employeeid, int businessId);
    Employee getEmployee(int employeeId);
    ArrayList<Booking> getBookingsForService(int serviceId);
    void deleteSpecialisations(int serviceId);
    void deleteService(int serviceId);
    void setUpDefaultShifts();
    ArrayList<BusinessOwner> getAllBusinessOwners();
    ArrayList<Booking> getUpcomingBookingsForCustomer(int customerId);
    ArrayList<Booking> getPastBookingsForCustomer(int customerId);
    BusinessOwner getBusinessOwnerById(int businessOwnerId);
    int getAccountStatus();
    Customer getCustomerByUsername(String username);
    BusinessOwner getBusinessOwnerByUsername(String username);
    Customer getCustomerById(int customerId);
    void saveWorkShifts(ArrayList<WorkShift> toBeAdded);
    void deleteWorkShifts(ArrayList<WorkShift> toBeDeleted);
    void saveCustomer(Customer newCustomer);
    void saveWorks(ArrayList<Work> toBeAdded);
    void deleteWorks(ArrayList<Work> toBeDeleted);
    void saveEmployee(Employee newEmployee);
    int getEmployeeIdByName(String name);
    void removeEmployee(int id);


}
