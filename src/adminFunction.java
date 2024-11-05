import java.sql.ResultSet;
import java.sql.SQLException;
public class adminFunction {
    public static void adminMenu() {
        while(true){
            Administrator admin = (Administrator) Main.user;
            System.out.println("Select an option:");
            System.out.println("1. Add a new banquet");
            System.out.println("2. Manage banquet");
            System.out.println("3. View all banquets");
            System.out.println("4. Manage attendee");
            System.out.println("5. Manage registration");
            System.out.println("6. Exit");
            int choice = Main.sc.nextInt();
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
                    manageRegistration();
                case 6:
                    System.exit(0);
            }
        }
    }

    public static void insertBanquet() {
        System.out.println("Enter the name of the banquet:");
        String name = Main.sc.next();
        System.out.println("Enter the date of the banquet:");
        String date = Main.sc.next();
        System.out.println("Enter the time of the banquet:");
        String time = Main.sc.next();
        System.out.println("Enter the location of the banquet:");
        String location = Main.sc.next();
        System.out.println("Enter the address of the banquet:");
        String address = Main.sc.next();
        System.out.println("Enter the first name of the contact person:");
        String contactFirstName = Main.sc.next();
        System.out.println("Enter the last name of the contact person:");
        String contactLastName = Main.sc.next();
        System.out.println("Is the banquet available now?");
        boolean available = Main.sc.nextBoolean();
        System.out.println("Enter the quota of the banquet:");
        int quota = Main.sc.nextInt();
        Meal[] meals = new Meal[4];
        for (int i = 0; i < 4; i++) {
            System.out.println("Enter the BIN of the meal:");
            int BIN = Main.sc.nextInt();
            System.out.println("Enter the name of the meal:");
            String mealName = Main.sc.next();
            System.out.println("Enter the type of the meal:");
            String type = Main.sc.next();
            System.out.println("Enter the price of the meal:");
            int price = Main.sc.nextInt();
            System.out.println("Enter the cuisine of the meal:");
            String cuisine = Main.sc.next();
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
        int BIN = Main.sc.nextInt();
        try {
            System.out.println(Banquet.query(new vInt(BIN)));
            System.out.println("Select an option:");
            System.out.println("1. Update banquet");
            System.out.println("2. Update meal");
            System.out.println("3. Take attendance");
            System.out.println("4. Manage relative registration");
            int choice = Main.sc.nextInt();
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
                case 4:
                    manageRegistration(BIN);
                    break;
            }
        } catch (SQLException e) {
            System.out.println("Banquet not found");
        }
    }
    public static void updateBanquet() {
        System.out.println("Enter the BIN of the banquet you want to update:");
        int BIN = Main.sc.nextInt();
        System.out.println("Enter the attribute you want to update:");
        String attr = Main.sc.next();
        if (attr.equals("BIN")) {
            System.out.println("BIN cannot be updated");
        }
        System.out.println("Enter the new value:");
        String val = Main.sc.next();
        try {
            Banquet.update(new vStr("BIN"), new vStr(attr), new vInt(BIN), new vStr(val));
        } catch (SQLException e) {
            System.out.println("Banquet not updated");
            e.printStackTrace();
        }
    }
    public static void updateMeal(int BIN){
        System.out.println("Enter the name of the meal you want to update:");
        String name = Main.sc.next();
        System.out.println("Enter the attribute you want to update:");
        String attr = Main.sc.next();
        System.out.println("Enter the new value:");
        String val = Main.sc.next();
        if(attr.equals("BIN")){
            System.out.println("BIN cannot be updated");
            return;
        }
        if(attr.equals("Name")){
            try{
                ResultSet result = Main.db.query("SELECT * FROM Meal WHERE Name = " + new vStr(name) + " AND BIN = " + BIN);
                Meal tmp = new Meal(new vInt(BIN), new vStr(val), new vStr(result.getString("Type")), new vInt(result.getInt("Price")), new vStr(result.getString("Cuisine")));
                tmp.insert();
                Main.db.update("UPDATE Registration SET MealName = " + new vStr(val) + " WHERE MealName = " + new vStr(name) + " AND BIN = " + BIN);
                Main.db.delete("DELETE FROM Meal WHERE Name = " + new vStr(name) + " AND BIN = " + BIN);
            }
            catch(SQLException e){
                System.out.println("Meal not found");
                return;
            }
            return;
        }
        try {
            Meal.update(new vInt(BIN), new vStr(name), new vStr(attr), new vStr(val));
        } catch (SQLException e) {
            System.out.println("Meal not updated");
            e.printStackTrace();
        }
    }
    public static void takeAttendance(int BIN) {
        while(true) {
            System.out.println("Enter the email of the attendee(Input 0 to exit):");
            String email = Main.sc.next();
            if(email.equals("0")){
                break;
            }
            try {
                Main.db.update("UPDATE Registration SET Attended = true WHERE AttendeeEmail = " + new vStr(email) + " AND BIN = " + BIN);
            } catch (SQLException e) {
                System.out.println("Registration failed");
                e.printStackTrace();
            }
        }
    }
    public static void viewAll(){
        try{
            ResultSet result = Main.db.query("SELECT BIN, Name FROM Banquet");
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
        String email = Main.sc.next();
        try {
            System.out.println(Attendee.query(new vStr("Email")));
        } catch (SQLException e) {
            System.out.println("Attendee not found");
            return;
        }
        System.out.println("Select an option:");
        System.out.println("1. Update attendee");
        System.out.println("2. Manage relative registration");
        int choice = Main.sc.nextInt();
        switch(choice){
            case 1:
                updateAttendee(email);
                break;
            case 2:
                manageRegistration(email);
                break;
        }
    }
    public static void updateAttendee(String email) {
        System.out.println("Enter the attribute you want to update:");
        String attr = Main.sc.next();
        if (attr.equals("Email") || attr.equals("Password")) {
            System.out.println("Email or Password cannot be updated");
        }
        System.out.println("Enter the new value:");
        String val = Main.sc.next();
        try {
            Attendee.update(new vStr("Email"), new vStr(attr), new vStr(email), new vStr(val));
        } catch (SQLException e) {
            System.out.println("Attendee not updated");
            e.printStackTrace();
        }
    }
    public static void manageRegistration(){
        System.out.println("Enter the email:");
        String email = Main.sc.next();
        manageRegistration(email);
    }
    public static void manageRegistration(int BIN){
        System.out.println("Enter the email:");
        String email = Main.sc.next();
        manageRegistration(email, BIN);
    }
    public static void manageRegistration(String email){
        System.out.println("Enter the BIN:");
        int BIN = Main.sc.nextInt();
        manageRegistration(email, BIN);
    }
    public static void manageRegistration(String email, int BIN){
        System.out.println("Select an option:");
        System.out.println("1. Update registration");
        System.out.println("2. Unregister");
        int choice = Main.sc.nextInt();
        switch(choice){
            case 1:
                updateRegistration(email, BIN);
                break;
            case 2:
                unregister(email, BIN);
                break;
        }
    }
    public static void updateRegistration(String email, int BIN){
        System.out.println("Enter the attribute you want to update:");
        String attr = Main.sc.next();
        if (attr.equals("AttendeeEmail") || attr.equals("BIN")) {
            System.out.println("Email or BIN cannot be updated");
        }
        System.out.println("Enter the new value:");
        String val = Main.sc.next();
        try {
            Main.db.update("UPDATE Registration SET " + attr + " = " + val + " WHERE AttendeeEmail = " + email + " AND BIN = " + BIN);
        } catch (SQLException e) {
            System.out.println("Registration not updated");
            e.printStackTrace();
        }
    }
    public static void unregister(String email, int BIN) {
        try {
            Main.db.delete("DELETE FROM Registration WHERE AttendeeEmail = " + email + " AND BIN = " + BIN);
        } catch (SQLException e) {
            System.out.println("Unregister failed");
            e.printStackTrace();
        }
    }
}
