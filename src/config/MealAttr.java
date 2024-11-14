package config;

import utils.validator.NameValidator;
import utils.validator.*;
import view.View;

import java.sql.SQLException;
import java.util.HashSet;

public enum MealAttr implements Attr{
    BANQUET_ID(
            BanquetAttr.BANQUET_ID.getAttrName(),
            BanquetAttr.BANQUET_ID.getDescription(),
            BanquetAttr.BANQUET_ID.getValidator()
    ),
    MEAL_ID("Dish_name", "dish name", new NameValidator()),
    CUISINE("Cuisine", "cuisine name", new NameValidator()),
    PRICE("Price", "price", new IntValidator()),
    TYPE("Type", "type name", new NameValidator()),;

    private final String attrName;
    public final String description; // user friendly description
    private final Validator vd;
    private final static Tables table = Tables.MEAL;

    MealAttr(String attrName, String description, Validator vd) {
        this.attrName = attrName;
        this.description = description;
        this.vd = vd;
    }

    public static String[][] getValidMealSet(String banquetID) throws SQLException {
        HashSet<String> usedNames = new HashSet<>();
        String[][] mealSet = new String[4][];
        for (int i = 0; i < 4; i++) {
            String mealName;
            while (true) {
                mealName = MealAttr.MEAL_ID.inputNewVal();
                if (!usedNames.contains(mealName)) {
                    usedNames.add(mealName);
                    break;
                }
                View.displayError("Meal name already exists");
            }
            String mealType = MealAttr.TYPE.inputNewVal();
            String mealCuisine = MealAttr.CUISINE.inputNewVal();
            String mealPrice = MealAttr.PRICE.inputNewVal();
            mealSet[i] = new String[]{banquetID, mealName, mealType, mealCuisine, mealPrice};
        }
        return mealSet;
    }

    @Override public Tables getTable() { return table; }
    @Override public String getAttrName() { return attrName; }
    @Override public String getDescription() { return description; }
    @Override public Validator getValidator() { return vd; }
    @Override public boolean isUpdatable() { return this != BANQUET_ID && this != MEAL_ID; }
}
