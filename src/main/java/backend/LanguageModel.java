package backend;

import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class LanguageModel {
    private String skillsTemplateContent = Files.readString(Path.of("./src/main/java/backend/Skills/SkillsTemplate.txt"));
    private Map<String, List<String>> slotsExamples = createSlotsExamples();
    private Map<Integer, List<String>> allExamples = createExamples();
    private String accessToken = "70752a229df0f14eacfde52a678b32b9";
    private String clip_API_URL = "https://api.clip.jina.ai:8443";

    public LanguageModel() throws Exception {
    }

    public HashMap<String, List<String>> createSlotsExamples() throws Exception {
        HashMap<String, List<String>> slotsExamples = new HashMap<>();
        String contentToRead = skillsTemplateContent;

        while (contentToRead.contains("<")) {
            if (contentToRead.contains("Question") && contentToRead.contains("Slot") && contentToRead.indexOf("<") > contentToRead.indexOf("Slot")) contentToRead = contentToRead.substring(contentToRead.indexOf("Question"));
            else if (contentToRead.contains("Slot") && contentToRead.indexOf("<") > contentToRead.indexOf("Slot")) return slotsExamples;
            contentToRead = contentToRead.substring(contentToRead.indexOf("<") + 1);

            if (!contentToRead.contains(">"))
                throw new Exception("SkillsTemplate must have slots inside the form \'<>\'");

            String slot = contentToRead.substring(0, contentToRead.indexOf(">"));
            List<String> slotExamples;
            contentToRead = contentToRead.substring(contentToRead.indexOf(">") + 1);
            String skill = contentToRead.contains("Question") ? contentToRead.split("Question")[0] : contentToRead;
            if (skill.contains("\nSlot <" + slot + "> ")) {
                skill = skill.substring(skill.indexOf("\nSlot <" + slot + "> "), skill.indexOf("\nAction"));
                slotExamples = Arrays.stream(skill.split("\nSlot <" + slot + "> ")).filter(Predicate.not(String::isEmpty)).filter(str -> !str.contains("Slot")).toList();
            } else
                slotExamples = slotExamplesOutOfSkillsTemplate(slot);
            slotsExamples.put(slot, slotExamples);
        }

        return slotsExamples;
    }

    private ArrayList<String> slotExamplesOutOfSkillsTemplate(String slot) {
        ArrayList<String> slotExamples = new ArrayList<>();
        String[] courseSlot = {"calculus", "theoretical computer science", "discrete mathematics", "mathematical modelling", "human computer interaction"};
        String[] topicSlot = {"multivariate", "regular expression", "power set", "system", "design"};
        if (slot.equals("COURSE")) slotExamples.addAll(Arrays.asList(courseSlot));
        else if (slot.equals("TOPIC")) slotExamples.addAll(Arrays.asList(topicSlot));
        else slotExamples.add(slot);
        return slotExamples;
    }

    public Map<Integer, List<String>> createExamples() {
        Map<Integer, List<String>> skillsExamples = new HashMap<>();
        String skillsToRead = skillsTemplateContent;
        int skillCount = 1;
        while (skillsToRead.contains("Question")) {
            skillsToRead = skillsToRead.substring(skillsToRead.indexOf("Question"));

            Integer skillNumber = skillCount;
            String templateQuestion = skillsToRead.substring("Question ".length(), skillsToRead.indexOf("\n")).strip();

            String[] questionSeparator = templateQuestion.split("]|>");
            List<List<String>> allPossibleStrings = new ArrayList<>();
            for (String separated : questionSeparator) {
                if (separated.contains("<"))
                    allPossibleStrings.add(slotsExamples.get(separated.strip().replaceFirst("<", "")));
                else
                    allPossibleStrings.add(Arrays.stream(separated.strip().replaceFirst("\\[", "").split(", ")).toList());
            }

            List<String> skillExamples = stringsCombinations(allPossibleStrings, 0, new ArrayList<>(), new ArrayList<>());
            skillsExamples.put(skillNumber, skillExamples);

            skillExamples.stream().limit(3).forEach(s -> System.out.println('"' + s + '"'));

            skillsToRead = skillsToRead.substring("Question".length());
            skillCount++;
        }
        return skillsExamples;
    }

    public List<String> stringsCombinations(List<List<String>> allPossibleStrings, int position, List<String> exampleSubstrings, List<String> skillExamples) {
        if (position == allPossibleStrings.size()) {
            String skillExample = "";
            for (String exampleSubstring : exampleSubstrings) {
                skillExample += exampleSubstring + " ";
            }
            skillExamples.add(skillExample.strip());
            return skillExamples;
        }

        List<String> stringsPosition = allPossibleStrings.get(position);
        for (String possibleString : stringsPosition) {
            exampleSubstrings.add(position, possibleString);
            skillExamples = stringsCombinations(allPossibleStrings, position+1, exampleSubstrings, skillExamples);
            exampleSubstrings.remove(position);
        }
        return skillExamples;
    }

    public void skillSimilarity() throws IOException {
        /*String[] examples = '{"data":[{"text": "First do it"},
        {"text": "then do it right"},
        {"text": "then do it better"},
        {"uri": "https://picsum.photos/200"}],
        "execEndpoint":"/"}'*/

        URL url = new URL(clip_API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String jsonInputString = "{\"data\":[{\"text\": \"First do it\"}, {\"text\": \"then do it right\"}, {\"text\": \"then do it better\"}, {\"uri\": \"https://picsum.photos/200\"}], \"execEndpoint\":\"/\"}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response);
        }
    }

    public static void main(String[] args) throws Exception {
        LanguageModel model = new LanguageModel();
        model.createExamples();
        System.out.println();
        //System.out.println(model.allExamples.get(0).stream().map(s -> "\"text\": \"" + s + '"').toList());

//        String question = "[in which lecture slides of, for the course] <COURSE> [can you find] <TOPIC>";
    }
}