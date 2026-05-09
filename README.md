# Hospital Online Appointment Scheduling System

Java/Spring Boot appointment scheduling backend for a hospital outpatient clinic.
The app uses Controller -> Service -> Repository -> MySQL with JDBC only.

## Implemented Features

- Browse availability at `GET /slots`
- Book appointments at `GET /book` and `POST /book`
- View and cancel appointments at `GET /appointments`
- Provider availability management at `GET /provider/availability` and `POST /provider/availability`
- Double-booking prevention with a transaction, `READ_COMMITTED`, and `SELECT ... FOR UPDATE`
- Mock notification service boundary:
  - Main app endpoint: `POST /api/integrations/appointments/{appointmentId}/notifications`
  - Mock external service: `POST /mock-api/notifications/booking-confirmation`
- Health and metrics endpoint at `GET /health`

## Database

The project is configured for MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_appointment
spring.datasource.username=root
spring.datasource.password=password
```

Create the `hospital_appointment` database first, then adjust credentials in `src/main/resources/application.properties` if your local MySQL account differs. On startup, the schema initializer creates the project tables and inserts demo records for patients, doctors, one admin, some services, and sample slots.

## Run

```bash
sh mvnw spring-boot:run
```

or run hospitalApplication.java

Open `http://localhost:8080`.
