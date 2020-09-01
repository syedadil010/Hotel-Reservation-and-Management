package OnlineBookingSystem.ModelClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Work {

    private static Logger logger = Logger.getLogger("Work");

    public static ArrayList<Work> getAllWork(int businessId){
        ArrayList<Work> listOfWork = new ArrayList<Work>();
        try{
            Connection c = Database.getConnection();
            PreparedStatement get = c.prepareStatement("" +
                    "SELECT * " +
                    "FROM " +
                    " Work " +
                    "JOIN " +
                    " employee On work.employee = employee.id " +
                    "WHERE employee.business = ?");
            get.setInt(1, businessId);

            ResultSet workresults = get.executeQuery();
            while(workresults.next()){
                Work w = new Work(
                        workresults.getInt("id"),
                        workresults.getInt("employee"),
                        workresults.getInt("workShift")
                );
                listOfWork.add(w);
            }
            return listOfWork;
        }
        catch(SQLException se){
            logger.severe("SQL Exception in GetShift, Details: " + se.getMessage());
            return listOfWork;
        }
    }

    /**
     * Get all rostered shifts for an employee
     * @param employeeId the ID of the employee
     * @return an ArrayList of work rostered for an employee
     */
    public static ArrayList<Work> getAllWorkForEmployee(int employeeId){
        ArrayList<Work> listOfWork = new ArrayList<>();
        try{
            Connection c = Database.getConnection();
            PreparedStatement get = c.prepareStatement("" +
                    "SELECT * " +
                    "FROM " +
                    " Work " +
                    "WHERE employee = ?");
            get.setInt(1, employeeId);

            ResultSet workresults = get.executeQuery();
            while(workresults.next()){
                Work w = new Work(
                        workresults.getInt("id"),
                        workresults.getInt("employee"),
                        workresults.getInt("workShift")
                );
                listOfWork.add(w);
            }
        }
        catch(SQLException se){
            logger.severe("SQL Exception in GetShift, Details: " + se.getMessage());
        }
        return listOfWork;
    }

    /**
     * Method to get a single work by ID
     * @param workId database ID for the work relation
     * @return null if not found otherwise the work object.
     */
    public static Work getWorkById(int workId){
        Work w;
        try{
            Connection c = Database.getConnection();
            PreparedStatement get = c.prepareStatement("" +
                    "SELECT * FROM work WHERE id = ?;");
            get.setInt(1, workId);
            ResultSet results = get.executeQuery();
            if(results.next()){
                w = new Work(
                        results.getInt("id"),
                        results.getInt("employee"),
                        results.getInt("workShift")
                );
            }
            else{
                w = null;
            }
            return w;
        }
        catch(SQLException se){
            logger.severe("SQL Exception in getWorkById, Details: " + se.getMessage());
            return null;
        }
    }

    public static boolean saveWorks(List<Work> toBeSaved){
        for(Work w : toBeSaved){
            if(w.getId() == -1){
                try{
                    PreparedStatement save = Database.getConnection().prepareStatement("" +
                            "INSERT INTO work ( " +
                            " employee, workShift ) " +
                            "VALUES ( " +
                            "? , ? );");
                    save.setInt(1, w.getEmployee().getId());
                    save.setInt(2, w.getWorkShift().getId());
                    save.executeUpdate();
                }
                catch (SQLException se){
                    se.printStackTrace();
                    logger.severe(se.getMessage());
                    return false;
                }
            }
            else{
                try{
                    PreparedStatement save = Database.getConnection().prepareStatement("" +
                            "INSERT OR REPLACE INTO workShift ( " +
                            " id, employee, workShift ) " +
                            "VALUES ( " +
                            "?, ?, ?);");
                    save.setInt(1, w.getId());
                    save.setInt(2, w.getEmployee().getId());
                    save.setInt(3, w.getWorkShift().getId());
                    save.executeUpdate();
                }
                catch (SQLException se){
                    se.printStackTrace();
                    logger.severe(se.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deletes a List of workshifts from the database.
     * @param toBeDeleted List to be deleted
     * @return true for success false for failure
     */
    public static boolean deleteWorkShifts(ArrayList<Work> toBeDeleted){
        for(Work w : toBeDeleted){
            if(w.getId() == -1){
                continue;
            }
            else{
                try{
                    PreparedStatement delete = Database.getConnection().prepareStatement("" +
                            "DELETE FROM work WHERE id = ? ;");
                    delete.setInt(1, w.getId());
                    delete.executeUpdate();
                }
                catch (SQLException se){
                    se.printStackTrace();
                    logger.severe(se.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


    private int id;
    private Employee employee;
    private WorkShift workShift;

    private Work(int id, int employee, int workShift){
        this.id = id;
        this.employee = Employee.getEmployee(employee);
        this.workShift = WorkShift.getWorkShiftById(workShift);
    }

    public Work(int employee, int workShift){
        this.id = -1;
        this.employee = Employee.getEmployee(employee);
        this.workShift = WorkShift.getWorkShiftById(workShift);
    }

    public LocalDateTime getStart(){
        return workShift.getStartDateTime();
    }

    public LocalDateTime getEnd(){
        return workShift.getEndDateTime();
    }

    public int getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public WorkShift getWorkShift() {
        return workShift;
    }
}
