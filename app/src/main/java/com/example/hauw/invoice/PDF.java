package com.example.hauw.invoice;

/**
 * Created by MSI on 9.4.2015.
 */
import  com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PDF {
    public static void main(String[] args) throws FileNotFoundException {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("Ime.pdf"));

            document.open();
            Paragraph paragraph = new Paragraph();
            paragraph.add("KLOBASA");
            document.add(paragraph);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
