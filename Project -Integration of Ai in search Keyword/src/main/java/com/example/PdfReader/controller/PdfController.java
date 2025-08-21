package com.example.PdfReader.controller;import com.example.PdfReader.modelDto.SearchResponse;
import com.example.PdfReader.modelDto.UploadResponse;
import com.example.PdfReader.Service.PdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RequestMapping("api/v1/document")
@RestController
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadPdf(@RequestParam("file") MultipartFile file) {
        UploadResponse response = pdfService.uploadPdf(file);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{file-id}/search")
    public ResponseEntity<SearchResponse> searchInUploadedPdf(
            @PathVariable("file-id") UUID fileId, @RequestParam("query") String query,
            @RequestParam(value = "caseSensitive", required = false, defaultValue = "false") Boolean caseSensitive,
            @RequestParam(value = "contextChars", required = false, defaultValue = "100") Integer contextChars,
            @RequestParam(value = "useSemantic", required = false, defaultValue = "false") Boolean useSemantic) {

        SearchResponse response = pdfService.searchInUploaded(fileId, query, caseSensitive, contextChars, useSemantic);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/search")
    public ResponseEntity<SearchResponse> uploadAndSearch(
            @RequestParam("file") MultipartFile file,
            @RequestParam("query") String query,
            @RequestParam(value = "caseSensitive", required = false, defaultValue = "false") Boolean caseSensitive,
            @RequestParam(value = "contextChars", required = false, defaultValue = "100") Integer contextChars,
            @RequestParam(value = "useSemantic", required = false, defaultValue = "false") Boolean useSemantic) {

        SearchResponse response = pdfService.uploadAndSearch(file, query, caseSensitive, contextChars, useSemantic);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}