package OnlineBookingSystem.ModelClasses;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BusinessOwner extends User {

    private BusinessOwner(int id, String username, String password, String businessName, String businessOwnerName, String address, String phone, String tagline) {
        super(id, username, password);
        this.businessName = businessName;
        this.businessOwnerName = businessOwnerName;
        this.address = address;
        this.phone = phone;
        this.tagline = tagline;
    }

    public BusinessOwner(String username, String password, String businessName, String businessOwnerName,
                         String address, String phone, String tagline) {
        super(-1, username, password);
        this.businessName = businessName;
        this.businessOwnerName = businessOwnerName;
        this.address = address;
        this.phone = phone;
        this.tagline = tagline;
    }

    private String businessName;
    private String businessOwnerName;
    private String address;
    private String phone;
    private String tagline;

    public static ArrayList<BusinessOwner> getAllBusinessOwners(){
        ArrayList<BusinessOwner> owners = new ArrayList<>();
        try{
            PreparedStatement getBusinessOwner = Database.getConnection().prepareStatement("" +
                    "SELECT * FROM businessOwner;");
            ResultSet result = getBusinessOwner.executeQuery();
            while(result.next()){
                BusinessOwner b = new BusinessOwner(
                        result.getInt("id"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("businessName"),
                        result.getString("businessOwnerName"),
                        result.getString("address"),
                        result.getString("phone"),
                        result.getString("tagline")
                );
                owners.add(b);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return owners;
    }

    //Method to get customer by username
    public static BusinessOwner getByUsername(String username){
        try{
            PreparedStatement getBusinessOwner = Database.getConnection().prepareStatement("SELECT * FROM businessOwner WHERE username = ?;");
            getBusinessOwner.setString(1, username);
            ResultSet result = getBusinessOwner.executeQuery();
            if(result.next()){
                return new BusinessOwner(
                        result.getInt("id"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("businessName"),
                        result.getString("businessOwnerName"),
                        result.getString("address"),
                        result.getString("phone"),
                        result.getString("tagline")
                );
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static BusinessOwner getById(int id){
        try{
            PreparedStatement getBusinessOwner = Database.getConnection().prepareStatement("SELECT * FROM businessOwner WHERE id = ?;");
            getBusinessOwner.setInt(1, id);
            ResultSet result = getBusinessOwner.executeQuery();
            if(result.next()){
                return new BusinessOwner(
                        result.getInt("id"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("businessName"),
                        result.getString("businessOwnerName"),
                        result.getString("address"),
                        result.getString("phone"),
                        result.getString("tagline")
                );
            }
            else {
                return null;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    //Method to save business owner
    public static void save(BusinessOwner b){
        try{
            //Check to see if this is a new customer
            if(b.getId() == -1) {
                PreparedStatement saveBusinessOwner = Database.getConnection().prepareStatement("" +
                        "INSERT INTO businessOwner (" +
                        "username, password, businessName, businessOwnerName, phone, address, tagline) " +
                        "VALUES (" +
                        "?, ?, ?, ?, ?, ?, ? )");
                saveBusinessOwner.setString(1,b.getUsername());
                saveBusinessOwner.setString(2,b.getPassword());
                saveBusinessOwner.setString(3,b.getBusinessName());
                saveBusinessOwner.setString(4,b.getBusinessOwnerName());
                saveBusinessOwner.setString(5,b.getPhone());
                saveBusinessOwner.setString(6,b.getAddress());
                saveBusinessOwner.setString(7,b.getTagline());
                saveBusinessOwner.executeUpdate();
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessOwnerName() {
        return businessOwnerName;
    }

    public void setBusinessOwnerName(String businessOwnerName) {
        this.businessOwnerName = businessOwnerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getTagline() {
        return tagline;
    }

    public static void update(String businessName, String businessOwnerName, String address, String phone, String tagline){
        try{
            //Check to see if this is a new customer
            PreparedStatement saveBusinessOwner = Database.getConnection().prepareStatement(
                    "UPDATE businessOwner SET businessName=?, businessOwnerName=?, address=?, phone=?, tagline=? WHERE id=1;");
            saveBusinessOwner.setString(1,businessName);
            saveBusinessOwner.setString(2,businessOwnerName);
            saveBusinessOwner.setString(3,address);
            saveBusinessOwner.setString(4,phone);
            saveBusinessOwner.setString(5,tagline);
            saveBusinessOwner.executeUpdate();
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }
}
