package io.github.gasparyanvazgen.pdfparser.exceptions;

public class PdfProcessingException extends Exception {

    public PdfProcessingException(String message) {
        super(message);
    }

    public PdfProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
