package model;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import config.*;
import control.Controller;
import exceptions.BMSException;
import utils.SecurityUtils;
import view.View;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static control.Controller.db;
import static control.Controller.sc;
import static utils.InputUtils.*;

public class Admin implements User {
    private final Menu menu;
    transient private final char[] passwd;
    private final int ID;

    public Admin(String email, char[] passwd) throws BMSException {
        this.passwd = passwd;
        try(ResultSet rs = Tables.ADMIN.query(new String[]{AdminAttr.ADMIN_ID.getAttrName()}, AdminAttr.EMAIL + " = ?", new String[]{email})) {
            if (!rs.next()) throw new BMSException("Admin not found");
            ID = rs.getInt(1);
        } catch (SQLException e) {
            throw new BMSException(e.getMessage());
        }
        menu = new AdminMainMenu(ID);
    }

    @Override
    public void login() throws BMSException, SQLException {
        Attr ID = AdminAttr.ADMIN_ID;
        Attr PW = AdminAttr.PASSWORD;
        if (User.auth(ID, PW, this.ID, passwd, true)) menu.start();
        else View.displayError("Email or password incorrect");
    }

    /**
     * Securely close the admin object
     * Erase the email and password from memory
     */
    @Override
    public void close() {
        Arrays.fill(passwd, '\0');
    }
}

class AdminMainMenu implements Menu {
    private static final Menu menu1 = new AdminViewBanquet();
    private static final Menu menu2 = new AdminNewBanquet();
    private static final Menu menu3 = new AdminEditBanquet();
    private static final Menu menu4 = new AdminMenuAttendee();
    static Menu menu5;
    static Menu menu6;
    private static final Menu menu7 = new NewAdmin();
    private static final Menu menu8 = new genReport();

    AdminMainMenu(int ID) {
        menu5 = new UpdateEmail(ID);
        menu6 = new UpdatePassword(ID);
    }

    @Override
    public void start() throws SQLException {
        String options = """
                1. View All Banquets
                2. Create New Banquet
                3. Manage Banquets
                4. Manage Attendee Registration
                5. Change Email
                6. Change Password
                7. Add New Admin
                8. Generate Report
                0. Logout
                """;
        while (true) {
            View.displayOptions("Action", options);
            int op = getDigit("Action");
            switch (op) {
                case 1 -> menu1.start();
                case 2 -> menu2.start();
                case 3 -> menu3.start();
                case 4 -> menu4.start();
                case 5 -> menu5.start();
                case 6 -> menu6.start();
                case 7 -> menu7.start();
                case 8 -> menu8.start();
                case 0 -> { return; }
                default -> View.displayBadInput("Single digit 1~6", op);
            }
        }
    }
}

