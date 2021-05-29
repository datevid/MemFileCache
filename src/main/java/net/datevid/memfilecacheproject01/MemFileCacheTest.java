/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.datevid.memfilecacheproject01;

import java.io.IOException;

/**
 *
 * @author doctor
 */
public class MemFileCacheTest {
    public static void main(String[] arg){
        try {

            //inicializacion
            String ruta="J:\\py2020\\Java2020\\mavenproject1\\src\\main\\java\\com\\mycompany\\mavenproject1\\CarpetaJava01";
            //MemFileCache.getInstance(ruta,1000*60*5,null);//5 minutos, default 1H
            MemFileCache.getInstance(ruta,1000*60*1,
                    false,1000L*60*1);

            //set data and key
            MemFileCache.getInstance().setData("userId-56783","Información de José Perez en JSON o cualquier otro formato");

            //get data from key
            System.out.println("data key userId-56783 is:");
            String dataString = MemFileCache.getInstance().getData("userId-56783");
            System.out.println(dataString);

            //data expired(archivo modificado supera el limite de fecha de expiracion) return null
            System.out.println("data key expired userId-56782 is:");
            String userId_expired = MemFileCache.getInstance().getData("userId-56782");
            System.out.println(userId_expired);

            //key invalid return null
            System.out.println("data key userId-no-exist is:");
            System.out.println(MemFileCache.getInstance().getData("userId-no-exist"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
