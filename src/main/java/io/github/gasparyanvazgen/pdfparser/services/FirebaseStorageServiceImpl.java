package io.github.gasparyanvazgen.pdfparser.services;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Collections;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    @Autowired
    private FirebaseApp firebaseApp;

    @Value("${firebase.storage.storageBucket}")
    private String storageBucket;

    @Override
    public String uploadImage(byte[] imageContent, String messageId, String attachmentId, int imageIndex) {
        // get the storage instance
        Storage storage = StorageClient.getInstance(firebaseApp).bucket().getStorage();

        // prepare input stream for the image content
        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageContent);

        // define the blob name and its info
        String blobName = generateBlobName(messageId, attachmentId, imageIndex);
        BlobInfo blobInfo = createBlobInfo(blobName);

        // create the blob in Firebase Cloud Storage
        storage.create(blobInfo, imageContent);

        // generate and return the image URL
        return generateImageUrl(blobName);
    }

    // generate the blob name based on parameters
    private String generateBlobName(String messageId, String attachmentId, int imageIndex) {
        return String.format("%s/%s/image_%d.png", messageId, attachmentId, imageIndex);
    }

    // create BlobInfo with ACL and content type
    private BlobInfo createBlobInfo(String blobName) {
        return BlobInfo.newBuilder(BlobId.of(storageBucket, blobName))
                .setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                .setContentType("image/png")
                .build();
    }

    // generate the image URL based on the blob name and storage bucket
    private String generateImageUrl(String blobName) {
        return String.format("https://storage.googleapis.com/%s/%s", storageBucket, blobName);
    }

}
