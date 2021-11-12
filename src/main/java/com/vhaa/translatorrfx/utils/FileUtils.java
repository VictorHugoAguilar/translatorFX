package com.vhaa.translatorrfx.utils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    private static final String BASE_PATH = "./dictionaries/";
    public static final String ORIGINAL_FILES = "./originalFiles/";

    public static List<Language> readLanguages(Path path) {
        List<Language> list = new ArrayList<>();
        BufferedReader br = null;
        List<List<String>> lines = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(BASE_PATH.concat("languages.txt")));
            String line = "";
            // TODO: quitar luego unasola
            boolean unasola = true;
            while ((line = br.readLine()) != null && unasola) {
                List<String> lenguage = new ArrayList<>();
                lenguage.addAll(Arrays.asList(line.split(";")));
                lines.add(lenguage);
                // TODO: Quitar luego solo test
                // unasola = false;
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        } finally {
            try {
                br.close();
            } catch (Exception e) {
            }
        }

        lines.stream().forEach(l -> {
            getDictionaryByLanguage(list, l);
            // TODO:  QUIZAS NO VALGA
            /*
            Thread t = new Thread(() -> {

            });
            t.start();
            System.out.println("Thread -> " + t.getName());
           */
        });

        return list;
    }

    private static void getDictionaryByLanguage(List<Language> list, List<String> l) {
        try {
            HashMap<Integer, String> original = getSentencesOfDictionary(BASE_PATH.concat(l.get(2)));
            HashMap<Integer, String> translation = getSentencesOfDictionary(BASE_PATH.concat(l.get(3)));

            if (original.size() != translation.size()) {
                throw new Exception("There is not the same amount of element");
            }
            Language lan = new Language(l.get(0), l.get(1));
            original.forEach((o, index) -> {
                lan.addSentence(original.get(o), translation.get(o));
            });
            list.add(lan);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HashMap<Integer, String> getSentencesOfDictionary(String path) throws IOException {
        return Files.lines(Paths.get(path)).collect(HashMap<Integer, String>::new,
                (map, streamValue) -> map.put(map.size(), streamValue),
                (map, map2) -> {
                });
    }


    public static void main(String[] args) {
        // readLanguages(null);

        getListaFileData(Paths.get(ORIGINAL_FILES));
    }

    public static String readFile(Path path) throws IOException {

        return null;
    }

    public static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectory(entry);
                }
            }
        }
        Files.delete(path);
    }

    public static List<FileData> getListaFileData(Path path) {
        try (Stream<Path> stream = Files.list(path)) {
            return stream.map(
                    f -> new FileData(f.getFileName().toString(),
                            f.getParent().toString().concat("/"))
            ).collect(Collectors.toList());
        } catch (IOException ex) {
            System.err.println("Error reading " + ex);
        }
        return Collections.emptyList();
    }
}
