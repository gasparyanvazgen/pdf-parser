package io.github.gasparyanvazgen.pdfparser.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
            "Final hours to get 50% off Pro + exclusive soft skills trainings, false",
            "Top launches you missed, false",
            "RandomUnlikelySubject123, true"
    })
    void fetchMessagesBySubject(String subject, boolean expectedResult) throws IOException {
        // attempt to fetch messages by subject
        boolean actualResult = Optional.ofNullable(gmailService.fetchMessagesBySubject(subject)).isEmpty();
        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @CsvSource({
            "18b775781a8d6172, false",
            "incorrectMessageId, true",
            "18b775781a8d6174, true"
    })
    void fetchMessageById(String messageId, boolean expectedResult) throws IOException {
        boolean actualResult;

        try {
            // attempt to fetch the message by ID
            Optional<Message> message = Optional.ofNullable(gmailService.fetchMessageById(messageId));
            actualResult = message.isEmpty();
        } catch (GoogleJsonResponseException e) {
            // handle the 404 Not Found error
            actualResult = true;
        }

        assertEquals(expectedResult, actualResult);
    }
}