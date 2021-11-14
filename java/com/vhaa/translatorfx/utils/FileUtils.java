package com.vhaa.translatorfx.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class FileUtils
 *
 * @author Victor Hugo Aguilar Aguilar
 */
public class FileUtils {
    private static final String BASE_PATH = "./dictionaries/";

    public static List<Language> readLanguages(Path path) throws IOException {
        List<Language> listLanguages = new ArrayList<>();
        List<List<String>> lines = new ArrayList<>();

        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(line -> {
                List<String> language = new ArrayList<>();
                language.addAll(Arrays.asList(line.split(";")));
                lines.add(language);
            });
        }
        lines.stream().forEach(line -> getDictionaryByLanguage(listLanguages, line));

        return listLanguages;
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
                String purgeKey = original.get(o).trim().toLowerCase();
                if (purgeKey.contains(".")) {
                    purgeKey = purgeKey.substring(0, purgeKey.indexOf("."));
                }
                lan.addSentence(purgeKey, translation.get(o));
            });
            list.add(lan);
        } catch (Exception e) {
           MessageUtils.log(MessageUtils.MessageType.ERROR, "Error in load dictionaries ".concat(e.getMessage()));
        }
    }

    private static HashMap<Integer, String> getSentencesOfDictionary(String path) throws IOException {
        return Files.lines(Paths.get(path)).collect(HashMap<Integer, String>::new,
                (map, streamValue) -> map.put(map.size(), streamValue),
                (map, map2) -> {
                });
    }

    public static String readFile(Path path) throws IOException {
        String result = new String();
        try (BufferedReader reader = Files.newBufferedReader(
                path, StandardCharsets.UTF_8)) {
            result = reader.lines()
                    .limit(15)
                    .map(String::new)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
           MessageUtils.log(MessageUtils.MessageType.ERROR, "Error in read file".concat(e.getMessage()));
        }
        return result;
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

    public static List<FileData> getListaFileData(Path path) throws IOException {
        try (Stream<Path> stream = Files.list(path)) {
            return stream.map(
                    f -> new FileData(f.getFileName().toString(),
                            f.getParent().toString().concat("/"))
            ).collect(Collectors.toList());
        }
    }
}
