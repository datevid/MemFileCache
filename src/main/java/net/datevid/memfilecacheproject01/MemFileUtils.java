/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.datevid.memfilecacheproject01;

import java.io.File;

public class MemFileUtils {

    /**
     * buscar un slash o backslash en la ruta path
     * @param text
     * @param textToFind
     * @return
     */
    public static boolean containText(String text, String textToFind)
    {
        int intIndex = text.indexOf(textToFind);
        if (intIndex == -1) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * obtiene el ultimo directorio de una ruta /this/is/the/path
     * con separadores linux o windows
     * @param pathStr
     * @return
     */
    public static String getLastDirectoryOfPath(String pathStr) {
        String containSlash = "/";
        String containBackSlash = "\\";
        String lastDirectory=null;
        if (containText(pathStr, containSlash)) {
            lastDirectory = getLastDirectorySlash(pathStr);
        } else if (containText(pathStr, containBackSlash)) {
            lastDirectory = getLastDirectoryBackSlash(pathStr);
        }
        return lastDirectory;
    }

    /**
     * obtiene el ultimo directorio de una ruta /this/is/the/path
     * con separadores windows
     * @param pathStr
     * @return
     */
    public static String getLastDirectoryBackSlash(String pathStr) {
        String[] split = pathStr.split("\\\\");
        String lastDirectory = split[split.length - 1];
        return lastDirectory;
    }

    /**
     * obtiene el ultimo directorio de una ruta /this/is/the/path
     * con separadores linux
     * @param pathStr
     * @return
     */
    public static String getLastDirectorySlash(String pathStr) {
        String[] split = pathStr.split("/");
        String lastDirectory = split[split.length - 1];
        return lastDirectory;
    }

    /**
     * Crea un directorio en la ruta indicada en pathStr y con el nombre indicado en directoryName
     * Si no se indica pathStr o se envia null, se crea en la raiz donde corre el proyecto.
     * @param pathStr
     * @param separate
     * @param directoryName
     */
    public static boolean createDirectory(String pathStr,String separate,String directoryName) {
        String pathFinal;
        if (pathStr == null) {
            pathFinal = directoryName;
        } else {
            pathFinal =pathStr + separate + directoryName;
        }
        File directory = new File(pathFinal);
        boolean mkdir = directory.mkdir();
        if (mkdir) {
            String absolutePath = directory.getAbsolutePath();
            return true;
        }
        return false;
    }
}
