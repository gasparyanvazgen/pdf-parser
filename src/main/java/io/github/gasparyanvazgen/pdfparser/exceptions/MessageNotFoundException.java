package io.github.gasparyanvazgen.pdfparser.exceptions;

public class MessageNotFoundException extends Exception {

    public MessageNotFoundException(String message) {
        super(message);
    }

    public MessageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
