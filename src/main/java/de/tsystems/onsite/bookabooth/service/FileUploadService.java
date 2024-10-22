package de.tsystems.onsite.bookabooth.service;

import de.tsystems.onsite.bookabooth.config.ApplicationProperties;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Service class for taking files and storing them to disk
 */

@Service
public class FileUploadService {

    private final Logger log = LoggerFactory.getLogger(FileUploadService.class);
    private final ApplicationProperties applicationProperties;

    public FileUploadService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String saveFile(String directory, Long id, String contentBase64) throws RuntimeException {
        try {
            ensureDirectoryExists(directory);
            var content = convertToByteArray(contentBase64);

            var suffix = getFileSuffix(content);
            if (suffix.isEmpty()) {
                throw new RuntimeException("Could not determine file suffix");
            }

            var path = Paths.get(applicationProperties.getUploadFolder(), directory, id + suffix);
            Files.write(path, content);
            removeFiles(directory, id, path);
            return path.toString();
        } catch (IOException e) {
            log.error("Could not save file", e);
            throw new RuntimeException("Could not save file", e);
        }
    }

    // Create directory inside BASEDIR_UPLOADS if it does not exist
    private void ensureDirectoryExists(String directory) throws IOException {
        // Create directory if it does not exist
        var path = Paths.get(applicationProperties.getUploadFolder(), directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private byte[] convertToByteArray(String contentBase64) {
        return Base64.getDecoder().decode(contentBase64);
    }

    private String getFileSuffix(byte[] imageBytes) throws IOException {
        Tika tika = new Tika();
        var fileType = tika.detect(imageBytes);
        return "." + fileType.split("/")[1];
    }

    private void removeFiles(String directory, Long id, Path excludePath) {
        try {
            String filePattern = String.format("%d.*", id);
            var path = Paths.get(applicationProperties.getUploadFolder(), directory);
            var files = getFilesMatchingPattern(path.toString(), filePattern);
            files.removeIf(p -> p.equals(excludePath));

            for (Path file : files) {
                Files.deleteIfExists(file);
            }
        } catch (IOException e) {
            log.error("Could not delete file", e);
        }
    }

    public static List<Path> getFilesMatchingPattern(String directory, String globPattern) throws IOException {
        List<Path> matchingFiles = new ArrayList<>();
        Path dirPath = Paths.get(directory);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, globPattern)) {
            for (Path entry : stream) {
                matchingFiles.add(entry);
            }
        }

        return matchingFiles;
    }
}
