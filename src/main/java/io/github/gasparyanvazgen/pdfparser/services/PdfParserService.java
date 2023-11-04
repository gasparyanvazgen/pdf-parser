package io.github.gasparyanvazgen.pdfparser.services;

import com.google.api.services.gmail.model.Message;
import io.github.gasparyanvazgen.pdfparser.exceptions.GmailServiceFetchException;
import io.github.gasparyanvazgen.pdfparser.exceptions.MessageNotFoundException;
import io.github.gasparyanvazgen.pdfparser.exceptions.PdfParsingException;
import io.github.gasparyanvazgen.pdfparser.model.PdfContent;

import java.util.List;

public interface PdfParserService {

    /**
     * Extracts text and images from a PDF attachment.
     *
     * @param pdfBytes the content of the PDF attachment
     * @param messageId the ID of the Gmail message
     * @param attachmentId the ID of the attachment within the message
     * @return a {@link PdfContent} object containing extracted text and image URLs
     * @throws PdfParsingException
     */
    PdfContent extractPdfContentFromAttachment(byte[] pdfBytes, String messageId, String attachmentId) throws PdfParsingException;


    /**
     * Extracts PDF content from a list of Gmail messages.
     *
     * @param messages the list of Gmail messages containing PDF attachments
     * @return a list of {@link PdfContent} objects containing extracted text and image URLs
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     * @throws PdfParsingException
     */
    List<PdfContent> extractPdfContentsFromAttachments(List<Message> messages) throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException;

}
