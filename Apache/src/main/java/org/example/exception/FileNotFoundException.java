package org.example.exception;

public class FileNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public FileNotFoundException(String message) {
        super(message);
    }

}