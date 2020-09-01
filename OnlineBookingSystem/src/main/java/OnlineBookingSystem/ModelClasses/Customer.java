package OnlineBookingSystem.ModelClasses;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Customer extends User {

    private static Logger logger = Logger.getLogger("Customer");

    public Customer(String firstName, String lastName, String email, String username, String password){
        super(-1, username, password);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private Customer(int id, String firstName, String lastName, String email, String username, String password){
        super(id, username, password);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private String email;
    private String firstName;
    private String lastName;

    //Method to get customer by username
    public static Customer getByUsername(String username){
        try{
            PreparedStatement getCustomer = Database.getConnection().prepareStatement("SELECT * FROM customer WHERE username = ?;");
            getCustomer.setString(1, username);
            ResultSet result = getCustomer.executeQuery();
            if(result.next()){
                return new Customer(
                        result.getInt("id"),
                        result.getString("firstName"),
                        result.getString("lastName"),
                        result.getString("email"),
                        result.getString("username"),
                        result.getString("password")
                );
            }
        }
        catch (SQLException e){
            logger.severe(e.getMessage());
        }
        return null;
    }

    public static Customer getById(int id){
        try{
            PreparedStatement getCustomer = Database.getConnection().prepareStatement("SELECT * FROM customer WHERE id = ?;");
            getCustomer.setInt(1, id);
            ResultSet result = getCustomer.executeQuery();
            if(result.next()){
                return new Customer(
                        result.getInt("id"),
                        result.getString("firstName"),
                        result.getString("lastName"),
                        result.getString("email"),
                        result.getString("username"),
                        result.getString("password")
                );
            }
        }
        catch (SQLException e){
            logger.severe(e.getMessage());
        }
        return null;
    }

    public static ArrayList<Customer> getAllCustomers(){
        ArrayList<Customer> customers = new ArrayList<>();

        try{
            PreparedStatement stmt = Database.getConnection().prepareStatement("" +
                    "SELECT * FROM customer;");
            ResultSet result = stmt.executeQuery();
            while(result.next()){
                customers.add(new Customer(
                        result.getInt("id"),
                        result.getString("firstName"),
                        result.getString("lastName"),
                        result.getString("email"),
                        result.getString("username"),
                        result.getString("password")
                ));
            }
        }
        catch (SQLException se){
            logger.severe(se.getMessage());
        }
        return customers;
    }
    //Method to save customer
    public static void saveCustomer(Customer c){
        try{
            //Check to see if this is a new customer
            if(c.getId() == -1) {
                PreparedStatement saveCustomer = Database.getConnection().prepareStatement("" +
                        "INSERT INTO customer (" +
                        "username, password, firstName, lastName, email) " +
                        "VALUES (" +
                        "?, ?, ?, ?, ? )");
                saveCustomer.setString(1,c.getUsername());
                saveCustomer.setString(2,c.getPassword());
                saveCustomer.setString(3,c.getFirstName());
                saveCustomer.setString(4,c.getLastName());
                saveCustomer.setString(5,c.getEmail());

                saveCustomer.executeUpdate();
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
