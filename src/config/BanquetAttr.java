package config;

import utils.validator.*;

public enum BanquetAttr implements Attr{
    BANQUET_ID("BIN", "Banquet ID", null),
    BANQUET_NAME("Banquet_name", "Banquet Name", new NameValidator()),
    DATE("Banquet_date", "Date", new DateValidator()),
    TIME("Banquet_time", "Time", new TimeValidator()),
    LOCATION("Location", "Location", new AddressValidator()),
    ADDRESS("Address", "Address", new AddressValidator()),
    CONTACT_FIRST_NAME("First_name", "First name", new NameValidator()),
    CONTACT_LAST_NAME("Last_name", "Last name", new NameValidator()),
    AVAILABILITY("Availability", "Availability (0/1)", new BoolValidator()),
    QUOTA("Quota", "Quota", new IntValidator());

    private final String attrName;
    private final String description;
    private final Validator vd;

    private final static Tables table = Tables.BANQUET;

    BanquetAttr(String attrName, String description, Validator vd) {
        this.attrName = attrName;
        this.description = description;
        this.vd = vd;
    }

    @Override public Tables getTable() { return table; }
    @Override public String getAttrName() { return attrName; }
    @Override public String getDescription() { return description; }
    @Override public Validator getValidator() { return vd;}
    @Override public boolean isUpdatable() { return this != BANQUET_ID; }
}
