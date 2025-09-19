# 💊 MedReminder - Medication Reminder API

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](#)

A comprehensive RESTful API for managing medication reminders and schedules. Built with Spring Boot, featuring JWT authentication, automatic dose scheduling, and medication tracking capabilities.

## 🚀 Features

- **🔐 Secure Authentication** - JWT-based authentication with refresh tokens
- **💊 Medication Management** - Full CRUD operations for medications
- **📅 Smart Scheduling** - Flexible posology (dosing schedules) with recurring patterns
- **⏰ Automatic Dose Generation** - Intelligent dose scheduling based on frequency rules
- **📊 Dose Tracking** - Mark doses as taken and track medication adherence
- **📚 API Documentation** - Interactive Swagger UI
- **🛡️ Security** - Token blacklisting, CORS support, and secure endpoints

## Technology Stack

- **Backend Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** H2 (development) / PostgreSQL (production ready)
- **Security:** Spring Security + JWT
- **Documentation:** Swagger/OpenAPI 3
- **Build Tool:** Maven
- **Mapping:** MapStruct
- **Validation:** Hibernate Validator

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git

## ⚡ Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/Grupo-10-HackathonF5/MedReminderBackend-Group10.git
cd MedReminderBackend-Group10
```

### 2. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Access the application
- **API Base URL:** http://localhost:8080/api
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:medreminder`
  - Username: `sa`
  - Password: *(empty)*

## 📖 API Endpoints

### 🔐 Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/auth/register` | Register a new user | ❌ |
| `POST` | `/api/auth/login` | Login user | ❌ |
| `POST` | `/api/auth/refresh` | Refresh access token | ✅ |
| `POST` | `/api/auth/logout` | Logout user | ✅ |

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "message": "login",
  "tokenType": "Bearer",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "johndoe",
  "refreshToken": "dGhpc2lzYXJlZnJlc2h0b2tlbg=="
}
```

### 💊 Medication Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/medications` | Get all medications | ✅ |
| `GET` | `/api/medications/{id}` | Get medication by ID | ✅ |
| `POST` | `/api/medications` | Create new medication | ✅ |
| `PUT` | `/api/medications/{id}` | Update medication | ✅ |
| `DELETE` | `/api/medications/{id}` | Delete medication | ✅ |

#### Create Medication
```http
POST /api/medications
Authorization: Bearer {your-jwt-token}
Content-Type: application/json

{
  "userId": 1,
  "name": "Ibuprofen",
  "dosageQuantity": 400,
  "dosageUnit": "mg",
  "active": true,
  "notes": "Take with food to avoid stomach irritation"
}
```

### 📅 Posology (Dosing Schedules)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/posologies` | Get all posologies | ✅ |
| `GET` | `/api/posologies/{id}` | Get posology by ID | ✅ |
| `GET` | `/api/posologies/medication/{medicationId}` | Get posologies by medication | ✅ |
| `GET` | `/api/posologies/active` | Get active posologies | ✅ |
| `POST` | `/api/posologies` | Create new posology | ✅ |
| `PUT` | `/api/posologies/{id}` | Update posology | ✅ |
| `DELETE` | `/api/posologies/{id}` | Delete posology | ✅ |

#### Create Posology
```http
POST /api/posologies
Authorization: Bearer {your-jwt-token}
Content-Type: application/json

{
  "medicationId": 1,
  "userId": 1,
  "startDate": "2025-09-18",
  "endDate": "2025-12-18",
  "dayTime": "2025-09-18T08:00:00",
  "frequencyValue": 8,
  "frequencyUnit": "HOUR",
  "quantity": 1.0,
  "reminderMessage": "Take after breakfast",
  "dosesNumber": 90
}
```

**Frequency Units:**
- `HOUR` - Every X hours
- `DAY` - Every X days
- `WEEK` - Every X weeks
- `MONTH` - Every X months

### 💉 Dose Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/doses/user/{userId}` | Get doses for user in date range | ✅ |
| `GET` | `/api/doses/user/{userId}/today` | Get today's doses for user | ✅ |
| `PATCH` | `/api/doses/{doseId}/taken` | Mark dose as taken | ✅ |

#### Get User Doses
```http
GET /api/doses/user/1?from=2025-09-18T00:00:00&to=2025-09-19T23:59:59
Authorization: Bearer {your-jwt-token}
```

#### Mark Dose as Taken
```http
PATCH /api/doses/123/taken
Authorization: Bearer {your-jwt-token}
```

## 🏗️ Project Structure

