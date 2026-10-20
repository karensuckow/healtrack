# 🌿 HealTrack API (Backend)

**HealTrack** is a REST API developed in Spring Boot for the complete management of a holistic therapy clinic. This module serves as the backend engine for the HealTrack system, providing a robust administrative portal for therapist and a client portal for booking sessions and tracking therapeutic tasks.

> **⚙️ Execution & Setup:** This module is part of the HealTrack monorepo. For instructions on how to configure environment variables, build, and run the full stack (Backend + Angular Frontend) using Docker, please refer to the **[Root Repository README](../README.md)**.

## 🛠️ Tech Stack

This project was built using modern Java ecosystem tools and best practices:

*   **Core:** Java 21, Spring Boot 4.1.1
*   **Database & ORM:** PostgreSQL, Spring Data JPA, Hibernate, Flyway (Migrations)
*   **Security:** Spring Security, JWT (JSON Web Tokens) for stateless authentication
*   **Testing:** JUnit 5, Mockito, AssertJ, Spring Boot Test (`@WebMvcTest`, `@MockitoBean`)
*   **Documentation:** SpringDoc OpenAPI (Swagger UI)
*   **Utilities:** Lombok, Java Records (DTO Pattern), Bean Validation

## 🏛️ Architecture & Code Quality

The API was designed with maintainability and scalability in mind:
*   **Clean Architecture:** Strict separation of concerns (Controllers, Services, Repositories).
*   **DTO Pattern:** Data encapsulation using immutable Java `record` to prevent over-posting and isolate domain models.
*   **Global Exception Handling:** Centralized `@ControllerAdvice` for consistent API error responses.
*   **Automated Testing:** Comprehensive unit tests covering business logic (Services) and HTTP layers (Controllers) without loading the full application context.
*   **Soft Deletion:** Safe data removal implementation for entities like Users and Appointments.

## 📚 API Documentation (Swagger)

Once the backend service is running, the interactive API documentation is automatically generated. You can explore and test all endpoints directly through the browser:

*   **Swagger UI:** `http://localhost:8080/swagger-ui.html`
*   **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 🚀 Features and Development Status

### 👩‍⚕️ Therapist Portal (Admin)
- [x] **Working Hours Management:** Configuration of standard working hours and breaks.
- [x] **Time Off Management:** Addition of days off, trips, and schedule blocks.
- [x] **Client Management:** Paginated listing, data editing, soft deletion, and reactivation of client profiles.
- [ ] **Appointment Approval:** Accept, reject, or reschedule booking requests made by clients.
- [ ] **Active Scheduling:** Manual blocking and booking of appointments on behalf of clients.
- [ ] **Notes:** Recording of private observations for each session.
- [ ] **Task Prescription:** Creation of exercises and practices for the client to perform at home.

### 👤 Client Portal
- [x] **Authentication and Registration:** Account creation, login (with JWT), and retrieval of the logged-in profile (`/users/me`).
- [x] **Profile Management:** Update of personal data and password.
- [ ] **Schedule Viewing:** Consultation of available days and times for sessions.
- [ ] **Booking and Cancellation:** Reservation or cancellation of appointments.
- [ ] **Task Tracking:** Marking recommended therapeutic practices as completed.

---

## 📐 Data Conventions

To ensure seamless integration with the front-end, the system strictly adheres to the following formats:

*   **Dates and Times:** International **ISO-8601** standard.
    *   Dates only: `YYYY-MM-DD` (e.g., `2026-10-19`)
    *   Times only: `HH:MM:SS` (e.g., `14:30:00`)
    *   Date and Time: `YYYY-MM-DDTHH:MM:SS` (e.g., `2026-10-19T14:30:00`)
*   **Days of the Week:** Standardized numeric representation from 1 to 7 (`Short` type), which facilitates calendar manipulation in JavaScript.
    *   `1` = Monday
    *   `2` = Tuesday
    *   `3` = Wednesday
    *   `4` = Thursday
    *   `5` = Friday
    *   `6` = Saturday
    *   `7` = Sunday

