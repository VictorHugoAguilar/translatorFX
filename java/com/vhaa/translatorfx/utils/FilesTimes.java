package com.vhaa.translatorfx.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class FilesTimes
 * <p>
 * This class implements the singleton model to have a single instance of it
 *
 * @author Victor Hugo Aguilar Aguilar
 */
public class FilesTimes {

    /**
     * FileTime Static
     */
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

    /**
     * Method for get list of file times process
     *
     * @return List<FileTimeProcess>
     */
    public List<FileTimeProcess> getList() {
        return list;
    }

    /**
     * Method for add new file time process
     *
     * @param fileTimeProcess
     */
    public void addFileTime(FileTimeProcess fileTimeProcess) {
        if (Objects.isNull(fileTimeProcess)) {
            return;
        }
        this.list.add(fileTimeProcess);
    }

    /**
     * Method for reset list of file time process
     */
    public void clearList() {
        if (this.list.isEmpty()) {
            return;
        }
        this.list.clear();
    }
}
