## Profiles and Database Setup

This project uses **Spring Profiles** to separate configuration for different environments.

### Profiles

- **`dev`**
    - Database: Local MySQL (`medreminder` database)
    - Location: `src/main/resources/application-dev.yml`
    - Behavior: Connects to MySQL, creates the schema if it does not exist, updates if it does.

- **`test`**
    - Database: In-memory H2
    - Location: `src/main/resources/application-test.yml`
    - Behavior: Used for unit/integration tests, schema auto-created at runtime.

- **default (`application.yml`)**
    - Base configuration shared by all profiles (e.g., logging, Swagger, etc.)

### How to Run with Profiles

#### Run with Maven
```bash
# Run with dev profile (MySQL)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with test profile (H2)
mvn spring-boot:run -Dspring-boot.run.profiles=test
