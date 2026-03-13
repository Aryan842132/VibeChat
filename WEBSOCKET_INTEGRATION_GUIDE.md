# WebSocket Integration Guide for Frontend (socket.io)

## Overview
This guide explains how to integrate the VibeChat WebSocket backend with a socket.io frontend.

## Backend Configuration

### WebSocket Endpoint
- **URL**: `ws://localhost:8080/ws-chat` (development)
- **Protocol**: STOMP over WebSocket (pure WebSocket, no SockJS)
- **Compatible with**: socket.io client

## Message Destinations

### Application Prefix (Sending Messages)
All messages sent from client to server must use `/app` prefix:
- Send message: `/app/chat.send`
- Private message: `/app/chat.private.{userId}`
- Update status: `/app/chat.status`

### Message Broker (Receiving Messages)
Subscribe to these destinations to receive messages:

#### Public Topics (Broadcast)
- `/topic/messages` - Receive all broadcast messages

#### Private Queues (User-specific)
- `/user/queue/messages` - Receive private messages
- `/user/queue/private-message` - Receive direct private messages
- `/user/queue/status` - Receive message status updates

## Frontend Integration Example

### Using socket.io Client

```javascript
import io from 'socket.io-client';

// Connect to WebSocket endpoint
const socket = io('http://localhost:8080', {
  transports: ['websocket'],
  forceNew: true
});

// Subscribe to topics after connection
socket.on('connect', () => {
  console.log('Connected to WebSocket');
  
  // Subscribe to public topic
  socket.subscribe('/topic/messages', (message) => {
    console.log('Received broadcast message:', message);
  });
  
  // Subscribe to private queue
  socket.subscribe('/user/queue/messages', (message) => {
    console.log('Received private message:', message);
  });
});

// Send a message
function sendMessage(senderId, receiverId, content, messageType = 'TEXT') {
  const message = {
    senderId: senderId,
    receiverId: receiverId,
    content: content,
    messageType: messageType, // TEXT, IMAGE, VIDEO
    timestamp: new Date().toISOString()
  };
  
  socket.emit('send', '/app/chat.send', message);
}

// Send private message to specific user
function sendPrivateMessage(senderId, receiverId, content) {
  const message = {
    senderId: senderId,
    content: content,
    messageType: 'TEXT'
  };
  
  socket.emit('send', `/app/chat.private.${receiverId}`, message);
}

// Update message status
function updateMessageStatus(messageId, status) {
  const statusUpdate = {
    messageId: messageId,
    status: status // SENT, DELIVERED, READ
  };
  
  socket.emit('send', '/app/chat.status', statusUpdate);
}
```

### Using Pure WebSocket with STOMP.js (Recommended)

```javascript
import { Client } from '@stomp/stompjs';

// Create STOMP client
const stompClient = new Client({
  brokerURL: 'ws://localhost:8080/ws-chat',
  reconnectDelay: 5000,
  debug: function(str) {
    console.log(str);
  },
  onConnect: () => {
    console.log('Connected to STOMP broker');
    
    // Subscribe to public topic
    stompClient.subscribe('/topic/messages', (message) => {
      const msg = JSON.parse(message.body);
      console.log('Received broadcast message:', msg);
    });
    
    // Subscribe to private queue (automatically prefixed with /user)
    stompClient.subscribe('/user/queue/messages', (message) => {
      const msg = JSON.parse(message.body);
      console.log('Received private message:', msg);
    });
    
    // Subscribe to status updates
    stompClient.subscribe('/user/queue/status', (message) => {
      const msg = JSON.parse(message.body);
      console.log('Message status updated:', msg);
    });
  },
  onStompError: (frame) => {
    console.error('STOMP error:', frame);
  }
});

// Activate connection
stompClient.activate();

// Send a message
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

// Send private message
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

// Update message status
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

// Disconnect
function disconnect() {
  if (stompClient.active) {
    stompClient.deactivate();
    console.log('Disconnected from WebSocket');
  }
}
```

## Message Format

### ChatMessage Object
```json
{
  "id": "message-id-here",
  "senderId": "user-id-sender",
  "receiverId": "user-id-receiver",
  "content": "Hello, World!",
  "messageType": "TEXT",
  "status": "SENT",
  "timestamp": "2026-03-13T14:30:00"
}
```

### MessageType Enum
- `TEXT` - Text message
- `IMAGE` - Image message (contains URL to image)
- `VIDEO` - Video message (contains URL to video)

### MessageStatus Enum
- `SENT` - Message sent to server
- `DELIVERED` - Message delivered to recipient
- `READ` - Message read by recipient

## REST API Endpoints

### Get Chat History
```http
GET /api/chat/history/{userId1}/{userId2}
```

Response:
```json
{
  "success": true,
  "message": "Chat history retrieved",
  "data": [
    {
      "id": "...",
      "senderId": "...",
      "receiverId": "...",
      "content": "...",
      "messageType": "TEXT",
      "status": "READ",
      "timestamp": "2026-03-13T14:30:00"
    }
  ]
}
```

## Complete Flow Example

### 1. User A sends message to User B

```javascript
// User A's client
sendMessage(
  'user-a-id',
  'user-b-id',
  'Hello from User A!',
  'TEXT'
);
```

**Backend processes:**
1. Receives message at `/app/chat.send`
2. Saves to MongoDB
3. Broadcasts to `/topic/messages`
4. Sends to User B's private queue `/user/queue/messages`

### 2. User B receives message

```javascript
// User B's client (already subscribed)
stompClient.subscribe('/user/queue/messages', (message) => {
  const msg = JSON.parse(message.body);
  // Display message in chat UI
  displayMessage(msg);
  
  // Mark as delivered
  updateMessageStatus(msg.id, 'DELIVERED');
});
```

### 3. User B reads message

```javascript
// When User B opens the chat
updateMessageStatus(messageId, 'READ');

// User A receives status update
stompClient.subscribe('/user/queue/status', (message) => {
  const msg = JSON.parse(message.body);
  if (msg.status === 'READ') {
    updateMessageUI(msg.id, 'read');
  }
});
```

## Important Notes

1. **No SockJS**: The backend uses pure WebSocket only. Do not use SockJS fallback.
2. **STOMP Protocol**: Use STOMP.js or similar library for best compatibility.
3. **Authentication**: Implement JWT token authentication before connecting to WebSocket (recommended).
4. **Reconnection**: Handle reconnection logic in your frontend code.
5. **Error Handling**: Always handle connection errors gracefully.

## Troubleshooting

### Connection Issues
- Ensure WebSocket endpoint is accessible: `ws://localhost:8080/ws-chat`
- Check CORS settings if connecting from different origin
- Verify firewall allows WebSocket connections

### Message Not Received
- Verify correct destination format (`/app/chat.send`)
- Ensure subscription is active before sending
- Check message format matches expected structure

### Status Not Updating
- Use correct message ID from saved message
- Ensure status values match enum (SENT, DELIVERED, READ)

## Testing with WebSocket Clients

You can test the WebSocket endpoint using:
- **Postman WebSocket**: Connect and send messages
- **wscat**: Command-line WebSocket client
- **Browser DevTools**: Network tab > WS filter

Example wscat command:
```bash
wscat -c ws://localhost:8080/ws-chat
```

Then subscribe and publish:
```
SUBSCRIBE
destination:/topic/messages

SEND
destination:/app/chat.send
content-type:application/json

{"senderId":"user1","receiverId":"user2","content":"Test","messageType":"TEXT"}
```
