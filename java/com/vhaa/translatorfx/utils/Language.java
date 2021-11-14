package com.vhaa.translatorfx.utils;

import java.util.*;

public class Language {

    private String from;

    private String to;

    private HashMap<String, String>sentences = new HashMap<>();

    public Language(String stringFrom, String stringTo) {
        this.from = stringFrom;
        this.to = stringTo;
    }

    public void addSentence(String original, String traducer) {
        if (Objects.isNull(original)  || Objects.isNull(traducer))
            return;
        sentences.put(original, traducer);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTraduction(String original){
        return sentences.get(original);
    }

    public Boolean getCurrentLanguage(String sentence){
        return !Objects.isNull(sentences.get(sentence)) ;
    }

    public static void main(String[] args) {
        Language lan = new Language("es", "en");
        lan.addSentence("hola", "hello");
        lan.addSentence("adios", "bye");
        lan.addSentence("auto", "car");
        lan.getTraduction("hola");
    }

}
