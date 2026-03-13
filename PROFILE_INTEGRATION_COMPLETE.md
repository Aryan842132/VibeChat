# 📸 Complete Profile Picture Integration Guide

## ✅ New Endpoints Added!

I've created **3 new endpoints** to manage profile pictures with automatic integration in AuthResponse.

---

## 🚀 New API Endpoints

### 1. Upload & Update Profile Picture (Recommended)

**Endpoint:** `POST /api/users/{userId}/upload-profile-picture`

**Use Case:** Upload image and update user profile in ONE request!

**Request:**
```http
POST /api/users/69b2a4ea3309db162a586654/upload-profile-picture
Content-Type: multipart/form-data

file: [your-image.jpg]
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile picture updated successfully",
  "data": {
    "id": "69b2a4ea3309db162a586654",
    "username": "Aryan Nagardhankar",
    "email": "aryannagardhankar2020@gmail.com",
    "profilePicture": "https://vibechat-profile-pictures.s3.us-east-1.amazonaws.com/profile-pictures/abc-123.jpg",
    "status": "ONLINE",
    "lastSeen": "2026-03-12T11:35:20.784",
    "createdAt": "2026-03-12T11:35:06.234"
  }
}
```

✨ **Now the profilePicture field is populated!**

---

### 2. Update Profile Picture URL Directly

**Endpoint:** `PUT /api/users/{userId}/profile-picture`

**Request:**
```http
PUT /api/users/69b2a4ea3309db162a586654/profile-picture
Content-Type: application/json

{
  "profilePicture": "https://vibechat-profile-pictures.s3.us-east-1.amazonaws.com/profile-pictures/abc-123.jpg"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile picture updated successfully",
  "data": {
    "id": "69b2a4ea3309db162a586654",
    "username": "Aryan Nagardhankar",
    "email": "aryannagardhankar2020@gmail.com",
    "profilePicture": "https://vibechat-profile-pictures.s3.us-east-1.amazonaws.com/profile-pictures/abc-123.jpg",
    "status": "ONLINE",
    ...
  }
}
```

---

### 3. Remove Profile Picture

**Endpoint:** `DELETE /api/users/{userId}/profile-picture`

**Response:**
```json
{
  "success": true,
  "message": "Profile picture removed successfully",
  "data": {
    "id": "69b2a4ea3309db162a586654",
    "username": "Aryan Nagardhankar",
    "email": "aryannagardhankar2020@gmail.com",
    "profilePicture": null,
    "status": "ONLINE",
    ...
  }
}
```

---

## 🎯 Complete Testing Workflow

### Step 1: Test Upload Endpoint

**Using Postman:**

1. **Create Request:**
   - Method: POST
   - URL: `https://vibechat-production-24a1.up.railway.app/api/users/69b2a4ea3309db162a586654/upload-profile-picture`
   
2. **Body → form-data:**
   - Key: `file`
   - Type: File
   - Value: Select an image from your computer

3. **Send**

4. **Expected Response:**
   ```json
   {
     "success": true,
     "message": "Profile picture updated successfully",
     "data": {
       "id": "69b2a4ea3309db162a586654",
       "username": "Aryan Nagardhankar",
       "email": "aryannagardhankar2020@gmail.com",
       "profilePicture": "https://...s3...amazonaws.com/profile-pictures/uuid.jpg",
       ...
     }
   }
   ```

### Step 2: Verify User Data

**Fetch User:**
```http
GET https://vibechat-production-24a1.up.railway.app/api/users/69b2a4ea3309db162a586654
```

**New Response:**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": "69b2a4ea3309db162a586654",
    "username": "Aryan Nagardhankar",
    "email": "aryannagardhankar2020@gmail.com",
    "profilePicture": "https://vibechat-profile-pictures.s3.us-east-1.amazonaws.com/profile-pictures/uuid.jpg",  ← NOW POPULATED!
    "status": "ONLINE",
    ...
  }
}
```

### Step 3: Test Login Again

**Login:**
```http
POST https://vibechat-production-24a1.up.railway.app/api/users/login
Content-Type: application/json

