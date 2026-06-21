package com.github.kharisovruslan.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileUtils {

    public boolean isFileImage(String file) {
        try {
            String mimeType = Files.probeContentType(Path.of(file));
            if ((mimeType != null) && (mimeType.startsWith("image"))) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
