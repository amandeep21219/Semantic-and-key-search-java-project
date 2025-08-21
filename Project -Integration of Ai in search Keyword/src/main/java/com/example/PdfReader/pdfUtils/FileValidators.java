package com.example.PdfReader.pdfUtils;
import com.example.PdfReader.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;

@Component
public class FileValidators {
@Value("${pdf.upload.max-file-size}")
 private  Integer MaxSize;

    public  void validatePdfFile(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ApiException("File is required", HttpStatus.BAD_REQUEST);
        }
        if (!"application/pdf".equals(file.getContentType())) {
            throw new ApiException("Only PDF files are allowed", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MaxSize) {
            throw new ApiException("File size cannot exceed 10MB", HttpStatus.BAD_REQUEST);
        }
    }
}