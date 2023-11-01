package io.github.gasparyanvazgen.pdfparser.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PdfContent {

    private String textContent;
    private List<String> imageUrls = new ArrayList<>();

    public void addImageUrl(String imageUrl) {
        imageUrls.add(imageUrl);
    }

}
