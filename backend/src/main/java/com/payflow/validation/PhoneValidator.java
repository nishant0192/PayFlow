package com.payflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    // Indian phone number pattern
    private static final String PHONE_PATTERN = "^[6-9]\\d{9}$";
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);

    @Override
    public void initialize(ValidPhone constraintAnnotation) {}

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        return phone != null && pattern.matcher(phone).matches();
    }
}