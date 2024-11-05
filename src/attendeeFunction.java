public class attendeeFunction {
    public static void attendeeMenu() {
        Attendee attendee = (Attendee) Main.user;
        System.out.println("Select an option:");
        System.out.println("1. Register for a banquet");
        System.out.println("2. Update registration");
        System.out.println("3. View registration");
        System.out.println("4. Delete registration");
        System.out.println("5. Exit");
        int choice = Main.sc.nextInt();
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
}
