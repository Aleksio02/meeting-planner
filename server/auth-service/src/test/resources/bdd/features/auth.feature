Feature: Authentication API

  Scenario: Successful login with valid credentials
    Given a user exists with login "alice" and password "securePass1"
    When the client sends a POST request to "/api/auth/login" with login "alice" and password "securePass1"
    Then the response status is 200
    And the response body contains a token
    And the response body contains a user with username "alice"

  Scenario: Login fails with wrong password
    Given a user exists with login "alice" and password "securePass1"
    When the client sends a POST request to "/api/auth/login" with login "alice" and password "wrongPass12"
    Then the response status is 401
    And the response body contains error message "Invalid password"

  Scenario: Login fails when user does not exist
    Given no user exists with login "ghost"
    When the client sends a POST request to "/api/auth/login" with login "ghost" and password "somePass12"
    Then the response status is 401
    And the response body contains error message "User not found"

  Scenario: Successful registration with valid data
    Given no user exists with login "bob" and email "bob@example.com"
    When the client sends a POST request to "/api/auth/register" with login "bob", email "bob@example.com", and password "myPassword1"
    Then the response status is 200
    And the response body contains a token
    And the response body contains a user with username "bob"

  Scenario: Registration fails when user already exists
    Given a user exists with login "alice" and email "alice@example.com" and password "securePass1"
    When the client sends a POST request to "/api/auth/register" with login "alice", email "alice@example.com", and password "myPassword1"
    Then the response status is 400
    And the response body contains error message "User with this username or email exists"
