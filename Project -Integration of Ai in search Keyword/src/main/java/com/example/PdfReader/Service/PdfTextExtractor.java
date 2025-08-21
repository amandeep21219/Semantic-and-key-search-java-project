package com.example.PdfReader.Service;

import com.example.PdfReader.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Component
@Slf4j
public class PdfTextExtractor {

    public List<String> extractTextByPages(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.isEncrypted()) {
                throw new ApiException("Encrypted PDFs are not supported", HttpStatus.BAD_REQUEST);
            }
            int totalPages = document.getNumberOfPages();
            List<String> pages = new ArrayList<>();
            PDFTextStripper textStripper = new PDFTextStripper();

            for (int index = 1; index <= totalPages; index++) {
                textStripper.setStartPage(index);
                textStripper.setEndPage(index);
                String pageText = textStripper.getText(document);
                pages.add(pageText);
            }
            log.info("[PdfTextExtractor] :: extractTextByPages :: Text extracted for {} pages", totalPages);
            return pages;

        } catch (IOException e) {
            throw new ApiException("Failed to extract text from PDF: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}