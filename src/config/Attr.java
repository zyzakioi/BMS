package config;

import control.Controller;
import utils.SecurityUtils;
import utils.validator.Validator;
import view.View;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static control.Controller.db;
import static utils.InputUtils.getStr;

public interface Attr {
    String getAttrName();
    String getDescription();
    Validator getValidator();
    Tables getTable();
    boolean isUpdatable();

    /**
     *
     * @param newVal the new value to set to
     * @param conditions the conditions for SQL WHERE clause
     * @throws SQLException when unexpected SQL error occurs
     */
    default void updateTo(String newVal, String conditions) throws SQLException {
        if (!isUpdatable()) throw new RuntimeException("Attribute " + getAttrName() + " not updatable");
        String sql = "UPDATE " + getTable() + " SET " + getAttrName() + " = ?";
        if (!conditions.isEmpty()) sql += " WHERE " + conditions;
        db.executeUpdate(sql, new String[]{newVal});
    }

    /**
     * Repeatedly prompts the user to enter a valid attribute value
     * until the entered value actually exists in the database.
     * @return a valid attribute value that exists in the database
     * @throws SQLException when unexpected SQL error occurs
     */
    default String inputHasVal() throws SQLException {
        String sql = "SELECT " + getAttrName() + " FROM " + getTable() + " WHERE " + getAttrName() + " = ?";
        while (true) {
            String val = getStr(getDescription());
            try (ResultSet rs = db.executeQuery(sql, val)) {
                if (rs.next()) return val;
                View.displayError(getAttrName() + " = " + val + " does not exist");
            }
        }
    }

    /**
     * Repeatedly prompts the user to enter a valid attribute value,
     * until the entered value is both UNIQUE and VALID.
     * <b>DO NOT</b> use this method to input password, as that would leak sensitive information.
     * Attempts to input password with this method will cause <b>fatal error</b>
     */
    default String inputUniqueVal() throws SQLException {
        String sql = "SELECT " + getAttrName() + " FROM " + getTable() + " WHERE " + getAttrName() + " = ?";
        Validator vd = getValidator();
        while (true) {
            String val = getStr(getDescription());
            assert vd != null; // null indicates password attribute
            if (vd.eval(val)) {
                try (ResultSet rs = db.executeQuery(sql, val)){
                    if (rs.next()) View.displayError("ID already registered");
                    else return val;
                }
            } else View.displayError(vd.reason());
        }
    }

    /**
     * Prompts the user to enter a valid input for the attribute.
     * The method ensures that it repeatedly prompts the user until a valid input is entered. <br><br>
     * <b>DO NOT</b> use this method to input password, as that may leak sensitive information.
     * Attempts to input password with this method will cause <b>fatal error</b>
     *
     * @return a valid input for the attribute
     */
    default String inputNewVal() {
        Validator vd = getValidator();
        while (true) {
            String input = getStr(getDescription());
            assert vd != null;
            if (vd.eval(input)) return input; // if input is valid according to validator
            else View.displayError(vd.reason());
        }
    }

    /**
     * Takes an array of Attr we wish to prompt the user to enter.
     * The method ensures that it repeatedly prompts the user until a valid input for each attribute is entered.
     * The returned String array is guaranteed to contain valid inputs for each attribute.
     *
     * @param attrs the array of Attr we wish to prompt the user to enter
     * @return an array of valid inputs for the corresponding attributes
     */
    static String[] inputNewVals(Attr[] attrs) {
        String[] res = new String[attrs.length];
        for (int i = 0; i < attrs.length; i++) {
            switch (attrs[i]) {
                case BanquetAttr.BANQUET_ID -> res[i] = (++Controller.banquetNum) + "";
                case RegistryAttr.BANQUET_ID -> throw new RuntimeException("Cannot generate BIN in Registry context");
                case MealAttr.BANQUET_ID -> throw new RuntimeException("Cannot generate BIN in Meal context");
                default -> res[i] = attrs[i].inputNewVal();
            }
        }
        return res;
    }

    /**
     * Takes an array of valid Attr to check with.
     * The method ensures that it repeatedly prompts the user until a valid attribute is entered.
     * The returned Attr is guaranteed to be in attrs.
     *
     * @param sc scanner object
     * @param attrs the array of valid Attr to check with
     * @return the valid attribute to update
     */
    static Attr inputValidAttr(Scanner sc, Attr[] attrs) {
        while (true) {
            View.displayPrompt("Attribute to update");
            String strAttr = sc.nextLine().trim();
            for (Attr attr : attrs) {
                if (attr.getAttrName().equalsIgnoreCase(strAttr))
                    return attr;
            }
            View.displayError("Attribute does not exist");
        }
    }

    static String[] getColumns(Attr[] attrs) {
        String[] res = new String[attrs.length];
        for (int i = 0; i < attrs.length; i++)
            res[i] = attrs[i].getDescription();
        return res;
    }
}
