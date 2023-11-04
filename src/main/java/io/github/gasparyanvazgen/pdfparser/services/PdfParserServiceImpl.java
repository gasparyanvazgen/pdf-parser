package io.github.gasparyanvazgen.pdfparser.services;

import com.google.api.services.gmail.model.Message;
import io.github.gasparyanvazgen.pdfparser.exceptions.GmailServiceFetchException;
import io.github.gasparyanvazgen.pdfparser.exceptions.MessageNotFoundException;
import io.github.gasparyanvazgen.pdfparser.exceptions.PdfParsingException;
import io.github.gasparyanvazgen.pdfparser.model.PdfContent;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfParserServiceImpl implements PdfParserService {

    @Autowired
    private GmailService gmailService;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Override
    public PdfContent extractPdfContentFromAttachment(byte[] pdfBytes, String messageId, String attachmentId) throws PdfParsingException {
        PdfContent pdfContent = new PdfContent();

        try (PDDocument document = PDDocument.load(pdfBytes)) {
            // extract text from the pdf
            PDFTextStripper textStripper = new PDFTextStripper();
            String pdfText = textStripper.getText(document);
            pdfContent.setTextContent(pdfText);

            // extract images from the pdf
            PDPageTree pages = document.getPages();
            int imageIndex = 1;

            for (PDPage page : pages) {
                PDResources resources = page.getResources();
                Iterable<COSName> xObjectNames = resources.getXObjectNames();

                for (COSName xObjectName : xObjectNames) {
                    if (resources.isImageXObject(xObjectName)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(xObjectName);

                        // convert the image to a buffered image
                        BufferedImage bufferedImage = image.getImage();

                        // convert the buffered image to a byte array
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "PNG", baos);
                        byte[] imageContent = baos.toByteArray();

                        // upload the image to firebase storage and get the URL
                        String imageUrl = firebaseStorageService.uploadImage(imageContent, messageId, attachmentId, imageIndex);
                        pdfContent.addImageUrl(imageUrl);

                        imageIndex++;
                    }
                }
            }
        } catch (IOException e) {
            String errorMessage = String.format("Error during PDF processing for message ID: %s, attachment ID: %s", messageId, attachmentId);
            throw new PdfParsingException(errorMessage, e);
        }

        return pdfContent;
    }

    @Override
    public List<PdfContent> extractPdfContentsFromAttachments(List<Message> messages) throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException {
        List<PdfContent> pdfContents = new ArrayList<>();

        if (messages != null) {
            for (Message message : messages) {
                List<String> attachmentIds = gmailService.fetchAttachmentIds(message.getId());

                for (String attachmentId : attachmentIds) {
                    if (attachmentId != null) {
                        byte[] pdfBytes = gmailService.fetchPdfAttachment(message.getId(), attachmentId);

                        PdfContent pdfContent = extractPdfContentFromAttachment(pdfBytes, message.getId(), attachmentId);
                        pdfContents.add(pdfContent);
                    }
                }
            }
        }

        return pdfContents;
    }

}
