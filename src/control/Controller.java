package control;

import config.AttendeeAttr;
import config.Attr;
import config.Tables;
import exceptions.BMSException;
import model.Admin;
import model.Attendee;
import utils.InputUtils;
import utils.SecurityUtils;
import view.View;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

import static utils.InputUtils.*;


public class Controller {
    public static final Scanner sc = new Scanner(System.in);
    public static final DBConnect db = new DBConnect();
    public static int banquetNum;
    public void init() throws SQLException {
        String options = """
                1. Attendee
                2. Administrator
                3. New User Registration
                4. Exit
                """;
        boolean isRunning = true;
        ResultSet bN = db.executeQuery("SELECT COUNT(*) FROM Banquet");
        bN.next();
        banquetNum = bN.getInt(1);
        while (isRunning) {
            View.displayOptions("account type", options);
            int op = getDigit("");
            switch (op) {
                case 1 -> initAttendee();
                case 2 -> initAdmin();
                case 3 -> initRegister();
                case 4 -> {
                    View.displayExit();
                    isRunning = false;
                }
                default -> View.displayBadInput("{1, 2, 3, 4}", op);
            }
        }
    }

    private void initAttendee() throws SQLException{
        while (true) {
            String email = getStr("[Attendee] email");
            char[] password = getPasswd("[Attendee] password");
            try (Attendee attendeeSession = new Attendee(email, password)) {
                attendeeSession.login();
                break;
            } catch (BMSException e) {
                View.displayError(e.getMessage());
            }
        }
    }

    private void initAdmin() throws SQLException{
        while (true) {
            String email = getStr("[Admin] email");
            char[] password = getPasswd("[Admin] password");
            try (Admin adminSession = new Admin(email, password)) {
                adminSession.login();
                break;
            } catch (BMSException e) {
                View.displayError(e.getMessage());
            }
        }
    }

    private void initRegister() throws SQLException{
        Attr ID = AttendeeAttr.ATTENDEE_ID;
        String[] finalVals = new String[8];
        finalVals[0] = ID.inputNewVal();
        char[] rawPasswd = getPasswd("password");
        finalVals[1] = SecurityUtils.toHash(rawPasswd);
        Attr[] infos = Arrays.copyOfRange(AttendeeAttr.values(), 2, 8);
        String[] infoVals = Attr.inputNewVals(infos);
        System.arraycopy(infoVals, 0, finalVals, 2, 6);

        try {
            Tables.ATTENDEE.insert(finalVals);
            View.displayMessage("registration successful");
        } catch (BMSException e) {
            View.displayError(e.getMessage());
            return;
        }

        // Directly login
        try (Attendee attendeeSession = new Attendee(finalVals[0], rawPasswd)) {
            attendeeSession.login();
        } catch (BMSException e) {
            View.displayError(e.getMessage());
        }
    }


}
