package control;

import java.sql.*;

import oracle.jdbc.driver.*;
import view.View;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.util.Scanner;

public class DBConnect {
    private final static Scanner sc = new Scanner(System.in);
    //private final static String url = "jdbc:oracle:thin:@40.79.43.65:1521:dbms";
    // private final static String username = "\"23110134d\"";
    // private final static String passwd = "mhhgtkic";
    private final static String url = "jdbc:oracle:thin:@localhost:1521:XE";
    private final static String username = "system";
    private final static String passwd = "oracledb";
    private final static OracleConnection connection;

    static {
        try {
            DriverManager.registerDriver(new OracleDriver());
            // String username = getStr("[Oracle Database] username (requires double quote)");
            // String passwd = getStr("[Oracle Database] password");
            connection = (OracleConnection) DriverManager.getConnection(url, username, passwd);
            System.out.println("Connected.");
        } catch (SQLException e) {
            View.displayError("database connection failed to establish");
            throw new RuntimeException(e);
        }
    }

    public void close() throws SQLException {
        try {
            connection.close();
            System.out.println("Connection closed");
        } catch (SQLException e) {
            View.displayError("database connection failed to close");
            throw e;
        }
    }

    /**
     * for UPDATE, DELETE, INSERT
     */
    public void executeUpdate(String sql, String[] params) throws SQLException{
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.execute();
        } catch (SQLException e) {
            View.displayError("failed to execute: " + sql);
            throw e;
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException{
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            for (int i = 0; i < params.length; i++)
                pstmt.setObject(i + 1, params[i]);

            // copies ResultSet to memory for offline manipulation
            // solves the issue where ResultSet is closed when statement is closed by try-with-resources
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(pstmt.executeQuery());
            return crs;
        } catch (SQLException e) {
            View.displayError("failed to execute: " + sql);
            throw e;
        }
    }
}
