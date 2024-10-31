import java.sql.*;
public class Meal {
    private vInt BIN;
    private vStr name;
    private vStr type;
    private vInt price;
    private vStr cuisine;
    Meal(vInt BIN, vStr name, vStr type, vInt price, vStr cuisine) {
        this.BIN = BIN;
        this.name = name;
        this.type = type;
        this.price = price;
        this.cuisine = cuisine;
    }
    public static void insert(vInt BIN, vStr name, vStr type, vInt price, vStr cuisine) throws SQLException {
        Main.db.insert("INSERT INTO Meal VALUES (" + BIN + ", " + name + ", " + type + ", " + price + ", " + cuisine + ")");
    }
    public void insert() throws SQLException{
        Main.db.insert("INSERT INTO Meal VALUES (" + BIN + ", " + name + ", " + type + ", " + price + ", " + cuisine + ")");
    }
    public static void update(vStr getAttr ,vStr attr, Value pos, Value val) throws SQLException {
        Main.db.update("UPDATE Meal SET " + attr + " = " + val + " WHERE " + getAttr + " = " + pos);
    }
    public static void update(vInt BIN, vStr name, vStr attr, Value val) throws SQLException {
        Main.db.update("UPDATE Banquet SET " + attr + " = " + val + " WHERE BIN = " + BIN + " AND name = " + name);
    }
    public String query(vStr getAttr, vStr attr, Value val) throws SQLException {
        ResultSet result =  Main.db.query("SELECT" + getAttr + "FROM Meal WHERE " + attr + " = " + val);
        String ret = "";
        while(result.next()){
            ret += result.getString(getAttr.getValue()) + "\n";
        }
        return ret;
    }
    public static boolean check(Meal[] meal) {
        for (int i = 0; i < meal.length; i++) {
            for (int j = i + 1; j < meal.length; j++) {
                if (meal[i].name.getValue().equals(meal[j].name.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }
}
