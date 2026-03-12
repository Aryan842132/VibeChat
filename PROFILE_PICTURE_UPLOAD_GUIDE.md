# 🖼️ Profile Picture Upload Guide - AWS S3

## ✅ Implementation Complete!

Your VibeChat app now supports **profile picture uploads** to AWS S3 with automatic integration in AuthResponse.

---

## 📁 Files Created

1. **S3Config.java** - AWS S3 client configuration
2. **S3Service.java** - File upload/delete service
3. **FileUploadController.java** - REST endpoints for uploads
4. **FileUploadResponse.java** - Upload response DTO
5. **Updated UserService.java** - Profile picture management

---

## 🔧 Environment Variables (Railway)

Add these to your Railway project:

```env
# AWS S3 Credentials
AWS_ACCESS_KEY_ID=your_access_key_here
AWS_SECRET_ACCESS_KEY=your_secret_key_here
AWS_REGION=us-east-1
AWS_BUCKET_NAME=vibechat-profile-pictures
```

---

## 📡 API Endpoints

### 1. Upload Profile Picture

**Endpoint:** `POST /api/upload/profile-picture`

**Request Type:** `multipart/form-data`

**Parameters:**
- `file` (required) - Image file (JPEG, PNG, GIF, WebP, max 5MB)

**Example (cURL):**
```bash
curl -X POST http://localhost:8080/api/upload/profile-picture \
  -F "file=@/path/to/profile.jpg"
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Upload successful",
  "data": {
    "fileName": "profile.jpg",
    "fileUrl": "https://vibechat-profile-pictures.s3.us-east-1.amazonaws.com/profile-pictures/abc123-def456.jpg",
    "fileType": "image/jpeg",
    "fileSize": 245678,
    "message": "Profile picture uploaded successfully"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Only JPEG, PNG, GIF, and WebP images are allowed",
  "data": null
}
```

---

### 2. Get Pre-signed Upload URL (Advanced)

**Endpoint:** `GET /api/upload/presigned-url?fileName=image.jpg`

**Use Case:** Direct browser-to-S3 uploads (bypasses your server)

**Response:**
```json
{
  "success": true,
  "message": "Pre-signed URL generated",
  "data": "https://vibechat-profile-pictures.s3.amazonaws.com/profile-pictures/image.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&..."
}
```

---

## 🔄 Integration with AuthResponse

The profile picture URL is automatically included in **AuthResponse** after login/register:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": "69b29a889cfffb5193cf9735",
    "username": "johndoe",
    "email": "john@example.com",
    "profilePicture": "https://vibechat-profile-pictures.s3.us-east-1.amazonaws.com/profile-pictures/abc123.jpg",
    "status": "ONLINE",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

## 🚀 Usage Flow

### Option 1: Upload → Update User Profile

