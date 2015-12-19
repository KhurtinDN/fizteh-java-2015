package ru.mipt.diht.students.IrinaMudrova.Twitter.library.validators;


import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class NonNegativeInteger implements IParameterValidator {
    public void validate(String name, String value)
            throws ParameterException {
        int n;
        try {
            n = Integer.parseInt(value);
        } catch (Exception e) {
            n = -1;
        }
        if (n < 0) {
            throw new ParameterException(new StringBuilder().append("Parameter ").append(name)
                    .append(" should be non-negative integer (found ").append(value).append(")").toString());
        }
    }
}
