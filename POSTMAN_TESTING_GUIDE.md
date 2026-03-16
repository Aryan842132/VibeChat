# Postman Testing Guide for VibeChat WebSocket API

## ⚠️ Important Note About WebSocket vs REST

**WebSocket endpoints CANNOT be tested directly in Postman's regular HTTP requests.** 

Postman can only test the **REST API endpoints**, not the `@MessageMapping` WebSocket endpoints.

### What You CAN Test in Postman:
- ✅ REST endpoints (GET, POST, PUT, DELETE)
- ✅ `/api/chat/history/{userId1}/{userId2}` - Get chat history

### What You CANNOT Test in Postman:
- ❌ `/app/chat.send` - WebSocket message sending
- ❌ `/app/chat.private.{userId}` - Private messaging
- ❌ `/app/chat.status` - Status updates

---

## 📋 Part 1: Testing REST Chat Endpoints in Postman

### Prerequisites
1. Start your Spring Boot application
2. Ensure MongoDB is running
3. Have at least 2 users created in the database

---

### Test 1: Get Chat History Between Two Users

#### Endpoint Details

```
Method: GET
URL: http://localhost:8080/api/chat/history/{userId1}/{userId2}
Headers: Content-Type: application/json
```

#### Step-by-Step Instructions

**Step 1: Create a New Request**
1. Open Postman
2. Click "New" → "HTTP Request"
3. Set method to **GET**
4. Enter URL: `http://localhost:8080/api/chat/history/user-id-1/user-id-2`
   - Replace `user-id-1` and `user-id-2` with actual user IDs from your database

**Step 2: Get User IDs**
First, you need valid user IDs. Use this endpoint:
```
GET http://localhost:8080/api/users/{userId}
```

Or get user by email:
```
GET http://localhost:8080/api/users/by-email?email=test@example.com
```

**Step 3: Send the Request**
```
GET http://localhost:8080/api/chat/history/507f1f77bcf86cd799439011/507f1f77bcf86cd799439012
```

**Step 4: Expected Response**

If messages exist:
```json
{
    "success": true,
    "message": "Chat history retrieved",
    "data": [
        {
            "id": "msg-123",
            "senderId": "507f1f77bcf86cd799439011",
            "receiverId": "507f1f77bcf86cd799439012",
            "content": "Hello!",
            "messageType": "TEXT",
            "status": "READ",
            "timestamp": "2026-03-13T15:30:00"
        },
        {
            "id": "msg-124",
            "senderId": "507f1f77bcf86cd799439012",
            "receiverId": "507f1f77bcf86cd799439011",
            "content": "Hi there!",
            "messageType": "TEXT",
            "status": "READ",
            "timestamp": "2026-03-13T15:31:00"
        }
    ]
}
```

If no messages exist:
```json
{
    "success": true,
    "message": "Chat history retrieved",
    "data": []
}
```

**Step 5: Common Errors**

**Error 404 - User Not Found:**
```json
{
    "success": false,
    "message": "User not found"
}
```
→ Fix: Check that both user IDs exist in MongoDB

**Error 500 - Internal Server Error:**
→ Check application logs
→ Ensure MongoDB connection is working

---

## 🔌 Part 2: Testing WebSocket Endpoints (Alternative Tools)

Since Postman cannot test WebSocket `@MessageMapping` endpoints directly, use these alternatives:

---

### Option A: Using wscat (Recommended for Quick Testing)

#### Installation
```bash
npm install -g wscat
```

#### Step 1: Connect to WebSocket
```bash
wscat -c ws://localhost:8080/ws-chat
```

#### Step 2: Subscribe to Message Queue
Once connected, send a STOMP SUBSCRIBE frame:
```stomp
SUBSCRIBE
destination:/user/queue/messages
id:sub-0

```
(Press Enter twice to send)

#### Step 3: Send a Message
Send a STOMP SEND frame:
```stomp
SEND
destination:/app/chat.send
content-type:application/json

{"senderId":"user-123","receiverId":"user-456","content":"Hello from wscat!","messageType":"TEXT"}
```