```javascript
// Step 1: Upload image
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const uploadRes = await fetch('http://localhost:8080/api/upload/profile-picture', {
  method: 'POST',
  body: formData
});

const uploadData = await uploadRes.json();
const imageUrl = uploadData.data.fileUrl;

// Step 2: Update user profile (you'll need to add this endpoint)
await fetch(`http://localhost:8080/api/users/${userId}/profile-picture`, {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({ profilePicture: imageUrl })
});
```

### Option 2: Direct Frontend Display

```javascript
// After login
const loginRes = await fetch('/api/users/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});

const userData = await loginRes.json();
const profilePicUrl = userData.data.profilePicture;

// Display in UI
document.getElementById('profilePic').src = profilePicUrl || 'default-avatar.png';
```

---

## 📝 Testing with Postman

### Upload Profile Picture:

1. **Method:** POST
2. **URL:** `http://localhost:8080/api/upload/profile-picture`
3. **Headers:** None (Postman sets Content-Type automatically)
4. **Body:** 
   - Select `form-data`
   - Key: `file` (set type to File)
   - Value: Choose an image file
5. **Send**

Expected Response:
```json
{
  "success": true,
  "message": "Upload successful",
  "data": {
    "fileName": "my-photo.jpg",
    "fileUrl": "https://...s3...amazonaws.com/profile-pictures/uuid.jpg",
    "fileType": "image/jpeg",
    "fileSize": 123456
  }
}
```

---

## 🛡️ Security Features

✅ **File Type Validation** - Only images (JPEG, PNG, GIF, WebP)
✅ **Size Limit** - Maximum 5MB per file
✅ **Unique Filenames** - UUID-based naming prevents collisions
✅ **Organized Storage** - All files in `profile-pictures/` folder
✅ **Automatic Cleanup** - Old pictures deleted when updating

---

## 🎯 Next Steps

### Add Profile Update Endpoint:

```java
@PutMapping("/{userId}/profile-picture")
public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(
        @PathVariable String userId,
        @RequestBody Map<String, String> body) {
    
    String profilePictureUrl = body.get("profilePicture");
    UserResponse updatedUser = userService.updateProfilePicture(userId, profilePictureUrl);
    
    return ResponseEntity.ok(ApiResponse.success("Profile picture updated", updatedUser));
}
```

### Or Combine Upload + Update:

```java
@PostMapping("/{userId}/upload-profile-picture")
public ResponseEntity<ApiResponse<UserResponse>> uploadAndUpdateProfilePicture(
        @PathVariable String userId,
        @RequestParam MultipartFile file) throws IOException {
    
    // Upload to S3
    String imageUrl = s3Service.uploadProfilePicture(file);
    
    // Update user
    UserResponse updatedUser = userService.updateProfilePicture(userId, imageUrl);
    
    return ResponseEntity.ok(ApiResponse.success("Profile picture updated", updatedUser));
}
```

---

## ☁️ AWS S3 Bucket Setup

### 1. Create Bucket

```bash
aws s3 mb s3://vibechat-profile-pictures --region us-east-1
```

### 2. Configure CORS (Required for browser uploads)

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

Apply CORS:
```bash
aws s3api put-bucket-cors \
  --bucket vibechat-profile-pictures \
  --cors-configuration file://cors-config.json
```

### 3. Set Public Read (Optional)

If you want public URLs:
```bash
aws s3api put-bucket-acl \
  --bucket vibechat-profile-pictures \
  --acl public-read
```

Or use CloudFront for secure distribution.

---

## 📊 File Structure in S3

```
vibechat-profile-pictures/
└── profile-pictures/
    ├── 550e8400-e29b-41d4-a716-446655440000.jpg
    ├── 6ba7b810-9dad-11d1-80b4-00c04fd430c8.png
    └── 6ba7b812-9dad-11d1-80b4-00c04fd430c8.webp
```

---

## 🎨 Frontend Integration Example

### React Component:

```jsx
function ProfilePictureUpload({ userId, token }) {
  const [preview, setPreview] = useState(null);

  const handleUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    const res = await fetch('/api/upload/profile-picture', {
      method: 'POST',
      body: formData
    });

    const data = await res.json();
    setPreview(data.data.fileUrl);
  };

  return (
    <div>
      <img src={preview} alt="Profile" />
      <input type="file" onChange={handleUpload} accept="image/*" />
    </div>
  );
}
```

---

## ✅ Deployment Checklist

- [ ] Add AWS credentials to Railway environment variables
- [ ] Create S3 bucket in AWS console
- [ ] Configure S3 bucket CORS settings
- [ ] Test upload endpoint locally
- [ ] Deploy to Railway
- [ ] Test upload in production
- [ ] Verify images load correctly from S3

---

## 🐛 Troubleshooting

### Error: "Access Denied"
- Check AWS credentials are correct
- Verify S3 bucket policy allows uploads
- Check IAM user has S3 permissions

### Error: "CORS Policy Blocked"
- Configure CORS on S3 bucket (see above)
- Ensure `AllowedOrigins` includes your frontend domain

### Images Not Loading
- Check S3 bucket is public or using CloudFront
- Verify image URL is correct
- Check browser console for errors

---

**🎉 Your profile picture upload system is ready!**

Deploy to Railway and start uploading! 🚀
