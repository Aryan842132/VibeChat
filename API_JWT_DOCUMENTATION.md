# VibeChat API Documentation - JWT Authentication

## 🔐 JWT Token Implementation

The login and registration endpoints now generate **JWT (JSON Web Tokens)** for authentication.

---

## 📡 Updated API Responses

### 1️⃣ Register User - WITH JWT TOKEN

**Endpoint:** `POST /api/users/register`

**Request:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "65f1a2b3c4d5e6f7g8h9i0j1",
    "username": "johndoe",
    "email": "john@example.com",
    "profilePicture": null,
    "status": "OFFLINE",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI2NWYxYTJiM2M0ZDVlNmY3ZzhoOWkwajEiLCJlbWFpbCI6ImpvaG5AZXhhbXBsZS5jb20iLCJ1c2VybmFtZSI6ImpvaG5kb2UiLCJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzEwMjQ1NjAwLCJleHAiOjE3MTAzMzIwMDB9.abc123xyz"
  }
}
```

---

### 2️⃣ Login User - WITH JWT TOKEN

**Endpoint:** `POST /api/users/login`

**Request:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": "65f1a2b3c4d5e6f7g8h9i0j1",
    "username": "johndoe",
    "email": "john@example.com",
    "profilePicture": null,
    "status": "ONLINE",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI2NWYxYTJiM2M0ZDVlNmY3ZzhoOWkwajEiLCJlbWFpbCI6ImpvaG5AZXhhbXBsZS5jb20iLCJ1c2VybmFtZSI6ImpvaG5kb2UiLCJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzEwMjQ1NjAwLCJleHAiOjE3MTAzMzIwMDB9.abc123xyz"
  }
}
```

---

## 🔑 JWT Token Structure

The token contains:

```json
{
  "userId": "65f1a2b3c4d5e6f7g8h9i0j1",
  "email": "john@example.com",
  "username": "johndoe",
  "sub": "john@example.com",
  "iat": 1710245600,      // Issued at timestamp
  "exp": 1710332000       // Expiration timestamp (24 hours)
}
```

**Token Expiration:** 
- Default: **86400000 ms** (24 hours)
- Configurable via `JWT_EXPIRATION` environment variable

---

## 🔧 How to Use the Token

### Using in Postman

1. **Register or Login** to get the token
2. **Copy the token** from the response
3. **For authenticated requests**, add header:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
   ```

### Using in JavaScript/Frontend

```javascript
// After login
const response = await fetch('http://localhost:8080/api/users/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'john@example.com',
    password: 'password123'
  })
});

const data = await response.json();
const token = data.data.token;

// Store token (localStorage or secure cookie)
localStorage.setItem('authToken', token);

// Use in subsequent requests
fetch('http://localhost:8080/api/users/profile', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

### Using in React

```jsx
import { useState, useEffect } from 'react';

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));

  const handleLogin = async (email, password) => {
    const res = await fetch('/api/users/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    
    const data = await res.json();
    setToken(data.data.token);
    localStorage.setItem('token', data.data.token);
  };

  return (
    <div>
      {token ? <Dashboard token={token} /> : <Login onLogin={handleLogin} />}
    </div>
  );
}
```

---

## 🛡️ Protected Endpoints (Future)

Currently `/register` and `/login` are **public**. Other endpoints will require JWT:

```http
GET /api/users/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## ⚙️ Configuration

### Environment Variables for Railway

Add these to your Railway project:

```env
# JWT Secret Key (use a strong random string)
JWT_SECRET=YourVerySecretKeyForJWTTokenGenerationAndValidation2026

# JWT Expiration in milliseconds
# 86400000 = 24 hours
# 3600000 = 1 hour
# 604800000 = 7 days
JWT_EXPIRATION=86400000
```

### Local Development

In `application.properties`:
```properties
jwt.secret=VibeChatSecretKeyForJWTTokenGenerationAndValidation2026
jwt.expiration=86400000
```

---

## 🔍 Decode JWT Token

To verify/decode your token, visit: [https://jwt.io](https://jwt.io)

Paste your token to see the decoded payload!

---

## 📝 Example cURL Requests

### Register
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

---

## 🚀 Deployment Checklist

1. ✅ Set `JWT_SECRET` environment variable in Railway
2. ✅ Set `JWT_EXPIRATION` (optional, defaults to 24 hours)
3. ✅ Rebuild and redeploy application
4. ✅ Test registration endpoint
5. ✅ Verify token is returned in response
6. ✅ Test token validation

---

## 🎯 Next Steps

With JWT tokens working, you can now:

- ✅ Add JWT authentication filter for protected routes
- ✅ Implement WebSocket authentication with tokens
- ✅ Create user profile endpoints
- ✅ Build chat functionality with authenticated users
- ✅ Add token refresh mechanism

---

## 📦 Complete Response Schema

```typescript
interface AuthResponse {
  success: boolean;
  message: string;
  data: {
    id: string;
    username: string;
    email: string;
    profilePicture: string | null;
    status: "ONLINE" | "OFFLINE";
    token: string;  // ← JWT Token
  };
}
```

---

**🎉 Your API now generates JWT tokens for authentication!**

Deploy the changes to Railway and test the endpoints again. You should receive a JWT token in the response after registration and login.
