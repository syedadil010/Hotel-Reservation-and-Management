package OnlineBookingSystem.ModelClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class BusinessService {

    private static Logger logger = Logger.getLogger("BusinessService");

    private BusinessService(int id, int business, String serviceName, int serviceCost, int duration) {
        this.id = id;
        this.business = business;
        this.serviceName = serviceName;
        this.serviceCost = serviceCost;
        this.duration = duration;
    }

    public BusinessService(int business, String serviceName, int serviceCost, int duration) {
        this.id = -1;
        this.business = business;
        this.serviceName = serviceName;
        this.serviceCost = serviceCost;
        this.duration = duration;
    }

    private int id;
    private int business;
    private String serviceName;
    private int serviceCost;
    private int duration;

    public int getId() {
        return id;
    }

    public int getBusiness() {
        return business;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getServiceCost() {
        return serviceCost;
    }

    public int getDuration() {
        return duration;
    }

    /**
     * Method to get all services for a business.
     * @param businessid the id of the business
     * @return An ArrayList of services belonging to the business.
     */
    public static ArrayList<BusinessService> getServices(int businessid){
        //Get the database connection
        ArrayList<BusinessService> businessServices = new ArrayList<>();
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM service WHERE business = ?;");
            stmt.setInt(1, businessid);
            ResultSet results = stmt.executeQuery();
            //Instantiate and store the employees
            while(results.next()){
                BusinessService s = new BusinessService(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("serviceName"),
                        results.getInt("serviceCost"),
                        results.getInt("duration")
                );
                businessServices.add(s);
            }
        }
        catch(SQLException e){
            logger.severe(e.getMessage());
        }
        return businessServices;
    }

    /**
     * Method to save a new service to the database
     * @param s Service to be saved
     */
    public static void saveService(BusinessService s){
        try{
            //Check to see if this is a new service
            if(s.getId() == -1) {
                PreparedStatement statement = Database.getConnection().prepareStatement("" +
                        "INSERT INTO service (" +
                        "business, serviceName, serviceCost, duration) " +
                        "VALUES (" +
                        "?, ?, ?, ? )");
                statement.setInt(1, s.getBusiness());
                statement.setString(2, s.getServiceName());
                statement.setInt(3, s.getServiceCost());
                statement.setInt(4, s.getDuration());
                statement.executeUpdate();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Removes a service from the database.
     * @param serviceId The unique id for that service
     */
    public static void deleteService(int serviceId) {
        try{
            PreparedStatement statement = Database.getConnection().prepareStatement("" +
                    "delete " +
                    "from service " +
                    "where id = ?;");
            statement.setInt(1, serviceId);
            statement.executeUpdate();
        }catch (SQLException e){
            logger.severe(e.getMessage());
        }
    }

    /**
     * Method to get all services for a business.
     * @param serviceId the id of the business
     * @return An ArrayList of services belonging to the business.
     */
    public static BusinessService getServiceById (int serviceId){
        //Get the database connection
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM service WHERE id = ?;");
            stmt.setInt(1, serviceId);
            ResultSet results = stmt.executeQuery();
            //Instantiate and store the employees
            if(results.next()){
                return new BusinessService(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("serviceName"),
                        results.getInt("serviceCost"),
                        results.getInt("duration")
                );
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to get a single serivce by ID for a perticular business
     * @param business business ID
     * @param serviceId name of the service
     * @return null if not found otherwise the service object.
     */
    static BusinessService getService(int business, int serviceId){
        try{
            //Query Database for Employee
            PreparedStatement stmt = Database.getConnection().prepareStatement("" +
                    "SELECT * FROM service WHERE business = ? AND id = ?;");
            stmt.setInt(1,business);
            stmt.setInt(2, serviceId);
            ResultSet results = stmt.executeQuery();
            if(results.next()){
                return new BusinessService(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("serviceName"),
                        results.getInt("serviceCost"),
                        results.getInt("duration")
                );
            }
            else{
                return null;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to get a list of services specific to one employee.
     * A business may offer many services, but an employee of that business
     * may specialise in a only a few of those services.
     * @param employeeId The id of the employee who specialises
     * @param businessId The id of the business that is offering services
     * @return a list of services that the employee offers
     */
    public static ArrayList<BusinessService> getSpecialisedServices(int employeeId, int businessId) {
        //Get the database connection
        ArrayList<BusinessService> businessServices = new ArrayList<>();
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "select * " +
                    "from service " +
                    "join specialisation on specialisation.service = service.id " +
                    "join employee on specialisation.employee = employee.id " +
                    "where service.business = ? " +
                    "and employee.id = ?;");
            stmt.setInt(1, businessId);
            stmt.setInt(2, employeeId);
            ResultSet results = stmt.executeQuery();
            //Instantiate and store the employees
            while(results.next()){
                BusinessService s = new BusinessService(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("serviceName"),
                        results.getInt("serviceCost"),
                        results.getInt("duration")
                );
                businessServices.add(s);
            }
        }
        catch(SQLException e){
            logger.severe(e.getMessage());
        }
        return businessServices;
    }

}
