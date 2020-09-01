package OnlineBookingSystem.ModelClasses;

import OnlineBookingSystem.*;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Booking {

    private static Logger logger = Logger.getLogger("Booking");
    private int id;
    private Customer customer;
    private Employee employee;
    private BusinessService businessService;
    private Timestamp startTime;

    public Booking(int customer, int employee, int service, Timestamp startTime) {
        try{
            this.id = -1;
            this.customer = Customer.getById(customer);
            this.employee = Employee.getEmployee(employee);
            if(this.employee == null){
                throw new OBSException("Failed to get booking, employee id " + employee + "is null");
            }
            this.businessService = BusinessService.getService(this.employee.getBusiness(), service);
            this.startTime = startTime;
        }
        catch(OBSException obs){
            logger.severe(obs.getMessage());
        }
    }

    private Booking(int id, int customer, int employee, int service, Timestamp startTime){
        try{
            this.id = id;
            this.customer = Customer.getById(customer);
            this.employee = Employee.getEmployee(employee);
            if(this.employee == null){
                throw new OBSException("Failed to get booking, employee id " + employee + "is null");
            }
            this.businessService = BusinessService.getService(this.employee.getBusiness(), service);
            this.startTime = startTime;
        }
        catch(OBSException obs){
            logger.severe(obs.getMessage());
        }
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = Customer.getById(customer);
    }

    public Employee getEmployee() {
        return employee;
    }

    public BusinessService getBusinessService() {
        return businessService;
    }

    public void setService(int businessId, int service) {
        this.businessService = BusinessService.getService(businessId, service);
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public String getTimeString(){
        LocalDateTime start = startTime.toLocalDateTime();
        LocalDateTime end = start.plusMinutes(businessService.getDuration());
        return String.format("%s - %s",
                start.format(DateTimeFormatter.ofPattern("HH:mm")),
                end.format(DateTimeFormatter.ofPattern("HH:mm"))
        );
    }

    /**
     * Gets the start time of the booking
     * @return LocalDateTime of start time.
     */
    public LocalDateTime getStartLocalDateTime(){
        return startTime.toLocalDateTime();
    }

    /**
     * gets the end time of the booking
     * @return LocalDateTime of end time.
     */
    public LocalDateTime getEndLocalDateTime(){
        return startTime.toLocalDateTime().plusMinutes(businessService.getDuration());
    }

    public boolean cancelBooking(){
        try{
            PreparedStatement cancel = Database.getConnection().prepareStatement("" +
                    "DELETE FROM booking WHERE id = ?");
            cancel.setInt(1, id);
            cancel.executeUpdate();
            return true;
        }catch (SQLException s){
            logger.severe(s.getMessage());
            return false;
        }
    }

    public boolean withinNextSunday(){
        //Get timestamp of Monday Morning
        LocalDate now = LocalDate.now();
        LocalDate monday = now.with(DayOfWeek.MONDAY);
        Timestamp tmonday = Timestamp.valueOf(monday.atStartOfDay());
        //Get timestamp of Sunday Night
        LocalDate sunday = now.with(DayOfWeek.SUNDAY);
        Timestamp tsunday = Timestamp.valueOf(sunday.atTime(11, 59));

        return startTime.after(tmonday) && startTime.before(tsunday);
    }

    /**
     * Check to see if a booking occupies a perticular time frame
     * To check a perticular point, just set the end time and the start time to the same value.
     * @param start start of time frame
     * @param end end of time frame
     * @return true if booking occupies the time frame, else false. Also returns true if the input end time is before
     * the start time.
     */
    public boolean within(LocalDateTime start, LocalDateTime end){
        Timestamp tStart = Timestamp.valueOf(start);
        Timestamp tEnd = Timestamp.valueOf(end);
        LocalDateTime bookingStartldt = this.startTime.toLocalDateTime();
        LocalDateTime bookingEndLdt = bookingStartldt.plusMinutes(this.businessService.getDuration());
        Timestamp bookingEnd = Timestamp.valueOf(bookingEndLdt);

        //Check if the input is logical, if it's not, return true
        if(end.isBefore(start)){
            return true;
        }

        //If this booking started before the interested block
        if(this.startTime.before(tStart)){
            //if the end is after the start of the block, then this block must be occupied.
            if(bookingEnd.after(tStart)){
                return false;
            }
        }
        else if(this.startTime.equals(tStart)){
            //If this booking started at exactly the same time as the start of this block, this block is occupied.
            return true;
        }
        else{
            //If this booking started after the start time
            //Check to see if started after the end of the block we are interested it
            //If it starts before the end of the block, it also occupies this block.
            if(this.startTime.before(tEnd)){
                return true;
            }
        }

        //If it passes all of those checks, this booking does not belong in the slot
        return false;
    }

    public void save(){
        try{
            if(this.id == -1){
                PreparedStatement stmt = Database.getConnection().prepareStatement("" +
                        "INSERT OR REPLACE INTO booking (customer, employee, startTime, service) VALUES " +
                        "(?, ?, ?, ?);");
                stmt.setInt(1, this.customer.getId());
                stmt.setInt(2, this.employee.getId());
                stmt.setTimestamp(3,this.startTime);
                stmt.setInt(4, this.businessService.getId());
                stmt.executeUpdate();
            }
            else{
                PreparedStatement stmt = Database.getConnection().prepareStatement("" +
                        "INSERT OR REPLACE INTO booking (id, customer, employee, startTime, service) VALUES " +
                        "(?, ?, ?, ?, ?);");
                stmt.setInt(1,this.id);
                stmt.setInt(2, this.customer.getId());
                stmt.setInt(3, this.employee.getId());
                stmt.setTimestamp(4,this.startTime);
                stmt.setInt(5, this.businessService.getId());
                stmt.executeUpdate();
            }
        }
        catch (SQLException e){
            logger.severe(e.getMessage());
        }
    }

    /**
     * Method to get all bookings for a business.
     * @param businessid the id of the business
     * @return An ArrayList of bookings belonging to the business.
     */
    public static ArrayList<Booking> getBookings(int businessid){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("" +
                    "SELECT booking.* " +
                    "FROM booking " +
                    "JOIN employee on booking.employee = employee.id " +
                    "WHERE employee.business = ?;");
            stmt.setInt(1, businessid);
            ResultSet results = stmt.executeQuery();
            ArrayList<Booking> bookings = new ArrayList<>();
            //Instantiate and store the bookings
            while(results.next()){
                Booking b = new Booking(
                        results.getInt("id"),
                        results.getInt("customer"),
                        results.getInt("employee"),
                        results.getInt("service"),
                        results.getTimestamp("startTime"));
                bookings.add(b);
            }
            return bookings;
        }
        catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Method to get all bookings for a particular service
     * @param serviceId The service to check bookings against
     * @return an array list of all bookings for the given service
     */
    public static ArrayList<Booking> getBookingsForService(int serviceId){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("" +
                    "select * " +
                    "from booking join service " +
                    "on booking.service = service.id " +
                    "and service.id = ?;");
            stmt.setInt(1, serviceId);
            ResultSet results = stmt.executeQuery();
            ArrayList<Booking> bookings = new ArrayList<>();
            //Instantiate and store the bookings
            while(results.next()){
                Booking b = new Booking(
                        results.getInt("id"),
                        results.getInt("customer"),
                        results.getInt("employee"),
                        results.getInt("service"),
                        results.getTimestamp("startTime"));
                bookings.add(b);
            }
            return bookings;
        }
        catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Method to get all bookings for an employee.
     * @param employeeid the id of the employee to query.
     * @return An ArrayList of bookings belonging to the employee.
     */
    public static ArrayList<Booking> getBookingsForEmployee(int employeeid){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("" +
                    "SELECT * " +
                    "FROM booking " +
                    "WHERE employee = ? " +
                    "ORDER BY startTime;");
            stmt.setInt(1, employeeid);
            ResultSet results = stmt.executeQuery();
            ArrayList<Booking> bookings = new ArrayList<>();
            //Instantiate and store the bookings
            while(results.next()){
                Booking b = new Booking(
                        results.getInt("id"),
                        results.getInt("customer"),
                        results.getInt("employee"),
                        results.getInt("service"),
                        results.getTimestamp("startTime"));
                bookings.add(b);
            }
            return bookings;
        }
        catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static ArrayList<Booking> getBookingsForCustomer(int customerid){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("" +
                    "SELECT * " +
                    "FROM booking " +
                    "WHERE customer = ? " +
                    "ORDER BY startTime;");
            stmt.setInt(1, customerid);
            ResultSet results = stmt.executeQuery();
            ArrayList<Booking> bookings = new ArrayList<>();
            //Instantiate and store the bookings
            while(results.next()){
                Booking b = new Booking(
                        results.getInt("id"),
                        results.getInt("customer"),
                        results.getInt("employee"),
                        results.getInt("service"),
                        results.getTimestamp("startTime"));
                bookings.add(b);
            }
            return bookings;
        }
        catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static ArrayList<Booking> getUpcomingBookingsForCustomer(int customerid){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("" +
                    "SELECT * " +
                    "FROM booking " +
                    "WHERE customer = ? " +
                    "AND startTime > ?" +
                    "ORDER BY startTime;");
            stmt.setInt(1, customerid);
            //LocalDateTime of today at 00.00am
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)));
            ResultSet results = stmt.executeQuery();
            ArrayList<Booking> bookings = new ArrayList<>();
            //Instantiate and store the bookings
            while(results.next()){
                Booking b = new Booking(
                        results.getInt("id"),
                        results.getInt("customer"),
                        results.getInt("employee"),
                        results.getInt("service"),
                        results.getTimestamp("startTime"));
                bookings.add(b);
            }
            return bookings;
        }
        catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static ArrayList<Booking> getPastBookingsForCustomer(int customerid){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("" +
                    "SELECT * " +
                    "FROM booking " +
                    "WHERE customer = ? " +
                    "AND startTime < ?" +
                    "ORDER BY startTime;");
            stmt.setInt(1, customerid);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)));
            ResultSet results = stmt.executeQuery();
            ArrayList<Booking> bookings = new ArrayList<>();
            //Instantiate and store the bookings
            while(results.next()){
                Booking b = new Booking(
                        results.getInt("id"),
                        results.getInt("customer"),
                        results.getInt("employee"),
                        results.getInt("service"),
                        results.getTimestamp("startTime"));
                bookings.add(b);
            }
            return bookings;
        }
        catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static Booking getBookingForCustomer(int customerid, int bookingId){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("" +
                    "SELECT * " +
                    "FROM booking " +
                    "WHERE customer = ? " +
                    "AND id = ? " +
                    "ORDER BY startTime;");
            stmt.setInt(1, customerid);
            stmt.setInt(2,bookingId);
            ResultSet results = stmt.executeQuery();
            //Instantiate and store the bookings
            if(results.next()){
                return new Booking(
                        results.getInt("id"),
                        results.getInt("customer"),
                        results.getInt("employee"),
                        results.getInt("service"),
                        results.getTimestamp("startTime"));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public static Booking getBooking(int businessid, int bookingid){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("" +
                    "SELECT booking.* " +
                    "FROM booking " +
                    "JOIN employee on booking.employee = employee.id " +
                    "WHERE employee.business = ? " +
                    "AND booking.id = ?;");
            stmt.setInt(1, businessid);
            stmt.setInt(2, bookingid);
            ResultSet results = stmt.executeQuery();
            ArrayList<Booking> bookings = new ArrayList<>();
            //Instantiate and store the bookings
            if(results.next()){
                return new Booking(
                        results.getInt("id"),
                        results.getInt("customer"),
                        results.getInt("employee"),
                        results.getInt("service"),
                        results.getTimestamp("startTime"));
            }
        }
        catch(SQLException e){
            logger.severe(e.getMessage());
        }
        return null;
    }
}
