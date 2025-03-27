package topsis;

import io.cucumber.java.en.*;
import utils.ScaleConverter;

import static org.junit.jupiter.api.Assertions.*;

public class BDDTest {
    private String lastValidationResult;
    private String rating;
    private String scale;

    @Given("the application is running")
    public void appIsRunning() {
    }

    @When("the expert rates {string} on the {string} scale")
    public void expertRates(String r, String s) {
        rating = r;
        scale = s;
        double convertedValue = ScaleConverter.convert(scale, rating);
        if (convertedValue != 0.0 || (rating.equals("0") && scale.equals("numeric"))) { // 0.0 может быть реальной оценкой в numeric шкале
            lastValidationResult = "Valid rating";
        } else {
            lastValidationResult = "Invalid rating";
        }
    }

    @Then("the response should be {string}")
    public void checkResponse(String expectedResponse) {
        assertEquals(expectedResponse, lastValidationResult);
    }

    @Then("the normalized value of rating is {double}")
    public void getNormalizedValue(Double expectedNormalizedValue) {
        assertEquals(expectedNormalizedValue, ScaleConverter.convert(scale, rating));
    }
}
