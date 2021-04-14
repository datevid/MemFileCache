/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.datevid.memfilecacheproject01;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Manejo de archivos temporales con expiración.
 * Pensado para guardar sesion de datos, configuracion del sitio y similares
 */
public class MemFileCache {

    static private MemFileCache instance;
    private String path;
    private String separator = File.separator;
    private String extension="";
    private String charsetName = "UTF-8";
    private long limitMiliseconds;

    private MemFileCache(String path){
        this.path=path;
        this.limitMiliseconds =1000*60*60;//default 1H
    }
    private MemFileCache(String path,long limitMiliseconds){
        this.path=path;
        this.limitMiliseconds = limitMiliseconds;
    }

    static public MemFileCache getInstance() throws IOException {
        if (instance == null) {
            throw new IOException("No se ha inializado el objeto ni definido la ruta del directorio. Use .getInstance(pathHere)");
        }
        return instance;
    }

    static public MemFileCache getInstance(String path){
        if (instance == null) {
            instance = new MemFileCache(path);
        }
        return instance;
    }
    static public MemFileCache getInstance(String path,long expirationMiliseconds){
        if (instance == null) {
            instance = new MemFileCache(path,expirationMiliseconds);
        }
        return instance;
    }

    /**
     *
     * @param key el nombre del archivo
     * @return devuelve el contenido del archivo en string
     * @throws IOException
     */
    public String getData(String key ) throws IOException {

        File file = new File(this.path+this.separator+key+this.extension);

        //if file exist
        if(!file.exists() || file.isDirectory()) {
            return null;
        }

        //get attributes
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        FileTime modifiedTime = attr.lastModifiedTime();

        //String pattern = "yyyy-MM-dd HH:mm:ss";
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        //String formatted = simpleDateFormat.format( new Date( modifiedTime.toMillis() ) );
        //System.out.println( "The file modified date and time is: " + formatted );

        boolean expired;
        expired = ifExpired(new Date(), new Date(modifiedTime.toMillis()), this.limitMiliseconds);
        if (expired) {
            return null;//expired
        }

        //get content
        FileInputStream fileInputStream = new FileInputStream(file);
        try (BufferedReader br =new BufferedReader(new InputStreamReader(fileInputStream, this.charsetName))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * guardamos dato en archivo con el nombre indicado en key
     *
     * see:
     * https://stackoverflow.com/q/20426111/7105200
     * @param key nombre del archivo
     * @param value contenido del archivo
     * @throws IOException
     */
    /*public void setData(String key,String value) throws IOException {
        String jsonString = value;

        Path path = FileSystems.getDefault().getPath(this.path+this.separator+key+this.extension);

        Charset charset = Charset.forName("UTF-8");

        try {
            BufferedWriter writer = Files.newBufferedWriter(path, charset);
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            e.getStackTrace();
            throw e;
        }
    }*/
    public void setData(String key,String value) throws IOException {
        String jsonString = value;

        try {
            // Creates a FileWriter
            //FileWriter output = new FileWriter("output.txt");
            FileWriter output = new FileWriter(path+this.separator+key+this.extension);

            // Writes the string to the file
            output.write(jsonString);

            // Closes the writer
            output.close();

        }

        catch (IOException e) {
            e.getStackTrace();
            throw e;
        }
        //this.key = key;
    }

    /**
     * see:
     * https://es.stackoverflow.com/a/177512
     * http://oliviertech.com/java/how-to-get-a-file-creation-date/
     * @param fechaActual
     * @param fechaModificado
     * @param limitMiliseconds
     * @return
     */
    public boolean ifExpired(Date fechaActual, Date fechaModificado, long limitMiliseconds) {
        long millDif = fechaActual.getTime() - fechaModificado.getTime();
        if (millDif >= limitMiliseconds) {
            System.out.println("El archivo ha expirado. por "+(millDif/(1000*60))+" minutos");
            return true;
        }else{
            return false;
        }
    }
}
