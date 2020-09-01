package OnlineBookingSystem.ModelClasses;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.logging.Logger;

@Component
public class Database {

    private static Logger logger = Logger.getLogger("Database");
    private Connection connection = null;
    private static Database db;

    @PostConstruct
    public void init(){
        db = this;
        File f = new File("./obs.db");
        if(!f.exists()){
            db.connect("jdbc:sqlite:./obs.db");
            InputStream createDb = getClass().getResourceAsStream("/SQL/createdb.sql");
            db.executeFromFile(createDb);
            InputStream initLive = getClass().getResourceAsStream("/SQL/initLiveDb.sql");
            db.executeFromFile(initLive);
        }
        else{
            db.connect("jdbc:sqlite:./obs.db");
        }
    }

    /**
     * Initialisation method for a seperate test database.
     * Need to be static because @beforeclass requires static method.
     */
    public static void initTest(){
        db = new Database();
        db.initTestDatabase();
    }

    private void initTestDatabase(){
        //Check that if the file exists, delete it
        File dbFile = new File("./obstest.db");
        if(dbFile.exists() && !dbFile.delete()) {
            logger.severe("Error: Failed to delete test database");
            System.exit(1);
        }
        db.connect("jdbc:sqlite:./obstest.db");
        InputStream createDb = getClass().getResourceAsStream("/SQL/createdb.sql");
        db.executeFromFile(createDb);
        InputStream initTest = getClass().getResourceAsStream("/SQL/initTestDb.sql");
        db.executeFromFile(initTest);
        Database.generateBookings();
    }

    public static Database getDb(){
        return db;
    }


    /**
     * Connect to the database.
     * @param connectionString destination database.
     */
    public void connect(String connectionString){
        try {

            //Set up the connection
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(connectionString);
            logger.info("Connection to Databased Opened successfully");

        } catch ( Exception e ) {
            logger.severe("Failed to connect to database");
        }
    }

    /**
     * Method to get the active database connection.
     * @return Connection returns a database connection.
     */
    public static Connection getConnection(){
        return db.connection;
    }

    /**
     * Executes a series of SQL statements from file.
     * @param input the File object that contains the SQL statements to be executed.
     */
    public void executeFromFile(InputStream input){
        String initSql = "";
        String line = null;
        try{
            //populate initsql from init file
            logger.info("Reading file: " + input);
            BufferedReader file = new BufferedReader(new InputStreamReader(input));
            while((line = file.readLine()) != null){
                String[] lineParts = line.split("--");
                line = lineParts[0];
                initSql += line;
                if(initSql.contains(";")){
                    String[] sqlparts = initSql.split(";");
                    String sql = sqlparts[0];
                    Statement init = Database.getConnection().createStatement();
                    logger.fine("Executing: " + sql);
                    init.execute(sql);
                    initSql = "";
                }
            }
        }
        catch (Exception e){
            logger.severe("ERROR: Failed to initialise Database, Details: "  + e.getMessage());
            logger.severe("Please check that you are executing the programe from the root directory.");
            System.exit(1);
        }
    }

    private static void generateBookings(){
        try{
            //populate initsql from init file
            logger.info("Generating Bookings");
            //Get the Monday 9:30am
            LocalDate ld = LocalDate.now();
            Timestamp monday = Timestamp.valueOf(ld.with(DayOfWeek.MONDAY).atTime(9,30));
            Timestamp thursday = Timestamp.valueOf(ld.with(DayOfWeek.THURSDAY).atTime(12,30));
            PreparedStatement init = Database.getConnection().prepareStatement("" +
                "INSERT OR REPLACE INTO booking(" +
                "id, customer, employee, startTime, service " +
                ") VALUES (" +
                "?, 1, 1, ?, 2 );");
            init.setInt(1,1);
            init.setTimestamp(2, monday);
            init.executeUpdate();
            logger.fine("Booking scheduled for " + monday);
            init.setInt(1,2);
            init.setTimestamp(2,thursday);
            init.executeUpdate();
            logger.fine("Booking scheduled for " + thursday);
       }
        catch (Exception e){
            logger.severe("ERROR: Failed to initialise Database, Details: "  + e.getMessage());
        }
    }

    public void setupDefaultShifts(){
        executeFromFile(getClass().getResourceAsStream("/SQL/createShift.sql"));
    }

    public static void closeConnection(){
        try{
            db.connection.close();
        }
        catch(SQLException se){
            logger.severe(se.getMessage());
        }
    }

}
