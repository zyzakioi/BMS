package model;

import config.*;
import exceptions.BMSException;
import utils.SecurityUtils;
import view.View;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static utils.InputUtils.*;

public class Attendee implements User {
    private final Menu menu;
    private final char[] passwd;
    private final int ID;

    public Attendee(String email, char[] passwd) throws BMSException {
        this.passwd = passwd;
        try (ResultSet rs = Tables.ATTENDEE.query(new String[]{AttendeeAttr.ATT_ID.getAttrName()}, AttendeeAttr.EMAIL.getAttrName() + " = ?", new String[]{email})) {
            if (rs.next()) ID = rs.getInt(1);
            else throw new BMSException("Attendee not found");
        } catch (SQLException e) {
            throw new BMSException("Unexpected SQL error occurred");
        }
        this.menu = new AttendeeMainMenu(ID);
    }

    public void login() throws BMSException, SQLException {
        Attr ID = AttendeeAttr.ATT_ID;
        Attr PW = AttendeeAttr.PASSWORD;
        if (User.auth(ID, PW, this.ID, passwd, false)) menu.start();
        else View.displayError("Password incorrect");
    }

    @Override
    public void close() {
        Arrays.fill(passwd, '\0');
    }
}

class AttendeeMainMenu implements Menu {
    static Menu menu1, menu2, menu3;

    AttendeeMainMenu(int ID) {
        menu1 = new UpdateInfo(ID);
        menu2 = new ShowBanquets();
        menu3 = new SignUp(ID);
    }


    @Override
    public void start() throws SQLException{
        String options = """
                1. Update Personal Info
                2. Show Available Banquets
                3. Register a Banquet
                0. Logout
                """;
        boolean isRunning = true;
        while (isRunning) {
            View.displayOptions("Action", options);
            int op = getDigit("");
            switch (op) {
                case 1 -> menu1.start();
                case 2 -> menu2.start();
                case 3 -> menu3.start();
                case 0 -> isRunning = false;
                default -> View.displayBadInput("{1, 2, 3, 0}", op);
            }
        }
        View.displayMessage("Logged out");
    }
}

class UpdateInfo implements Menu {
    private final int ID;

    UpdateInfo(int ID) {
        this.ID = ID;
    }

    @Override
    public void start() throws SQLException{
        String options = """
                1. Update Email
                2. Update Password
                3. Update First Name
                4. Update Last Name
                5. Update Organization
                6. Update Type
                7. Update Address
                8. Update Phone
                0. Go Back
                """;
        AttendeeAttr[] attrs = AttendeeAttr.values();

        while (true) {
            View.displayOptions("Info to update", options);
            int op = getDigit("Action");
            switch (op) {
                case 0 -> { return; }
                case 1 -> updateId(ID);
                case 2 -> updatePw(ID);
                case 3, 4, 5, 6, 7, 8 -> {
                    String val = attrs[op].inputNewVal();
                    String condition = AttendeeAttr.ATT_ID + " = " + ID;
                    attrs[op].updateTo(val, condition);
                }
            }
        }
    }

    private void updateId(int ID) throws SQLException{
        // Assumes CASCADE UPDATE is on
        String newId = AttendeeAttr.EMAIL.inputUniqueVal();
        String condition = AttendeeAttr.ATT_ID.getAttrName() + " = " + ID;
        AttendeeAttr.EMAIL.updateTo(newId, condition);
    }

    private void updatePw(int ID) throws SQLException {
        char[] newPw = getNewPasswd("New password");
        String condition = AttendeeAttr.ATT_ID.getAttrName() + " = " + ID;
        String hashPw = SecurityUtils.toHash(newPw);
        AttendeeAttr.PASSWORD.updateTo(hashPw, condition);
    }
}

class SignUp implements Menu {
    private final int ID;
    SignUp(int ID) {this.ID = ID;}

    @Override
    public void start() throws SQLException{
        String banquetID = BanquetAttr.BIN.inputHasVal();
        try(ResultSet rs = Tables.REGISTRATION.query(new String[]{}, BanquetAttr.BIN.getAttrName() + " = ? AND Att_ID = ?", new String[]{banquetID, Integer.toString(ID)})) {
            if (rs.next()) {
                View.displayError("Already registered to this banquet");
                return;
            }
        }
        String[] columns = new String[]{};
        String[] conditionVals = new String[]{banquetID, "1", "0"};
        String conditionClause = BanquetAttr.BIN + " = ? AND " + BanquetAttr.AVAILABILITY + " = ? AND " + BanquetAttr.QUOTA + " > ?";
        try (ResultSet rs = Tables.BANQUET.query(columns, conditionClause, conditionVals)) {
            if (!rs.next()) {
                View.displayError("Banquet not available");
                return;
            }
        }
        try(ResultSet rs = Tables.MEAL.query(new String[]{MealAttr.DISH_NAME.getAttrName()}, MealAttr.BIN.getAttrName() + " = ?", new String[]{banquetID})) {
            View.displayMessage("Available meals:");
            while(rs.next()){
                View.displayMessage(rs.getString(1));
            }
        }
        String mealID = MealAttr.DISH_NAME.inputHasVal();
        conditionVals = new String[]{banquetID, mealID};
        conditionClause = RegistrationAttr.BIN + " = ? AND " + RegistrationAttr.DISH_NAME + " = ?";
        try (ResultSet rs = Tables.MEAL.query(columns, conditionClause, conditionVals)) {
            if (!rs.next()) {
                View.displayError("Meal not available in this banquet");
                return;
            }
        }
        RegistrationAttr[] otherAttrs = new RegistrationAttr[]{RegistrationAttr.DRINK, RegistrationAttr.SEAT, RegistrationAttr.REMARKS};
        String[] otherVals = Attr.inputNewVals(otherAttrs);
        String[] vals = new String[]{Integer.toString(ID), banquetID, mealID, otherVals[0], otherVals[1], "0", otherVals[2]};
        try {
            Tables.REGISTRATION.insert(vals);
            ResultSet rs = Tables.BANQUET.query(new String[]{"Quota"}, BanquetAttr.BIN.getAttrName() + " = ?", new String[]{banquetID});
            rs.next();
            int quota = rs.getInt(1);
            BanquetAttr.QUOTA.updateTo(quota - 1 + "", BanquetAttr.BIN.getAttrName() + " = " + banquetID);
            rs = Tables.BANQUET.query(new String[]{"Quota"}, BanquetAttr.BIN.getAttrName() + " = ?", new String[]{banquetID});
            rs.next();
            if (rs.getInt(1) == 0) BanquetAttr.AVAILABILITY.updateTo("0", BanquetAttr.BIN.getAttrName() + " = " + banquetID);
            rs.close();
        } catch (BMSException e) {
            View.displayError("Already registered to this banquet");
        }
    }
}

class ShowBanquets implements Menu {
    public void start() throws SQLException {
        String[] header = {"BIN","Name","Date","Time"};
        ArrayList<String[]> rows = new ArrayList<>();
        int colNum = header.length;

        String[] columns = new String[]{};
        String conditionClause = BanquetAttr.AVAILABILITY.getAttrName() + " = ? AND Quota > ?";
        String[] conditionVals = new String[]{"1","0"};

        try(ResultSet rs = Tables.BANQUET.query(columns, conditionClause, conditionVals)) {
            while (rs.next()) {
                String[] row = new String[colNum];
                for (int i = 0; i < colNum; i++)
                    row[i] = rs.getString(i + 1);
                rows.add(row);
            }
            if (rows.isEmpty())
                View.displayMessage("No banquets available");
            else View.displayTable(header, rows);
        }

    }
}
