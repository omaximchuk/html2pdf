package com.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.*;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.DefaultPDFCreationListener;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Flying saucer example. Based on IText.
 * <ul>
 * <li>
 * render html with style to pdf: yes, but html must be converted to xhtml first.
 * </li>
 * <li>
 * write custom metadata: yes, metadata is written in XMP format, i.e. in a standard way
 * </li>
 * <li>
 * read metadata: reads both custom and general metadata as a map
 * </li>
 * <li>
 * license: LGPL license
 * </li>
 * </ul>
 */

public class FlyingSaucer {

    public static void main(String... args) throws IOException {
        String outputFile = "./target/saucer.pdf";
        createPDF(outputFile);
        printMetaData(outputFile);
    }

    private static void createPDF(String pdf)
            throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(pdf);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(getDocument(), null);
            renderer.layout();
            // this listener can write any metadata into pdf file
            renderer.setListener(new MetaDataCreationListener());
            renderer.createPDF(os);

            os.close();
            os = null;
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static Document getDocument() throws IOException {
        TagNode node = new HtmlCleaner().clean(FlyingSaucer.class.getResourceAsStream("/mailBody.html"));
        try {
            return new DomSerializer(new CleanerProperties()).createDOM(node);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Could not create document from html");
        }
    }

    private static void printMetaData(String pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        HashMap info = reader.getInfo();
        Set set = info.entrySet();
        for (Object o : set) {
            Map.Entry e = (Map.Entry) o;
            System.out.println(e.getKey() + " :: " + e.getValue());
        }
    }

    static class MetaDataCreationListener extends DefaultPDFCreationListener {

        @Override
        public void preOpen(ITextRenderer renderer) {
            PdfDictionary info = renderer.getWriter().getInfo();
            // custom metadata
            info.put(new PdfName("foo"), new PdfString("bar", PdfObject.TEXT_UNICODE));
            // standard metadata
            info.put(new PdfName("Author"), new PdfString("Developer", PdfObject.TEXT_UNICODE));
            info.put(new PdfName("Producer"), new PdfString("DonRiver Inc.", PdfObject.TEXT_UNICODE));
        }
    }

}
