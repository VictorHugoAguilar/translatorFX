package com.vhaa.translatorfx.utils;

/**
 *  Class FileTimeProcess
 *
 *  @author Victor Hugo Aguilar Aguilar
 */
public class FileTimeProcess {

    private String file;
    private Double time;

    public FileTimeProcess(String file, Double time) {
        this.file = file;
        this.time = time;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }
}
