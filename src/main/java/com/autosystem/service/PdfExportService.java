package com.autosystem.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] generateIeeePdfFromMarkdown(File markdownFile) throws Exception {
        List<String> lines = Files.readAllLines(markdownFile.toPath());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, baos);

        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                document.add(new Paragraph(" "));
                continue;
            }

            if (line.startsWith("# ")) {
                Paragraph title = new Paragraph(line.substring(2).toUpperCase(), titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
            } else if (line.startsWith("## ")) {
                document.add(new Paragraph(line.substring(3).toUpperCase(), sectionFont));
            } else {
                document.add(new Paragraph(line, normalFont));
            }
        }

        document.close();
        return baos.toByteArray();
    }
}
