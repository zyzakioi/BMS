package config;

import utils.validator.*;

public enum RegistryAttr implements Attr {
    ATTENDEE_ID(
            AttendeeAttr.ATTENDEE_ID.getAttrName(),
            AttendeeAttr.ATTENDEE_ID.getDescription(),
            AttendeeAttr.ATTENDEE_ID.getValidator()
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
    DRINK("Drink", "Drink", new NameValidator()),
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
    @Override public boolean isUpdatable() { return this != ATTENDEE_ID && this != BANQUET_ID && this != MEAL_ID;}

    public static boolean hasAttr(String attr) {
        for (RegistryAttr a: RegistryAttr.values()) {
            if (a.toString().equalsIgnoreCase(attr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert string to RegistryAttr
     * @param attr the String to convert
     * @return the RegistryAttr. null if not found
     */
    public static RegistryAttr fromString(String attr) {
        for (RegistryAttr a: RegistryAttr.values()) {
            if (a.toString().equalsIgnoreCase(attr)) {
                return a;
            }
        }
        return null;
    }
}
