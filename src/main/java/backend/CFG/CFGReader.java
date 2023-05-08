package backend.CFG;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CFGReader {

    private final String fileURL;
    private final Map<String, List<String>> rules;
    private final Map<String, Map<String, String>> actions;

    public CFGReader(String url) {
        this.fileURL = url;
        this.rules = new HashMap<>();
        this.actions = new HashMap<>();
        this.readFile();
    }

    public Map<String, List<String>> getRules() {
        return this.rules;
    }

    public Map<String, Map<String, String>> getActions() {
        return this.actions;
    }

    private void readFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.fileURL))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("Rule")) {
                    this.addARule(line.substring(line.indexOf(" ") + 1));
                } else if(line.startsWith("Action")) {
                    this.addAnAction(line.substring(line.indexOf(" ") + 1));
                }
            }
        } catch (IOException e) {
            System.out.println("Error with the file : " + this.fileURL);
        }
    }

    /*** RULES ***/
    private void addARule(String line) {
        String key = line.substring(0, line.indexOf(">") + 1);
        String value = line.substring(line.indexOf(">") + 1);
        List<String> values = new ArrayList<>(Arrays.asList(value.split("\\|")));
        this.rules.put(key, values);
    }

    /*** ACTIONS ***/
    private void addAnAction(String line) {
        String key = line.substring(0, line.indexOf(">") + 1);
        String value = line.substring(line.indexOf("*") + 2, line.lastIndexOf("*") - 1);
        String answer = line.substring(line.lastIndexOf("*") + 2);
        String secondKey = getSecondKeyFromValue(value);
        if(secondKey.equals("")) {
            List<String> pol = this.rules.get(value);
            if(pol == null) {
                this.addInSecondMap(key, "*KEY*", answer);
            } else {
                for(String p : pol) {
                    if(!this.actions.get(key.trim()).containsKey(p.trim().toLowerCase())) {
                        // value += (" " + p).trim();
                        secondKey = p.trim();
                        String temp = (p.trim() + " " + answer.trim()).trim();
                        this.addInSecondMap(key, secondKey, temp);
                    }
                }
            }
        } else {
            this.addInSecondMap(key, secondKey, answer);
        }
    }

    private String getSecondKeyFromValue(String value) {
        int index = value.indexOf("<");
        String temp = "";
        while (index != -1) {
            value = value.substring(value.indexOf(">") + 1);
            index = value.indexOf("<");
            if(index != -1) {
                temp += value.substring(0, index);
            } else {
                temp += value;
            }
        }
        return temp.trim();
    }

    private void addInSecondMap(String key, String secondKey, String answer) {
        secondKey = secondKey.toLowerCase();
        if(this.actions.containsKey(key)) {
            Map<String, String> second = this.actions.get(key);
            second.put(secondKey, answer);
        } else {
            Map<String, String> second = new HashMap<>();
            second.put(secondKey, answer);
            this.actions.put(key, second);
        }
    }
}