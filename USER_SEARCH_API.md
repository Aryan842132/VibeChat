# User Search API Documentation

## 🔍 New Search Endpoint

I've created a powerful user search API that allows you to find users by their username for chat purposes.

---

## 📋 API Endpoint Details

### **Search Users by Name**

```http
GET /api/users/search?query={searchTerm}&currentUserId={userId}
```

---

## 🔧 Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | String | **Yes** | Search term (username to search for) |
| `currentUserId` | String | No | ID of the current user (to exclude from results) |

---

## 📊 Response Format

### Success Response (200 OK)

```json
{
    "success": true,
    "message": "Search completed successfully. Found 3 user(s).",
    "data": [
        {
            "id": "69b3e8bef3d78e3460382cea",
            "username": "john_doe",
            "email": "john@example.com",
            "profilePicture": "https://bucket.s3.region.amazonaws.com/profile-pictures/user1.jpg",
            "status": "ONLINE",
            "lastSeen": "2026-03-16T16:00:00",
            "createdAt": "2026-03-10T10:00:00"
        },
        {
            "id": "69b2a4ea3309db162a586654",
            "username": "john_smith",
            "email": "smith@example.com",
            "profilePicture": null,
            "status": "OFFLINE",
            "lastSeen": "2026-03-15T14:30:00",
            "createdAt": "2026-03-08T09:00:00"
        }
    ]
}
```

### Empty Results (200 OK)

```json
{
    "success": true,
    "message": "Search completed successfully. Found 0 user(s).",
    "data": []
}
```

### Error Response (500 Internal Server Error)

```json
{
    "success": false,
    "message": "Search failed: Invalid query parameter"
}
```

---

## 🎯 Features

### ✅ Case-Insensitive Search
```javascript
// All these return the same results:
GET /api/users/search?query=John
GET /api/users/search?query=john
GET /api/users/search?query=JOHN
```

### ✅ Partial Match Search
```javascript
// Search "joh" matches "john", "john_doe", "johnny", etc.
GET /api/users/search?query=joh
```

### ✅ Excludes Current User
```javascript
// If currentUserId is provided, that user won't appear in results
GET /api/users/search?query=john&currentUserId=69b3e8bef3d78e3460382cea
// Result: Won't include user with ID 69b3e8bef3d78e3460382cea
```

### ✅ Real-Time Status
Returns user's current online/offline status for presence awareness

---

## 💡 Usage Examples

### Frontend JavaScript Example

```javascript
// Search for users
async function searchUsers(searchTerm, currentUserId) {
    try {
        const response = await fetch(
            `https://vibechat-production-24a1.up.railway.app/api/users/search?query=${encodeURIComponent(searchTerm)}&currentUserId=${currentUserId}`
        );
        
        const result = await response.json();
        
        if (result.success) {
            console.log(`Found ${result.data.length} users`);
            return result.data; // Array of user objects
        } else {
            console.error('Search failed:', result.message);
            return [];
        }
    } catch (error) {
        console.error('Search error:', error);
        return [];
    }
}

// Example usage:
const users = await searchUsers('john', 'user-123');
console.log(users);
```

---

### React Component Example

```jsx
import React, { useState, useEffect } from 'react';

