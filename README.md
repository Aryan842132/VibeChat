# VibeChat - Real-time Chat Application Backend

A WhatsApp-like real-time chat application built with Java Spring Boot, WebSocket, and MongoDB.

## 📁 Project Structure

```
src/main/java/com/vibechat/
├── VibeChatApplication.java          # Main application entry point
├── config/
│   ├── SecurityConfig.java           # Security & password encoding
│   ├── MongoConfig.java              # MongoDB configuration
│   └── WebSocketConfig.java          # WebSocket setup for real-time messaging
├── controller/
│   └── UserController.java           # REST endpoints for user management
├── service/
│   └── UserService.java              # Business logic for users
├── repository/
│   └── UserRepository.java           # Data access layer
├── model/
│   └── User.java                     # User entity
├── dto/
│   ├── RegisterRequest.java          # Registration request DTO
│   ├── LoginRequest.java             # Login request DTO
│   ├── UserResponse.java             # User response DTO
│   └── ApiResponse.java              # Generic API response wrapper
└── exception/
    ├── ResourceNotFoundException.java
    ├── ValidationException.java
    └── GlobalExceptionHandler.java   # Centralized error handling
```

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data MongoDB** - Database
- **Spring WebSocket** - Real-time communication
- **Lombok** - Reduce boilerplate code
- **Maven** - Build tool
- **BCrypt** - Password hashing

## 📋 Features

### Part 1 - User Management

✅ **User Registration** (`POST /api/users/register`)
- Username, email, password validation
- Email/username uniqueness check
- Password encryption

✅ **User Login** (`POST /api/users/login`)
- Email/password authentication
- Status update (ONLINE/OFFLINE)
- Last seen timestamp

✅ **Get User** (`GET /api/users/{id}`)
- Retrieve user by ID
- Returns sanitized user data (no password)

### User Entity Fields

- `id` - Unique identifier
- `username` - User's display name
- `email` - User's email
- `password` - Encrypted password
- `profilePicture` - Profile image URL
- `status` - ONLINE / OFFLINE
- `lastSeen` - Last activity timestamp
- `createdAt` - Account creation date

## 🔧 Configuration

### application.properties

```properties
spring.application.name=VibeChat

# MongoDB Configuration
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/vibechat}
spring.data.mongodb.database=vibechat

# Server Configuration
server.port=${PORT:8080}

# Logging
logging.level.com.vibechat=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG

# WebSocket Configuration
spring.websocket.enabled=true
```

### Environment Variables

Set these in Railway or your deployment platform:

- `MONGODB_URI` - MongoDB connection string
- `PORT` - Server port (default: 8080)

## 🛠️ Setup & Run

### Prerequisites

- Java 17+
- MongoDB installed locally OR MongoDB Atlas URI
- Maven 3.6+

### Local Development

1. **Clone the repository**
```bash
cd VibeChat
```

2. **Start MongoDB** (if running locally)
```bash
mongod
```

3. **Build the application**
```bash
./mvnw clean install
```

4. **Run the application**
```bash
./mvnw spring-boot:run
```

### Using Docker

```bash
docker build -t vibechat .
docker run -p 8080:8080 -e MONGODB_URI=mongodb://host.docker.internal:27017/vibechat vibechat
```

## 📡 API Endpoints

### Authentication & User Management

#### Register User
```http
POST /api/users/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login User
```http
POST /api/users/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

#### Get User by ID
```http
GET /api/users/{userId}
```

### Response Format

All responses follow this format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Responses

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

## 🔌 WebSocket Configuration

WebSocket endpoint configured at: `/ws`

### Message Broker Configuration

- **Simple Broker**: `/topic`, `/queue`
- **Application Prefix**: `/app`
- **User Destination**: `/user`

Example STOMP connection:
```javascript
const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/ws'
});
```

## 🗂️ Database Schema

### Users Collection

```json
{
  "_id": "ObjectId",
  "username": "string",
  "email": "string",
  "password": "string (hashed)",
  "profilePicture": "string",
  "status": "ONLINE|OFFLINE",
  "lastSeen": "datetime",
  "createdAt": "datetime"
}
```

## 🔐 Security

- Passwords encrypted using BCrypt
- No sensitive data exposed in responses
- Input validation on all endpoints
- CORS enabled for frontend integration

## 📝 Next Steps (Future Parts)

- [ ] JWT Authentication
- [ ] Chat Message Model & Repository
- [ ] WebSocket Message Controller
- [ ] Group Chat Support
- [ ] Message Persistence
- [ ] File Upload (Profile Pictures)
- [ ] Online Status Tracking
- [ ] Message Read Receipts

## 🧪 Testing

Run tests:
```bash
./mvnw test
```

## 📦 Deployment

### Railway Deployment

1. Connect GitHub repository to Railway
2. Set environment variables:
   - `MONGODB_URI`
   - `PORT` (optional, defaults to 8080)
3. Deploy automatically via Dockerfile

## 🤝 Contributing

This is a learning project. Feel free to extend with additional features!

## 📄 License

MIT License - Feel free to use this project for learning!
