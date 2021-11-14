package com.vhaa.translatorfx.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class FilesTimes
 *
 * @author Victor Hugo Aguilar Aguilar
 */
public class FilesTimes {

    private static FilesTimes filesTimes;

    private List<FileTimeProcess> list;

    FilesTimes() {
        list = new ArrayList<>();
    }

    public static FilesTimes getInstance() {
        if (Objects.isNull(filesTimes)) {
            filesTimes = new FilesTimes();
        }
        return filesTimes;
    }


    public List<FileTimeProcess> getList() {
        return list;
    }

    public void addFileTime(FileTimeProcess fileTimeProcess) {
        if (Objects.isNull(fileTimeProcess)) {
            return;
        }
        this.list.add(fileTimeProcess);
    }


    public void clearList() {
        if(this.list.isEmpty()){
            return;
        }
        this.list.clear();
    }
}
