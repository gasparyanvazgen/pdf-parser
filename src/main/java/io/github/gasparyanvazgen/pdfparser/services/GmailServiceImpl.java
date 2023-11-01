package io.github.gasparyanvazgen.pdfparser.services;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GmailServiceImpl implements GmailService {

    @Autowired
    private Gmail gmail;

    @Value("${gmail.userId}")
    private String userId;

    @Override
    public List<Message> fetchMessagesBySubject(String subject) throws IOException {
        String query = "subject:" + subject;
        return fetchMessagesByQuery(query);
    }

    @Override
    public List<String> fetchAttachmentIds(String messageId) throws IOException {
        Message message = fetchMessageById(messageId);
        List<MessagePart> attachmentParts = extractAttachmentPartsFromMessage(message);

        return attachmentParts.stream()
                .map(part -> part.getBody().getAttachmentId())
                .collect(Collectors.toList());
    }

    @Override
    public byte[] fetchPdfAttachment(String messageId, String attachmentId) throws IOException {
        return gmail.users().messages().attachments().get(userId, messageId, attachmentId).execute().decodeData();
    }

    private Message fetchMessageById(String messageId) throws IOException {
        return gmail.users().messages().get(userId, messageId).execute();
    }

    private List<Message> fetchMessagesByQuery(String query) throws IOException {
        return gmail.users().messages().list(userId).setQ(query).execute().getMessages();
    }

    private List<MessagePart> extractAttachmentPartsFromMessage(Message message) {
        return message.getPayload().getParts()
                .stream()
                .filter(this::isAttachment)
                .collect(Collectors.toList());
    }

    private boolean isAttachment(MessagePart part) {
        return part.getFilename() != null && part.getBody() != null;
    }

}
