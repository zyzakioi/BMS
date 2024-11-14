package model;

import config.*;
import exceptions.BMSException;
import view.View;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static control.Controller.db;
import static control.Controller.sc;
import static utils.InputUtils.*;

public class Admin implements User {
    private static final Menu menu = new AdminMainMenu();

    transient private final char[] email;
    transient private final char[] passwd;

    public Admin(String email, char[] passwd) throws BMSException {
        this.email = email.toCharArray();
        this.passwd = passwd;
    }

    @Override
    public void login() throws BMSException, SQLException {
        Attr ID = AdminAttr.ADMIN_ID;
        Attr PW = AdminAttr.PASSWORD;
        if (User.auth(ID, PW, new String(email), passwd)) menu.start();
        else View.displayError("email or password incorrect");
    }

    /**
     * Securely close the admin object
     * Erase the email and password from memory
     */
    @Override
    public void close() {
        Arrays.fill(email, '\0');
        Arrays.fill(passwd, '\0');
    }
}

class AdminMainMenu implements Menu {
    private static final Menu menu1 = new AdminViewBanquet();
    private static final Menu menu2 = new AdminNewBanquet();
    private static final Menu menu3 = new AdminEditBanquet();
    private static final Menu menu4 = new AdminMenuAttendee();

    @Override
    public void start() throws SQLException {
        String options = """
                1. View All Banquets
                2. Create New Banquet
                3. Manage Existing Banquets
                4. Manage Attendee Registration
                0. Logout
                """;
        while (true) {
            View.displayOptions("action", options);
            int op = getDigit("action");
            switch (op) {
                case 1 -> menu1.start();
                case 2 -> menu2.start();
                case 3 -> menu3.start();
                case 4 -> menu4.start();
                case 0 -> { return; }
                default -> View.displayBadInput("single digit 1~6", op);
            }
        }
    }
}

class AdminEditBanquet implements Menu {
    @Override
    public void start() throws SQLException{
        String banquetID = BanquetAttr.BANQUET_ID.inputHasVal();

        String options = """
                1. Update Banquet
                2. Update Meal
                3. Take Attendance
                0. Go back
                """;

        boolean isRunning = true;
        while (isRunning) {
            View.displayOptions("action", options);
            int op = getDigit("action");
            try {
                switch (op) {
                    case 1 -> updateBanquet(banquetID);
                    case 2 -> updateMeal(banquetID);
                    case 3 -> takeAttendance(banquetID);
                    case 0 -> isRunning = false;
                }
            } catch (BMSException e) {
                View.displayError(e.getMessage());
            }
        }
    }

    private static void updateBanquet(String banquetID) throws SQLException{
        String options = """
                1. Update Name
                2. Update Date
                3. Update Time
                4. Update Location
                5. Update Address
                6. Update Contact First Name
                7. Update Contact Last Name
                8. Update Quota
                9. Update Availability
                0. Go Back
                """;

        BanquetAttr[] attrs = BanquetAttr.values();
        // attrs[0] is BANQUET_ID, which is not updatable. Safely ignored in the following code.

        while (true) {
            View.displayOptions("action", options);
            int op = getDigit("action");
            if (op == 0) return;
            if (1 <= op && op <= 9) {
                String val = attrs[op].inputNewVal();
                String condition = BanquetAttr.BANQUET_ID + " = " + banquetID;
                attrs[op].updateTo(val, condition);
                break;
            }
        }
    }

    private static void updateMeal(String banquetID) throws SQLException, BMSException{
        String mealName = MealAttr.MEAL_ID.inputHasVal();
        MealAttr attr = (MealAttr) Attr.inputValidAttr(sc, MealAttr.values());

        String val = attr.inputNewVal();
        String condition = String.format(
                "%s = %s AND %s = %s",
                MealAttr.BANQUET_ID, banquetID,
                MealAttr.MEAL_ID, mealName
        );
        MealAttr.MEAL_ID.updateTo(val, condition);
    }

