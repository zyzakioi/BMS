package config;

import utils.validator.*;

public enum RegistryAttr implements Attr {
    EMAIL(
            AttendeeAttr.EMAIL.getAttrName(),
            AttendeeAttr.EMAIL.getDescription(),
            AttendeeAttr.EMAIL.getValidator()
    ),
    BANQUET_ID(
            BanquetAttr.BANQUET_ID.getAttrName(),
            BanquetAttr.BANQUET_ID.getDescription(),
            BanquetAttr.BANQUET_ID.getValidator()
    ),
    MEAL_ID(
            MealAttr.MEAL_ID.getAttrName(),
            MealAttr.MEAL_ID.getDescription(),
            MealAttr.MEAL_ID.getValidator()
    ),
    DRINK("Drink", "Drink", new DrinkValidator()),
    SEAT("Seat", "Seat number", new SeatValidator()),
    ATTENDANCE("Attendance", "Attendance", new BoolValidator()), // NUMBER(1)
    REMARKS("Remarks", "Remarks", new RemarksValidator());

    private final String attrName;
    private final String description; // user friendly description
    private final Validator vd;
    private final static Tables table = Tables.REGISTRY;

    RegistryAttr(String attrName, String description, Validator vd) {
        this.attrName = attrName;
        this.description = description;
        this.vd = vd;
    }

    @Override public Tables getTable() { return table; }
    @Override public String getAttrName() { return attrName; }
    @Override public String getDescription() { return description; }
    @Override public Validator getValidator() { return vd; }
    @Override public boolean isUpdatable() { return this != EMAIL && this != BANQUET_ID && this != MEAL_ID;}
}
