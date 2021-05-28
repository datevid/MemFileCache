package net.datevid.memfilecacheproject01;

public class MemFileFrecuencyBean {
    private String key; //key
    private long tamanhoKey;//tamaño de la variable key
    private long frecuencia;//frecuencia de la solicitud de la variable key

    public MemFileFrecuencyBean(String key, long tamanhoKey, long frecuencia) {
        this.key = key;
        this.tamanhoKey = tamanhoKey;
        this.frecuencia = frecuencia;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTamanhoKey() {
        return tamanhoKey;
    }

    public void setTamanhoKey(long tamanhoKey) {
        this.tamanhoKey = tamanhoKey;
    }

    public long getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(long frecuencia) {
        this.frecuencia = frecuencia;
    }

    @Override
    public String toString() {
        return "MemFileFrecuencyBean{" +
                "key='" + key + '\'' +
                ", tamanhoKey=" + tamanhoKey +
                ", frecuencia=" + frecuencia +
                '}';
    }
}
