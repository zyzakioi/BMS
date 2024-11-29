package utils.validator;

public class QuotaValidator implements Validator{

    @Override
    public boolean eval(String str) {
        Validator vd = new IntValidator();
        if (!vd.eval(str)) return false;
        return Integer.parseInt(str) >= 1;
    }

    @Override
    public String reason() {
        return "quota must be an integer >= 1";
    }
}
