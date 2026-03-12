# 🚀 Deploy to Railway - JWT Token Implementation

## ✅ Changes Made

I've added **JWT token generation** to your login and registration endpoints!

### Files Added/Modified:

1. **Added Dependencies** (`pom.xml`)
   - JWT libraries (io.jsonwebtoken)
   
2. **New Configuration** (`JwtConfig.java`)
   - JWT secret key configuration
   - Token expiration settings
   
3. **Token Provider** (`JwtTokenProvider.java`)
   - Generate JWT tokens
   - Validate tokens
   - Extract user info from tokens
   
4. **Updated Service** (`UserService.java`)
   - `register()` now returns JWT token
   - `login()` now returns JWT token
   
5. **Updated Controller** (`UserController.java`)
   - Returns `AuthResponse` with token
   - Includes user details + JWT token
   
6. **New DTO** (`AuthResponse.java`)
   - Combines user info with JWT token
   
7. **Updated Security** (`SecurityConfig.java`)
   - Public access to `/register` and `/login`
   - CSRF disabled for API
   
8. **Configuration** (`application.properties`)
   - JWT secret with fallback default
   - JWT expiration time (24 hours)

---

## 🔧 Environment Variables for Railway

Add these to your Railway project dashboard:

```env
# MongoDB Connection
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/vibechat

# JWT Secret (use a strong random string)
JWT_SECRET=YourVerySecretKeyForJWTTokenGenerationAndValidation2026ChangeThis

# JWT Expiration (optional - defaults to 24 hours)
JWT_EXPIRATION=86400000

# Server Port (Railway sets this automatically)
PORT=8080
```

---

## 📡 New API Response Format

### Register Response:
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
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### Login Response:
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
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

## 🚀 Deploy Steps

### 1. Commit and Push Changes
```bash
cd d:\VibeChat
git add .
git commit -m "feat: Add JWT token generation for login and register"
git push origin main
```

### 2. Railway Will Auto-Deploy
- Railway detects the push
- Builds the Docker image
- Deploys with environment variables

### 3. Test the API

**Register:**
```http
POST https://vibechat-production-24a1.up.railway.app/api/users/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "...",
    "username": "testuser",
    "email": "test@example.com",
    "status": "OFFLINE",
    "token": "eyJhbGciOiJIUzI1NiJ9..."  ← JWT TOKEN HERE!
  }
}
```

**Login:**
```http
POST https://vibechat-production-24a1.up.railway.app/api/users/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": "...",
    "username": "testuser",
    "email": "test@example.com",
    "status": "ONLINE",
    "token": "eyJhbGciOiJIUzI1NiJ9..."  ← JWT TOKEN HERE!
  }
}
```

---

## 🎯 What to Do Next

### For Frontend Integration:

1. **Store the token** after login/register:
   ```javascript
   localStorage.setItem('authToken', response.data.token);
   ```

2. **Use token in requests**:
   ```javascript
   headers: {
     'Authorization': `Bearer ${response.data.token}`
   }
   ```

3. **Decode token** to get user info:
   ```javascript
   const userInfo = JSON.parse(atob(token.split('.')[1]));
   console.log(userInfo.userId, userInfo.email);
   ```

### For WebSocket Authentication:

```javascript
const stompClient = new StompJs.Client({
  brokerURL: 'wss://vibechat-production-24a1.up.railway.app/ws',
  connectHeaders: {
    Authorization: `Bearer ${token}`
  }
});
```

---

## 🔍 Troubleshooting

### If you still get 401 Unauthorized:

1. **Check Railway logs** for errors
2. **Verify environment variables** are set correctly
3. **Wait for deployment** to complete (check Railway dashboard)
4. **Clear browser cache** and try again

### If token is not generated:

1. Check Railway logs for compilation errors
2. Verify `JWT_SECRET` is set (or use default)
3. Ensure MongoDB connection is working

---

## 📝 Testing with Postman

1. **Create Collection** → "VibeChat API"
2. **Add Request** → POST /api/users/register
3. **Send request** with user data
4. **Copy token** from response
5. **Save token** as collection variable:
   ```javascript
   // In Tests tab
   pm.collectionVariables.set("authToken", pm.response.json().data.token);
   ```
6. **Use in other requests**:
   ```
   Authorization: Bearer {{authToken}}
   ```

---

## ✨ Benefits of JWT

- ✅ **Stateless** - No server-side session storage needed
- ✅ **Secure** - Signed with secret key
- ✅ **Scalable** - Works across multiple servers
- ✅ **Self-contained** - Contains all user info
- ✅ **Standard** - Industry standard for authentication

---

## 🎉 Success Criteria

After deployment, you should:

- ✅ Get **200 OK** (not 401) on `/register`
- ✅ Receive a **JWT token** in response
- ✅ Get **200 OK** on `/login`
- ✅ Receive a **JWT token** in response
- ✅ Token should be **valid for 24 hours**
- ✅ Token contains **userId, email, username**

---

**Ready to deploy!** 🚀

Push the code to GitHub and Railway will automatically deploy with JWT token support.
