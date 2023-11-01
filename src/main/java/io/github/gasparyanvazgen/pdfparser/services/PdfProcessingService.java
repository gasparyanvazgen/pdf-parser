package io.github.gasparyanvazgen.pdfparser.services;

import io.github.gasparyanvazgen.pdfparser.exceptions.PdfProcessingException;
import io.github.gasparyanvazgen.pdfparser.model.PdfContent;

import java.io.IOException;

public interface PdfProcessingService {

    PdfContent extractTextAndImages(byte[] pdfBytes, String messageId, String attachmentId) throws IOException, PdfProcessingException;

}
