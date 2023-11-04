package io.github.gasparyanvazgen.pdfparser.services;

public interface FirebaseStorageService {

    /**
     * Uploads an image to Firebase Cloud Storage and generates an image URL.
     *
     * @param imageContent the byte array containing the image content
     * @param messageId the ID of the Gmail message
     * @param attachmentId the ID of the attachment within the message
     * @param imageIndex the index of the image within the attachment
     * @return the URL of the uploaded image
     */
    String uploadImage(byte[] imageContent, String messageId, String attachmentId, int imageIndex);

}
