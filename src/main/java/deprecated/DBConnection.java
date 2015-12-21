/*
 * 
 * 
 */
package deprecated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author kataev
 */
public class DBConnection {
    // JDBC URL, username and password of MySQL server
    private String dbUrl;
    private String user;
    private String password;
    
    public DBConnection() {
        this.dbUrl = "jdbc:mysql://localhost:3306/poligon";
        this.user = "root";
        this.password = "poligon";
    }
    
    public DBConnection(String dbUrl, String user, String password) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
    }
        
    public String getDBUrl() {
        return dbUrl;
    }
    
    public void setDBUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
        
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public ResultSet getResult(String query) throws SQLException {
        
        return ((DriverManager.getConnection(dbUrl, user, password)).createStatement()).executeQuery(query);

    }
    public int getUpdateResult(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(dbUrl, user, password);
        PreparedStatement pst =  connection.prepareStatement(query);
        int numRowsChanged = pst.executeUpdate(query);
        pst.close();
        connection.close();
        return numRowsChanged;

    }
}
