package lk.jiat.calivon.service;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.WebApplicationException;
import lk.jiat.calivon.util.Env;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.ContentDisposition;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadService {
    private static final String UPLOAD_DIRECTORY_NAME = "/uploads";
    private final ServletContext context;

    public FileUploadService(ServletContext context) {
        this.context = context;
    }

    public FileItem uploadFile(String directoryName, InputStream inputStream, ContentDisposition fileMetaData) {
        return writeFile(UPLOAD_DIRECTORY_NAME + "/" +  directoryName, inputStream, fileMetaData);
    }

    private FileItem writeFile(String relativePath, InputStream inputStream, ContentDisposition contentDisposition) {
        try {
            // Get the absolute server path to save the file
            Path uploadDir = Paths.get(context.getRealPath(relativePath));
            if (!Files.exists(uploadDir)) {
                System.out.println("Upload path not found! Creating Directory: " + uploadDir);
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename with extension
            String extension = FilenameUtils.getExtension(contentDisposition.getFileName());
            String fileName = System.currentTimeMillis() + "." + extension;

            // Write the file to the server
            Path filePath = uploadDir.resolve(fileName);
            try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            }

            // Construct web-accessible URL
            String webPath = relativePath.replace("\\", "/"); // normalize slashes
            String url = context.getContextPath() + webPath + "/" + fileName; // e.g., /calivon/uploads/product/1/12345.png
            String appUrl = Env.get("app.url"); // e.g., http://localhost:8080
            String fullUrl = appUrl + url;

            return new FileItem(fileName, contentDisposition.getFileName(), url, url, fullUrl);
            // Note: filePath parameter replaced with URL for DB storage

        } catch (IOException e) {
            throw new WebApplicationException("Error while uploading file: " + e.getMessage());
        }
    }

    public static class FileItem{
        private String fileName;
        private String originalFileName;
        private String filePath;
        private String url;
        private String fullUrl;

        public FileItem(String fileName, String originalFileName, String filePath, String url, String fullUrl) {
            this.fileName = fileName;
            this.originalFileName = originalFileName;
            this.filePath = filePath;
            this.url = url;
            this.fullUrl = fullUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public void setOriginalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFullUrl() {
            return fullUrl;
        }

        public void setFullUrl(String fullUrl) {
            this.fullUrl = fullUrl;
        }
    }
}