---

## 📖 Endpoints (API Routes)

### 🔐 1. Authentication (`/auth`)

*   **`POST /auth/register`**
    *   **Description:** Creates a new client account.
    *   **Payload (`RegisterRequestDTO`):**
        ```json
        {
          "name": "John Doe",
          "email": "john@email.com",
          "password": "securePassword123"
        }
        ```
*   **`POST /auth/login`**
    *   **Description:** Authenticates the user and returns access tokens.
    *   **Payload (`LoginRequestDTO`):**
        ```json
        {
          "email": "john@email.com",
          "password": "securePassword123"
        }
        ```
    *   **Response (`LoginResponseDTO`):** `token` and `refreshToken`.

### 👥 2. Users and Clients (`/users`)

*   **`GET /users/me`**
    *   **Description:** Returns the data of the currently logged-in user.
    *   **Access:** Authenticated
*   **`GET /users`**
    *   **Description:** Returns a paginated list of all registered clients.
    *   **Access:** `ADMIN` only
    *   **URL Parameters (Default):** `?page=0&size=10&sort=name,asc`
*   **`GET /users/{id}`**
    *   **Description:** Fetches a specific user.
    *   **Access:** `ADMIN` or the user themselves (if `id` matches the logged-in user).
*   **`PATCH /users/{id}`**
    *   **Description:** Updates name and/or email.
    *   **Access:** `ADMIN` or the user themselves.
    *   **Payload (`UpdateUserDTO`):** `name` and `email` (optional).
*   **`PATCH /users/{id}/password`**
    *   **Description:** Changes the user's password.
    *   **Access:** `ADMIN` or the user themselves.
    *   **Payload (`UpdatePasswordDTO`):** `password` (min. 8 characters).
*   **`DELETE /users/{id}`**
    *   **Description:** Logically inactivates (soft deletes) a client.
    *   **Access:** `ADMIN` only.
*   **`PATCH /users/{id}/reactivate`**
    *   **Description:** Reactivates a previously inactivated client.
    *   **Access:** `ADMIN` only.

### 🕒 3. Scheduling and Times (`/schedule`)

#### Working Hours (`/schedule/working-hours`)
*   **`GET /schedule/working-hours`**
    *   **Description:** Returns the configured standard working days and hours for the clinic.
    *   **Access:** Authenticated
*   **`POST /schedule/working-hours`**
    *   **Description:** Updates the entire working hours schedule. The submitted data replaces the previous schedule.
    *   **Access:** `ADMIN` only
    *   **Payload (`List<WorkingHoursRequestDTO>`):**
        ```json
        [
          {
            "dayOfWeek": 1, 
            "startTime": "08:00:00",
            "endTime": "18:00:00",
            "breakStart": "12:00:00",
            "breakEnd": "13:00:00"
          }
        ]
        ```

#### Time Offs and Blocks (`/schedule/time-off`)
*   **`GET /schedule/time-off`**
    *   **Description:** Fetches schedule blocks (e.g., trips, holidays). For front-end convenience, simply send parameters in the `YYYY-MM-DD` format. The backend automatically converts them to cover the full day boundaries.
    *   **Query Params:**
        *   `?date=2026-10-19` (Checks for blocks on a specific day).
        *   `?start=2026-10-01&end=2026-10-31` (Fetches all blocks within a date range).
    *   **Access:** Authenticated
*   **`POST /schedule/time-off`**
    *   **Description:** Creates a schedule block. Rejects inverted dates or overlapping times.
    *   **Access:** `ADMIN` only
    *   **Payload (`TimeOffRequestDTO`):**
        ```json
        {
          "startDatetime": "2026-11-10T14:00:00",
          "endDatetime": "2026-11-10T18:00:00",
          "reason": "Parapsychology Congress"
        }
        ```
*   **`DELETE /schedule/time-off/{id}`**
    *   **Description:** Removes a scheduled block.
    *   **Access:** `ADMIN` only.