function UserSearch({ currentUserId }) {
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        const delayDebounce = setTimeout(async () => {
            if (searchQuery.trim()) {
                setIsLoading(true);
                
                try {
                    const response = await fetch(
                        `/api/users/search?query=${encodeURIComponent(searchQuery)}&currentUserId=${currentUserId}`
                    );
                    const result = await response.json();
                    
                    if (result.success) {
                        setSearchResults(result.data);
                    }
                } catch (error) {
                    console.error('Search failed:', error);
                } finally {
                    setIsLoading(false);
                }
            } else {
                setSearchResults([]);
            }
        }, 300); // 300ms debounce

        return () => clearTimeout(delayDebounce);
    }, [searchQuery, currentUserId]);

    return (
        <div>
            <input
                type="text"
                placeholder="Search users..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
            />
            
            {isLoading && <p>Loading...</p>}
            
            <ul>
                {searchResults.map(user => (
                    <li key={user.id}>
                        <img 
                            src={user.profilePicture || '/default-avatar.png'} 
                            alt={user.username}
                            width="40"
                        />
                        <span>{user.username}</span>
                        <span className={user.status === 'ONLINE' ? 'online' : 'offline'}>
                            {user.status}
                        </span>
                        <button onClick={() => startChat(user.id)}>
                            Chat
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
}
```

---

### Vue.js Component Example

```vue
<template>
  <div class="user-search">
    <input 
      v-model="searchQuery" 
      placeholder="Search users..."
      @input="handleSearch"
    />
    
    <div v-if="loading">Loading...</div>
    
    <div v-else class="results">
      <div v-for="user in results" :key="user.id" class="user-item">
        <img :src="user.profilePicture || '/default.png'" :alt="user.username" />
        <span>{{ user.username }}</span>
        <span :class="user.status">{{ user.status }}</span>
        <button @click="startChat(user.id)">Chat</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['currentUserId'],
  data() {
    return {
      searchQuery: '',
      results: [],
      loading: false
    };
  },
  methods: {
    async handleSearch() {
      if (!this.searchQuery.trim()) {
        this.results = [];
        return;
      }
      
      this.loading = true;
      
      try {
        const response = await fetch(
          `/api/users/search?query=${encodeURIComponent(this.searchQuery)}&currentUserId=${this.currentUserId}`
        );
        const result = await response.json();
        
        if (result.success) {
          this.results = result.data;
        }
      } catch (error) {
        console.error('Search failed:', error);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
```

---

## 🧪 Testing with Postman

### Test 1: Basic Search

```http
GET http://localhost:8080/api/users/search?query=john
```

**Expected:**
```json
{
    "success": true,
    "message": "Search completed successfully. Found 2 user(s).",
    "data": [...]
}
```

---

### Test 2: Search with Current User Exclusion

```http
GET http://localhost:8080/api/users/search?query=john&currentUserId=69b3e8bef3d78e3460382cea
```

**Expected:** Same results but excludes the current user

---

### Test 3: Empty Query

```http
GET http://localhost:8080/api/users/search?query=
```

**Expected:**
```json
{
    "success": true,
    "message": "Search completed successfully. Found 0 user(s).",
    "data": []
}
```

---

### Test 4: Case Insensitive Search

```http
GET http://localhost:8080/api/users/search?query=JOHN
```

**Expected:** Same results as searching for "john"

---

## 🔍 Search Algorithm Details

### How It Works

1. **Input Validation**: Checks if query is not null or empty
2. **Database Query**: Uses MongoDB regex search (case-insensitive)
3. **Filtering**: Removes current user from results
4. **Mapping**: Converts User entities to UserResponse DTOs
5. **Return**: Returns array of matching users

### Performance Characteristics

- **Time Complexity**: O(n) where n = number of users in database
- **Index**: Automatically indexed on `username` field by MongoDB
- **Optimization**: Consider adding text index for large datasets (>10k users)

---

## 🎨 UI/UX Best Practices

### Debounce Search Input

```javascript
// Wait 300ms after user stops typing before searching
const debouncedSearch = debounce((query) => {
    searchUsers(query);
}, 300);

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}
```

### Show Loading State

```javascript
// Always show loading indicator during search
setLoading(true);
await searchUsers(query);
setLoading(false);
```

### Display User Status

```jsx
<span className={`status-badge ${user.status.toLowerCase()}`}>
    {user.status === 'ONLINE' ? '🟢 Online' : '⚫ Offline'}
</span>
```

---

## 📊 Complete Search Flow

```
User types in search box
         ↓
Frontend debounces input (300ms)
         ↓
GET /api/users/search?query={term}&currentUserId={id}
         ↓
Backend validates query
         ↓
MongoDB searches: findByUsernameContainingIgnoreCase(query)
         ↓
Filter out current user
         ↓
Convert to UserResponse DTOs
         ↓
Return JSON array
         ↓
Frontend displays results
         ↓
User clicks "Chat" → Start conversation
```

---

## 🔐 Security Considerations

### Current Implementation
- ✅ Public endpoint (no authentication required)
- ✅ Excludes current user from results
- ✅ Prevents empty queries

### Production Recommendations
1. Add JWT authentication
2. Rate limit search requests (prevent abuse)
3. Limit maximum results (e.g., max 50 users)
4. Log search activity for analytics

---

## ⚡ Advanced Features (Future Enhancements)

### 1. Pagination
```javascript
GET /api/users/search?query=john&page=1&limit=20
```

### 2. Sort Options
```javascript
GET /api/users/search?query=john&sort=username&order=asc
```

### 3. Filter by Status
```javascript
GET /api/users/search?query=john&status=ONLINE
```

### 4. Search Email Too
```javascript
// Backend enhancement to search both username and email
public List<UserResponse> searchUsers(String query, String currentUserId) {
    List<User> byUsername = userRepository.findByUsernameContainingIgnoreCase(query);
    List<User> byEmail = userRepository.findByEmailContainingIgnoreCase(query);
    // Combine and deduplicate
}
```

---

## 📝 Integration with Chat Flow

### Complete User Search & Chat Flow

```javascript
// 1. Search for user
const results = await searchUsers('john', currentUserId);

// 2. User selects someone
const selectedUser = results[0];

// 3. Get or create conversation
const convResponse = await fetch(
    `/api/conversations/between/${currentUserId}/${selectedUser.id}`
);
const { data: conversation } = await convResponse.json();

// 4. Connect to WebSocket and send message
stompClient.publish({
    destination: '/app/chat.send',
    body: JSON.stringify({
        senderId: currentUserId,
        receiverId: selectedUser.id,
        content: 'Hey!',
        messageType: 'TEXT'
    })
});
```

---

## ✅ Checklist for Integration

- [ ] Implement search input UI component
- [ ] Add debounce logic (300ms recommended)
- [ ] Display loading state during search
- [ ] Show user avatar, username, and status
- [ ] Handle empty results gracefully
- [ ] Add "Chat" button for each result
- [ ] Navigate to chat screen on selection
- [ ] Cache recent search results (optional)
- [ ] Clear results when input cleared

---

## 📞 Support Resources

- **Full API Docs**: [API_README_FOR_FRONTEND.md](./API_README_FOR_FRONTEND.md)
- **CORS Configuration**: [CORS_FIX_GUIDE.md](./CORS_FIX_GUIDE.md)
- **WebSocket Guide**: [WEBSOCKET_INTEGRATION_GUIDE.md](./WEBSOCKET_INTEGRATION_GUIDE.md)

---

**The search API is ready to use! Just call the endpoint with your search query.** 🚀
