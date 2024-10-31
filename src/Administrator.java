import java.sql.*;
public class Administrator extends User{
    private vStr email;
    private vStr passwd;
    Administrator(vStr email, vStr passwd){
        this.email = email;
        this.passwd = passwd;
    }
    public static boolean login(vStr email, vStr passwd) {
        ResultSet result;
        try {
            result = Main.db.query("SELECT * FROM Administrator WHERE Email = " + email);
        }
        catch(SQLException e) {
            System.out.println("Login failed");
            return false;
        }
        String rpasswd = null;
        try {
            rpasswd = result.getString("Password");
        } catch (SQLException e) {
            System.out.println("Login failed");
            return false;
        }
        if(rpasswd.equals(passwd.getValue())) {
            System.out.println("Login successful");
            Main.user = new Administrator(email, passwd);
            return true;
        } else {
            System.out.println("Login failed");
            return false;
        }
    }
    public void insertAdmin(vStr email, vStr passwd) {
        try{
            Main.db.insert("INSERT INTO Administrator VALUES (" + email + ", " + passwd + ")");
        }
        catch(SQLException e){
            System.out.println("Insertion failed");
        }
    }
    public void updateAdmin(vStr getAttr ,vStr attr, Value pos, Value val) throws SQLException {
        Main.db.update("UPDATE Administrator SET " + attr + " = " + val + " WHERE " + getAttr + " = " + pos);
    }
}
