package io.github.gasparyanvazgen.pdfparser.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GmailServiceImpl implements GmailService {

    @Autowired
    private Gmail gmail;

    @Override
    public List<Message> fetchAllMessages() throws IOException {
        return gmail.users().messages().list("me").execute().getMessages();
    }

    @Override
    public List<Message> fetchMessagesBySubject(String subject) throws IOException {
        String query = "subject:" + subject;
        List<Message> messages = gmail.users().messages().list("me").setQ(query).execute().getMessages();
        return messages;
    }

    @Override
    public Message fetchMessageById(String messageId) throws IOException {
        return gmail.users().messages().get("me", messageId).execute();
    }
}
