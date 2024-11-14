package utils.validator;

public class DrinkValidator implements Validator{
    @Override
    public boolean eval(String str) {
        return str.matches("[A-Za-z ]{1,100}") && str.split(" +").length <= 10;
    }

    @Override
    public String reason() {
        return "Drink name must be 1-10 words";
    }
}
