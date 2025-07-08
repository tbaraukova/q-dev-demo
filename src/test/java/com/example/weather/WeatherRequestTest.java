package com.example.weather;

import com.example.weather.model.WeatherRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class WeatherRequestTest {

    private Validator validator;

    @BeforeMethod
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidRequest() {
        WeatherRequest request = new WeatherRequest();
        request.setCity("London");
        request.setDays("3");

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(request);
        Assert.assertEquals(violations.size(), 0, "Valid request should have no violations");
    }

    @Test
    public void testEmptyCity() {
        WeatherRequest request = new WeatherRequest();
        request.setCity("");
        request.setDays("3");

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(request);
        Assert.assertEquals(violations.size(), 1, "Empty city should have one violation");
        Assert.assertEquals(violations.iterator().next().getMessage(), "City is required");
    }

    @Test
    public void testNullCity() {
        WeatherRequest request = new WeatherRequest();
        request.setCity(null);
        request.setDays("3");

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(request);
        Assert.assertEquals(violations.size(), 1, "Null city should have one violation");
        Assert.assertEquals(violations.iterator().next().getMessage(), "City is required");
    }

    @Test
    public void testInvalidCityFormat() {
        WeatherRequest request = new WeatherRequest();
        request.setCity("London123");
        request.setDays("3");

        Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(request);
        Assert.assertEquals(violations.size(), 1, "Invalid city format should have one violation");
        Assert.assertEquals(violations.iterator().next().getMessage(), "City must contain only letters and spaces");
    }

    @Test
    public void testValidDaysValues() {
        WeatherRequest request = new WeatherRequest();
        request.setCity("London");

        // Test valid values: 1, 3, 7
        String[] validDays = {"1", "3", "7"};
        for (String days : validDays) {
            request.setDays(days);
            Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(request);
            Assert.assertEquals(violations.size(), 0, "Days value " + days + " should be valid");
        }
    }

    @Test
    public void testInvalidDaysValues() {
        WeatherRequest request = new WeatherRequest();
        request.setCity("London");

        // Test invalid values
        String[] invalidDays = {"0", "2", "4", "5", "6", "8", "10", "-1", "abc"};
        for (String days : invalidDays) {
            request.setDays(days);
            Set<ConstraintViolation<WeatherRequest>> violations = validator.validate(request);
            Assert.assertEquals(violations.size(), 1, "Days value " + days + " should be invalid");
            Assert.assertEquals(violations.iterator().next().getMessage(), "Days must be 1, 3, or 7");
        }
    }

    @Test
    public void testDefaultDaysValue() {
        WeatherRequest request = new WeatherRequest();
        Assert.assertEquals(request.getDays(), "1", "Default days value should be 1");
    }
}