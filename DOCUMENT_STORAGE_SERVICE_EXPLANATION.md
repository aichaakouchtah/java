# 📚 DocumentStorageService.java - Role and Purpose

## 🎯 Main Role

**`DocumentStorageService`** is a **file management service** that handles all physical storage operations for PDF books and digital documents in your library system.

Think of it as the **"librarian's filing cabinet"** - it manages where books are stored, how they're organized, and how to retrieve them.

---

## 🔧 What It Does

### 1. **File Storage Management**
- Saves uploaded PDF/EPUB files to the filesystem
- Organizes files in a structured directory (by year/month)
- Generates unique filenames to prevent conflicts
- Creates necessary directories automatically

### 2. **File Retrieval**
- Retrieves documents when users need to read/download them
- Validates file paths for security
- Checks if files exist before serving

### 3. **File Security**
- Ensures files are stored **outside** the project directory (security)
- Validates all file paths to prevent path traversal attacks
- Prevents unauthorized file access

### 4. **File Integrity**
- Calculates SHA-256 hash for each file (to verify file hasn't been corrupted)
- Tracks file size and metadata

### 5. **File Deletion**
- Safely deletes documents when removed from library
- Handles errors gracefully

---

## 🏗️ How It Fits in Your Architecture

```
┌─────────────────────────────────────────────────┐
│           User Interface (JavaFX)                │
│  (User clicks "Upload PDF" or "Download PDF")   │
└──────────────────┬────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────────┐
│           Controller Layer                       │
│  (DocumentController, LoanController)            │
│  - Handles user actions                         │
│  - Validates permissions                        │
└──────────────────┬────────────────────────────────┘
                  │
                  ↓
┌─────────────────────────────────────────────────┐
│           Service Layer                          │
│  (DocumentService, LoanService)                 │
│  - Business logic                               │
│  - Calls DAO for database operations            │
│  - Calls DocumentStorageService for files        │
└──────────────────┬────────────────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
        ↓                   ↓
┌──────────────┐   ┌──────────────────────┐
│  DAO Layer   │   │ DocumentStorageService│
│  (Database)  │   │   (File System)       │
│              │   │                      │
│ - Metadata   │   │ - PDF files          │
│ - User data  │   │ - File paths         │
│ - Loans      │   │ - File hashes        │
└──────────────┘   └──────────────────────┘
```

**Key Point:** 
- **Database (DAO)** stores **metadata** (title, author, file path, etc.)
- **DocumentStorageService** stores **actual PDF files** on disk

---

## 📋 Key Responsibilities

### ✅ **What DocumentStorageService DOES:**

1. **Save Files**
   ```java
   StorageInfo info = storage.saveDocument(inputStream, "book.pdf", documentId);
   // Saves to: ~/Documents/InfinitPagesLibrary/pdf/2024/01/doc_000001.pdf
   ```

2. **Retrieve Files**
   ```java
   InputStream file = storage.getDocument(filePath);
   // Returns file stream for reading/downloading
   ```

3. **Organize Files**
   - Creates directory structure: `pdf/2024/01/`
   - Generates unique filenames: `doc_000001.pdf`
   - Keeps files organized by date

4. **Secure Files**
   - Validates paths (prevents `../` attacks)
   - Stores outside project directory
   - Ensures files are within allowed directory

5. **Track File Information**
   - File size (in bytes and MB)
   - File hash (SHA-256 for integrity)
   - File path and filename

### ❌ **What DocumentStorageService DOES NOT DO:**

1. ❌ **Does NOT store metadata** (that's the database's job)
2. ❌ **Does NOT check user permissions** (that's the Controller's job)
3. ❌ **Does NOT handle business logic** (that's the Service layer's job)
4. ❌ **Does NOT create UI** (that's JavaFX's job)

---

## 🔄 Typical Workflow

### **Uploading a PDF Book:**

```
1. User selects PDF file in UI
   ↓
2. Controller receives file
   ↓
3. Controller calls DocumentStorageService.saveDocument()
   ↓
4. DocumentStorageService:
   - Validates file size
   - Creates directory structure
   - Saves file to disk
   - Calculates hash
   - Returns file path
   ↓
5. Controller saves metadata to database (via DAO)
   - Stores: title, author, file path, hash, etc.
   ↓
6. Done! File is stored and metadata is in database
```

### **Downloading a PDF Book:**

```
1. User clicks "Download" in UI
   ↓
2. Controller checks permissions (user has loan?)
   ↓
3. Controller gets file path from database (via DAO)
   ↓
4. Controller calls DocumentStorageService.getDocument(filePath)
   ↓
5. DocumentStorageService:
   - Validates path security
   - Checks file exists
   - Returns file stream
   ↓
6. Controller serves file to user
```

---

## 🔐 Security Features

### 1. **Path Validation**
```java
// Prevents attacks like: "../../../etc/passwd"
validateFilePath(filePath);
```

### 2. **Project Directory Check**
```java
// Ensures files are NOT in project directory
validateStoragePath(basePath);
// Throws error if path is inside project!
```

### 3. **Base Directory Restriction**
```java
// Files can only be accessed within base directory
if (!normalizedPath.startsWith(normalizedBase)) {
    throw new SecurityException("File path outside allowed directory");
}
```

---

## 💡 Why Separate from Database?

### **Database stores:**
- ✅ Metadata (title, author, description)
- ✅ File path (where file is stored)
- ✅ File hash (for verification)
- ✅ File size
- ✅ User loans, reviews, etc.

### **File System stores:**
- ✅ Actual PDF file content (can be 5-50 MB each)
- ✅ Organized directory structure
- ✅ Physical file storage

**Why not store PDFs in database?**
- ❌ Database would become huge (each PDF = 5-50 MB)
- ❌ Slow queries (loading PDFs slows database)
- ❌ Expensive storage
- ❌ Hard to backup

**Better approach:**
- ✅ Database: Small metadata (fast searches)
- ✅ File System: Large files (efficient storage)

---

## 📊 Example Usage

### **In Your Controller:**

```java
public class DocumentController {
    private DocumentStorageService storageService;
    private DocumentDAO documentDAO;
    
    public DocumentController() {
        // Initialize storage service (uses secure default path)
        this.storageService = new DocumentStorageService();
        this.documentDAO = new DocumentDAO();
    }
    
    public void uploadDocument(File uploadedFile, DocumentNumerique document) {
        try {
            // 1. Save file to filesystem
            try (InputStream is = new FileInputStream(uploadedFile)) {
                StorageInfo info = storageService.saveDocument(
                    is, 
                    uploadedFile.getName(), 
                    document.getId()
                );
                
                // 2. Update document with file info
                document.setCheminFichier(info.getFilePath());
                document.setTaille(info.getSizeMB());
                document.setHashFichier(info.getHash());
                
                // 3. Save metadata to database
                documentDAO.save(document);
            }
        } catch (IOException e) {
            // Handle error
        }
    }
    
    public void downloadDocument(DocumentNumerique document) {
        try {
            // 1. Get file from filesystem
            InputStream fileStream = storageService.getDocument(
                document.getCheminFichier()
            );
            
            // 2. Serve to user (save to their computer)
            // ... save file stream to user's chosen location
        } catch (FileNotFoundException e) {
            // Handle error
        }
    }
}
```

---

## 🎓 Summary

**DocumentStorageService is:**
- 📁 **File Manager** - Handles all file operations
- 🔒 **Security Layer** - Validates paths and prevents attacks
- 📊 **Organizer** - Organizes files in structured directories
- ✅ **Integrity Checker** - Calculates hashes for verification
- 🚀 **Performance Optimizer** - Stores files efficiently outside database

**It's the bridge between:**
- Your application (Java code)
- Physical file storage (disk)

**Without it:**
- ❌ No way to store PDF files
- ❌ No way to retrieve PDF files
- ❌ No file organization
- ❌ Security vulnerabilities

**With it:**
- ✅ Secure file storage
- ✅ Organized file structure
- ✅ Efficient file retrieval
- ✅ File integrity verification

---

**In simple terms:** DocumentStorageService is your **"digital filing cabinet"** that safely stores and retrieves PDF books for your library system! 📚✨

