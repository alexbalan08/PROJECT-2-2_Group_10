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
        this.questionToken = "?";
        this.type = "";
        this.generateSentence();
    }

    public String findSentence(String input) {
        for(var entry : this.sentences.entrySet()) {
            for(String next : entry.getValue()) {
                if(StringUtils.areSimilarSentences(input, next, 1)) {
                    return entry.getKey();
                }
                if(StringUtils.areSimilarSentences(input, next, 0.5)) {
                    return entry.getKey();
                }
            }
        }
        return "I don't know.";
    }

    public void generateSentence() {
        for(String entry : this.rules.get("<ACTION>")) {
            this.type = entry.trim();
            replace(entry);
        }
    }

    public String getExamplesOfQuestions() {
        StringBuilder resume = new StringBuilder();
        for(var entry : this.sentences.entrySet()) {
            resume.append(entry.getKey().replace("<", "").replace(">", "")).append(" : \n");
            resume.append("- ").append(entry.getValue().get(0)).append(" \n");
            resume.append("\n");
        }
        resume.append("SPOTIFY API").append("\n").append("- Can you play \"TITLE\" ?").append("\n\n");
        resume.append("WEATHER API").append("\n").append("- What is the temperature at \'CITY\' ?").append("\n\n");
        resume.append("CANVAS API").append("\n").append("- For the course COURSE can you find 'TOPIC' ?").append("\n\n");
        resume.append("WIKIPEDIA API").append("\n").append("- Can you explain to me what is SUBJECT ?").append("\n");
        return resume.toString();
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
        sentence = sentence.trim().replace("  ", " ").replace("  ", " ");
        if(!sentence.endsWith(this.questionToken)) {
            sentence = sentence + " " + this.questionToken;
        }
        return sentence.toLowerCase();
    }
}
