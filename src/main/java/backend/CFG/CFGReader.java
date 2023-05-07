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

    private void readFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.fileURL))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("Rule")) {
                    line = line.substring(line.indexOf(" ") + 1);
                    String key = line.substring(0, line.indexOf(">") + 1);
                    String value = line.substring(line.indexOf(">") + 1);
                    List<String> values = new ArrayList<>(Arrays.asList(value.split("\\|")));
                    this.rules.put(key, values);
                } else if(line.startsWith("Action")) {
                    line = line.substring(line.indexOf(" ") + 1);
                    String key = line.substring(0, line.indexOf(">") + 1);
                    String value = line.substring(line.indexOf("*") + 2, line.lastIndexOf("*") - 1);
                    String answer = line.substring(line.lastIndexOf("*") + 2);
                    if(getValues(value).equals("")) {
                        List<String> pol = this.rules.get(value);
                        for(String p : pol) {
                            if(!this.actions.get(key).containsKey(p)) {
                                value += " " + p;
                                answer = p + " " + answer;

                            }
                        }
                    }
                    this.addInSecondMap(key, getValues(value), answer);
                }
            }
        } catch (IOException e) {
            System.out.println("Error with the file : " + this.fileURL);
        }
    }

    public Map<String, List<String>> getRules() {
        return this.rules;
    }

    public Map<String, Map<String, String>> getActions() {
        return this.actions;
    }

    private void addInSecondMap(String key, String value, String answer) {
        if(this.actions.containsKey(key)) {
            Map<String, String> second = this.actions.get(key);
            second.put(value, answer);
        } else {
            Map<String, String> second = new HashMap<>();
            second.put(value, answer);
            this.actions.put(key, second);
        }
    }

    private String getValues(String value) {
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
        return temp;
    }
}