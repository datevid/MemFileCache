package net.datevid.memfilecacheproject01;

import java.io.IOException;

public class MemFileUtilsTest {

    public static void main(String[] args) throws IOException {


        //String pathStr = "/payara/MemFileCachePath/";
        String pathStr = "\\payara\\MemFileCachePath";
        //String pathStr = "J:\\py2020\\Java2020\\mavenproject1\\src\\main\\java\\com\\mycompany\\mavenproject1\\FileInput01";
        String lasstDirectory = MemFileUtils.getLastDirectoryOfPath(pathStr);
        System.out.println(lasstDirectory);


        boolean directoryCreated;
        directoryCreated = MemFileUtils.createDirectory(null, "\\", "hola6");
        if (directoryCreated) {
            System.out.println("Directorio creado");
        } else {
            System.out.println("No se creó el directorio");
        }
    }
}
