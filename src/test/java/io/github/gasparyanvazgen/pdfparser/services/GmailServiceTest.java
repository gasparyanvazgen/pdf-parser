package io.github.gasparyanvazgen.pdfparser.services;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(GmailServiceTest.class);

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
                            logger.error("Attachment ID: {} is not a valid PDF.", attachmentId);
                        }
                    } else {
                        logger.error("Attachment data is null for attachment ID: {}", attachmentId);
                    }
                }
            }
        }
    }

    private boolean isValidPdf(byte[] data) {
        try {
            PDDocument.load(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}