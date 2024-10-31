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
                    attendeeMenu();
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
                    adminMenu();
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
                    attendeeMenu();
                } catch (SQLException e) {
                    System.out.println("Registration failed");
                    e.printStackTrace();
                }
                break;
        }
    }


    //Functions for Admin
    public static void adminMenu() {
        while(true){
            Administrator admin = (Administrator) user;
            System.out.println("Select an option:");
            System.out.println("1. Add a new banquet");
            System.out.println("2. Manage banquet");
            System.out.println("3. View all banquets");
            System.out.println("4. Manage attendee");
            System.out.println("5. Exit");
            int choice = sc.nextInt();
            switch(choice){
                case 1:
                    insertBanquet();
                    break;
                case 2:
                    manageBanquet();
                    break;
                case 3:
                    viewAll();
                    break;
                case 4:
                    manageAttendee();
                    break;
                case 5:
                    System.exit(0);
            }
        }
    }

    public static void insertBanquet() {
        System.out.println("Enter the name of the banquet:");
        String name = sc.next();
        System.out.println("Enter the date of the banquet:");
        String date = sc.next();
        System.out.println("Enter the time of the banquet:");
        String time = sc.next();
        System.out.println("Enter the location of the banquet:");
        String location = sc.next();
        System.out.println("Enter the address of the banquet:");
        String address = sc.next();
        System.out.println("Enter the first name of the contact person:");
        String contactFirstName = sc.next();
        System.out.println("Enter the last name of the contact person:");
        String contactLastName = sc.next();
        System.out.println("Is the banquet available now?");
        boolean available = sc.nextBoolean();
        System.out.println("Enter the quota of the banquet:");
        int quota = sc.nextInt();
        Meal[] meals = new Meal[4];
        for (int i = 0; i < 4; i++) {
            System.out.println("Enter the BIN of the meal:");
            int BIN = sc.nextInt();
            System.out.println("Enter the name of the meal:");
            String mealName = sc.next();
            System.out.println("Enter the type of the meal:");
            String type = sc.next();
            System.out.println("Enter the price of the meal:");
            int price = sc.nextInt();
            System.out.println("Enter the cuisine of the meal:");
            String cuisine = sc.next();
            meals[i] = new Meal(new vInt(BIN), new vStr(mealName), new vStr(type), new vInt(price), new vStr(cuisine));
        }
        if(!Meal.check(meals)){
            System.out.println("Meal names must be unique");
            return;
        }
        try {
            Banquet.insert(new vStr(name), new vStr(date), new vStr(time), new vStr(location), new vStr(address), new vStr(contactFirstName), new vStr(contactLastName), available, new vInt(quota));
            for(Meal meal : meals){
                meal.insert();
            }
        }
        catch (SQLException e){
            System.out.println("Banquet not added");
            e.printStackTrace();
        }
    }
    public static void manageBanquet(){
        System.out.println("Enter the BIN:");
        int BIN = sc.nextInt();
        try {
            System.out.println(Banquet.query(new vInt(BIN)));
            System.out.println("Select an option:");
            System.out.println("1. Update banquet");
            System.out.println("2. Update meal");
            System.out.println("3. Take attendance");
            int choice = sc.nextInt();
            switch(choice){
                case 1:
                    updateBanquet();
                    break;
                case 2:
                    updateMeal(BIN);
                    break;
                case 3:
                    takeAttendance(BIN);
                    break;
            }
        } catch (SQLException e) {
            System.out.println("Banquet not found");
        }
    }
    public static void updateBanquet() {
        System.out.println("Enter the BIN of the banquet you want to update:");
        int BIN = sc.nextInt();
        System.out.println("Enter the attribute you want to update:");
        String attr = sc.next();
        if (attr.equals("BIN")) {
            System.out.println("BIN cannot be updated");
        }
        System.out.println("Enter the new value:");
        String val = sc.next();
        try {
            Banquet.update(new vStr("BIN"), new vStr(attr), new vInt(BIN), new vStr(val));
        } catch (SQLException e) {
            System.out.println("Banquet not updated");
            e.printStackTrace();
        }
    }
    public static void updateMeal(int BIN){
        System.out.println("Enter the name of the meal you want to update:");
        String name = sc.next();
        System.out.println("Enter the attribute you want to update:");
        String attr = sc.next();
        System.out.println("Enter the new value:");
        String val = sc.next();
        try {
            Meal.update(new vInt(BIN), new vStr(name), new vStr(attr), new vStr(val));
        } catch (SQLException e) {
            System.out.println("Meal not updated");
            e.printStackTrace();
        }
    }
    public static void takeAttendance(int BIN) {
        System.out.println("Enter the email of the attendee:");
        String email = sc.next();
        try {
            db.update("UPDATE Registration SET Attended = true WHERE AttendeeEmail = " + new vStr(email) + " AND BIN = " + BIN);
        } catch (SQLException e) {
            System.out.println("Registration failed");
            e.printStackTrace();
        }
    }
    public static void viewAll(){
        try{
            ResultSet result = db.query("SELECT BIN, Name FROM Banquet");
            while(result.next()){
                System.out.println(result.getInt("BIN") + " " + result.getString("Name"));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public static void manageAttendee(){
        System.out.println("Enter the email:");
        String email = sc.next();
        try {
            System.out.println(Attendee.query(new vStr("Email")));
        } catch (SQLException e) {
            System.out.println("Attendee not found");
        }
    }


    //Functions for Attendee
    public static void attendeeMenu() {
        Attendee attendee = (Attendee) user;
        System.out.println("Select an option:");
        System.out.println("1. Register for a banquet");
        System.out.println("2. Update registration");
        System.out.println("3. View registration");
        System.out.println("4. Delete registration");
        System.out.println("5. Exit");
        int choice = sc.nextInt();
        switch (choice){
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:
                System.exit(0);
        }
    }
    public static void main(String[] args) throws SQLException {
        login();
        db.close();
    }
}