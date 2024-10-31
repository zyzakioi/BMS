import java.sql.*;
public class Attendee extends User {
    private vStr email;
    private vStr passwd;
    private vStr firstName;
    private vStr lastName;
    private vStr type;
    private vInt phone;
    private vStr address;
    private vStr organization;
    Attendee(vStr email, vStr passwd, vStr firstName, vStr lastName, vStr type, vInt phone, vStr address, vStr organization) {
        this.email = email;
        this.passwd = passwd;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.phone = phone;
        this.address = address;
        this.organization = organization;
    }
    public String getEmail() { return email.getValue(); }
    public static boolean login(vStr email, vStr passwd) {
        ResultSet result;
        try{
            result = Main.db.query("SELECT * FROM Attendee WHERE Email = " + email);
        }
        catch(SQLException e){
            System.out.println("Login failed");
            return false;
        }
        String rpasswd = null;
        Attendee tmp = null;
        try {
            rpasswd = result.getString("Password");
            tmp = new Attendee(email, passwd, new vStr(result.getString("FirstName")), new vStr(result.getString("LastName")),
                    new vStr(result.getString("Type")), new vInt(result.getInt("Phone")), new vStr(result.getString("Address")), new vStr(result.getString("Organization")));
        } catch (SQLException e) {
            System.out.println("Login failed");
            return false;
        }
        if(rpasswd.equals(passwd.getValue())) {
            System.out.println("Login successful");
            Main.user = tmp;
            return true;
        } else {
            System.out.println("Login failed");
            return false;
        }
    }
    public static void insert(vStr email, vStr passwd, vStr firstName, vStr lastName, vStr type, vInt phone, vStr address, vStr organization) throws SQLException {
        Main.db.insert("INSERT INTO Attendee VALUES (" + email + ", " + passwd + ", " + firstName + ", " + lastName + ", " + type + ", " + phone + ", " + address + ", " + organization + ")");
    }
    public static void update(vStr getAttr ,vStr attr, Value pos, Value val) throws SQLException {
        Main.db.update("UPDATE Attendee SET " + attr + " = " + val + " WHERE " + getAttr + " = " + pos);
    }
    public static String query(vStr getAttr, vStr attr, Value val) throws SQLException {
        ResultSet result =  Main.db.query("SELECT" + getAttr + "FROM Attendee WHERE " + attr + " = " + val);
        String ret = "";
        while(result.next()){
            ret += result.getString(getAttr.getValue()) + "\n";
        }
        return ret;
    }
    public static String query(vStr email) throws SQLException{
        ResultSet result = Main.db.query("SELECT * FROM Attendee WHERE Email = " + email);
        String ret = "";
        while(result.next()){
            ret += result.getString("Email") + "\n";
            ret += result.getString("FirstName") + "\n";
            ret += result.getString("LastName") + "\n";
            ret += result.getString("Type") + "\n";
            ret += result.getString("Phone") + "\n";
            ret += result.getString("Address") + "\n";
            ret += result.getString("Organization") + "\n";
        }
        return ret;
    }
}
