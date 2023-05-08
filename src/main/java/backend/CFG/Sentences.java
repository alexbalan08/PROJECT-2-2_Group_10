package backend.CFG;

import utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sentences {
    private final Map<String, List<String>> rules;
    private final Map<String, List<String>> sentences;
    private final String questionToken;
    private String type;

    public Sentences(Map<String, List<String>> rules) {
        this.rules = rules;
        this.sentences = new HashMap<>();
        this.questionToken = " ?";
        this.type = "";
        this.generateSentence();
        // this.printSentences();
    }

    public String findSentence(String input) {
        for(var entry : this.sentences.entrySet()) {
            for(String next : entry.getValue()) {
                if(StringUtils.areSimilarSentences(input, next)) {
                    return entry.getKey();
                }
            }
        }
        return "";
    }

    private void printSentences() {
        System.out.println();
        System.out.println("CFG :");
        for(var entry : this.sentences.entrySet()) {
            System.out.println(entry.getKey() + " : ");
            for(String next : entry.getValue()) {
                System.out.println("--- " + next);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void generateSentence() {
        for(String entry : this.rules.get("<ACTION>")) {
            this.type = entry.trim();
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
            entry = replaceArrows(entry);
        }
    }

    private String replaceArrows(String entry) {
        String chevron = entry.substring(entry.indexOf("<"), entry.indexOf(">") + 1);
        if(this.rules.containsKey(chevron)) {
            for(String next : this.rules.get(chevron)) {
                String temp = entry.substring(0, entry.indexOf("<")) + next + entry.substring(entry.indexOf(">") + 1);
                if(temp.contains("<")) {
                    replaceArrows(temp);
                } else {
                    addIfNotExist(temp);
                }
            }
            entry = entry.substring(0, entry.indexOf("<")) + entry.substring(entry.indexOf(">") + 1);
        }
        return entry;
    }

    private void addIfNotExist(String sentence) {
        sentence = this.clearSentence(sentence);
        if(this.sentences.containsKey(this.type)) {
            List<String> second = this.sentences.get(this.type);
            second.add(sentence);
        } else {
            List<String> second = new ArrayList<>();
            second.add(sentence);
            this.sentences.put(this.type, second);
        }
    }

    private String clearSentence(String sentence) {
        sentence = sentence.trim().replace("  ", " ").replace("  ", " ") + this.questionToken;
        return sentence.toLowerCase();
    }
}