class AdminEditBanquet implements Menu {
    @Override
    public void start() throws SQLException{
        String banquetID = BanquetAttr.BIN.inputHasVal();

        String options = """
                1. Update Banquet
                2. Update Meal
                3. Take Attendance
                0. Go back
                """;

        boolean isRunning = true;
        while (isRunning) {
            View.displayOptions("Action", options);
            int op = getDigit("Action");
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
                8. Update Availability
                9. Update Quota
                0. Go Back
                """;

        BanquetAttr[] attrs = BanquetAttr.values();
        // attrs[0] is BANQUET_ID, which is not updatable. Safely ignored in the following code.

        while (true) {
            View.displayOptions("Action", options);
            int op = getDigit("Action");
            if (op == 0) return;
            if (1 <= op && op <= 9) {
                String val = attrs[op].inputNewVal();
                String condition = BanquetAttr.BIN + " = " + banquetID;
                attrs[op].updateTo(val, condition);
                break;
            }
        }
    }

    private static void updateMeal(String banquetID) throws SQLException, BMSException{
        String mealName = MealAttr.DISH_NAME.inputHasVal();
        MealAttr attr = (MealAttr) Attr.inputValidAttr(sc, MealAttr.values());

        String val = attr.inputNewVal();
        String condition = String.format(
                "%s = %s AND %s = %s",
                MealAttr.BIN, banquetID,
                MealAttr.DISH_NAME, mealName
        );
        MealAttr.DISH_NAME.updateTo(val, condition);
    }

    private static void takeAttendance(String banquetID) throws SQLException, BMSException{
        String email = AttendeeAttr.EMAIL.inputHasVal();
        int ID = AttendeeAttr.getID(email);
        String condition = String.format(
                "%s = %s AND %s = %d",
                RegistrationAttr.BIN, banquetID,
                RegistrationAttr.ATT_ID, ID
        );
        RegistrationAttr.ATTENDANCE.updateTo("1", condition);
    }
}

class AdminMenuAttendee implements Menu {
    @Override
    public void start () throws SQLException{
        String banquetID = RegistrationAttr.BIN.inputHasVal();

        String options = """
                1. Update Register Attendee
                2. Unregister Attendee
                3. View Attendees
                0. Go Back
                """;

        while (true) {
            View.displayOptions("Action", options);
            int op = getDigit("Action");
            try {
                switch (op) {
                    case 0 -> { return;}
                    case 1 -> updateAttendee(banquetID);
                    case 2 -> unregisterAttendee(banquetID);
                    case 3 -> viewAttendees(banquetID);
                    default -> View.displayBadInput("Single digit 1~3", op);
                }
            } catch (BMSException e) {
                View.displayError(e.getMessage());
            }
        }
    }

    private static void updateAttendee(String banquetID) throws SQLException, BMSException {
        String email = AttendeeAttr.EMAIL.inputHasVal();
        int ID = AttendeeAttr.getID(email);
        RegistrationAttr attr = (RegistrationAttr) Attr.inputValidAttr(sc, RegistrationAttr.values());
        String newVal = attr.inputNewVal();
        String condition = String.format(
                "%s = %s AND %s = %d",
                RegistrationAttr.BIN, banquetID,
                RegistrationAttr.ATT_ID, ID
        );
        attr.updateTo(newVal, condition);
    }

    private static void unregisterAttendee(String banquetID) throws SQLException{
        String email = AttendeeAttr.EMAIL.inputHasVal();
        int ID = AttendeeAttr.getID(email);
        String conditions = String.format(
                "%s = %s AND %s = %d",
                RegistrationAttr.BIN, banquetID,
                RegistrationAttr.ATT_ID, ID
        );
        ResultSet rs = db.executeQuery("SELECT COUNT(*) FROM Registration WHERE " + conditions);
        rs.next();
        if (rs.getInt(1) == 0) {
            View.displayError("Attendee ID \"" + ID + "\" is not registered");
            return;
        }
        Tables.REGISTRATION.delete(conditions);
        View.displayMessage("Attendee ID \"" + ID + "\" has been unregistered");
        rs = Tables.BANQUET.query(new String[]{BanquetAttr.QUOTA.getAttrName()}, BanquetAttr.BIN.getAttrName() + " = ?", new String[]{banquetID});
        rs.next();
        int quota = rs.getInt(1);
        BanquetAttr.QUOTA.updateTo(String.valueOf(quota + 1), BanquetAttr.BIN + " = " + banquetID);
        if(quota == 0) BanquetAttr.AVAILABILITY.updateTo("1", BanquetAttr.BIN + " = " + banquetID);
        rs.close();
    }

    private static void viewAttendees(String BIN) throws SQLException{
        String[] columns = Attr.getColumns(RegistrationAttr.values()), descriptions = Attr.getDescriptions(RegistrationAttr.values());
        ArrayList<String[]> rows = new ArrayList<>();
        int colNum = columns.length;

        try(ResultSet rs = Tables.REGISTRATION.query(columns, "BIN = ?", new String[]{BIN})) {
            String[] row = new String[colNum];
            while (rs.next()) {
                try(ResultSet trs = Tables.ATTENDEE.query(new String[]{AttendeeAttr.EMAIL.getAttrName()}, AttendeeAttr.ATT_ID + " = ?", new String[]{rs.getString(2)})) {
                    trs.next();
                    row[0] = trs.getString(1);
                }
                for (int i = 1; i < colNum; i++) {
                    row[i] = rs.getString(columns[i]);
                }
                rows.add(row);
            }
        }

        if (rows.isEmpty()) View.displayMessage("No attendee available");
        else View.displayTable(descriptions, rows);
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

        String[] header = {"BIN","Name","Date","Time"};
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
                View.displayMessage("No banquets");
            else View.displayTable(header, rows);
        }
    }
}

class UpdateEmail implements Menu {
    private static int ID;
    UpdateEmail(int ID) {
        UpdateEmail.ID = ID;
    }
    public void start() throws SQLException {
        String newEmail = AdminAttr.EMAIL.inputUniqueVal();
        AdminAttr.EMAIL.updateTo(newEmail, AdminAttr.ADMIN_ID.getAttrName() + " = " + ID);
    }
}

class UpdatePassword implements Menu {
    private static int ID;
    UpdatePassword(int ID) {
        UpdatePassword.ID = ID;
    }
    public void start() throws SQLException {
        char[] newPw = getNewPasswd("New Password");
        String condition = AdminAttr.ADMIN_ID + " = " + ID;
        String hashPw = SecurityUtils.toHash(newPw);
        AdminAttr.PASSWORD.updateTo(hashPw, condition);
    }
}

class NewAdmin implements Menu {
    public void start() throws SQLException {
        String email = AdminAttr.EMAIL.inputUniqueVal();
        char[] rawPw = getNewPasswd("Password");
        String hashPw = SecurityUtils.toHash(rawPw);
        try {
            Tables.ADMIN.insert(Integer.toString(++Controller.adminNum),email, hashPw);
            View.displayMessage("Admin Added");
        } catch (BMSException e) {
            View.displayError(e.getMessage());
        }
    }
}

class genReport implements Menu {
    public void start() throws SQLException {
        String file_name = getStr("Input the name of the report file: ");
        if(!file_name.endsWith(".pdf")) file_name += ".pdf";
        try{
            PdfDocument pdf = new PdfDocument(new PdfWriter(file_name));
            Document document = new Document(pdf);
            document.add(new com.itextpdf.layout.element.Paragraph("Banquet Management System Report"));
            document.add(new com.itextpdf.layout.element.Paragraph("Banquet Information"));
            document.add(new com.itextpdf.layout.element.Paragraph("Attendee Information"));
            document.close();
        } catch (Exception e) {
            View.displayError(e.getMessage());
        }
    }
}