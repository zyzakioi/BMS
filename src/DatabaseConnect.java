import oracle.jdbc.driver.*;
import java.sql.*;
public class DatabaseConnect {
    private static final DatabaseConnect database;
    static {
        try {
            database = new DatabaseConnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private DatabaseConnect() throws SQLException{
        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static OracleConnection connection;
    private static String username;
    private static String passwd;
    private static final String url = "jdbc:oracle:thin:@40.79.43.65:1521:dbms";

    public static DatabaseConnect getDatabase(){ return database; }

    private void connect() throws SQLException {
        try {
            System.out.println("Enter username and password:");
            username = Main.sc.nextLine();
            passwd = Main.sc.nextLine();
            DriverManager.registerDriver(new OracleDriver());
            connection = (OracleConnection) DriverManager.getConnection(url, username, passwd);
            System.out.println("Connection success");
        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
            throw e;
        }
    }

    public void close() throws SQLException {
        try {
            connection.close();
            System.out.println("Connection closed");
        }
        catch (SQLException e) {
            System.out.println("Connection failed to close");
            e.printStackTrace();
            throw e;
        }
    }

    public void insert(String s) throws SQLException {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(s);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Insert failed");
            e.printStackTrace();
            throw e;
        }
    }

    public void update(String s) throws SQLException {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(s);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Update failed");
            e.printStackTrace();
            throw e;
        }
    }

    public void delete(String s) throws SQLException {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(s);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Delete failed");
            e.printStackTrace();
            throw e;
        }
    }

    public ResultSet query(String s) throws SQLException{
        try{
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(s);
            stmt.close();
            return rs;
        } catch (SQLException e) {
            System.out.println("Query failed");
            e.printStackTrace();
            throw e;
        }
    }
}
