import java.sql.*;
import java.util.*;
public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static DatabaseConnect db = DatabaseConnect.getDatabase();
    public static boolean isAttendee = false;
    public static User user;


    //User login
    public static void login() {
        System.out.println("Select your account type:");
        System.out.println("1. Attendee");
        System.out.println("2. Administrator");
        System.out.println("3. New User Registration");
        System.out.println("4. Exit");
        String email;
        String password;
        int choice = sc.nextInt();
        switch (choice){
            case 1:
                isAttendee = true;
                System.out.println("Enter your email:");
                email = sc.next();
                System.out.println("Enter your password:");
                password = sc.next();
                if(Attendee.login(new vStr(email), new vStr(password)))
                    attendeeFunction.attendeeMenu();
                else
                    login();
                break;
            case 2:
                isAttendee = false;
                System.out.println("Enter your email:");
                email = sc.next();
                System.out.println("Enter your password:");
                password = sc.next();
                if(Administrator.login(new vStr(email), new vStr(password)))
                    adminFunction.adminMenu();
                else
                    login();
                break;
            case 3:
                System.out.println("Enter your email:");
                email = sc.next();
                System.out.println("Enter your password:");
                password = sc.next();
                System.out.println("Enter your first name:");
                String firstName = sc.next();
                System.out.println("Enter your last name:");
                String lastName = sc.next();
                System.out.println("Enter your account type:");
                String type = sc.next();
                System.out.println("Enter your phone number:");
                int phone = sc.nextInt();
                System.out.println("Enter your address:");
                String address = sc.next();
                System.out.println("Enter your organization:");
                String organization = sc.next();
                Attendee newAttendee = new Attendee(new vStr(email), new vStr(password), new vStr(firstName), new vStr(lastName), new vStr(type), new vInt(phone), new vStr(address), new vStr(organization));
                try {
                    Attendee.insert(new vStr(email), new vStr(password), new vStr(firstName), new vStr(lastName), new vStr(type), new vInt(phone), new vStr(address), new vStr(organization));
                    System.out.println("Registration successful");
                    user = newAttendee;
                    attendeeFunction.attendeeMenu();
                } catch (SQLException e) {
                    System.out.println("Registration failed");
                    e.printStackTrace();
                }
                break;
        }
    }


    //Functions for Admin



    //Functions for Attendee

    public static void main(String[] args) throws SQLException {
        login();
        db.close();
    }
}