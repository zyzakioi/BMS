package utils.validator;

import config.Attr;

import java.sql.SQLException;
import java.util.Scanner;

public interface Validator {
    boolean eval(String str);
    String reason();
}

