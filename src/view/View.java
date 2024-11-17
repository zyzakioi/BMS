package view;

import java.util.ArrayList;

public class View {
    /**
     * 1. Attendee
     * 2. Administrator
     * 3. New User Registration
     * 4. Exit
     */
    public static void displayOptions(String action, String options) {
        System.out.println("\n\nSelect " + action + ": ");
        System.out.print(options);
    }

    public static void displayPrompt(String s) {
        System.out.print("\nEnter " + s + ":\n>>> ");
    }

    public static void displayBadInput(String expect, String received) {
        System.out.printf("Error: expects %s, but received %s\n", expect, received);
    }

    public static void displayBadInput(String expect, int received) {
        System.out.printf("Error: expects %s, but received %d\n", expect, received);
    }

    public static void displayExit() {
        System.out.println("System exited gracefully.");
    }

    public static void displayError(String err) {
        System.out.println("Error: " + err);
    }

    public static void displayMessage(String msg) {
        System.out.println("Info: " + msg);
    }

    public static void displayTable(String[] header, ArrayList<String[]> rows){
        System.out.println();
        for (String s : header) {
            System.out.print(s + "\t");
        }
        System.out.println();
        for (String[] row : rows) {
            for (String s : row) {
                System.out.print(s + "\t");
            }
            System.out.println();
        }
    }
}