#### Step 4: Receive Message
You should see:
```stomp
MESSAGE
destination:/user/queue/messages
content-type:application/json
message-id:msg-123

{"id":"msg-123","senderId":"user-123","receiverId":"user-456","content":"Hello from wscat!","messageType":"TEXT","status":"SENT","timestamp":"2026-03-13T15:30:00"}
```

#### Step 5: Update Message Status
```stomp
SEND
destination:/app/chat.status
content-type:application/json

{"messageId":"msg-123","status":"DELIVERED"}
```

---

### Option B: Using Postman WebSocket (Limited Support)

**Note:** Postman has WebSocket support but it's basic and doesn't fully support STOMP protocol.

#### Step 1: Create WebSocket Request
1. Click "New" → "WebSocket Request"
2. Enter URL: `ws://localhost:8080/ws-chat`
3. Click "Connect"

#### Step 2: Send STOMP CONNECT Frame
In the message box, type:
```stomp
CONNECT
accept-version:1.1,1.0
heart-beat:10000,10000


```
(Two blank lines at the end)

Expected response:
```stomp
CONNECTED
version:1.1
heart-beat:0,0

```

#### Step 3: Subscribe
```stomp
SUBSCRIBE
destination:/topic/messages
id:sub-0


```

#### Step 4: Send Message
```stomp
SEND
destination:/app/chat.send
content-type:application/json

{"senderId":"user-123","receiverId":"user-456","content":"Test message","messageType":"TEXT"}
```

---

### Option C: Using Browser DevTools (Best for Real Testing)

Create an HTML file to test in browser:

**File: `websocket-test.html`**
```html
<!DOCTYPE html>
<html>
<head>
    <title>VibeChat WebSocket Test</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
</head>
<body>
    <h1>VibeChat WebSocket Tester</h1>
    
    <div>
        <label>Sender ID:</label>
        <input type="text" id="senderId" value="user-123"><br><br>
        
        <label>Receiver ID:</label>
        <input type="text" id="receiverId" value="user-456"><br><br>
        
        <label>Message:</label>
        <textarea id="messageContent">Hello!</textarea><br><br>
        
        <button onclick="sendMessage()">Send Message</button>
        <button onclick="markAsRead()">Mark as Read</button>
    </div>
    
    <h2>Messages Received:</h2>
    <div id="messages"></div>
    
    <h2>Log:</h2>
    <div id="log"></div>

    <script>
        let stompClient = null;
        let lastMessageId = null;

        // Connect to WebSocket
        function connect() {
            log('Connecting to WebSocket...');
            
            const socket = new WebSocket('ws://localhost:8080/ws-chat');
            stompClient = Stomp.over(socket);
            
            stompClient.connect({}, () => {
                log('✅ Connected to WebSocket');
                
                // Subscribe to public topic
                stompClient.subscribe('/topic/messages', (message) => {
                    const msg = JSON.parse(message.body);
                    displayMessage(msg);
                    log('📢 Received broadcast: ' + msg.content);
                });
                
                // Subscribe to private queue
                stompClient.subscribe('/user/queue/messages', (message) => {
                    const msg = JSON.parse(message.body);
                    displayMessage(msg);
                    log('📬 Received private message: ' + msg.content);
                    
                    // Auto-send delivery receipt
                    sendDeliveryReceipt(msg.id);
                });
                
                // Subscribe to status updates
                stompClient.subscribe('/user/queue/status', (message) => {
                    const msg = JSON.parse(message.body);
                    log('📊 Status update: ' + msg.status);
                });
                
            }, (error) => {
                log('❌ Connection error: ' + error);
            });
        }

        // Send message
        function sendMessage() {
            const senderId = document.getElementById('senderId').value;
            const receiverId = document.getElementById('receiverId').value;
            const content = document.getElementById('messageContent').value;
            
            const message = {
                senderId: senderId,
                receiverId: receiverId,
                content: content,
                messageType: 'TEXT'
            };
            
            log('📤 Sending message: ' + content);
            stompClient.send('/app/chat.send', {}, JSON.stringify(message));
        }

        // Mark as read
        function markAsRead() {
            if (lastMessageId) {
                sendStatusUpdate(lastMessageId, 'READ');
            } else {
                log('No message to mark as read');
            }
        }

        // Send status update
        function sendStatusUpdate(messageId, status) {
            const statusUpdate = {
                messageId: messageId,
                status: status
            };
            
            log('📊 Sending status update: ' + status);
            stompClient.send('/app/chat.status', {}, JSON.stringify(statusUpdate));
        }

        // Display message
        function displayMessage(message) {
            lastMessageId = message.id;
            const messagesDiv = document.getElementById('messages');
            const msgDiv = document.createElement('div');
            msgDiv.innerHTML = `
                <strong>${message.senderId}</strong> → ${message.receiverId}: 
                ${message.content} 
                [${message.messageType}] 
                (${message.status})
                <button onclick="sendStatusUpdate('${message.id}', 'READ')">Mark Read</button>
            `;
            messagesDiv.appendChild(msgDiv);
        }

        // Send delivery receipt
        function sendDeliveryReceipt(messageId) {
            const statusUpdate = {
                messageId: messageId,
                status: 'DELIVERED'
            };
            
            log('📊 Sending delivery receipt for: ' + messageId);
            stompClient.send('/app/chat.status', {}, JSON.stringify(statusUpdate));
        }

        // Log helper
        function log(message) {
            const logDiv = document.getElementById('log');
            const logLine = document.createElement('div');
            logLine.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
            logDiv.appendChild(logLine);
        }

        // Connect on page load
        connect();
    </script>
</body>
</html>
```