{
  "email": "aryannagardhankar2020@gmail.com",
  "password": "yourpassword"
}
```

**AuthResponse will now include:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": "69b2a4ea3309db162a586654",
    "username": "Aryan Nagardhankar",
    "email": "aryannagardhankar2020@gmail.com",
    "profilePicture": "https://vibechat-profile-pictures.s3.us-east-1.amazonaws.com/profile-pictures/uuid.jpg",  ← INCLUDED HERE TOO!
    "status": "ONLINE",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

## 💻 Frontend Integration Example

### React Component:

```jsx
function ProfilePictureUpload({ userId }) {
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    setLoading(true);
    try {
      const res = await fetch(
        `https://vibechat-production-24a1.up.railway.app/api/users/${userId}/upload-profile-picture`,
        {
          method: 'POST',
          body: formData
        }
      );

      const data = await res.json();
      setPreview(data.data.profilePicture);
      alert('Profile picture updated!');
    } catch (error) {
      console.error('Upload failed:', error);
      alert('Upload failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <img 
        src={preview || '/default-avatar.png'} 
        alt="Profile" 
        style={{ width: 150, height: 150, borderRadius: '50%' }}
      />
      <input 
        type="file" 
        onChange={handleUpload} 
        accept="image/*"
        disabled={loading}
      />
      {loading && <p>Uploading...</p>}
    </div>
  );
}
```

### Using After Login:

```jsx
function App() {
  const [user, setUser] = useState(null);

  const handleLogin = async (email, password) => {
    const res = await fetch('/api/users/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    const data = await res.json();
    setUser(data.data); // Contains profilePicture URL!
    
    // Store in localStorage
    localStorage.setItem('user', JSON.stringify(data.data));
  };

  useEffect(() => {
    // Load user from localStorage
    const stored = localStorage.getItem('user');
    if (stored) {
      setUser(JSON.parse(stored));
    }
  }, []);

  return (
    <div>
      {user && (
        <div>
          <img 
            src={user.profilePicture || '/default-avatar.png'} 
            alt={user.username}
          />
          <h2>{user.username}</h2>
          <ProfilePictureUpload userId={user.id} />
        </div>
      )}
    </div>
  );
}
```

---

## 🔧 Deployment Steps

### 1. Deploy Code to Railway

```bash
cd d:\VibeChat
git add .
git commit -m "feat: Add profile picture upload endpoints"
git push origin main
```

### 2. Verify Environment Variables in Railway

Make sure these are set:
```env
AWS_ACCESS_KEY_ID=your_key
AWS_SECRET_ACCESS_KEY=your_secret
AWS_REGION=us-east-1
AWS_BUCKET_NAME=vibechat-profile-pictures
```

### 3. Test Immediately

After deployment completes (~2-3 minutes):

```bash
curl -X POST https://vibechat-production-24a1.up.railway.app/api/users/69b2a4ea3309db162a586654/upload-profile-picture \
  -F "file=@test-image.jpg"
```

---

## 🎨 S3 Bucket Configuration

### Make Sure CORS is Enabled:

Create `cors-config.json`:
```json
{
  "CORSRules": [
    {
      "AllowedHeaders": ["*"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
      "AllowedOrigins": ["*"],
      "ExposeHeaders": []
    }
  ]
}
```

Apply to your bucket:
```bash
aws s3api put-bucket-cors \
  --bucket vibechat-profile-pictures \
  --cors-configuration file://cors-config.json
```

---

## ✅ Success Checklist

- [ ] Code deployed to Railway
- [ ] AWS credentials configured in Railway
- [ ] S3 bucket created and accessible
- [ ] Test upload endpoint with Postman
- [ ] Verify profilePicture appears in user response
- [ ] Test login endpoint includes profilePicture
- [ ] Frontend displays profile pictures correctly

---

## 🐛 Troubleshooting

### Profile Picture Still Null?

1. **Check S3 upload worked** - Visit the S3 URL in browser
2. **Check MongoDB** - Verify document has profilePicture field
3. **Clear cache** - Browser/app might be caching old user data

### Upload Fails?

1. **Check file size** - Must be < 5MB
2. **Check file type** - Only JPEG, PNG, GIF, WebP allowed
3. **Check AWS credentials** - Verify in Railway dashboard
4. **Check S3 bucket exists** - Create if needed

### 403 Error on Upload?

The upload endpoint is public. If you want authentication:
- Let me know and I'll update SecurityConfig
- Or add JWT token header: `Authorization: Bearer your_token`

---

**🎉 You're all set! Upload a profile picture and see it appear in all responses!** 🚀
