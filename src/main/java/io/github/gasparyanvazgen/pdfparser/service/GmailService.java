package io.github.gasparyanvazgen.pdfparser.service;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.List;

public interface GmailService {

    List<Message> fetchAllMessages() throws IOException;

    List<Message> fetchMessagesBySubject(String subject) throws IOException;

    List<String> fetchAttachmentIds(String messageId) throws IOException;

    byte[] fetchPdfAttachment(String messageId, String attachmentId) throws IOException;
}