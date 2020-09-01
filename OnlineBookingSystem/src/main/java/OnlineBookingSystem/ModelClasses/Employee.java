package OnlineBookingSystem.ModelClasses;


import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Employee {
    private static Logger logger = Logger.getLogger("Employee");

    private int id = -1;
    private int business;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Boolean deleted = false;

    private Employee(int id, int business, String name, String email, String phone, String address, Boolean deleted) {
        this.id = id;
        this.business = business;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.deleted = deleted;
    }

    public Employee(int business, String name, String email, String phone, String address) {
        this.id = -1;
        this.business = business;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    
    //getters
    public int getId() {
        return id;
    }
    public int getBusiness() {
        return business;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    
    public boolean getDeleted() {
    	return deleted;
    }
    
    //setters
    public void setDeleted(Boolean deleted) {
    	this.deleted = deleted;
    }
    
    public void setId(int id) {
    	this.id = id;
    }

    /**
     * Method to get all employees for a business.
     * @param businessid the id of the business
     * @return A ArrayList of employees belonging to the business.
     */
    public static ArrayList<Employee> getEmployees(int businessid){
        //Get the database connection
        ArrayList<Employee> employees = new ArrayList<>();
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM employee WHERE business = ?;");
            stmt.setInt(1, businessid);
            ResultSet results = stmt.executeQuery();
            //Instantiate and store the employees
            while(results.next()){
                Employee e = new Employee(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("name"),
                        results.getString("email"),
                        results.getString("phone"),
                        results.getString("address"),
                        results.getBoolean("deleted")
                );
                if(!e.deleted)
                	employees.add(e);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * Get a list of employees filtered by specialisations
     * @param businessid int of the business
     * @param serviceid db int of the service
     * @return a list of employees who match the service. Empty if none or error.
     */
    public static ArrayList<Employee> getEmployeesByService(int businessid, int serviceid){
        //Get the database connection
        ArrayList<Employee> employees = new ArrayList<>();
        try{
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "select * from employee " +
                            "join specialisation on employee.id = specialisation.employee " +
                            "where specialisation.service = ? " +
                            "and employee.business = ?;");
            stmt.setInt(1, serviceid);
            stmt.setInt(2, businessid);
            ResultSet results = stmt.executeQuery();
            //Instantiate and store the employees
            while(results.next()){
                Employee e = new Employee(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("name"),
                        results.getString("email"),
                        results.getString("phone"),
                        results.getString("address"),
                        results.getBoolean("deleted")
                );
                if(!e.deleted)
                	employees.add(e);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * Method to get one employee from the database
     * @param employeeId the ID of the employee
     * @return null if not found, otherwise the Employee object.
     */
    public static Employee getEmployee(int employeeId){
        try{
            //Query Database for Employee
            PreparedStatement stmt = Database.getConnection().prepareStatement("" +
                    "SELECT * FROM employee WHERE id = ?;");
            stmt.setInt(1, employeeId);
            ResultSet results = stmt.executeQuery();
            if(results.next()){
                Employee e =  new Employee(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("name"),
                        results.getString("email"),
                        results.getString("phone"),
                        results.getString("address"),
                        results.getBoolean("deleted")
                );
                if (!e.getDeleted())
                	return e;
            }
            else{
                return null;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Method to get one employee from the database
     * @param name of the employee
     * @return null if not found, otherwise the Employee object.
     */
    public static Employee getEmployee(String name){
        try{
            //Query Database for Employee
            PreparedStatement stmt = Database.getConnection().prepareStatement("" +
                    "SELECT * FROM employee WHERE name = ?;");
            stmt.setString(1, name);
            ResultSet results = stmt.executeQuery();
            if(results.next()){
                Employee e = new Employee(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("name"),
                        results.getString("email"),
                        results.getString("phone"),
                        results.getString("address"),
                        results.getBoolean("deleted")
                );
                if (!e.getDeleted())
                	return e;
            }
            else{
                return null;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Method to get one employee from the database
     * @param name of the employee
     * @return null if not found, otherwise the Employee object.
     */
    public static int getEmployeeIdByName(String name){
        try{
            //Query Database for Employee
            PreparedStatement stmt = Database.getConnection().prepareStatement("" +
                    "SELECT * FROM employee WHERE name = ?;");
            stmt.setString(1, name);
            ResultSet results = stmt.executeQuery();
            if(results.next())
                return results.getInt("id");
            else
                return -1;
        }
        catch(SQLException e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Method to get a single employee for a perticular business
     * @param business business ID
     * @param employeeId employee ID
     * @return null if not found otherwise the Employee object.
     */
    public static Employee getEmployee(int business, int employeeId){
        try{
            //Query Database for Employee
            PreparedStatement stmt = Database.getConnection().prepareStatement("" +
                    "SELECT * FROM employee WHERE business = ? AND id = ?;");
            stmt.setInt(1,business);
            stmt.setInt(2, employeeId);
            ResultSet results = stmt.executeQuery();
            if(results.next()){
                Employee e = new Employee(
                        results.getInt("id"),
                        results.getInt("business"),
                        results.getString("name"),
                        results.getString("email"),
                        results.getString("phone"),
                        results.getString("address"),
                        results.getBoolean("deleted")
                );
                if (!e.getDeleted())
                	return e;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Method to same an employee to the database.
     * Note, if e.id is -1, a new employee will be created.
     * Else the employee is overwritten
     * @param e the employee to be saved.
     */
    public static void saveEmployee(Employee e){
        try{
            //Check to see if this is a new employee
            if(e.getId() == -1) {
                PreparedStatement saveEmployee = Database.getConnection().prepareStatement("" +
                        "INSERT INTO employee (" +
                        "name, " +
                        "business, " +
                        "email, " +
                        "phone, " +
                        "address, " +
                        "deleted)" +
                        "VALUES (" +
                        "?, ?, ?, ?, ?, ?)");
                saveEmployee.setString(1,e.getName());
                saveEmployee.setInt(2,e.getBusiness());
                saveEmployee.setString(3,e.getEmail());
                saveEmployee.setString(4,e.getPhone());
                saveEmployee.setString(5,e.getAddress());
                saveEmployee.setBoolean(6,e.getDeleted());
                saveEmployee.executeUpdate();
            }
            else{
                PreparedStatement saveEmployee = Database.getConnection().prepareStatement("" +
                        "REPLACE INTO employee (" +
                        "id, " +
                        "name, " +
                        "business, " +
                        "email, " +
                        "phone, " +
                        "address, " +
                        "deleted )" +
                        "VALUES (" +
                        "?, ?, ?, ?, ?, ?, ?)");
                saveEmployee.setInt(1, e.getId());
                saveEmployee.setString(2,e.getName());
                saveEmployee.setInt(3,e.getBusiness());
                saveEmployee.setString(4,e.getEmail());
                saveEmployee.setString(5,e.getPhone());
                saveEmployee.setString(6,e.getAddress());
                saveEmployee.setBoolean(7,e.getDeleted());
                saveEmployee.executeUpdate();
            }
        }
        catch (Exception ex){
            logger.severe(ex.getMessage());
        }
    }

    /**
     * Method to check if an employee exists
     * @param name the name of the employee
     * @param businessId the business the employee belongs to
     * @return true if found, else false.
     */
    public static boolean checkExists(String name, int businessId) {
        ArrayList<Employee> employees = Employee.getEmployees(businessId);
        boolean found = false;
        for (Employee e: employees) {
            if(e.getName().equals(name)) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    public static void removeEmployee(int employeeId) {
    	// set delete flag to 1
    	try {
			PreparedStatement stmt = Database.getConnection().prepareStatement(
			        "UPDATE employee " +
			        "SET deleted= 1 " + 
			        "WHERE id= ? ;");
			stmt.setInt(1, employeeId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
