package backend.recognition;

import UI.HelloApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
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
    private static final double confidence = 0.7;
    private static String accessToken = "70752a229df0f14eacfde52a678b32b9";
    private static String clip_API_URL = "https://api.clip.jina.ai:8443/post";
    static String[] canvasSkill = {"In which lecture slides of Theoretical Computer Science can you find about regular expressions", "For the course Calculus where can you find about 'multivariate'", "Where can I find 'matrix multiplication' in Linear Algebra", "Can you find the topic MRI in the course Computational and Cognitive Neuroscience", "Where could multithreading be in Databases", "For Numerical Mathematics I want to find 'Euler method'", "Are there slides for 'recursion' in Computer Science 1", "Where does Software Engineering have slides for 'design patterns'", "Where to find 'best first search' in the Reasoning Techniques course", "What the heck is universal constant for Logic course"};
    static String[] spotifyPlaySkill = {"Can you play the song 'Mad World'", "I would like you to play Bohemian Rhapsody", "Can I listen to 'Dancing Queen'", "I want to listen to The Logical Song", "I want to listen to the song 'Shame on You'", "Can 'Breakfast in America' be played", "Play 'Love Tonight'", "Please put 'Dancing Queen'", "Put 'Zombie' on", "Put Waka Waka by Shakira", "Could you play 'Something Just Like This' by Coldplay", "Play the song 'Thank You'"};
    static String[] spotifyStopSkill = {"Can you stop Mad World", "Stop 'Bohemian Rhapsody'", "Can you pause 'Dancing Queen'", "Pause 'Love Tonight'", "Stop the music", "Stop the song", "Pause what's sounding", "Could you pause the song", "Stop playing the song", "Can you shut up the music"};
    static String[] spotifyResumeSkill = {"Can you resume 'Mad World'", "Keep on playing 'Bohemian Rhapsody'", "Can you replay 'Dancing Queen'", "Replay 'Love Tonight'", "Resume the music", "Resume the song", "Keep playing what was sounding", "Could you replay the song", "Replay the song", "Keep playing Spotify"};
    static String[] spotifyInfoSkill = {/*"What's the music at the moment", "What's that music playing", "What song is Spotify playing", */"What's the name of the track playing in the background in Spotify", "Can you tell me the artist of the song playing now in Spotify", "What's the album of the current track playing", "Who wrote and produced the song that is playing right now", "Can you give me some information about the current track"/*, "Give me some info about the current song", "Can I know more about this song"*/};
    static String[] weatherPlaceSkill = {"Can you tell me about the weather in Maastricht", "What weather does Amsterdam have now", "Tell me the forecast for Liege", "How cold is it in Paris", "What's the current temperature in Vienna", "Can you give me the weather condition for Rome", "How is the temperature now in Athens", "Is it hot in Madrid right now", "Gimme the current weather here in Stockholm", "London is freaking cold, what's the current temperature"};
    static String[] weatherPlaceTimeSkill = {"What will the weather be like in Liege at 9am", "What weather will Maastricht have at 2pm", "What will be the weather in Amsterdam at 3pm", "Can you tell me the forecast for Paris at 5pm", "How cold will it be in Madrid at 10am today", "Can you give me the weather condition for Rome at 11am", "Can you tell me the temperature in Vienna at 5am", "At 10pm, what will the temperature be like in Athens", "What will be the weather like in Stockholm at 7pm tonight", "How hot will it be at 12pm in Brussels"};
    static String[] wikipediaSkill = {"Can you explain to me what are genetically modified organism", "What is the definition of human genome", "What does Wikipedia say about 'the Renaissance'", "Can you explain to me what 'Artificial Intelligence' is", "Can you look up in Wikipedia what is gravitational force", "What are the characteristics for parabolas", "What can Wikipedia tell me about the topic 'evolution'", "Give me some info about 'fungi'", "I want to know what the heck is impressionism according to Wikipedia", "Tell me about 'simile'"};
    static String[] randomSkill = {"yoyoyo", "what's up", "am I pretty", "you are boring", "fretwe cv4 5rv", "erfhgbhew", "erigbwr45tgryv", "are you human", "I like you", "I'm in the mood for going crazy", "I love playing tennis", "how are you doing", "I feel I need to go to the bathroom", "what if I'm hungry", "I want to travel somewhere", "what's the best place to drink", "am I older than you", "Mary went down to the farm", "what is wrong with people"};
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
        for (String sentence : sentences) {
            jsonInputString += "{\"text\": \"" + sentence + "\"}, ";
        }
        jsonInputString = jsonInputString.strip().substring(0, jsonInputString.length() - 2);
        jsonInputString += "], \"execEndpoint\":\"/\"}";

        System.out.println("out:\n" + jsonInputString);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        StringBuilder response;
        try (BufferedReader br = new BufferedReader(
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
        //skillsExamples.put(List.of(weatherPlaceTimeSkill), "WeatherPlaceTime");
        skillsExamples.put(List.of(wikipediaSkill), "Wikipedia");
        skillsExamples.put(List.of(randomSkill), "Random");

        return skillsExamples;
    }

    public List<String> findSlot(String query) {
        String[] slots = null;
        String stringInQuotes = "";
        boolean areThereSingleQuotesInQuery = query.indexOf("'") != query.lastIndexOf("'") && (query.indexOf("'") != query.lastIndexOf("'") - 1);
        if (areThereSingleQuotesInQuery) {
            stringInQuotes = query.substring(query.indexOf("'") + 1, query.lastIndexOf("'"));
        }
        if (skill.equals("Canvas") || skill.equals("SpotifyPlay") || skill.equals("Wikipedia")) {
            int index;
            if (skill.equals("Wikipedia"))
                index = 1;
            else
                index = 2;
            slots = new String[index];
            if (!stringInQuotes.isEmpty())
                slots[index - 1] = stringInQuotes;
            else
                return new ArrayList<>();
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
            case "SpotifyStop" -> slots = new String[]{"stop"};
            case "SpotifyResume" -> slots = new String[]{"resume"};
            case "SpotifyInfo" -> slots = new String[]{"info"};
            case "WeatherPlace" -> {
                for (String city : cities) {
                    if (query.toLowerCase().contains(city)) {
                        slots = new String[]{city.substring(0, 1).toUpperCase() + city.substring(1)};
                    }
                }
            }
            case "WeatherPlaceTime" -> {
                slots = new String[2];
                for (String city : cities) {
                    if (query.toLowerCase().contains(city))
                        slots[0] = city.substring(0, 1).toUpperCase() + city.substring(1);
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
        if (slots != null) {
            return List.of(slots);
        } else {
            return new ArrayList<>();
        }
    }

    public String botResponse(List<String> slots) {
        String output = "It seems like you want to ";
        Map<String, String> questionsTemplate = new HashMap<>();
        questionsTemplate.put("Canvas", "In which lecture slides of <COURSE> can you find <TOPIC> ?");
        questionsTemplate.put("SpotifyPlay", "Can you play \'<TITLE>\'");
        questionsTemplate.put("WeatherPlace", "Can you tell me about the weather in <PLACE> ?");
        questionsTemplate.put("WeatherPlaceTime", "What will the weather be like in <PLACE> at <TIME> ?");
        questionsTemplate.put("Wikipedia", "What is the definition of <SUBJECT> ?");

        switch (skill) {
            case "Canvas" -> {
                output = output.concat("find the slides where ");
                if (slots != null && slots.size() == 2 && !slots.get(0).equals("") && slots.get(0) != null && !slots.get(1).equals("") && slots.get(1) != null) {
                    output = output.concat("the topic '" + slots.get(1) + "' appears inside the given course.\n");
                } else {
                    output = output.concat("a topic appears inside a certain course. Can you write it in the following form to know the desired course and topic to look for:\n");
                    output = output.concat(questionsTemplate.get("Canvas"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("Canvas"));
                }
            }
            case "SpotifyPlay" -> {
                output = output.concat("play ");
                if (slots != null && slots.size() == 2 && !slots.get(1).equals("") && slots.get(1) != null) {
                    output = output.concat("the song '" + slots.get(1) + "'.\n");
                } else {
                    output = output.concat("a song.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("SpotifyPlay"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("SpotifyPlay"));
                }
            }
            case "SpotifyStop" -> {
                output = output.concat("stop the music that is playing.\n");
            }
            case "SpotifyResume" -> {
                output = output.concat("resume the music that is playing.\n");
            }
            case "SpotifyInfo" -> output = output.concat("know about the music that is playing.\n");
            case "WeatherPlace" -> {
                output = output.concat("know about the weather in ");
                if (slots != null && slots.size() == 1 && !slots.get(0).equals("") && slots.get(0) != null) {
                    output = output.concat(slots.get(0) + ".\n");
                } else {
                    output = output.concat("a certain place.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("WeatherPlace"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("WeatherPlace"));
                }
            }
            case "WeatherPlaceTime" -> {
                output = output.concat("know about the weather in ");
                if (slots != null && slots.get(0) != null && slots.get(1) != null) {
                    output = output.concat(slots.get(0) + " at time " + slots.get(1) + ".\n");
                } else {
                    output = output.concat("a certain place at a certain time.\nCan you write it in the form:\n");
                    output = output.concat(questionsTemplate.get("WeatherPlaceTime"));
                    HelloApplication.getInstance().addToTextArea(questionsTemplate.get("WeatherPlaceTime"));
                }
            }
            case "Wikipedia" -> {
                output = output.concat("know about ");
                if (slots != null && slots.size() == 1 && !slots.get(0).equals("") && slots.get(0) != null) {
                    output = output.concat("the topic '" + slots.get(0) + "' that can be found in Wikipedia.\n");
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
        query = query.replaceAll("\"", "'");
        query = query.replaceAll("\\|/", "");

        String embeddingsFilePath = "./src/main/java/backend/recognition/Embeddings.txt";
        File embeddingsFile = new File(embeddingsFilePath);

        Map<List<List<Double>>, String> vectorizedSkillsExamples = new HashMap<>();
        if (embeddingsFile.exists()) {
            String[] separatedSkillsExamples = Files.readString(Path.of(embeddingsFilePath)).split("\\n");
            for (String skillExamples : separatedSkillsExamples) {
                String[] separatedSkillExamples = skillExamples.split("]");

                String skillName = separatedSkillExamples[0].substring(0, separatedSkillExamples[0].indexOf(":"));

                List<List<Double>> vectorizedSkillExamples = new ArrayList<>();
                for (String skillExample : separatedSkillExamples) {
                    skillExample = skillExample.substring(skillExample.indexOf("[") + 1);

                    String[] vectorElements = skillExample.split(",");
                    List<Double> vectorExample = new ArrayList<>();
                    for (String vectorElement : vectorElements) {
                        Double doubleElement = Double.parseDouble(vectorElement);
                        vectorExample.add(doubleElement);
                    }
                    System.out.println("DIMENSIONS: " + vectorExample.size());
                    vectorizedSkillExamples.add(vectorExample);
                }

                vectorizedSkillsExamples.put(vectorizedSkillExamples, skillName);
            }
        } else {
            String embeddingOutput = "";
            for (Map.Entry<List<String>, String> entry : skillsExamples.entrySet()) {
                List<List<Double>> vectorizedSkillExamples = encode(entry.getKey());
                String vectorizedSkillExamplesOutput = entry.getValue() + ": ";
                for (List<Double> vectorizedSkillExample : vectorizedSkillExamples) {
                    vectorizedSkillExamplesOutput += "[";
                    for (Double vectorElement : vectorizedSkillExample) {
                        vectorizedSkillExamplesOutput += vectorElement + ",";
                    }
                    vectorizedSkillExamplesOutput = vectorizedSkillExamplesOutput.substring(0, vectorizedSkillExamplesOutput.length() - 1) + "],";
                }
                vectorizedSkillExamplesOutput = vectorizedSkillExamplesOutput.substring(0, vectorizedSkillExamplesOutput.length() - 1);
                embeddingOutput += vectorizedSkillExamplesOutput + "\n";
                vectorizedSkillsExamples.put(vectorizedSkillExamples, entry.getValue());
            }
            embeddingOutput = embeddingOutput.strip();

            PrintWriter pw = new PrintWriter(embeddingsFile);
            pw.append(embeddingOutput);
            pw.flush();
        }

        List<List<Double>> vectorizedQuery = encode(List.of(new String[]{query}));

        double maxCosimilarity = 0;
        String closestSkillToQuery = "";
        for (Map.Entry<List<List<Double>>, String> entry : vectorizedSkillsExamples.entrySet()) {
            List<List<Double>> vectorizedSkillExamples = entry.getKey();

            double cosimilarity = maxCosimilarity(vectorizedSkillExamples, vectorizedQuery);
            System.out.println("The max cosine similarity with the skill \'" + entry.getValue() + "\' is: " + cosimilarity);
            if (cosimilarity > maxCosimilarity) {
                maxCosimilarity = cosimilarity;
                closestSkillToQuery = entry.getValue();
            }
        }

        System.out.println("The max cosine similarity is: " + maxCosimilarity);
        System.out.println("The closest skill to the query is: " + closestSkillToQuery);
        if (maxCosimilarity < confidence)
            return "";

        skill = closestSkillToQuery;

        if (closestSkillToQuery.contains("Spotify"))
            return "Spotify";
        else if (closestSkillToQuery.contains("Weather"))
            return "Weather";

        return closestSkillToQuery;
    }

    public double averageCosimilarity(List<List<Double>> vectorizedSkillExamples, List<List<Double>> vectorizedQuery) {
        double sum = 0;
        for (List<Double> vectorizedSkillExample : vectorizedSkillExamples) {
            sum += cosineSimilarity(vectorizedSkillExample, vectorizedQuery.get(0));
        }
        return sum / vectorizedSkillExamples.size();
    }

    public double maxCosimilarity(List<List<Double>> vectorizedSkillExamples, List<List<Double>> vectorizedQuery) {
        double maxSimilarity = 0;
        for (List<Double> vectorizedSkillExample : vectorizedSkillExamples) {
            double exampleQuerySimilarity = cosineSimilarity(vectorizedSkillExample, vectorizedQuery.get(0));
            if (maxSimilarity < exampleQuerySimilarity)
                maxSimilarity = exampleQuerySimilarity;
        }
        return maxSimilarity;
    }
}