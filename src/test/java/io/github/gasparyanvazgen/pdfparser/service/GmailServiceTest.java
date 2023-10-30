package io.github.gasparyanvazgen.pdfparser.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GmailServiceTest {

    @Autowired
    private Gmail gmail;

    @Autowired
    private GmailService gmailService;

    @Test
    void fetchAllMessages() throws IOException {
        List<Message> messages = gmailService.fetchAllMessages();
        assertNotNull(messages);
    }

    @ParameterizedTest
    @CsvSource({
            "Bank statement, false",
            "Top launches you missed, false",
            "RandomUnlikelySubject123, true"
    })
    void fetchMessagesBySubject(String subject, boolean expectedResult) throws IOException {
        boolean actualResult = Optional.ofNullable(gmailService.fetchMessagesBySubject(subject)).isEmpty();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void fetchAttachmentIds() {
    }

    @ParameterizedTest
    @CsvSource({
            "Bank statement"
    })
    void fetchPdfAttachment(String subject) throws IOException {
        List<Message> messages = gmailService.fetchMessagesBySubject(subject);

        for (Message message : messages) {
            List<String> attachmentIds = gmailService.fetchAttachmentIds(message.getId());

            assertNotNull(attachmentIds);
            assertFalse(attachmentIds.isEmpty(), "No attachment IDs found for message: " + message.getId());

            for (String attachmentId : attachmentIds) {
                if (attachmentId != null) {
                    byte[] attachmentData = gmailService.fetchPdfAttachment(message.getId(), attachmentId);

                    if (attachmentData != null) {
                        if (!isValidPdf(attachmentData)) {
                            System.err.println("Attachment ID: " + attachmentId + " is not a valid PDF.");
                        }
                    } else {
                        System.err.println("Attachment data is null for attachment ID: " + attachmentId);
                    }
                }
            }
        }
    }


    private boolean isValidPdf(byte[] data) {
        return data.length >= 4 &&
                data[0] == (byte) 0x25 &&  // %
                data[1] == (byte) 0x50 &&  // P
                data[2] == (byte) 0x44 &&  // D
                data[3] == (byte) 0x46;    // F
    }
}