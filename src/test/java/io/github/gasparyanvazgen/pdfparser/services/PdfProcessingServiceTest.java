package io.github.gasparyanvazgen.pdfparser.services;

import com.google.api.services.gmail.model.Message;
import io.github.gasparyanvazgen.pdfparser.exceptions.PdfProcessingException;
import io.github.gasparyanvazgen.pdfparser.model.PdfContent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfProcessingServiceTest {

    @Autowired
    private GmailService gmailService;

    @Autowired
    private PdfProcessingService pdfProcessingService;

    private static final Logger logger = LoggerFactory.getLogger(PdfProcessingServiceTest.class);

    @Test
    void extractTextAndImages() throws IOException, PdfProcessingException {
        List<Message> messages = gmailService.fetchMessagesBySubject("Bank statement");

        for (Message message : messages) {
            List<String> attachmentIds = gmailService.fetchAttachmentIds(message.getId());

            for (String attachmentId : attachmentIds) {
                if (attachmentId != null) {
                    byte[] pdfBytes = gmailService.fetchPdfAttachment(message.getId(), attachmentId);
                    PdfContent pdfContent = pdfProcessingService.extractTextAndImages(pdfBytes, message.getId(), attachmentId);

                    assertNotNull(pdfContent.getTextContent());
                    assertFalse(pdfContent.getImageUrls().isEmpty(), "No image URLs found");

                    logger.info("PDF Text Content: {}", pdfContent.getTextContent());

                    for (String imageUrl : pdfContent.getImageUrls()) {
                        assertNotNull(imageUrl, "Image URL is null");

                        logger.info("Image URL: {}", imageUrl);
                    }
                }
            }
        }
    }

}
