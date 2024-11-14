package config;

import utils.validator.*;

public enum AttendeeAttr implements Attr{
    ATTENDEE_ID("Account_ID", "email", new EmailValidator()),
    PASSWORD("Password", "password", null),
    FIRST_NAME("First_name", "first name", new NameValidator()),
    LAST_NAME("Last_name", "last name", new NameValidator()),
    ORGANIZATION("Organization", "organization", new OrganizationValidator()),
    ATTENDEE_TYPE("Attendee_type", "account type", new AccountTypeValidator()),
    ADDRESS("Address", "address", new AddressValidator()),
    PHONE("Mobile_num", "phone number", new PhoneValidator());

    private final String attrName;
    private final String description; // user friendly description
    private final Validator vd;

    private static final Tables table = Tables.ATTENDEE;

    AttendeeAttr(String attrName, String description, Validator vd) {
        this.attrName = attrName;
        this.description = description;
        this.vd = vd;
    }

    @Override public Tables getTable() { return table; }
    @Override public String getAttrName() { return attrName; }
    @Override public String getDescription() { return description; }
    @Override public Validator getValidator() { return vd;}
    @Override public boolean isUpdatable() { return this != ATTENDEE_ID; }
}
