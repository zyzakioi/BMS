package model;

import config.*;
import exceptions.BMSException;
import utils.SecurityUtils;
import view.View;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static utils.InputUtils.getDigit;
import static utils.InputUtils.getPasswd;

public class Attendee implements User {
    private final Menu menu;
    private final char[] email;
    private final char[] passwd;

    public Attendee(String email, char[] passwd) throws BMSException {
        this.email = email.toCharArray();
        this.passwd = passwd;
        this.menu = new AttendeeMainMenu(email);
    }

    public void login() throws BMSException, SQLException {
        Attr ID = AttendeeAttr.EMAIL;
        Attr PW = AttendeeAttr.PASSWORD;
        if (User.auth(ID, PW, new String(email), passwd, false)) menu.start();
        else View.displayError("Email or password incorrect");
    }

    @Override
    public void close() {
        Arrays.fill(email, '\0');
        Arrays.fill(passwd, '\0');
    }
}

class AttendeeMainMenu implements Menu {
    Menu menu1, menu2, menu3;

    AttendeeMainMenu(String email) {
        menu1 = new UpdateInfo(email);
        menu2 = new ShowBanquets(email);
        menu3 = new SignUp(email);
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
    private String email;

    UpdateInfo(String email) {
        this.email = "\"" + email + "\"";
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
                case 1 -> updateId(email);
                case 2 -> updatePw(email);
                case 3, 4, 5, 6, 7, 8 -> {
                    String val = attrs[op].inputNewVal();
                    String condition = AttendeeAttr.EMAIL + " = " + email;
                    attrs[op].updateTo(val, condition);
                }
            }
        }
    }

    private void updateId(String oldId) throws SQLException{
        // Assumes CASCADE UPDATE is on
        String newId = AttendeeAttr.EMAIL.inputUniqueVal();
        String condition = AttendeeAttr.EMAIL.getAttrName() + " = " + oldId;
        AttendeeAttr.EMAIL.updateTo(newId, condition);
        email = "\"" + newId + "\"";
    }

    private void updatePw(String Id) throws SQLException {
        char[] newPw = getPasswd("New password");
        String condition = AttendeeAttr.EMAIL.getAttrName() + " = " + Id;
        String hashPw = SecurityUtils.toHash(newPw);
        AttendeeAttr.PASSWORD.updateTo(hashPw, condition);
    }
}

class SignUp implements Menu {
    private final String email;

    SignUp(String email) {
        this.email = email;
    }

    @Override
    public void start() throws SQLException{
        String banquetID = BanquetAttr.BANQUET_ID.inputHasVal();
        String mealID;
        while (true) {
            mealID = MealAttr.MEAL_ID.inputHasVal();
            String[] columns = new String[]{};
            String[] conditionVals = new String[]{banquetID, "1", "0"};
            String conditionClause = BanquetAttr.BANQUET_ID + " = ? AND " + BanquetAttr.AVAILABILITY + " = ? AND " + BanquetAttr.QUOTA + " > ?";
            try (ResultSet rs = Tables.BANQUET.query(columns, conditionClause, conditionVals)) {
                if (!rs.next()) {
                    View.displayError("Banquet not available");
                    continue;
                }
            }
            conditionVals = new String[]{banquetID, mealID};
            conditionClause = RegistryAttr.BANQUET_ID + " = ? AND " + RegistryAttr.MEAL_ID + " = ?";
            try (ResultSet rs = Tables.REGISTRY.query(columns, conditionClause, conditionVals)) {
                if (rs.next()) break;
                View.displayError("Meal not available in this banquet");
            }
        }
        RegistryAttr[] otherAttrs = new RegistryAttr[]{RegistryAttr.DRINK, RegistryAttr.SEAT, RegistryAttr.REMARKS};
        String[] otherVals = Attr.inputNewVals(otherAttrs);
        String[] vals = new String[]{email, banquetID, mealID, otherVals[0], otherVals[1], "0", otherVals[2]};
        try {
            Tables.REGISTRY.insert(vals);
            BanquetAttr.QUOTA.updateTo("Quota - 1", BanquetAttr.BANQUET_ID.getAttrName() + " = " + banquetID);
            ResultSet rs = Tables.BANQUET.query(new String[]{"Quota"}, BanquetAttr.BANQUET_ID.getAttrName() + " = ?", new String[]{banquetID});
            rs.next();
            if (rs.getInt(1) == 0) BanquetAttr.AVAILABILITY.updateTo("0", BanquetAttr.BANQUET_ID.getAttrName() + " = " + banquetID);
        } catch (BMSException e) {
            View.displayError("Already registered to this banquet");
        }
    }
}

class ShowBanquets implements Menu {
    private final String email;

    ShowBanquets(String email) {
        this.email = email;
    }

    @Override
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
