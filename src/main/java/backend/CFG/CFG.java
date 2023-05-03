package backend.CFG;

import backend.Skills.WeatherData.Sys;

import java.util.*;

public class CFG {
    private final Map<String, List<String>> rules;
    private final List<String> sentences;
    private final String questionToken;

    public CFG() {
        this.rules = new HashMap<>();
        this.sentences = new ArrayList<>();
        this.questionToken = " ?";
        this.addRules();
        this.generateSentence();
        this.printSentences();
    }

    /* *
     *
     *         this.rules.put("S", List.of("NP VP"));
     *         this.rules.put("NP", Arrays.asList("DET NOUN PP", "NUM NOUN"));
     *         this.rules.put("VP", List.of("VERB NP"));
     *         this.rules.put("DET", List.of("The"));
     *         this.rules.put("NOUN", Arrays.asList("temperature", "Dubai", "degrees"));
     *         this.rules.put("PP", List.of("PREP NOUN"));
     *         this.rules.put("PREP", List.of("in"));
     *         this.rules.put("VERB", List.of("is"));
     *         this.rules.put("NUM", List.of("30"));
     *
     * */

    private void addRules() {
        this.rules.put("S", List.of("ACTION"));
        this.rules.put("ACTION", Arrays.asList("LOCATION", "SCHEDULE"));
        this.rules.put("SCHEDULE", List.of("Which lectures are there <TIMEEXPRESSION>"));
        this.rules.put("TIMEEXPRESSION", List.of("on <DAY> at <TIME>", "at <TIME> on <DAY>"));
        this.rules.put("TIME", List.of("9", "12"));
        this.rules.put("DAY", List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
        this.rules.put("LOCATION", List.of("Where is <ROOM>",  "How do <PRO> get to <ROOM>"));
        this.rules.put("ROOM", List.of("DeepSpace", "SpaceBox"));
        this.rules.put("PRO", List.of("I", "you", "he", "she"));
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
        for(String entry : this.rules.get("ACTION")) {
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
        String chevron = entry.substring(entry.indexOf("<") + 1, entry.indexOf(">"));
        if(this.rules.containsKey(chevron)) {
            for(String next : this.rules.get(chevron)) {
                String temp = entry.substring(0, entry.indexOf("<")) + next + entry.substring(entry.indexOf(">") + 1);
                if(temp.contains("<")) {
                    replaceChevron(temp);
                } else {
                    addIfNotExist(temp.trim() + this.questionToken);
                }
            }
            entry = entry.substring(0, entry.indexOf("<")) + entry.substring(entry.indexOf(">") + 1);
        }
        return entry;
    }

    private void addIfNotExist(String sentence) {
        if(!this.sentences.contains(sentence)) {
            this.sentences.add(sentence);
        }
    }

    private String generateSentence2(String symbol) {
        if (!this.rules.containsKey(symbol)) {
            return symbol;
        }

        List<String> productionOptions = this.rules.get(symbol);
        String chosenProduction = productionOptions.get(0);
        String[] symbols = chosenProduction.split(" ");

        StringBuilder result = new StringBuilder();
        for (String s : symbols) {
            result.append(generateSentence2(s)).append(" ");
        }

        if(result.toString().contains("<")) {
            result = new StringBuilder(replaceChevron2(result.toString()));
        }

        return result.toString().trim();
    }

    private String replaceChevron2(String entry) {
        String chevron = entry.substring(entry.indexOf("<") + 1, entry.indexOf(">"));
        if(this.rules.containsKey(chevron)) {
            return entry.substring(0, entry.indexOf("<")) + this.rules.get(chevron).get(0) + entry.substring(entry.indexOf(">") + 1);
        }
        return entry;
    }
}
