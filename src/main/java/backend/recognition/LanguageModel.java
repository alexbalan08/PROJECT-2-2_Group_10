package backend.recognition;

import UI.HelloApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageModel implements SkillRecognition, SlotRecognition {
    private String skill;
    private static String accessToken = "70752a229df0f14eacfde52a678b32b9";
    private static String clip_API_URL = "https://api.clip.jina.ai:8443/post";
    static String[] canvasSkill = {"In which lecture slides of Theoretical Computer Science can you find about 'regular expressions' ?", "For the course Calculus where can you find about 'multivariate' ?", "Where can I find 'matrix multiplication' in Linear Algebra ?", "Can you find the topic 'MRI' in the course Computational and Cognitive Neuroscience ?"};
    static String[] spotifyPlaySkill = {"Can you play the song 'Mad World' ?", "Can you play 'Bohemian Rhapsody' ?"};
    static String[] spotifyStopSkill = {"Can you stop the song 'Mad World' ?", "Can you stop 'Bohemian Rhapsody' ?", "Can you pause the song 'Dancing Queen' ?", "Can you pause 'Love Tonight' ?"};
    static String[] spotifyResumeSkill = {"Can you resume the song 'Mad World' ?", "Can you resume 'Bohemian Rhapsody' ?", "Can you replay the song 'Dancing Queen' ?", "Can you replay 'Love Tonight' ?"};
    static String[] spotifyInfoSkill = {"What is the music at the moment ?", "What is the music playing ?", "What song does Spotify play ?"};
    static String[] weatherPlaceSkill = {"Can you tell me about the weather in Maastricht ?", "What is the weather in Amsterdam ?", "Tell me the weather in Liege", "How is the weather in Paris ?"};
    static String[] weatherPlaceTimeSkill = {"What will the weather be like in Liege at 9am ?", "What weather will Maastricht have at 2pm ?"};
    static String[] wikipediaSkill = {"Can you explain to me what is genetically modified organism ?", "What is the definition of human genome ?", "What does Wikipedia say about the Renaissance ?", "Can you explain to me what Artificial Intelligence is ?"};
    static String[] cities = {"maastricht", "liege", "amsterdam", "brussels", "madrid", "paris", "milan", "athens", "rome", "london", "lisboa", "berlin", "prague", "stockholm", "vienna"};


    public List<List<Double>> encode(List<String> sentences) throws IOException {
        List<List<Double>> vectorRepresentations = new ArrayList<>();

        URL url = new URL(clip_API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", accessToken);
        con.setDoOutput(true);

        String jsonInputString = "{\"data\":[";
        for (String sentence: sentences) {
            jsonInputString += "{\"text\": \"" + sentence + "\"}, ";
        }
        jsonInputString = jsonInputString.strip().substring(0, jsonInputString.length() - 2);
        jsonInputString += "], \"execEndpoint\":\"/\"}";

        System.out.println("out:\n" + jsonInputString);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        StringBuilder response;
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        ObjectMapper om = new ObjectMapper();
        Object jsonOb = om.readValue(response.toString(), Object.class);
        // pretty print
        String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonOb);
        System.out.println(json);
        JsonNode rootData = om.readTree(json);
        Iterator<JsonNode> elements = rootData.elements();
        elements.next();
        elements.next();
        elements.next();
        Iterator<JsonNode> data = elements.next().elements();
        while (data.hasNext()) {
            JsonNode dataElement = data.next();
            Iterator<JsonNode> embeddingData = dataElement.get("embedding").elements();
            List<Double> embeddingElementsList = new ArrayList<>();
            while (embeddingData.hasNext()) {
                embeddingElementsList.add(embeddingData.next().asDouble());
            }
            vectorRepresentations.add(embeddingElementsList);
        }

        return vectorRepresentations;
    }

    public static double cosineSimilarity(List<Double> vector1, List<Double> vector2) {
        double dotProduct = 0;
        for (int i = 0; i < vector1.size(); i++)
            dotProduct += vector1.get(i) * vector2.get(i);
        return dotProduct / (vectorNorm(vector1) * vectorNorm(vector2));
    }

    public static double vectorNorm(List<Double> vector) {
        double sum = 0;
        for (double vectorValue : vector)
            sum += vectorValue * vectorValue;
        return Math.sqrt(sum);
    }

    public Map<List<String>, String> createSkillsExamples() {
        Map<List<String>, String> skillsExamples = new HashMap<>();
        skillsExamples.put(List.of(canvasSkill), "Canvas");
        skillsExamples.put(List.of(spotifyPlaySkill), "SpotifyPlay");
        skillsExamples.put(List.of(spotifyStopSkill), "SpotifyStop");
        skillsExamples.put(List.of(spotifyResumeSkill), "SpotifyResume");
        skillsExamples.put(List.of(spotifyInfoSkill), "SpotifyInfo");
        skillsExamples.put(List.of(weatherPlaceSkill), "WeatherPlace");
        skillsExamples.put(List.of(weatherPlaceTimeSkill), "WeatherPlaceTime");
        skillsExamples.put(List.of(wikipediaSkill), "Wikipedia");

        return skillsExamples;
    }

    public String[] findSlot(String query) {
        String[] slots = null;
        String stringInQuotes = "";
        boolean areThereQuotesInQuery = query.indexOf("\"") != query.lastIndexOf("\"") && (query.indexOf("\"") != query.lastIndexOf("\"") - 1);
        boolean areThereSingleQuotesInQuery = query.indexOf("'") != query.lastIndexOf("'") && (query.indexOf("'") != query.lastIndexOf("'") - 1);
        if (areThereQuotesInQuery || areThereSingleQuotesInQuery) {
            String quote = areThereQuotesInQuery ? "\"" : "'";
            stringInQuotes = query.substring(query.indexOf(quote) + 1, query.lastIndexOf(quote));
        }
        if (skill.equals("Canvas") || skill.equals("SpotifyPlay") || skill.equals("SpotifyStop") || skill.equals("SpotifyResume") || skill.equals("Wikipedia")) {
            int index;
            if (skill.equals("Wikipedia"))
                index = 1;
            else
                index = 2;
            slots = new String[index];
            if (!stringInQuotes.isEmpty())
                slots[index - 1] = stringInQuotes;
            else
                return slots;
        }
        switch (skill) {
            case "Canvas" -> {
                String[] courses = {"calculus", "neuroscience", "data structures", "databases", "discrete math", "graph theory", "human computer interaction", "ict", "computer science 1", "computer science 2", "data science", "algebra", "logic", "machine learning", "mathematical modelling", "numerical math", "probability and statistic", "project 1-1", "project 1-2", "project 2-1", "project 2-2", "reasoning", "statistical analysis", "software", "theoretical"};
                for (String course : courses) {
                    if (query.toLowerCase().contains(course))
                        slots[0] = course;
                }
                if (slots[0] == null)
                    slots[0] = "Non existent";
            }
            case "SpotifyPlay" -> slots[0] = "play";
            case "SpotifyStop" -> slots[0] = "stop";
            case "SpotifyResume" -> slots[0] = "resume";
            case "SpotifyInfo" -> slots = new String[]{"info"};
            case "WeatherPlace" -> {
                for (String city : cities) {
                    if (query.toLowerCase().contains(city)) {
                        System.out.println("The city is: " + city);
                        slots = new String[]{city.substring(0, 1).toUpperCase() + city.substring(1)};
                    }
                    else
                        return slots;
                }
            }
            case "WeatherPlaceTime" -> {
                slots = new String[2];
                for (String city : cities) {
                    if (query.toLowerCase().contains(city))
                        slots[0] = city.substring(0, 1).toUpperCase() + city.substring(1);
                    else
                        return slots;
                }
                Matcher matcher = Pattern.compile("\\d+am|\\d\\d+pm").matcher(query);
                String time = "";
                if (matcher.find()) {
                    time = matcher.group();
                }
                if (time.isEmpty())
                    return null;
                else
                    slots[1] = time;
            }
        }
        return slots;
    }

    public String botResponse(String[] slots) {
        String output = "It seems like you want to ";
        Map<String, String> questionsTemplate = new HashMap<>();
        questionsTemplate.put("Canvas", "In which lecture slides of <COURSE> can you find <TOPIC> ?");
        questionsTemplate.put("SpotifyPlay", "Play \"<TITLE>\" ?");
        questionsTemplate.put("SpotifyStop", "Stop \"<TITLE>\" ?");
        questionsTemplate.put("SpotifyResume", "Resume \"<TITLE>\" ?");
        questionsTemplate.put("WeatherPlace", "Can you tell me about the weather in <PLACE> ?");
        questionsTemplate.put("WeatherPlaceTime", "What will the weather be like in <PLACE> at <TIME> ?");
        questionsTemplate.put("Wikipedia", "What is the definition of <SUBJECT> ?");

        switch (skill) {
            case "Canvas" -> {
                output = output.concat("find the slides where ");
                if (slots != null) {
                    output = output.concat("the topic '" + slots[0] + "' appears inside the given course.\n");
                } else {
                    output = output.concat("a topic appears inside a certain course. Can you write it in the following form to know the desired course and topic to look for:\n");
                    output = output.concat(questionsTemplate.get("Canvas"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("Canvas"));
                }
            }
            case "SpotifyPlay" -> {
                output = output.concat("play ");
                if (slots != null) {
                    output = output.concat("the song '" + slots[1] + "'.\n");
                } else {
                    output = output.concat("a song.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("SpotifyPlay"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("SpotifyPlay"));
                }
            }
            case "SpotifyStop" -> {
                output = output.concat("stop ");
                if (slots != null) {
                    output = output.concat("the song '" + slots[1] + "'.\n");
                } else {
                    output = output.concat("a song.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("SpotifyStop"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("SpotifyStop"));
                }
            }
            case "SpotifyResume" -> {
                output = output.concat("resume ");
                if (slots != null) {
                    output = output.concat("the song '" + slots[1] + "'.\n");
                } else {
                    output = output.concat("a song.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("SpotifyResume"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("SpotifyResume"));
                }
            }
            case "SpotifyInfo" -> output = output.concat("know about the music that is playing.\n");
            case "WeatherPlace" -> {
                output = output.concat("know about the weather in ");
                if (slots != null) {
                    output = output.concat(slots[0] + ".\n");
                } else {
                    output = output.concat("a certain place.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("WeatherPlace"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("WeatherPlace"));
                }
            }
            case "WeatherPlaceTime" -> {
                output = output.concat("know about the weather in ");
                if (slots != null) {
                    output = output.concat(slots[0] + " at time " + slots[1] + ".\n");
                } else {
                    output = output.concat("a certain place at a certain time.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("WeatherPlaceTime"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("WeatherPlaceTime"));
                }
            }
            case "Wikipedia" -> {
                output = output.concat("know about ");
                if (slots != null) {
                    output = output.concat("the topic '" + slots[0] + "' that can be found in Wikipedia.\n");
                } else {
                    output = output.concat("a certain topic that can be found in Wikipedia.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("Wikipedia"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("Wikipedia"));
                }
            }
        }

        return output;
    }

    // Detect what skill does the query want
    public String determineSkill(String query) throws IOException {
        Map<List<String>, String> skillsExamples = createSkillsExamples();

        Map<List<List<Double>>, String> vectorizedSkillsExamples = new HashMap<>();
        for (Map.Entry<List<String>, String> entry : skillsExamples.entrySet()) {
            List<List<Double>> vectorizedSkillExamples = encode(entry.getKey());
            vectorizedSkillsExamples.put(vectorizedSkillExamples, entry.getValue());
        }

        List<List<Double>> vectorizedQuery = encode(List.of(new String[]{query}));

        double maxCosimilarity = 0;
        String closestSkillToQuery = "";
        for (Map.Entry<List<List<Double>>, String> entry : vectorizedSkillsExamples.entrySet()) {
            List<List<Double>> vectorizedSkillExamples = entry.getKey();
            double sum = 0;
            for (List<Double> vectorizedSkillExample : vectorizedSkillExamples) {
                sum += cosineSimilarity(vectorizedSkillExample, vectorizedQuery.get(0));
            }
            double averageCosimilarity = sum / vectorizedSkillExamples.size();
            System.out.println("The average cosine similarity with the skill \'" + entry.getValue() + "\' is: " + averageCosimilarity);

            if (averageCosimilarity > maxCosimilarity) {
                maxCosimilarity = averageCosimilarity;
                closestSkillToQuery = entry.getValue();
            }
        }
        skill = closestSkillToQuery;
        System.out.println("The closest skill to the query is: " + closestSkillToQuery);

        if (closestSkillToQuery.contains("Spotify"))
            return "Spotify";
        else if (closestSkillToQuery.contains("Weather"))
            return "Weather";

        return closestSkillToQuery;
    }

    public static void main(String[] args) throws IOException {
        LanguageModel lm = new LanguageModel();
        String[] spotifyPlaySkill = {"Can you play the song 'Mad World' ?", "Can you play 'Bohemian Rhapsody' ?"};
        String query = "what weather does Maastricht have right now?";
        //lm.determineSkill(query);
        lm.encode(List.of(spotifyPlaySkill));
    }
}