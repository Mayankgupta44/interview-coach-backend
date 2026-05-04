package com.interviewcoach.service.impl;

import com.interviewcoach.service.PdfExtractionService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfExtractionServiceImpl implements PdfExtractionService {

    @Override
    public PdfExtractionService.ExtractionResult extractText(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.trim().isBlank()) {
                return new PdfExtractionService.ExtractionResult(
                        false,
                        "",
                        "No readable text found in PDF"
                );
            }

            return new PdfExtractionService.ExtractionResult(
                    true,
                    text.trim(),
                    null
            );
        } catch (Exception ex) {
            return new PdfExtractionService.ExtractionResult(
                    false,
                    "",
                    "Failed to extract text from PDF"
            );
        }
    }
}