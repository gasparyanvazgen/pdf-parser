package io.github.gasparyanvazgen.pdfparser.services;

import io.github.gasparyanvazgen.pdfparser.exceptions.PdfProcessingException;
import io.github.gasparyanvazgen.pdfparser.model.PdfContent;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfProcessingServiceImpl implements PdfProcessingService {

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    private static final Logger logger = LoggerFactory.getLogger(PdfProcessingServiceImpl.class);

    public PdfContent extractTextAndImages(byte[] pdfBytes, String messageId, String attachmentId) throws PdfProcessingException {
        PdfContent pdfContent = new PdfContent();

        try (PDDocument document = PDDocument.load(pdfBytes)) {
            // extract text from the pdf
            PDFTextStripper textStripper = new PDFTextStripper();
            String pdfText = textStripper.getText(document);
            pdfContent.setTextContent(pdfText);
            logger.debug("pdfText: " + pdfText);

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
            logger.error("Error during PDF processing: " + e.getMessage());
            throw new PdfProcessingException("Error during PDF processing", e);
        }

        return pdfContent;
    }

}
