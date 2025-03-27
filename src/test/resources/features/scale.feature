Feature: Rating validation
  Verify if the given rating belongs to the specified scale.

  Scenario: Valid rating is provided
    Given the application is running
    When the expert rates "ЭВ" on the "long" scale
    Then the response should be "Valid rating"
    Then the normalized value of rating is 9.0

  Scenario: Invalid rating is provided
    Given the application is running
    When the expert rates "ЭВ" on the "short" scale
    Then the response should be "Invalid rating"
    Then the normalized value of rating is 0.0

  Scenario: The boundary value in the numerical scale
    Given the application is running
    When the expert rates "0" on the "numeric" scale
    Then the response should be "Valid rating"
    Then the normalized value of rating is 0.0

  Scenario: The value is outside the numerical scale
    Given the application is running
    When the expert rates "13" on the "numeric" scale
    Then the response should be "Invalid rating"
    Then the normalized value of rating is 0.0



