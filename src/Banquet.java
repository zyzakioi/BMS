import java.sql.ResultSet;
import java.sql.SQLException;

public class Banquet {
    private static int count;
    private int BIN;
    private vStr name;
    private vStr date;
    private vStr time;
    private vStr location;
    private vStr address;
    private vStr contactFirstName;
    private vStr contactLastName;
    private boolean available;
    private vInt quota;
    Banquet(vStr name, vStr date, vStr time, vStr location, vStr address, vStr contactFirstName, vStr contactLastName, boolean available, vInt quota, Meal[] meals) {
        BIN = ++count;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.address = address;
        this.contactFirstName = contactFirstName;
        this.contactLastName = contactLastName;
        this.available = available;
        this.quota = quota;
    }
    public static void insert(vStr name, vStr date, vStr time, vStr location, vStr address, vStr contactFirstName, vStr contactLastName, boolean available, vInt quota) throws SQLException {
        Main.db.insert("INSERT INTO Banquet VALUES (" + ++count + ", " + name + ", " + date + ", " + time + ", " + location + ", " + address + ", " + contactFirstName + ", " + contactLastName + ", " + available + ", " + quota + ")");
    }
    public static void update(vStr getAttr ,vStr attr, Value pos, Value val) throws SQLException {
        Main.db.update("UPDATE Banquet SET " + attr + " = " + val + " WHERE " + getAttr + " = " + pos);
    }
    public void update(vStr attr, Value val) throws SQLException {
        Main.db.update("UPDATE Banquet SET " + attr + " = " + val + " WHERE BIN = " + BIN);
    }
    public static String query(vStr getAttr, vStr attr, Value val) throws SQLException {
        ResultSet result =  Main.db.query("SELECT" + getAttr + "FROM Banquet WHERE " + attr + " = " + val);
        String ret = "";
        while(result.next()){
            ret += result.getString(getAttr.getValue()) + "\n";
        }
        return ret;
    }
    public static String query(vInt BIN) throws SQLException{
        ResultSet result = Main.db.query("SELECT * FROM Banquet WHERE BIN = " + BIN);
        String ret = "";
        while(result.next()){
            ret += result.getString("Name") + "\n";
            ret += result.getString("Date") + "\n";
            ret += result.getString("Time") + "\n";
            ret += result.getString("Location") + "\n";
            ret += result.getString("Address") + "\n";
            ret += result.getString("ContactFirstName") + "\n";
            ret += result.getString("ContactLastName") + "\n";
            ret += result.getString("Available") + "\n";
            ret += result.getString("Quota") + "\n";
        }
        return ret;
    }
}
