# Eagle Bank API

A RESTful banking API built with Spring Boot that allows users to manage accounts, perform transactions, and handle authentication with JWT security.

## 🚀 Features
- **User Management**: Complete CRUD operations with secure registration and profile management  
- **Account Handling**: Create and manage bank accounts with proper ownership validation  
- **Transaction Processing**: Deposit and withdrawal operations with balance validation  
- **JWT Authentication**: Secure token-based authentication for all endpoints  
- **Real-time Balance Updates**: Atomic transaction processing ensuring data consistency  
- **Comprehensive Error Handling**: Detailed error responses matching OpenAPI specification  

## Technology Stack
- Java 21 with Spring Boot 3.5.5  
- Spring Security with JWT authentication  
- H2 Database for development (easily switchable to production databases)  
- Hibernate JPA for data persistence  
- Maven for dependency management  
- Jakarta Validation for request validation  

## API Endpoints

### Authentication
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST   | /v1/auth/login | Authenticate user and receive JWT token | No |

### Users
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST   | /v1/users | Create a new user account | No |
| GET    | /v1/users/{userId} | Get user details | Yes |
| PATCH  | /v1/users/{userId} | Update user information | Yes |
| DELETE | /v1/users/{userId} | Delete user account | Yes |

### Accounts
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST   | /v1/accounts | Create a new bank account | Yes |
| GET    | /v1/accounts | List user's accounts | Yes |
| GET    | /v1/accounts/{accountNumber} | Get account details | Yes |
| PATCH  | /v1/accounts/{accountNumber} | Update account information | Yes |
| DELETE | /v1/accounts/{accountNumber} | Delete bank account | Yes |

### Transactions
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST   | /v1/accounts/{accountNumber}/transactions | Create deposit/withdrawal transaction | Yes |
| GET    | /v1/accounts/{accountNumber}/transactions | List account transactions | Yes |
| GET    | /v1/accounts/{accountNumber}/transactions/{transactionId} | Get transaction details | Yes |

## 🚦 Getting Started

### Prerequisites
- Java 21 or later  
- Maven 3.6 or later  

### Installation & Running
Clone and build the application:
```bash
git clone <your-repo-url>
cd eagle-bank-api
mvn clean install
```

Run the application:
```bash
mvn spring-boot:run
```

Access the application:
- **API Base URL**: http://localhost:8080  
- **H2 Database Console**: http://localhost:8080/h2-console  
- **JDBC URL**: jdbc:h2:mem:testdb  
- **Username**: sa  
- **Password**: (leave empty)  

### Testing with cURL Examples

**Create a User:**
```bash
curl -X POST http://localhost:8080/v1/users   -H "Content-Type: application/json"   -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+441234567890",
    "password": "securePassword123",
    "address": {
      "line1": "123 Main Street",
      "town": "London",
      "county": "Greater London",
      "postcode": "SW1A 1AA"
    }
  }'
```

**Login to Get JWT Token:**
```bash
curl -X POST http://localhost:8080/v1/auth/login   -H "Content-Type: application/json"   -d '{
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }'
```

**Create an Account (using token from login):**
```bash
curl -X POST http://localhost:8080/v1/accounts   -H "Content-Type: application/json"   -H "Authorization: Bearer YOUR_JWT_TOKEN"   -d '{
    "name": "Main Savings Account",
    "accountType": "personal"
  }'
```

**Make a Deposit:**
```bash
curl -X POST http://localhost:8080/v1/accounts/01123456/transactions   -H "Content-Type: application/json"   -H "Authorization: Bearer YOUR_JWT_TOKEN"   -d '{
    "amount": 100.50,
    "currency": "GBP",
    "type": "deposit",
    "reference": "Initial deposit"
  }'
```

## 🔐 Security Features
- JWT Authentication: All endpoints except user registration and login require valid JWT tokens  
- Password Hashing: BCrypt password encoding with salt  
- Ownership Validation: Users can only access their own resources  
- Input Validation: Comprehensive request validation with meaningful error messages  
- CORS Protection: Configured for secure cross-origin requests  

## 🧪 Testing
The application includes test coverage across service and integration layers:

```bash
# Run unit tests
mvn test

# Run tests with coverage report
mvn test jacoco:report
```

### Test Coverage Includes:
- Unit tests for service layer logic  
- Integration tests for API endpoints  
- Error scenario testing (insufficient funds, invalid credentials)  
- Security testing (authentication and authorization)  

## 📊 Database Schema
The application uses the following main entities:
- **Users**: User profile information and credentials  
- **Accounts**: Bank accounts with current balances  
- **Transactions**: Record of all financial transactions  

## 📝 License
This project is open source and available under the [MIT License](LICENSE).

