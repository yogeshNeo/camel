package org.example.util;

import org.example.exception.FileNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {

    public String handleFile() {
        return "File Handled";
    }

    public void fileFailed() throws FileNotFoundException {
        throw new FileNotFoundException("File not found");
    }

}
