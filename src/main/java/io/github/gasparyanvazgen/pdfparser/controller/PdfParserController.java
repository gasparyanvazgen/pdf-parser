package io.github.gasparyanvazgen.pdfparser.controller;

import com.google.api.services.gmail.model.Message;
import io.github.gasparyanvazgen.pdfparser.exceptions.GmailServiceFetchException;
import io.github.gasparyanvazgen.pdfparser.exceptions.MessageNotFoundException;
import io.github.gasparyanvazgen.pdfparser.exceptions.PdfParsingException;
import io.github.gasparyanvazgen.pdfparser.model.PdfContent;
import io.github.gasparyanvazgen.pdfparser.services.GmailService;
import io.github.gasparyanvazgen.pdfparser.services.PdfParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/pdf-parser")
public class PdfParserController {

    @Autowired
    private GmailService gmailService;

    @Autowired
    private PdfParserService pdfParserService;

    @GetMapping("/parse-bank-statements")
    public ResponseEntity<List<PdfContent>> parseBankStatements(
            @RequestParam("subject") String subject,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException {
        List<Message> messages;

        if (startDate != null && endDate != null) {
            // messages within the specified date range
            messages = gmailService.fetchMessagesBySubjectAndDateRange(subject, startDate, endDate);
        } else if (startDate != null) {
            // messages after the specified date
            messages = gmailService.fetchMessagesBySubjectAndStartDate(subject, startDate);
        } else if (endDate != null) {
            // messages before the specified date
            messages = gmailService.fetchMessagesBySubjectAndEndDate(subject, endDate);
        } else {
            // messages without date filtering
            messages = gmailService.fetchMessagesBySubject(subject);
        }

        List<PdfContent> pdfContents = pdfParserService.extractPdfContentsFromAttachments(messages);

        if (pdfContents.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pdfContents);
    }

}
