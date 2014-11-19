package com.pdf;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;

/**
 * PDF Box example. PDF Box can create, modify pdf and read data from pdf.
 * <ul>
 * <li>
 * render html with style to pdf: no, at least with high level API, html is inserted as plain text with tags
 * </li>
 * <li>
 * write custom metadata: no, only general (producer, path, url, modified, uid, creation date, modification date, content as plain text).
 * It can write custom metadata but not in standard format (XMP format).
 * </li>
 * <li>
 * read metadata: general and its custom
 * </li>
 * <li>
 * license: Apache License
 * </li>
 * </ul>
 */

public class PdfBox {

    public static void main(String... args) throws Exception {
        createPdfWithMetadata();
        printMetadata();
    }

    private static void createPdfWithMetadata() throws Exception {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.moveTextPositionByAmount(100, 700);
        contentStream.drawString(getTextFromTestHtml());
        contentStream.endText();
        PDDocumentCatalog cat = document.getDocumentCatalog();
        PDMetadata newMeta = getMetaData(document);
        cat.setMetadata(newMeta);
        contentStream.close();
        document.save("./target/pdfbox.pdf");
        document.close();
    }

    private static String getTextFromTestHtml() throws IOException {
        return "<html>\n" +
                "\t<head>Hello</head>\n" +
                "\t<body>Some text here</body>\n" +
                "</html>";
    }

    private static void printMetadata() throws Exception {
        File donRiverPdf = new File("./target/pdfbox.pdf");
        PDDocument doc = PDDocument.load(donRiverPdf);
        PDDocumentCatalog cat = doc.getDocumentCatalog();
        PDMetadata metadata = cat.getMetadata();
        System.out.println(metadata.getInputStreamAsString());
    }

    private static PDMetadata getMetaData(PDDocument doc) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setExpandEntityReferences(true);
        f.setIgnoringComments(true);
        f.setIgnoringElementContentWhitespace(true);
        f.setValidating(false);
        f.setCoalescing(true);
        f.setNamespaceAware(true);
        DocumentBuilder builder = f.newDocumentBuilder();
        Document metaData = builder.parse(PdfBox.class.getResourceAsStream("/metadata-templ.xml"));
        PDMetadata meta = new PDMetadata(doc);
        meta.importXMPMetadata(new XMPMetadata(metaData).asByteArray());
        return meta;
    }
}