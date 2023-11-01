package io.github.gasparyanvazgen.pdfparser.services;

public interface FirebaseStorageService {

    String uploadImage(byte[] imageContent, String messageId, String attachmentId, int imageIndex);

}
