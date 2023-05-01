package backend.CFG;

import backend.Skills.WeatherData.Sys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFG {
    private Map<String, List<String>> rules;

    public CFG() {
        this.rules = new HashMap<>();
        this.addRules();
        System.out.println(this.generateSentence2("S"));
    }

    /**
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
     * */
    private void addRules() {
        this.rules.put("S", List.of("ACTION"));
        this.rules.put("ACTION", Arrays.asList("SCHEDULE", "LOCATION"));
        this.rules.put("SCHEDULE", List.of("Which lectures are there <TIMEEXPRESSION>"));
        this.rules.put("TIMEEXPRESSION", List.of("on <DAY> at <TIME>", "at <TIME> on <DAY>"));
        this.rules.put("TIME", List.of("9", "12"));
        this.rules.put("DAY", List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
        this.rules.put("LOCATION", List.of("Where is <ROOM>",  "How do <PRO> get to <ROOM>"));
        this.rules.put("ROOM", List.of("DeepSpace", "SpaceBox"));
        this.rules.put("PRO", List.of("I", "You", "He", "She"));
    }

    public void generateSentence() {
        StringBuilder sentence = new StringBuilder();
        for(String entry : this.rules.get("S")) {
            sentence.append("--- ").append(replace(entry)).append(" \n");
        }
        System.out.println("CFG :");
        System.out.println(sentence);
    }

    private String replace(String entry) {
        StringBuilder sentence = new StringBuilder();
        // CHECK IF <> in ?
        if(entry.contains("<")) {

        } else {
            // SPLIT ON SPACE
            for(String split : entry.split(" ")) {
                if(this.rules.containsKey(split)) {
                    List<String> nextEntry = this.rules.get(split);
                    for(String next : nextEntry) {
                        sentence.append("(").append(this.replace(next)).append(")").append(" ");
                    }
                } else {
                    sentence.append(" ").append(split);
                }
            }
        }
        return sentence.toString();
    }

    private String replaceChevron(String entry) {
        String chevron = entry.substring(entry.indexOf("<") + 1, entry.indexOf(">"));
        if(this.rules.containsKey(chevron)) {
            return entry.substring(0, entry.indexOf("<")) + this.rules.get(chevron).get(0) + entry.substring(entry.indexOf(">") + 1);
        }
        return entry;
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
            result = new StringBuilder(replaceChevron(result.toString()));
        }

        return result.toString().trim();
    }
}
