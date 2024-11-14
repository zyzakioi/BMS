package utils.validator;

public class DrinkValidator implements Validator{
    @Override
    public boolean eval(String str) {
        return str.matches("[A-Za-z ]{1,100}") && str.split(" +").length <= 4;
    }

    @Override
    public String reason() {
        return "Drink name must be 1-4 words";
    }
}
