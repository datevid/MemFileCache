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
import java.util.*;

/**
 *
 * Manejo de archivos temporales con expiración.
 * Pensado para guardar sesion de datos, configuracion del sitio y similares
 */
public class MemFileCache {

    static private MemFileCache instance;
    private String path;
    private String separator = File.separator;
    private String extension="";//.txt
    private String charsetName = "UTF-8";
    private long expirationMilliseconds;

    /**
     * guarda la key y la cantidad de veces que fue consultada
     */
    private TreeMap<String, MemFileFrecuencyBean> statistics;

    //private MemFileCache(String path){
    //    this.path=path;
    //    this.expirationMilliseconds =1000*60*60;//default 1H
    //}

    private MemFileCache(String path,long expirationMilliseconds,Long periodMinutes){
        this.path=path;
        this.expirationMilliseconds = expirationMilliseconds;
        this.statistics = new TreeMap<>();

        //guarda las estadisticas en fichero cada cierto tiempo
        String fileName="MemFileCacheStatistics.csv";
        this.saveStatisticsSameFile(fileName,periodMinutes);
    }

    static public MemFileCache getInstance() throws IOException {
        if (instance == null) {
            throw new IOException("No se ha inializado el objeto ni definido la ruta del directorio. Use .getInstance(path)");
        }
        return instance;
    }

    //static public MemFileCache getInstance(String path){
    //    if (instance == null) {
    //        instance = new MemFileCache(path);
    //    }
    //    return instance;
    //}
    static public MemFileCache getInstance(String path,long expirationMiliseconds,Long periodSeconds){
        if (instance == null) {
            instance = new MemFileCache(path,expirationMiliseconds,periodSeconds);
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
        expired = ifExpired(new Date(), new Date(modifiedTime.toMillis()), this.expirationMilliseconds);
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
            this.updateStatistics(key);//actualiza estadisticas
            return sb.toString();
        }catch (IOException e){
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
    public boolean setData(String key,String value) throws IOException {
        String data = value;
        String fileName=key;
        //String pathComplete = path + this.separator + fileName + this.extension;
        //this.saveFile(data,pathComplete);
        boolean saved = this.saveFile(data, fileName);
        return saved;
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

    public void updateStatistics(String key) {
        MemFileFrecuencyBean memFileFrecuencyBean = this.statistics.get(key);
        if (memFileFrecuencyBean == null) {
            memFileFrecuencyBean = new MemFileFrecuencyBean(key, 0, 0);
        }
        memFileFrecuencyBean.setFrecuencia(memFileFrecuencyBean.getFrecuencia()+1);
        this.statistics.put(key, memFileFrecuencyBean);

    }

    /**
     * Guarda las estadisticas en un mismo archivo
     * que será actualizado cada cierto tiempo indicado en periodMinutes
     *
     * El formato del archivo es de formato nombreArchivo - FrecuenciaUso
     * @param fileName
     * @param periodMinutes
     */
    public void saveStatisticsSameFile(String fileName,Long periodMinutes) {

        //set period in minutes
        Long periodDefault=1L*15;//15 minutes
        periodMinutes = (periodMinutes == null) ? periodDefault : periodMinutes;

        //save each time
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                StringBuffer data = new StringBuffer();
                try {
                    for(Map.Entry m:statistics.entrySet())
                    {
                        //System.out.println(m.getKey()+" "+m.getValue());
                        MemFileFrecuencyBean value = (MemFileFrecuencyBean)m.getValue();
                        data.append(m.getKey());
                        data.append(",");
                        data.append(value.getFrecuencia());
                        data.append("\n");
                    }

                    boolean fileSaved = saveFile(data.toString(),fileName);
                    if (fileSaved) {
                        System.out.println("Guardado MemFileCache "+fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error al guardar estadísticas sobre MemFileCache.");
                    System.out.println(e);
                    System.err.format("IOException: %s%n", e);
                }
            }
        },0,periodMinutes);
    }

    /**
     * guarda una cadena en una ruta específica
     *
     * @param data
     * dato a guardar
     * @param fileName
     * nombre del archivo a guardar
     * @throws IOException
     */
    public boolean saveFile(String data, String fileName){
        String pathComplete = this.path + this.separator + fileName + this.extension;
        Path path = FileSystems.getDefault().getPath(this.path);
        boolean isDir = Files.isDirectory(path);
        if (!isDir) {

            //throw new IOException("Directory doesn't exist!!!");
            //crear archivo a prueba de fallos
            String directoryCache = MemFileUtils.getLastDirectoryOfPath(this.path);
            String pathWithoutDirectoryArray[] = this.path.split(directoryCache);
            String pathWithoutDirectory = pathWithoutDirectoryArray[pathWithoutDirectoryArray.length - 1];
            boolean directoryCreated = MemFileUtils.createDirectory(pathWithoutDirectory, this.separator, directoryCache);
            if (!directoryCreated) {
                return false;
            }
        }

        path = FileSystems.getDefault().getPath(pathComplete);
        Charset charset = Charset.forName("UTF-8");
        try {
            BufferedWriter writer = Files.newBufferedWriter(path, charset);
            writer.write(data);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            e.getStackTrace();
            //throw e;//no debe interrumpir el flujo del sistema principal
            return false;
        }
    }
}
