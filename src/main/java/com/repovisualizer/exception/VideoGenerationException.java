package com.repovisualizer.exception;

public class VideoGenerationException extends RuntimeException {

    public VideoGenerationException() {
        super();
    }

    public VideoGenerationException(String message) {
        super(message);
    }

    public VideoGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}