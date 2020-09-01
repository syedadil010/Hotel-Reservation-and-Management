package OnlineBookingSystem.ModelClasses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;


public class Setup {

    private static Logger logger = Logger.getLogger("Setup Model");
    public static int getAccountStatus() {
        int result = 0;
        try{
            PreparedStatement getAccount = Database.getConnection().prepareStatement("SELECT * FROM setup WHERE area = 'account';");
            ResultSet r = getAccount.executeQuery();
            r.next();
            result = r.getInt(2);
        }catch (SQLException e){
            logger.severe(e.getMessage());
        }
        return result;
    }
    public static void setAccountStatus(int newValue) {
        try{
            PreparedStatement setAccount = Database.getConnection().prepareStatement("UPDATE setup SET value=1 WHERE area = 'account';");
            setAccount.executeUpdate();
        }catch (SQLException e){
            logger.severe("Error updating account value");
        }
    }
}
