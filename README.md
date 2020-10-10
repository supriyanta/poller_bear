# Poller Bear

Backend of a Polling app where a user can create poll, vote for polls, user can see his/her poll feed and user profile which provides history of the created polls and voted polls by that user.

## Technology used

- Spring Boot
- MySQL

## Table of Contents

- [Setup](#setup)
- [Api Documentation](#api_doc)

  - [Authentication](#authentication)
  - [Poll](#poll)
  - [User](#user)
  - [Availability](#availability)

<a name="setup"></a>

## Setup

- Change properties in `appication.properties`

  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/<database name>
  spring.datasource.username=<username>
  spring.datasource.password=<password>
  ```

- Create the database

  ```sql
  CREATE DATABASE <database name>;
  ```

- Run the application to create the tables
  ```bash
  mvn spring-boot:run
  ```
- Insert two roles in the Role table

  ```sql
  INSERT INTO roles(name) VALUES('ROLE_USER');
  ```

- Rerun the app
  ```bash
  mvn spring-boot:run
  ```

<a name="api_doc"></a>

## Api Documentation

<a name="authentication"></a>

#### Authentication

- **Signup** `POST /api/signup`

  - Request body

    ```javascript
    {
        "name": <name String>,
        "username": <username String>,
        "email": <email String>,
        "password": <password String>
    }
    ```

- **Login** `POST /api/login`

  - Request body

    ```javascript
    {
        "emailOrUsername": <email_or_username String>,
        "password": <password String>
    }
    ```

  - returns `Authentication Token`

<a name="poll"></a>

#### Poll

- **All Polls** `GET /api/poll`

  - returns Paginated response of all polls

- **Create Poll** `POST /api/poll`

  - Request body

    ```javascript
    {
        "topic": <topic_of_the_Poll String>,
        "choices": [
            { "text": <choice1_text String> },
            { "text": <choice2_text String> },
            ...
        ],
        "duration": {
            "days": <days Integer>,
            "hours": <hours Integer>
        }
    }
    ```

- **Get Poll by id** `GET /api/poll/{pollId}`

  - returns single Poll response of id: pollId

- **Cast vote for a Poll** `POST /api/poll/{pollId}/vote
  - Request body
    ```json
    {
      "choiceId": <choice id>
    }
    ```
  * retuns updated Poll response

<a name="user"></a>

#### User

- **Get User Profile** `GET /api/user/{username}`

  - returns User Profile of user with given username

<a name="availability"></a>

#### Availability

- **Get Availability of username and email** `GET /api/user/availability`

  - Request params
    ```bash
      username=<username>
      email=<email>
    ```

  * returns username or email already exists or not