**How to Use:**
1. Save the file
2. Open in browser (double-click the file)
3. Adjust sender/receiver IDs
4. Click "Send Message"
5. Watch messages appear in real-time

---

## 🧪 Complete Test Scenario

### Scenario: User Alice sends message to User Bob

#### Setup Data
```
Alice User ID: alice-123
Bob User ID: bob-456
```

#### Test Flow

**1. Get Chat History (Before)**
```bash
GET http://localhost:8080/api/chat/history/alice-123/bob-456
Expected: Empty array or old messages
```

**2. Send Message via WebSocket**
Using wscat:
```stomp
SEND
destination:/app/chat.send
content-type:application/json

{"senderId":"alice-123","receiverId":"bob-456","content":"Hi Bob!","messageType":"TEXT"}
```

**3. Verify Message Saved (via Postman)**
```bash
GET http://localhost:8080/api/chat/history/alice-123/bob-456
Expected: Array containing the new message
```

**4. Update Status via WebSocket**
```stomp
SEND
destination:/app/chat.status
content-type:application/json

{"messageId":"<message-id-from-response>","status":"READ"}
```

**5. Verify Status Updated (via Postman)**
```bash
GET http://localhost:8080/api/chat/history/alice-123/bob-456
Expected: Message status changed to READ
```

---

## 🐛 Troubleshooting

### Problem: Cannot connect to WebSocket
**Solution:**
- Check if application is running: `http://localhost:8080/actuator/health`
- Verify endpoint: `ws://localhost:8080/ws-chat` (not `http`)
- Check CORS settings in console logs

### Problem: Messages not appearing in database
**Solution:**
- Check MongoDB connection string in `application.properties`
- Verify MongoDB is running: `mongosh` or MongoDB Compass
- Check application logs for errors

### Problem: Getting 404 on REST endpoint
**Solution:**
- Verify user IDs exist in database
- Check URL format (should be `/api/chat/history/{id1}/{id2}`)
- Look at server logs for detailed error

---

## 📝 Quick Reference Card

| Tool | Use Case | Command/URL |
|------|----------|-------------|
| **Postman** | Get chat history | `GET /api/chat/history/{id1}/{id2}` |
| **wscat** | Send WebSocket message | `wscat -c ws://localhost:8080/ws-chat` |
| **Browser** | Full integration test | Open `websocket-test.html` |
| **MongoDB Compass** | Verify data | Collection: `messages` |

---

## ✅ Testing Checklist

- [ ] Application started successfully
- [ ] MongoDB is running and accessible
- [ ] At least 2 users exist in database
- [ ] Can retrieve user by ID
- [ ] Can connect to WebSocket endpoint
- [ ] Can send message via WebSocket
- [ ] Message appears in MongoDB
- [ ] Can retrieve chat history via REST API
- [ ] Can update message status
- [ ] Status persists in database

---

**Remember:** Use Postman for REST endpoints only. For WebSocket testing, use wscat or the browser-based HTML tester!
