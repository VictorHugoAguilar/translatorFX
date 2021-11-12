package com.vhaa.translatorrfx.utils;

public class FileData {

    private String fileName;

    private String filePath;

    public FileData() {
    }

    public FileData(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
