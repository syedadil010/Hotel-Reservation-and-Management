package OnlineBookingSystem.ModelClasses;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Specialisation {

    private int employee;
    private int service;
    private static Logger logger = Logger.getLogger("Specialisation");

    public Specialisation(int employee, int service) {
        this.employee = employee;
        this.service = service;
    }

    public int getEmployee() {
        return employee;
    }

    public void setEmployee(int employee) {
        this.employee = employee;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    /**
     * Adds a specialisation to the database
     * @param specialisation
     */
    public static void addSpecialisation(Specialisation specialisation){
        try{
            PreparedStatement statement = Database.getConnection().prepareStatement(
                    "insert into specialisation values (?, ?)");
            statement.setInt(1, specialisation.getEmployee());
            statement.setInt(2, specialisation.getService());
            statement.executeUpdate();
        }
        catch (Exception ex){
            logger.severe(ex.getMessage());
        }
    }

    /**
     * Removes all records of a specialisation from the database.
     * When removing a service, this method must be called first.
     * @param serviceId the service in which employees specialise
     */
    public static void deleteSpecialisations(int serviceId) {
        try{
            PreparedStatement statement = Database.getConnection().prepareStatement("" +
                    "delete " +
                    "from specialisation " +
                    "where service = ?;");
            statement.setInt(1, serviceId);
            statement.executeUpdate();
        }catch (SQLException e){
            logger.severe(e.getMessage());
        }
    }
    
    public static void deleteEmployeeSpecialisations(int employeeId) {
    	try{
            PreparedStatement statement = Database.getConnection().prepareStatement("" +
                    "delete " +
                    "from specialisation " +
                    "where employee = ?;");
            statement.setInt(1, employeeId);
            statement.executeUpdate();
        }catch (SQLException e){
            logger.severe(e.getMessage());
        }
    }
}
