package OnlineBookingSystem.ModelClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Day {

    //Logger
    private static Logger logger = Logger.getLogger("Day");
    //Static Methods

    /**
     * Get the days configured in the database
     * @return Arraylist of days
     */
    public static ArrayList<Day> getDays(){
        ArrayList<Day> listOfDays = new ArrayList<Day>();
        try{
            Connection c = Database.getConnection();
            PreparedStatement sql = c.prepareStatement("SELECT * FROM day ORDER BY id;");
            ResultSet results = sql.executeQuery();
            while(results.next()){
                Day d = new Day(
                        results.getInt("id"),
                        results.getString("dayText")
                );
                listOfDays.add(d);
            }
            return listOfDays;
        }
        catch(SQLException se){
            logger.severe("SQL Exception in Days, Details: " + se.getMessage());
            return listOfDays;
        }
    }

    public static Day getDay(int id){
        try{
            Connection c = Database.getConnection();
            PreparedStatement sql = c.prepareStatement("SELECT * FROM day WHERE id = ?;");
            sql.setInt(1, id);
            ResultSet results = sql.executeQuery();
            if(results.next()){
                return new Day(
                        results.getInt("id"),
                        results.getString("dayText")
                );
            }
            return null;
        }
        catch(SQLException se){
            logger.severe("SQL Exception in Days, Details: " + se.getMessage());
            return null;
        }
    }


    private int id;
    private String name;

    public Day ( int dayId, String dayText
    ){
        this.id = dayId;
        this.name = dayText;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate(){
        LocalDate today = LocalDate.now();
        switch (id){
            case 1: today = today.with(DayOfWeek.MONDAY); break;
            case 2: today = today.with(DayOfWeek.TUESDAY); break;
            case 3: today = today.with(DayOfWeek.WEDNESDAY); break;
            case 4: today = today.with(DayOfWeek.THURSDAY); break;
            case 5: today = today.with(DayOfWeek.FRIDAY); break;
            case 6: today = today.with(DayOfWeek.SATURDAY); break;
            case 7: today = today.with(DayOfWeek.SUNDAY); break;
        }
        return today;
    }
}
