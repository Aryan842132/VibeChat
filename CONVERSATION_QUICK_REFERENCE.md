# Conversation Management - Quick Reference Card

## 🎯 New Endpoints Summary

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/conversations/create` | Create new conversation |
| GET | `/api/conversations/user/{userId}` | Get user's chat list |
| GET | `/api/conversations/{conversationId}` | Get specific conversation |
| GET | `/api/conversations/between/{userId1}/{userId2}` | Find or create 1-on-1 chat |
| DELETE | `/api/conversations/{conversationId}` | Delete conversation |

---

## 💡 Common Use Cases

### 1. Load Chat List (Sidebar)
```javascript
// GET /api/conversations/user/{userId}
const response = await fetch(`/api/conversations/user/${currentUserId}`);
const { data: conversations } = await response.json();

// Result: Array of conversations sorted by last activity
[
  {
    "id": "conv-123",
    "participants": ["user-a", "user-b"],
    "lastMessage": {
      "content": "See you tomorrow!",
      "senderId": "user-b",
      "timestamp": "2026-03-16T15:30:00"
    },
    "updatedAt": "2026-03-16T15:30:00"
  }
]
```

---

### 2. Start First Message to Someone
```javascript
// Step 1: Get or create conversation
const convResponse = await fetch(
  `/api/conversations/between/${currentUserId}/${recipientId}`
);
const { data: conversation } = await convResponse.json();

// Step 2: Send message via WebSocket
stompClient.publish({
  destination: '/app/chat.send',
  body: JSON.stringify({
    senderId: currentUserId,
    receiverId: recipientId,
    content: 'Hey!',
    messageType: 'TEXT'
  })
});

// Backend automatically:
// - Saves message
// - Creates/updates conversation
// - Updates lastMessage & updatedAt
```

---

### 3. Display Last Message Preview
```javascript
conversations.forEach(conv => {
  const otherUserId = conv.participants.find(id => id !== currentUserId);
  const lastMsgPreview = conv.lastMessage?.content || 'Start conversation';
  const timeAgo = formatTimeAgo(conv.updatedAt);
  
  renderChatItem(otherUserId, lastMsgPreview, timeAgo);
});
```

---

### 4. Check If Conversation Exists
```javascript
// Before messaging, check if conversation exists
const exists = await fetch(
  `/api/conversations/between/${user1}/${user2}`
);

if (exists.ok) {
  console.log('Conversation already exists');
} else {
  console.log('Will create new conversation on first message');
}
```

---

## 🔄 Automatic Behaviors

### ✅ What Happens Automatically

**When First Message is Sent:**
1. Message saved to MongoDB
2. Conversation created with both users as participants
3. `lastMessage` set to the new message
4. `createdAt` and `updatedAt` timestamps set

**When Subsequent Messages are Sent:**
1. Message saved to MongoDB
2. Existing conversation found
3. `lastMessage` updated to new message
4. `updatedAt` timestamp updated

**No manual conversation management needed!**

---

## 📊 Data Structure

### Conversation Object
```typescript
interface Conversation {
  id: string;
  participants: string[]; // User IDs
  lastMessage?: {
    id: string;
    senderId: string;
    content: string;
    messageType: 'TEXT' | 'IMAGE' | 'VIDEO';
    timestamp: string;
  };
  createdAt: string; // ISO datetime
  updatedAt: string; // ISO datetime
}
```

---

## ⚡ Performance Tips

### Efficient Queries
```javascript
// ✅ Good: Get all conversations for user (indexed, sorted)
GET /api/conversations/user/{userId}

// ✅ Good: Find conversation between users (indexed lookup)
GET /api/conversations/between/{userId1}/{userId2}

// ❌ Avoid: Don't manually search through messages
// Let the conversation system handle it
```

### Caching Strategy
```javascript
// Cache conversation list, refresh on:
// - New message sent/received
// - App focus/refresh
// - Pull-to-refresh gesture

// Cache individual conversation by ID
// Invalidate when deleted
```

---

## 🔗 Integration with WebSocket

### Complete Flow Example

```javascript
// 1. Load existing conversations
loadConversations();

// 2. Listen for new messages
stompClient.subscribe('/user/queue/messages', (message) => {
  const msg = JSON.parse(message.body);
  
  // Refresh conversation list to show new lastMessage
  refreshConversations();
  
  // Or update UI in real-time
  updateConversationInList(msg.senderId, msg.content);
});

// 3. Send message
function sendMessage(recipientId, content) {
  stompClient.publish({
    destination: '/app/chat.send',
    body: JSON.stringify({
      senderId: currentUserId,
      receiverId: recipientId,
      content: content,
      messageType: 'TEXT'
    })
  });
  // Backend handles conversation update automatically
}
```

---

## 🧪 Testing with Postman

### Test 1: Get User's Conversations
```http
GET http://localhost:8080/api/conversations/user/69b3e8bef3d78e3460382cea
```

**Expected:** Array of conversations sorted by recency

---

### Test 2: Find/Create Conversation
```http
GET http://localhost:8080/api/conversations/between/69b3e8bef3d78e3460382cea/69b2a4ea3309db162a586654
```

**Expected:** Returns existing conversation or creates new one

---

### Test 3: Send Message (Via WebSocket)
Use wscat or browser:
```javascript
stompClient.publish({
  destination: '/app/chat.send',
  body: JSON.stringify({
    senderId: 'user-1',
    receiverId: 'user-2',
    content: 'Test',
    messageType: 'TEXT'
  })
});
```

Then verify:
```http
GET http://localhost:8080/api/conversations/user/user-1
```

Should show new conversation with lastMessage.

---

## 🎨 UI Implementation Suggestions

### Chat List Component
```jsx
function ChatList({ currentUserId }) {
  const [conversations, setConversations] = useState([]);
  
  useEffect(() => {
    loadConversations();
    
    // Real-time update on new message
    const subscription = subscribeToNewMessages((msg) => {
      updateConversation(msg);
    });
    
    return () => subscription.unsubscribe();
  }, []);
  
  const loadConversations = async () => {
    const response = await fetch(`/api/conversations/user/${currentUserId}`);
    const { data } = await response.json();
    setConversations(data);
  };
  
  return (
    <div>
      {conversations.map(conv => (
        <ChatItem
          key={conv.id}
          conversation={conv}
          currentUserId={currentUserId}
        />
      ))}
    </div>
  );
}
```

---

## ✅ Checklist for Frontend Integration

- [ ] Implement conversation list view
- [ ] Add real-time updates on new messages
- [ ] Show last message preview
- [ ] Display "time ago" from `updatedAt`
- [ ] Handle empty state (no conversations)
- [ ] Auto-create conversation on first message
- [ ] Cache conversations locally
- [ ] Refresh on pull-to-refresh
- [ ] Navigate to chat detail on click

---

## 📞 Support Resources

- **Full API Docs**: [API_README_FOR_FRONTEND.md](./API_README_FOR_FRONTEND.md)
- **Implementation Details**: [CONVERSATION_IMPLEMENTATION_SUMMARY.md](./CONVERSATION_IMPLEMENTATION_SUMMARY.md)
- **WebSocket Guide**: [WEBSOCKET_INTEGRATION_GUIDE.md](./WEBSOCKET_INTEGRATION_GUIDE.md)
- **Testing Guide**: [POSTMAN_TESTING_GUIDE.md](./POSTMAN_TESTING_GUIDE.md)

---

**Quick Start**: Just call the endpoints - conversation management is fully automatic! 🚀
