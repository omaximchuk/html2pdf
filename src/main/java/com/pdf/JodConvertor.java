package com.pdf;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.io.File;

/**
 * JOD Convertor example. Java Open Document converter can only convert between different types of docs (e.g. ODT to DOC).
 * It will perform conversion only if there is a running instance of LibreOffice (OpenOffice) in the server headless mode.
 * <ul>
 * <li>
 * render html with style to pdf: yes
 * </li>
 * <li>
 * write custom metadata: no, only some general (producer, creation date)
 * </li>
 * <li>
 * read metadata: no
 * </li>
 * <li>
 * license: converter uses Apache License, LibreOffice - LGPL, OpenOffice - ApacheLicense.
 * </li>
 * </ul>
 * Note: to start office in a headless server mode on Windows use the following command
 * <p>cd path_to_office_root\program</p>
 * <p>soffice.exe -accept="socket,host=0.0.0.0,port=8100;urp;StarOffice.ServiceManager" -headless -nodefault -nofirststartwizard
 * -nolockcheck
 * -nologo -norestore</p>
 * Running this example will require libraries in the lib/ folder in your app classpath.
 */

public class JodConvertor {

    private static String officeHome = "C:\\Program Files (x86)\\LibreOffice 4";

    public static void main(String... args) {

        OfficeManager officeManager = new DefaultOfficeManagerConfiguration()
                .setOfficeHome(officeHome)
                .setPortNumber(8100)
                .buildOfficeManager();
        officeManager.start();
        OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        converter.convert(new File("./src/main/resources/mailBody.html"), new File("./target/jod.pdf"));
        officeManager.stop();
    }
}