```
src/main/java/com/hackathon/medreminder/
├── auth/                           # Authentication & JWT
│   ├── controller/AuthController.java
│   ├── service/AuthService.java
│   └── dto/                        # Auth DTOs
├── user/                           # User management
│   ├── entity/User.java
│   ├── service/UserService.java
│   └── repository/UserRepository.java
├── medication/                     # Medication CRUD
│   ├── controller/MedicationController.java
│   ├── service/MedicationService.java
│   ├── entity/Medication.java
│   └── repository/MedicationRepository.java
├── posology/                       # Dosing schedules
│   ├── controller/PosologyController.java
│   ├── service/PosologyService.java
│   ├── entity/Posology.java
│   └── repository/PosologyRepository.java
├── dose/                           # Individual doses
│   ├── controller/DoseController.java
│   ├── service/DoseService.java
│   ├── entity/Dose.java
│   └── repository/DoseRepository.java
└── shared/                         # Shared utilities
    ├── security/                   # Security config
    ├── exception/                  # Global exception handling
    └── config/                     # App configuration
```

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
```

### Medications Table
```sql
CREATE TABLE medications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    dosage_quantity INTEGER NOT NULL,
    dosage_unit VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    notes VARCHAR(1000),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Posologies Table
```sql
CREATE TABLE posologies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    medication_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    day_time TIMESTAMP NOT NULL,
    frequency_value INTEGER NOT NULL,
    frequency_unit VARCHAR(20) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    reminder_message VARCHAR(255),
    doses_number DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (medication_id) REFERENCES medications(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Doses Table
```sql
CREATE TABLE doses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reminder_id BIGINT,
    scheduled_day DATE,
    scheduled_date_time TIMESTAMP NOT NULL,
    taken BOOLEAN DEFAULT FALSE,
    taken_time TIMESTAMP,
    posology_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (posology_id) REFERENCES posologies(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:medreminder
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890123456789012345678901234567890
jwt.expiration-ms=3600000

# Server Configuration
server.port=8080
```

### Environment Variables (Production)
```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/medreminder
DATABASE_USERNAME=medreminder_user
DATABASE_PASSWORD=secure_password

# JWT
JWT_SECRET=your-very-secure-secret-key-here
JWT_EXPIRATION_MS=3600000

# Server
SERVER_PORT=8080
```

## 🧪 Testing the API

### Using curl

1. **Register a user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

2. **Login and get token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

3. **Create a medication:**
```bash
curl -X POST http://localhost:8080/api/medications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "userId": 1,
    "name": "Aspirin",
    "dosageQuantity": 100,
    "dosageUnit": "mg",
    "active": true,
    "notes": "Take with water"
  }'
```

## 🔒 Security Features

- **JWT Authentication** - Stateless authentication with access and refresh tokens
- **Token Blacklisting** - Invalidated tokens are blacklisted for security
- **CORS Configuration** - Configurable cross-origin resource sharing
- **Password Encryption** - BCrypt hashing for secure password storage
- **Input Validation** - Comprehensive validation using Bean Validation
- **SQL Injection Protection** - JPA/Hibernate provides built-in protection

##  Monitoring & Health

The application includes Spring Boot Actuator endpoints for monitoring:

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

## Frontend Integration
This backend API is designed to work with our React Frontend Application:
Frontend Repository: MedReminder Frontend
Full Stack Setup

Clone and run the backend (this repository):

bash   git clone https://github.com/Grupo-10-HackathonF5/MedReminderBackend-Group10.git
cd MedReminderBackend-Group10
mvn spring-boot:run

Clone and run the frontend:

bash   git clone https://github.com/Grupo-10-HackathonF5/Grupo-10-HackathonF5-MedReminderFrontEnd.git
cd Grupo-10-HackathonF5-MedReminderFrontEnd
npm install
npm start

Access the complete application:

Frontend UI: http://localhost:5173
Backend API: http://localhost:8080/api
API Documentation: http://localhost:8080/swagger-ui.html



CORS Configuration
The backend is pre-configured to accept requests from common frontend development ports:
http://localhost:5173 (Vite default)
http://localhost:3000 (React default)
http://localhost:4200 (Angular default)
http://localhost:8081 (Alternative port)


## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

-**Grupo 10 Hackathon F5** - *Initial work* - [MedReminderBackend-Group10](https://github.com/Grupo-10-HackathonF5/MedReminderBackend-Group10)


## 📞 Support

If you have any questions or issues, please [open an issue](https://github.com/Grupo-10-HackathonF5/MedReminderBackend-Group10/issues) on GitHub.

---

⭐ **Star this repository** if you found it helpful!

## 🎯 Roadmap

- [ ] Email notifications for missed doses
- [ ] Mobile push notifications
- [ ] Medication interaction checker
- [ ] Pharmacy integration
- [ ] Health metrics tracking
- [ ] Caregiver notifications
- [ ] Medication refill reminders