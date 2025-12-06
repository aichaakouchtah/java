package com.infinitpages.util.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDate;

/**
 * Service for managing document file storage on the filesystem.
 * 
 * This service handles:
 * - Saving uploaded PDF/EPUB files to organized directory structure
 * - Retrieving documents by file path
 * - Deleting documents
 * - Calculating file hashes for integrity verification
 * 
 * Storage Structure:
 * documents/
 *   ├── pdf/
 *   │   ├── 2024/
 *   │   │   ├── 01/
 *   │   │   │   └── doc_000001.pdf
 *   │   │   └── 02/
 *   ├── epub/
 *   └── images/
 */
public class DocumentStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentStorageService.class);
    
    // Base directory for storing documents
    private final Path baseDirectory;
    
    // Maximum file size (100 MB)
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;
    
    /**
     * Create a new DocumentStorageService with default secure path.
     * Files are stored outside the project directory for security.
     * 
     * Default locations:
     * - Linux/Mac: ~/InfinitPages/documents
     * - Windows: C:\Users\Username\InfinitPages\Documents
     */
    public DocumentStorageService() {
        this(getDefaultStoragePath());
    }
    
    /**
     * Create a new DocumentStorageService with custom path.
     * 
     * ⚠️ SECURITY WARNING: Never use project directory!
     * Use system directory or user home directory instead.
     * 
     * @param basePath The base directory path for storing documents
     *                 (e.g., "/var/lib/infinitpages/documents" or "C:\\ProgramData\\InfinitPages\\Documents")
     */
    public DocumentStorageService(String basePath) {
        // Validate path is not in project directory
        validateStoragePath(basePath);
        this.baseDirectory = Paths.get(basePath);
        initializeDirectories();
    }
    
    /**
     * Get default secure storage path (outside project directory).
     * 
     * Uses a path that is clearly separate from the project:
     * - Linux/Mac: ~/Documents/InfinitPagesLibrary/ or ~/.infinitpages/documents/
     * - Windows: C:\Users\Username\Documents\InfinitPagesLibrary\
     */
    private static String getDefaultStoragePath() {
        String userHome = System.getProperty("user.home");
        String separator = File.separator;
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            // Windows: Use Documents folder
            return userHome + separator + "Documents" + separator + "InfinitPagesLibrary";
        } else {
            // Linux/Mac: Use hidden directory in home or Documents
            // Option 1: Hidden directory (more secure, less visible)
            String hiddenPath = userHome + separator + ".infinitpages" + separator + "documents";
            
            // Option 2: Documents folder (more user-friendly)
            String documentsPath = userHome + separator + "Documents" + separator + "InfinitPagesLibrary";
            
            // Use Documents folder (easier to find and backup)
            return documentsPath;
        }
    }
    
    /**
     * Validate that storage path is secure (not in project directory).
     */
    private void validateStoragePath(String basePath) {
        Path path = Paths.get(basePath).normalize();
        String pathStr = path.toString().toLowerCase();
        
        // Get project directory (where the application is running from)
        String projectDir = System.getProperty("user.dir");
        Path projectPath = Paths.get(projectDir).normalize();
        
        // Check if storage path is inside project directory
        try {
            if (path.startsWith(projectPath)) {
                logger.error("❌ SECURITY ERROR: Storage path is inside project directory!");
                logger.error("Project: {}", projectPath);
                logger.error("Storage: {}", path);
                logger.error("This is INSECURE! Files must be stored OUTSIDE the project directory.");
                throw new IllegalArgumentException(
                    "Storage path cannot be inside project directory. " +
                    "Use a path outside the project (e.g., ~/Documents/InfinitPagesLibrary/ or /var/lib/infinitpages/documents/)"
                );
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            // If path comparison fails, continue with other checks
        }
        
        // Check if path contains common project directory names
        if (pathStr.contains("infinitpages") && 
            (pathStr.contains("src") || pathStr.contains("target") || pathStr.contains("resources") || 
             pathStr.contains("infinitpages") && pathStr.contains("documents"))) {
            logger.warn("⚠️ SECURITY WARNING: Storage path may be in project directory: {}", basePath);
            logger.warn("Recommended: Use ~/Documents/InfinitPagesLibrary/ or /var/lib/infinitpages/documents/");
        }
        
        // Check for path traversal attempts
        if (pathStr.contains("..") || (pathStr.contains("~") && !basePath.startsWith(System.getProperty("user.home")))) {
            throw new IllegalArgumentException("Invalid storage path: " + basePath);
        }
    }
    
    /**
     * Initialize storage directories if they don't exist.
     */
    private void initializeDirectories() {
        try {
            // Create base directory
            Files.createDirectories(baseDirectory);
            
            // Create subdirectories
            Files.createDirectories(baseDirectory.resolve("pdf"));
            Files.createDirectories(baseDirectory.resolve("epub"));
            Files.createDirectories(baseDirectory.resolve("images"));
            Files.createDirectories(baseDirectory.resolve("temp"));
            
            logger.info("Document storage initialized at: {}", baseDirectory.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to initialize document storage directories", e);
            throw new RuntimeException("Storage initialization failed", e);
        }
    }
    
    /**
     * Save an uploaded document file.
     * 
     * @param inputStream The file input stream
     * @param originalFilename Original filename from upload
     * @param documentId The document ID (for unique naming)
     * @return StorageInfo containing file path, size, and hash
     * @throws IOException If file cannot be saved
     */
    public StorageInfo saveDocument(InputStream inputStream, String originalFilename, int documentId) 
            throws IOException {
        
        // Validate file size (check available bytes if possible)
        long fileSize = inputStream.available();
        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                String.format("File too large. Maximum size: %d MB", MAX_FILE_SIZE / (1024 * 1024))
            );
        }
        
        // Determine file type and extension
        String extension = getFileExtension(originalFilename);
        String fileType = getFileType(extension);
        
        // Generate unique filename
        String filename = generateFilename(documentId, extension);
        
        // Create directory structure (by year/month)
        LocalDate now = LocalDate.now();
        Path targetDir = baseDirectory.resolve(fileType)
                                      .resolve(String.valueOf(now.getYear()))
                                      .resolve(String.format("%02d", now.getMonthValue()));
        Files.createDirectories(targetDir);
        
        // Full file path
        Path filePath = targetDir.resolve(filename);
        
        // Save file and calculate hash
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            long totalBytes = 0;
            
            try (InputStream is = inputStream;
                 OutputStream os = Files.newOutputStream(filePath)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    digest.update(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
            }
            
            // Calculate file hash
            String hash = bytesToHex(digest.digest());
            
            logger.info("Document saved: {} ({} bytes, {} MB)", 
                       filePath, totalBytes, String.format("%.2f", totalBytes / (1024.0 * 1024.0)));
            
            return new StorageInfo(
                filePath.toString(),
                filename,
                totalBytes,
                hash,
                extension
            );
            
        } catch (java.security.NoSuchAlgorithmException e) {
            // Clean up if save failed
            Files.deleteIfExists(filePath);
            logger.error("SHA-256 algorithm not available", e);
            throw new IOException("Failed to save document: hash calculation failed", e);
        } catch (Exception e) {
            // Clean up if save failed
            Files.deleteIfExists(filePath);
            logger.error("Failed to save document", e);
            throw new IOException("Failed to save document: " + e.getMessage(), e);
        }
    }
    
    /**
     * Retrieve a document file.
     * 
     * ⚠️ SECURITY: Always validate file path before calling this method!
     * Use getDocumentSecure() if you need permission checks.
     * 
     * @param filePath The stored file path
     * @return File input stream
     * @throws FileNotFoundException If file doesn't exist
     * @throws SecurityException If path is invalid or outside base directory
     */
    public InputStream getDocument(String filePath) throws FileNotFoundException, SecurityException {
        // Validate path security
        validateFilePath(filePath);
        
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Document not found: " + filePath);
        }
        
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            logger.error("Cannot read document: {}", filePath, e);
            throw new FileNotFoundException("Cannot read document: " + e.getMessage());
        }
    }
    
    /**
     * Validate that file path is secure (within base directory, no path traversal).
     * 
     * @param filePath The file path to validate
     * @throws SecurityException If path is invalid or outside base directory
     */
    private void validateFilePath(String filePath) throws SecurityException {
        if (filePath == null || filePath.isEmpty()) {
            throw new SecurityException("File path cannot be null or empty");
        }
        
        try {
            Path normalizedPath = Paths.get(filePath).normalize();
            Path normalizedBase = baseDirectory.normalize();
            
            // Check for path traversal attempts
            if (filePath.contains("..") || filePath.contains("~")) {
                logger.warn("Path traversal attempt detected: {}", filePath);
                throw new SecurityException("Invalid file path: " + filePath);
            }
            
            // Check if path is within base directory
            if (!normalizedPath.startsWith(normalizedBase)) {
                logger.warn("Access attempt outside base directory: {} (base: {})", 
                           filePath, baseDirectory);
                throw new SecurityException("File path is outside allowed directory");
            }
        } catch (Exception e) {
            if (e instanceof SecurityException) {
                throw e;
            }
            throw new SecurityException("Invalid file path: " + filePath, e);
        }
    }
    
    /**
     * Delete a document file.
     * 
     * @param filePath The file path to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteDocument(String filePath) {
        try {
            Path path = Paths.get(filePath);
            boolean deleted = Files.deleteIfExists(path);
            
            if (deleted) {
                logger.info("Document deleted: {}", filePath);
            }
            
            return deleted;
        } catch (IOException e) {
            logger.error("Failed to delete document: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * Check if a document file exists.
     * 
     * @param filePath The file path to check
     * @return true if file exists
     */
    public boolean documentExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Get file size in bytes.
     * 
     * @param filePath The file path
     * @return File size in bytes
     * @throws IOException If file cannot be accessed
     */
    public long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }
    
    /**
     * Generate unique filename based on document ID.
     */
    private String generateFilename(int documentId, String extension) {
        return String.format("doc_%06d.%s", documentId, extension);
    }
    
    /**
     * Extract file extension from filename.
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return "pdf"; // Default to PDF
    }
    
    /**
     * Get file type directory based on extension.
     */
    private String getFileType(String extension) {
        return switch (extension.toLowerCase()) {
            case "pdf" -> "pdf";
            case "epub" -> "epub";
            case "jpg", "jpeg", "png", "gif" -> "images";
            default -> "pdf";
        };
    }
    
    /**
     * Convert bytes to hexadecimal string.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
    
    /**
     * Get the base directory path.
     */
    public String getBaseDirectory() {
        return baseDirectory.toString();
    }
    
    /**
     * Storage information returned after saving a file.
     */
    public static class StorageInfo {
        private final String filePath;
        private final String filename;
        private final long sizeBytes;
        private final String hash;
        private final String extension;
        
        public StorageInfo(String filePath, String filename, long sizeBytes, String hash, String extension) {
            this.filePath = filePath;
            this.filename = filename;
            this.sizeBytes = sizeBytes;
            this.hash = hash;
            this.extension = extension;
        }
        
        public String getFilePath() { 
            return filePath; 
        }
        
        public String getFilename() { 
            return filename; 
        }
        
        public long getSizeBytes() { 
            return sizeBytes; 
        }
        
        public double getSizeMB() { 
            return sizeBytes / (1024.0 * 1024.0); 
        }
        
        public String getHash() { 
            return hash; 
        }
        
        public String getExtension() { 
            return extension; 
        }
        
        @Override
        public String toString() {
            return String.format("StorageInfo{filePath='%s', filename='%s', size=%.2f MB, hash='%s'}", 
                               filePath, filename, getSizeMB(), hash);
        }
    }
}

