package com.example.licentav1.AWS;

import com.itextpdf.text.DocumentException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;



@Service
public class DocToPdfConverter {
    public void convertDocToPdf(File docFile, File pdfFile) throws IOException, IOException, DocumentException {
        try {
            FileInputStream fis = new FileInputStream(docFile);

            String text;

            // Check the file extension
            String extension = docFile.getName().substring(docFile.getName().lastIndexOf(".") + 1);
            if ("doc".equalsIgnoreCase(extension)) {
                HWPFDocument document = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(document);
                text = extractor.getText();
            } else if ("docx".equalsIgnoreCase(extension)) {
                XWPFDocument document = new XWPFDocument(fis);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                text = extractor.getText();
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + extension);
            }

            System.out.println("Converting file: " + docFile.getName() + " to PDF");

            Document pdfDocument = new Document();
            PdfWriter.getInstance(pdfDocument, new FileOutputStream(pdfFile));
            pdfDocument.open();
            pdfDocument.add(new com.itextpdf.text.Paragraph(text));
            pdfDocument.close();

            System.out.println("File converted successfully: " + pdfFile.getName());

            fis.close();
        }catch (IOException | DocumentException e) {
            System.out.println("Exception in convertDocToPdf: " + e.getMessage()); // Log any exceptions
        }
    }
}
