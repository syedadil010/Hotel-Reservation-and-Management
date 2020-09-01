package OnlineBookingSystem.ModelClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class to hold a shift
 */
public class Shift {

    //Logger
    private static Logger logger = Logger.getLogger("Shift");

    /**
     *
     * @param businessId the business ID to get for their working shifts.
     * @return an ArrayList<Shift> of shifts that an employee can work.
     */
    public static ArrayList<Shift> getShifts(int businessId){
        ArrayList<Shift> listOfAvailableWork = new ArrayList<Shift>();
        try{
            Connection c = Database.getConnection();
            PreparedStatement sql = c.prepareStatement("" +
                    "SELECT * " +
                    "FROM " +
                    " shift " +
                    "WHERE " +
                    " business = ? ;");
            sql.setInt(1, businessId);
            ResultSet results = sql.executeQuery();
            while(results.next()){
                Shift s = new Shift(
                        results.getInt("id"),
                        results.getString("name"),
                        results.getInt("startHour"),
                        results.getInt("startMin"),
                        results.getInt("endHour"),
                        results.getInt("endMin")
                );
                listOfAvailableWork.add(s);
            }
            return listOfAvailableWork;
        }
        catch(SQLException se){
            logger.severe("SQL Exception in GetShift, Details: " + se.getMessage());
            return listOfAvailableWork;
        }
    }

    public static Shift getShift(int businessId, int shiftId){
        try{
            Connection c = Database.getConnection();
            PreparedStatement sql = c.prepareStatement("" +
                    "SELECT * " +
                    "FROM " +
                    " shift " +
                    "WHERE " +
                    " business = ? " +
                    "AND " +
                    " id = ?;");
            sql.setInt(1, businessId);
            sql.setInt(2, shiftId);
            ResultSet results = sql.executeQuery();
            if(results.next()){
                return new Shift(
                        results.getInt("id"),
                        results.getString("name"),
                        results.getInt("startHour"),
                        results.getInt("startMin"),
                        results.getInt("endHour"),
                        results.getInt("endMin")
                );
            }
            return null;
        }
        catch(SQLException se){
            logger.severe("SQL Exception in GetShift, Details: " + se.getMessage());
            return null;
        }
    }

    public static void setUpDefaultShifts(){
        Database.getDb().setupDefaultShifts();

    }

    /**
     * Constructor to build availability object from database.
     * @param id id of the shift for the business
     * @param name text name of the shift
     * @param startHour start hour of shift
     * @param startMin start minute of shift
     * @param endHour end hour of shift
     * @param endMin end minutes of shift
     */
    public Shift (
            int id,
            String name,
            int startHour,
            int startMin,
            int endHour,
            int endMin) {
        this.id = id;
        this.name = name;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
    }

    private int id;
    private String name;
    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    /**
     * @return returns a formatted string of the shift's start and end time.
     */
    public String getTimes(){
        return String.format("%d:%02d - %d:%02d", startHour, startMin, endHour, endMin);
    }


    public LocalTime getStartTime(){
        return LocalTime.of(startHour, startMin);
    }

    public LocalTime getEndTime(){
        return LocalTime.of(endHour, endMin);
    }
    /**
     * @return returns the total amount of minutes in a shift
     */
    public int getMinutes() {
    	int hours = endHour - startHour;
    	int minutes = endMin - startMin;
    	
    	int totalMinutes = (hours*60) + minutes;
    	return totalMinutes;
    }
}