    private static void takeAttendance(String banquetID) throws SQLException, BMSException{
        String email = RegistryAttr.ATTENDEE_ID.inputHasVal();
        String condition = String.format(
                "%s = %s AND %s = %s",
                RegistryAttr.BANQUET_ID, banquetID,
                RegistryAttr.ATTENDEE_ID, email
        );
        RegistryAttr.ATTENDANCE.updateTo("1", condition);
    }
}

class AdminMenuAttendee implements Menu {
    @Override
    public void start () throws SQLException{
        String banquetID = RegistryAttr.BANQUET_ID.inputHasVal();

        String options = """
                1. Update Register Attendee
                2. Unregister Attendee
                3. View Attendees
                0. Go Back
                """;

        while (true) {
            View.displayOptions("action", options);
            int op = getDigit("action");
            try {
                switch (op) {
                    case 0 -> { return;}
                    case 1 -> updateAttendee(banquetID);
                    case 2 -> unregisterAttendee(banquetID);
                    case 3 -> viewAttendees();
                    default -> View.displayBadInput("single digit 1~3", op);
                }
            } catch (BMSException e) {
                View.displayError(e.getMessage());
            }
        }
    }

    private static void updateAttendee(String banquetID) throws SQLException, BMSException {
        String email = RegistryAttr.ATTENDEE_ID.inputHasVal();
        RegistryAttr attr = (RegistryAttr) Attr.inputValidAttr(sc, RegistryAttr.values());
        String newVal = attr.inputNewVal();
        String condition = String.format(
                "%s = %s AND %s = %s",
                RegistryAttr.BANQUET_ID, banquetID,
                RegistryAttr.ATTENDEE_ID, email
        );
        attr.updateTo(newVal, condition);
    }

    private static void unregisterAttendee(String banquetID) throws SQLException{
        String attendeeID = RegistryAttr.ATTENDEE_ID.inputHasVal();
        String conditions = String.format(
                "%s = %s AND %s = %s",
                RegistryAttr.BANQUET_ID, banquetID,
                RegistryAttr.ATTENDEE_ID, attendeeID
        );
        Tables.REGISTRY.delete(conditions);
        View.displayMessage("Attendee ID \"" + attendeeID + "\" has been unregistered");
    }

    private static void viewAttendees() throws SQLException{
        String[] columns = Attr.getColumns(AttendeeAttr.values());
        ArrayList<String[]> rows = new ArrayList<>();
        int colNum = columns.length;

        try(ResultSet rs = Tables.REGISTRY.query(columns, "", new String[]{})) {
            String[] row = new String[colNum];
            while (rs.next()) {
                for (int i = 0; i < colNum; i++)
                    row[i] = rs.getString(columns[i]);
                rows.add(row);
            }
        }

        if (rows.isEmpty()) View.displayMessage("no attendee available");
        else View.displayTable(columns, rows);
    }
}

class AdminNewBanquet implements Menu {
    @Override
    public void start() throws SQLException{
        try {
            String[] vals = Attr.inputNewVals(BanquetAttr.values());
            Tables.BANQUET.insert(vals);
            String[][] mealSet = MealAttr.getValidMealSet(vals[0]);
            for (String[] meal : mealSet) Tables.MEAL.insert(meal);
        } catch (BMSException e) {
            // should not exist since BIN is auto-generated and guaranteed to be unique
            // If it does, it is a bug
            throw new RuntimeException(e);
        }
    }
}

class AdminViewBanquet implements Menu {
    public void start() throws SQLException {
        // Relationship Mapping of Banquet:
        // Banquet (Bin, Name, Address, Location, Availability, ContactLastName, ContactFirstName, Quota, Time)

        String[] header = Attr.getColumns(BanquetAttr.values());
        ArrayList<String[]> rows = new ArrayList<>();
        int colNum = header.length;

        String[] columns = new String[]{}; // all columns
        String conditionClause = "";
        String[] conditionVals = new String[]{};

        try (ResultSet rs = Tables.BANQUET.query(columns, conditionClause, conditionVals)) {
            while (rs.next()) {
                String[] row = new String[colNum];
                for (int i = 0; i < colNum; i++)
                    row[i] = rs.getString(i + 1);
                rows.add(row);
            }
            if (rows.isEmpty())
                View.displayMessage("no banquets");
            else View.displayTable(header, rows);
        }
    }
}