package io.github.gasparyanvazgen.pdfparser.services;

import com.google.api.services.gmail.model.Message;
import io.github.gasparyanvazgen.pdfparser.exceptions.GmailServiceFetchException;
import io.github.gasparyanvazgen.pdfparser.exceptions.MessageNotFoundException;
import io.github.gasparyanvazgen.pdfparser.exceptions.PdfParsingException;
import io.github.gasparyanvazgen.pdfparser.model.PdfContent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfParserServiceTest {

    @Autowired
    private GmailService gmailService;

    @Autowired
    private PdfParserService pdfParserService;

    @ParameterizedTest
    @CsvSource({
            "Bank statement, true",
            "Invalid subject, false"
    })
    void extractPdfContentFromAttachment(String subject, boolean expectedResult) throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException {
        if (expectedResult) {
            List<Message> messages = gmailService.fetchMessagesBySubject(subject);
            assertFalse(messages.isEmpty());

            for (Message message : messages) {
                List<String> attachmentIds = gmailService.fetchAttachmentIds(message.getId());
                assertNotNull(attachmentIds);

                for (String attachmentId : attachmentIds) {
                    if (attachmentId != null) {
                        byte[] pdfBytes = gmailService.fetchPdfAttachment(message.getId(), attachmentId);
                        PdfContent pdfContent = pdfParserService.extractPdfContentFromAttachment(pdfBytes, message.getId(), attachmentId);
                        assertNotNull(pdfContent);
                    }
                }
            }
        } else {
            assertThrows(MessageNotFoundException.class, () -> gmailService.fetchMessagesBySubject(subject));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Bank statement, true",
            "Invalid subject, false"
    })
    void extractPdfContentsFromAttachments(String subject, boolean expectedResult) throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException {
        if (expectedResult) {
            List<Message> messages = gmailService.fetchMessagesBySubject(subject);
            assertFalse(messages.isEmpty());

            List<PdfContent> pdfContents = pdfParserService.extractPdfContentsFromAttachments(messages);
            assertFalse(pdfContents.isEmpty());
        } else {
            assertThrows(MessageNotFoundException.class, () -> gmailService.fetchMessagesBySubject(subject));
        }
    }

}
