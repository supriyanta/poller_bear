# Poller Bear

## Table of Contents

- [Setup](#setup)
- [Api Documentation](#api_doc)

  - [Authentication](#authentication)
  - [Poll](#poll)

<a name="setup"></a>

## Setup

- Change properties from `appication.properties`

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

    ```json
    {
        "name": <email or username>,
        "username": <username>,
        "email": <email>,
        "password": <password>
    }
    ```

- **Login** `POST /api/login`

  - Request body

    ```json
    {
        "emailOrUsername": <email or username>,
        "password": <password>
    }
    ```

    - returns `Authentication Token`

<a name="poll"></a>
#### Poll

- **All Polls** `GET /api/poll`

  - returns Paginated response of all polls

- **Create Poll** `POST /api/login`

  - Request body

    ```json
    {
        "topic": <topic of the Poll>,
        "choices": [
            { "text": <choice1 text> },
            { "text": <choice2 text> },
            ...
        ],
        "duration": {
            "days": <days>,
            "hours": <hours>
        }
    }
    ```
