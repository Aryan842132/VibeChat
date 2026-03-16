# VibeChat API Documentation for Frontend Developers

Complete API reference for integrating the VibeChat backend with your frontend application.

---

## 📋 Table of Contents

1. [Base URL](#base-url)
2. [Authentication](#authentication)
3. [User Endpoints](#user-endpoints)
4. [Chat Endpoints](#chat-endpoints)
5. [File Upload Endpoints](#file-upload-endpoints)
6. [WebSocket Real-Time Messaging](#websocket-real-time-messaging)
7. [Error Handling](#error-handling)

---

## 🌐 Base URL

**Production:** `https://vibechat-production-24a1.up.railway.app`

**Development:** `http://localhost:8080`

All API endpoints are prefixed with `/api`

---

## 🔐 Authentication

Currently, most endpoints are publicly accessible for development. For production, JWT token authentication will be required in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

---

## 👥 User Endpoints

### 1. Register New User

**Endpoint:** `POST /api/users/register`

**Request Body:**
```json
{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123"
}
```

**Response (200 OK):**
```json
{
    "success": true,
    "message": "User registered successfully",
    "data": {
        "id": "69b3e8bef3d78e3460382cea",
        "username": "john_doe",
        "email": "john@example.com",
        "profilePicture": null,
        "status": "OFFLINE",
        "lastSeen": "2026-03-16T14:00:00",
        "createdAt": "2026-03-16T14:00:00"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 2. Login User

**Endpoint:** `POST /api/users/login`

**Request Body:**
```json
{
    "email": "john@example.com",
    "password": "SecurePass123"
}
```

**Response (200 OK):**
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "id": "69b3e8bef3d78e3460382cea",
        "username": "john_doe",
        "email": "john@example.com",
        "profilePicture": null,
        "status": "ONLINE",
        "lastSeen": "2026-03-16T14:00:00",
        "createdAt": "2026-03-16T14:00:00"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 3. Get User by ID

**Endpoint:** `GET /api/users/{userId}`

**Example:** `GET /api/users/69b3e8bef3d78e3460382cea`

**Response (200 OK):**
```json
{
    "success": true,
    "message": "User retrieved successfully",
    "data": {
        "id": "69b3e8bef3d78e3460382cea",
        "username": "john_doe",
        "email": "john@example.com",
        "profilePicture": "https://bucket.s3.region.amazonaws.com/profile-pictures/user.jpg",
        "status": "ONLINE",
        "lastSeen": "2026-03-16T14:00:00",
        "createdAt": "2026-03-16T14:00:00"
    }
}
```

---

### 4. Get User by Email

**Endpoint:** `GET /api/users/by-email?email={email}`

**Example:** `GET /api/users/by-email?email=john@example.com`

**Response (200 OK):**
```json
{
    "success": true,
    "message": "User retrieved successfully",
    "data": {
        "id": "69b3e8bef3d78e3460382cea",
        "username": "john_doe",
        "email": "john@example.com",
        "profilePicture": null,
        "status": "ONLINE",
        "lastSeen": "2026-03-16T14:00:00",
        "createdAt": "2026-03-16T14:00:00"
    }
}
```

---

## 💬 Chat Endpoints

### 1. Get Chat History Between Two Users

**Endpoint:** `GET /api/chat/history/{userId1}/{userId2}`

**Example:** `GET /api/chat/history/69b3e8bef3d78e3460382cea/69b2a4ea3309db162a586654`

**Response (200 OK):**
```json
{
    "success": true,
    "message": "Chat history retrieved",
    "data": [
        {
            "id": "msg-001",
            "senderId": "69b3e8bef3d78e3460382cea",
            "receiverId": "69b2a4ea3309db162a586654",
            "content": "Hey! How are you?",
            "messageType": "TEXT",
            "status": "READ",
            "timestamp": "2026-03-16T14:30:00"
        },
        {
            "id": "msg-002",
            "senderId": "69b2a4ea3309db162a586654",
            "receiverId": "69b3e8bef3d78e3460382cea",
            "content": "I'm good, thanks!",
            "messageType": "TEXT",
            "status": "READ",
            "timestamp": "2026-03-16T14:31:00"
        }
    ]
}
```

**Response when no messages exist:**
```json
{
    "success": true,
    "message": "Chat history retrieved",
    "data": []
}
```

---

## 📁 File Upload Endpoints

### 1. Upload Profile Picture (with optional userId)

**Endpoint:** `POST /api/upload/profile-picture`

**Content-Type:** `multipart/form-data`

**Form Data:**
- `file`: Image file (JPEG, PNG, GIF, WebP - max 5MB)
- `userId`: (Optional) User ID to update profile picture in database

**Example Request:**
```
POST /api/upload/profile-picture?userId=69b3e8bef3d78e3460382cea
Content-Type: multipart/form-data

file: [binary image data]
```

**Response (200 OK):**
```json
{
    "success": true,
    "message": "Upload successful",
    "data": {
        "fileName": "profile_pic.jpg",
        "fileUrl": "https://bucket.s3.region.amazonaws.com/profile-pictures/uuid.jpg",
        "fileType": "image/jpeg",
        "fileSize": 17432,
        "message": "Profile picture uploaded successfully and saved to database"
    }
}
```

---

### 2. Upload Profile Picture via User Endpoint

**Endpoint:** `POST /api/users/{userId}/upload-profile-picture`

**Content-Type:** `multipart/form-data`

**Form Data:**
- `file`: Image file (JPEG, PNG, GIF, WebP - max 5MB)

**Example Request:**
```
POST /api/users/69b3e8bef3d78e3460382cea/upload-profile-picture
Content-Type: multipart/form-data

file: [binary image data]
```

**Response (200 OK):**
```json
{
    "success": true,
    "message": "Profile picture updated successfully",
    "data": {
        "id": "69b3e8bef3d78e3460382cea",
        "username": "john_doe",
        "email": "john@example.com",
        "profilePicture": "https://bucket.s3.region.amazonaws.com/profile-pictures/uuid.jpg",
        "status": "ONLINE",
        "lastSeen": "2026-03-16T14:00:00",
        "createdAt": "2026-03-16T14:00:00"
    }
}
```

---

## 🔌 WebSocket Real-Time Messaging

### Connection Details

**WebSocket URL:** `ws://localhost:8080/ws-chat` (development)  
**Production URL:** `wss://vibechat-production-24a1.up.railway.app/ws-chat`

**Protocol:** STOMP over WebSocket (no SockJS fallback)

**Recommended Library:** [@stomp/stompjs](https://www.npmjs.com/package/@stomp/stompjs)

---

### WebSocket Connection Example

```javascript
import { Client } from '@stomp/stompjs';

const stompClient = new Client({
    brokerURL: 'wss://vibechat-production-24a1.up.railway.app/ws-chat',
    reconnectDelay: 5000,
    onConnect: () => {
        console.log('✅ Connected to WebSocket');
        
        // Subscribe to receive private messages
        stompClient.subscribe('/user/queue/messages', (message) => {
            const msg = JSON.parse(message.body);
            console.log('📬 Received message:', msg);
            displayMessage(msg);
            
            // Send delivery receipt
            sendStatusUpdate(msg.id, 'DELIVERED');
        });
        
        // Subscribe to status updates
        stompClient.subscribe('/user/queue/status', (message) => {
            const msg = JSON.parse(message.body);
            console.log('📊 Status update:', msg);
            updateMessageStatus(msg.id, msg.status);
        });
        
        // Subscribe to broadcast messages
        stompClient.subscribe('/topic/messages', (message) => {
            const msg = JSON.parse(message.body);
            console.log('📢 Broadcast:', msg);
        });
    },
    onStompError: (frame) => {
        console.error('❌ STOMP Error:', frame);
    }
});

stompClient.activate();
```

---

### Send Message (Real-Time)

**Destination:** `/app/chat.send`

**Message Format:**
```json
{
    "senderId": "69b3e8bef3d78e3460382cea",
    "receiverId": "69b2a4ea3309db162a586654",
    "content": "Hello! This is a text message",
    "messageType": "TEXT"
}
```

**Code Example:**
```javascript
function sendMessage(senderId, receiverId, content, messageType = 'TEXT') {
    const message = {
        senderId: senderId,
        receiverId: receiverId,
        content: content,
        messageType: messageType
    };
    
    stompClient.publish({
        destination: '/app/chat.send',
        body: JSON.stringify(message)
    });
}
```

**Server Response (via subscription):**
```json
{
    "id": "msg-003",
    "senderId": "69b3e8bef3d78e3460382cea",
    "receiverId": "69b2a4ea3309db162a586654",
    "content": "Hello! This is a text message",
    "messageType": "TEXT",
    "status": "SENT",
    "timestamp": "2026-03-16T14:45:00"
}
```

---

### Send Private Message

**Destination:** `/app/chat.private.{userId}`

**Example:** `/app/chat.private.69b2a4ea3309db162a586654`

**Message Format:**
```json
{
    "senderId": "69b3e8bef3d78e3460382cea",
    "content": "This is a private message",
    "messageType": "TEXT"
}
```

**Code Example:**
```javascript
function sendPrivateMessage(senderId, receiverId, content) {
    const message = {
        senderId: senderId,
        content: content,
        messageType: 'TEXT'
    };
    
    stompClient.publish({
        destination: `/app/chat.private.${receiverId}`,
        body: JSON.stringify(message)
    });
}
```

---

### Send Image/Video Message

**Message Type:** `IMAGE` or `VIDEO`

**Content:** URL to the media file (uploaded via file upload endpoint first)

**Example:**
```javascript
function sendImageMessage(senderId, receiverId, imageUrl) {
    const message = {
        senderId: senderId,
        receiverId: receiverId,
        content: imageUrl, // URL from file upload
        messageType: 'IMAGE'
    };
    
    stompClient.publish({
        destination: '/app/chat.send',
        body: JSON.stringify(message)
    });
}
```

---

### Update Message Status

**Destination:** `/app/chat.status`

**Status Values:** `SENT`, `DELIVERED`, `READ`

**Message Format:**
```json
{
    "messageId": "msg-003",
    "status": "READ"
}
```

**Code Example:**
```javascript
function updateMessageStatus(messageId, status) {
    const statusUpdate = {
        messageId: messageId,
        status: status
    };
    
    stompClient.publish({
        destination: '/app/chat.status',
        body: JSON.stringify(statusUpdate)
    });
}
```

**Server Response (to sender):**
```json
{
    "id": "msg-003",
    "senderId": "69b3e8bef3d78e3460382cea",
    "receiverId": "69b2a4ea3309db162a586654",
    "content": "Hello! This is a text message",
    "messageType": "TEXT",
    "status": "READ",
    "timestamp": "2026-03-16T14:45:00"
}
```

---

## ❌ Error Handling

### Standard Error Response Format

**400 Bad Request:**
```json
{
    "success": false,
    "message": "Invalid request data",
    "errors": [
        {
            "field": "email",
            "message": "Email is required"
        }
    ]
}
```

**404 Not Found:**
```json
{
    "success": false,
    "message": "User not found"
}
```

**500 Internal Server Error:**
```json
{
    "success": false,
    "message": "Internal server error"
}
```

---

## 📊 Data Models

### User Object
```typescript
interface User {
    id: string;
    username: string;
    email: string;
    profilePicture?: string | null;
    status: 'ONLINE' | 'OFFLINE';
    lastSeen: string; // ISO 8601 datetime
    createdAt: string; // ISO 8601 datetime
}
```

### ChatMessage Object
```typescript
interface ChatMessage {
    id: string;
    senderId: string;
    receiverId: string;
    content: string;
    messageType: 'TEXT' | 'IMAGE' | 'VIDEO';
    status: 'SENT' | 'DELIVERED' | 'READ';
    timestamp: string; // ISO 8601 datetime
}
```

### ApiResponse Object
```typescript
interface ApiResponse<T> {
    success: boolean;
    message: string;
    data?: T;
    token?: string; // Only for auth endpoints
}
```

---

## 🔧 Quick Start Examples

### Complete Chat Flow

```javascript
// 1. Register/Login
const response = await fetch('/api/users/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        username: 'john_doe',
        email: 'john@example.com',
        password: 'SecurePass123'
    })
});

const { data: user, token } = await response.json();
console.log('Logged in as:', user.username);

// 2. Connect to WebSocket
const stompClient = new Client({
    brokerURL: 'wss://vibechat-production-24a1.up.railway.app/ws-chat',
    onConnect: () => {
        console.log('Connected!');
        
        // Subscribe to messages
        stompClient.subscribe('/user/queue/messages', handleMessage);
        
        // Send initial message
        sendMessage(user.id, recipientId, 'Hello!');
    }
});
stompClient.activate();

// 3. Load chat history
const historyResponse = await fetch(`/api/chat/history/${user.id}/${recipientId}`);
const { data: messages } = await historyResponse.json();
displayMessages(messages);

// 4. Real-time messaging continues...
```

---

## 📝 Testing with Postman

See [POSTMAN_TESTING_GUIDE.md](./POSTMAN_TESTING_GUIDE.md) for detailed testing instructions.

---

## 🎯 Key Points for Frontend Integration

1. **WebSocket First**: Use WebSocket for real-time messaging
2. **REST Fallback**: Use REST API for loading chat history
3. **Message Types**: Support TEXT, IMAGE, and VIDEO messages
4. **Status Tracking**: Implement SENT → DELIVERED → READ flow
5. **Error Handling**: Always check `success` field in responses
6. **Reconnection**: Handle WebSocket reconnection gracefully
7. **CORS**: All endpoints support CORS for browser access

---

## 📞 Support

For questions or issues, refer to:
- [WEBSOCKET_INTEGRATION_GUIDE.md](./WEBSOCKET_INTEGRATION_GUIDE.md) - Detailed WebSocket guide
- [POSTMAN_TESTING_GUIDE.md](./POSTMAN_TESTING_GUIDE.md) - Testing instructions
- [CHAT_IMPLEMENTATION_SUMMARY.md](./CHAT_IMPLEMENTATION_SUMMARY.md) - Implementation details

---

**Happy Coding! 🚀**
