package config;

import utils.validator.*;

public enum BanquetAttr implements Attr{
    BANQUET_ID("BIN", "banquet ID", null),
    BANQUET_NAME("Banquet_name", "banquet name", new NameValidator()),
    DATE("Banquet_date", "date", new DateValidator()),
    TIME("Banquet_time", "time", new TimeValidator()),
    LOCATION("Location", "location", new AddressValidator()),
    ADDRESS("Address", "address", new AddressValidator()),
    CONTACT_FIRST_NAME("First_name", "first name", new NameValidator()),
    CONTACT_LAST_NAME("Last_name", "last name", new NameValidator()),
    AVAILABILITY("Availability", "availability (0/1)", new BoolValidator()),
    QUOTA("Quota", "quota", new IntValidator());

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
