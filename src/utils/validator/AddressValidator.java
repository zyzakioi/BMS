package utils.validator;

public class AddressValidator implements Validator{
    @Override
    public boolean eval(String str) {
        boolean res = !str.isEmpty() && str.length() <= 50;
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != ' ') {
                return false;
            }
        }
        return res;
    }

    @Override
    public String reason() {
        return "Address must not be empty, at most 50 characters long, and does not contain special characters";
    }
}
