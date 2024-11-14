package utils.validator;

public class OrganizationValidator implements Validator {
    /**
     * The organization can only be one of the followings:
     * - PolyU
     * - SPEED
     * - HKCC
     * @param str the organization to validate
     * @return true if the organization is valid, false otherwise
     */
    @Override
    public boolean eval(String str) {
        return str.equals("PolyU") || str.equals("SPEED") || str.equals("HKCC");
    }

    @Override
    public String reason() {
        return "Organization must be one of the following: PolyU, SPEED, HKCC";
    }
}
