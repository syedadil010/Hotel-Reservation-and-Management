package OnlineBookingSystem.ModelClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class to hold the business hours of the business.
 */
public class WorkShift {

    private static Logger logger = Logger.getLogger("WorkShift");

    public static ArrayList<WorkShift> getWorkShifts(int businessId){
        ArrayList<WorkShift> listOfAvailableWork = new ArrayList<WorkShift>();
        try{
            Connection c = Database.getConnection();
            PreparedStatement sql = c.prepareStatement("" +
                            "SELECT workShift.id, workShift.business, workShift.shift, workShift.day " +
                            "FROM " +
                            " workShift " +
                            "JOIN " +
                            " day ON workShift.day = day.id " +
                            "JOIN " +
                            " shift ON workshift.shift = shift.id " +
                            "WHERE workshift.business = ? " +
                            "ORDER BY day.id, shift.startHour;"
                    );
            sql.setInt(1, businessId);
            ResultSet wsresults = sql.executeQuery();
            while(wsresults.next()){

                WorkShift ws = new WorkShift(
                        wsresults.getInt("id"),
                        wsresults.getInt("business"),
                        wsresults.getInt("shift"),
                        wsresults.getInt("day")
                );
                listOfAvailableWork.add(ws);
            }
            return listOfAvailableWork;
        }
        catch(SQLException se){
            logger.severe("SQL Exception in GetWorkShift, Details: " + se.getMessage());
            return listOfAvailableWork;
        }

    }

    public static WorkShift getWorkShiftById(int id){
        try{
            Connection c = Database.getConnection();
            PreparedStatement sql = c.prepareStatement(
                    "SELECT * " +
                    "FROM " +
                    " workShift " +
                    "WHERE id = ? ;");
            sql.setInt(1, id);
            ResultSet wsresults = sql.executeQuery();
            if(wsresults.next()){
                WorkShift ws = new WorkShift(
                        wsresults.getInt("id"),
                        wsresults.getInt("business"),
                        wsresults.getInt("shift"),
                        wsresults.getInt("day")
                );
                return ws;
            }
            return null;
        }
        catch(SQLException se){
            logger.severe("SQL Exception in GetWorkShift, Details: " + se.getMessage());
            return null;
        }
    }

    /**
     * Method to save a list of workshifts to database.
     * @param toBeSaved List of workshifts to be saved.
     * @return
     */
    public static boolean saveWorkShifts(List<WorkShift> toBeSaved){
        for(WorkShift ws : toBeSaved){
            if(ws.getId() == -1){
                try{
                    PreparedStatement save = Database.getConnection().prepareStatement("" +
                            "INSERT INTO workShift ( " +
                            " day, business, shift ) " +
                            "VALUES ( " +
                            "? , ?, ? );");
                    save.setInt(1, ws.day.getId());
                    save.setInt(2, ws.business);
                    save.setInt(3, ws.shift.getId());
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
                            " id, day, business, shift ) " +
                            "VALUES ( " +
                            "?, ? , ?, ? );");
                    save.setInt(1, ws.getId());
                    save.setInt(2, ws.day.getId());
                    save.setInt(3, ws.business);
                    save.setInt(4, ws.shift.getId());
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
    public static boolean deleteWorkShifts(ArrayList<WorkShift> toBeDeleted){
        for(WorkShift ws : toBeDeleted){
            if(ws.getId() == -1){
                continue;
            }
            else{
                try{
                    PreparedStatement delete = Database.getConnection().prepareStatement("" +
                            "DELETE FROM workShift WHERE day = ? AND business = ? AND shift = ? ;");
                    delete.setInt(1, ws.day.getId());
                    delete.setInt(2, ws.business);
                    delete.setInt(3, ws.shift.getId());
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

    //Class variables (Properties)
    private int id;
    private int business;
    private Day day;
    private Shift shift;

    //Private constructor for reading from DB.
    private WorkShift(int id, int business, int shift, int day){
        this.id = id;
        this.business = business;
        this.day = Day.getDay(day);
        this.shift = Shift.getShift(business,shift);
    }

    //Package private constructor for creating a new workshift.
    public WorkShift(int business, int shift, int day){
        this.id = -1;
        this.business = business;
        this.day = Day.getDay(day);
        this.shift = Shift.getShift(business,shift);
    }

    /**
     * Check to see if a perticular workshift is safe to delete.
     * @return null if it is safe, else returns the employee rostered to work on this shift.
     */
    public Employee safeToDelete(){
        try{
            PreparedStatement safe = Database.getConnection().prepareStatement(
                    "SELECT work.employee FROM work JOIN workShift ON work.workShift = workShift.id WHERE workShift.id = ?;"
            );
            safe.setInt(1, this.id);
            ResultSet works = safe.executeQuery();
            if(works.next()){
                return Employee.getEmployee(works.getInt("employee"));
            }
        }
        catch(SQLException se){
            logger.severe("SQL ERROR: " + se.getMessage());
        }
        return null;
    }

    public LocalDateTime getStartDateTime(){
        LocalTime startTime = shift.getStartTime();
        LocalDate startDate = day.getDate();
        return LocalDateTime.of(startDate, startTime);
    }

    public LocalDateTime getEndDateTime(){
        LocalTime endTime = shift.getEndTime();
        LocalDate endDate = day.getDate();
        return LocalDateTime.of(endDate, endTime);
    }

    public int getId() {
        return id;
    }

    public int getBusiness() {
        return business;
    }

    public Day getDay() {
        return day;
    }

    public Shift getShift() {
        return shift;
    }
}
