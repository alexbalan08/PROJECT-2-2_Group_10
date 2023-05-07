package backend.CFG;

import backend.Skills.WeatherData.Sys;

import java.util.*;

public class CFG {
    private final Map<String, List<String>> rules;
    private final List<String> sentences;
    private final String questionToken;

    public CFG(Map<String, List<String>> rules) {
        this.rules = rules;
        this.sentences = new ArrayList<>();
        this.questionToken = " ?";
        this.generateSentence();
        this.printSentences();
    }

    private void printSentences() {
        System.out.println();
        System.out.println("CFG :");
        for(String sentence : this.sentences) {
            System.out.println(sentence);
        }
        System.out.println();
    }

    public void generateSentence() {
        for(String entry : this.rules.get("<ACTION>")) {
            replace(entry);
        }
    }

    private void replace(String entry) {
        if(this.rules.containsKey(entry)) {
            for(String next : this.rules.get(entry)) {
                replace(next);
            }
        }
        replaceRule(entry);
    }

    private void replaceRule(String entry) {
        while (entry.contains("<")) {
            entry = replaceChevron(entry);
        }
    }

    private String replaceChevron(String entry) {
        String chevron = entry.substring(entry.indexOf("<"), entry.indexOf(">") + 1);
        if(this.rules.containsKey(chevron)) {
            for(String next : this.rules.get(chevron)) {
                String temp = entry.substring(0, entry.indexOf("<")) + next + entry.substring(entry.indexOf(">") + 1);
                if(temp.contains("<")) {
                    replaceChevron(temp);
                } else {
                    addIfNotExist(temp);
                }
            }
            entry = entry.substring(0, entry.indexOf("<")) + entry.substring(entry.indexOf(">") + 1);
        }
        return entry;
    }

    private void addIfNotExist(String sentence) {
        sentence = sentence.trim().replace("  ", " ").replace("  ", " ") + this.questionToken;
        if(!this.sentences.contains(sentence)) {
            this.sentences.add(sentence);
        }
    }
}
