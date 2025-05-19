package com.bidmaster.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;

/**
 * Utility class for handling image uploads
 */
public class ImageUploadUtil {
    private static final Logger LOGGER = Logger.getLogger(ImageUploadUtil.class.getName());
    
    private static final String UPLOAD_DIRECTORY = "uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    
    /**
     * Uploads an image file to the server
     * 
     * @param filePart The file part from multipart request
     * @param context The servlet context
     * @return The relative path to the uploaded image
     * @throws IOException If an I/O error occurs
     * @throws IllegalArgumentException If the file is invalid
     */
    public static String uploadImage(Part filePart, ServletContext context) throws IOException, IllegalArgumentException {
        // Validate file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 5MB");
        }
        
        // Get file name and extension
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        
        // Validate file extension
        boolean isValidExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equals(fileExtension)) {
                isValidExtension = true;
                break;
            }
        }
        
        if (!isValidExtension) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: JPG, JPEG, PNG, GIF");
        }
        
        // Create upload directory if it doesn't exist
        String uploadPath = context.getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        
        // Generate unique file name
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = uploadPath + File.separator + uniqueFileName;
        
        // Save the file
        try {
            filePart.write(filePath);
            LOGGER.log(Level.INFO, "File uploaded successfully: {0}", uniqueFileName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving uploaded file", e);
            throw e;
        }
        
        // Return the relative path
        return UPLOAD_DIRECTORY + "/" + uniqueFileName;
    }
    
    /**
     * Deletes an image file from the server
     * 
     * @param imagePath The relative path to the image
     * @param context The servlet context
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteImage(String imagePath, ServletContext context) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        
        try {
            String fullPath = context.getRealPath("") + File.separator + imagePath;
            File file = new File(fullPath);
            
            if (file.exists()) {
                Files.delete(file.toPath());
                LOGGER.log(Level.INFO, "File deleted successfully: {0}", imagePath);
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting file", e);
        }
        
        return false;
    }
}
