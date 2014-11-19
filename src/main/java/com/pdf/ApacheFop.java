package com.pdf;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import java.io.*;

/**
 * Apache FOP example. FOP can render pdf from xsl-fo file only.
 * <ul>
 * <li>
 * render html with style to pdf: yes, but conversion must be done: html -> xml -> xsl-fo -> pdf so some html data can be lost
 * </li>
 * <li>
 * write custom metadata: unclear yet, but it can write some general info (producer, creation date)
 * </li>
 * <li>
 * read metadata: no
 * </li>
 * <li>
 * license: Apache License
 * </li>
 * </ul>
 */

public class ApacheFop {

    public static void main(String[] args) throws Exception {
        Document xml = getXml();
        Document fo = getXslFo(xml);
        OutputStream pdf = new FileOutputStream("./target/fop.pdf");
        pdf.write(fo2Pdf(fo, ApacheFop.class.getResourceAsStream("/xhtml2fo.xsl")));
        pdf.close();
    }

    private static Document getXml() throws IOException, ParserConfigurationException {
        InputStream file = ApacheFop.class.getResourceAsStream("/mailBody.html");
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        DomSerializer serializer = new DomSerializer(props, true);
        TagNode tag = cleaner.clean(file);
        return serializer.createDOM(tag);
    }

    private static Document getXslFo(Document xml) throws Exception {
        DOMSource xmlDomSource = new DOMSource(xml);
        DOMResult domResult = new DOMResult();

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();

        if (transformer == null) {
            System.out.println("Error creating transformer");
            System.exit(1);
        }

        try {
            transformer.transform(xmlDomSource, domResult);
        } catch (javax.xml.transform.TransformerException e) {
            return null;
        }

        return (Document) domResult.getNode();
    }

    private static byte[] fo2Pdf(Document foDocument, InputStream styleSheet) {
        FopFactory fopFactory = FopFactory.newInstance();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            Transformer transformer = getTransformer(styleSheet);

            Source src = new DOMSource(foDocument);
            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(src, res);

            return out.toByteArray();

        } catch (Exception ex) {
            return null;
        }
    }

    private static Transformer getTransformer(InputStream styleSheet) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();

            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            dFactory.setNamespaceAware(true);

            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            Document xslDoc = dBuilder.parse(styleSheet);
            DOMSource xslDomSource = new DOMSource(xslDoc);

            return tFactory.newTransformer(xslDomSource);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}