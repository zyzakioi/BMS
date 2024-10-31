import java.sql.*;
public class Registration {
    private vStr attendeeEmail;
    private vInt BIN;
    private vStr meal;
    private vStr drink;
    private vStr remarks;
    Registration(vStr attendeeEmail, vInt BIN, vStr meal, vStr drink, vStr remarks) {
        this.attendeeEmail = attendeeEmail;
        this.BIN = BIN;
        this.meal = meal;
        this.drink = drink;
        this.remarks = remarks;
    }
    public static void insert(vStr attendeeEmail, vInt BIN, vStr meal, vStr drink, vStr remarks) throws SQLException {
        ResultSet result = Main.db.query("SELECT Quota FROM Banquet WHERE BIN = " + BIN);
        int quota = result.getInt("Quota");
        if(quota == 0) {
            System.out.println("Banquet is full");
            return;
        }
        Main.db.update("UPDATE Banquet SET Quota = " + (quota - 1) + " WHERE BIN = " + BIN);
        Main.db.insert("INSERT INTO Registration VALUES (" + attendeeEmail + ", " + BIN + ", " + meal + ", " + drink + ", " + remarks + ")");
    }
    public static void update(vStr getAttr ,vStr attr, Value pos, Value val) throws SQLException {
        Main.db.update("UPDATE Registration SET " + attr + " = " + val + " WHERE " + getAttr + " = " + pos);
    }
    public static String query(vStr getAttr, vStr attr, Value val) throws SQLException {
        ResultSet result =  Main.db.query("SELECT" + getAttr + "FROM Registration WHERE " + attr + " = " + val);
        String ret = "";
        while(result.next()){
            ret += result.getString(getAttr.getValue()) + "\n";
        }
        return ret;
    }
    public static void delete(vStr email, vInt BIN) throws SQLException {
        Main.db.delete("DELETE FROM Registration WHERE " + email + " = AttendeeEmail AND " + BIN + " = BIN");
        Main.db.update("UPDATE Banquet SET Quota = Quota + 1 WHERE BIN = " + BIN + " AND AttendeeEmail = " + email);
    }
}
