package backend.recognition.api;

import backend.recognition.SkillRecognition;

import java.util.*;

/**
 *
 * This is the implementation of the ApiSkillRecognition class, which is used to determine which API-based skill to use based on the user's input.
 *
 * This class recognise the skill wanted.
 *
 * It has a LIST of interrogative words to be sur that the user want something.
 * It avoids problems with random words like "music wikipedia weather" etc.
 *
 * Is has a MAP of themes that link to the skill wanted.
 * With some key word, the app can determine which skill is requested.
 *
 * If the input contains an interrogative word, this class will search for a theme.
 * If a theme is determined, the function return the skill.
 *
 * */

public class ApiSkillRecognition implements SkillRecognition {
    private final List<String> request;
    private final Map<String, String> themes;

    public ApiSkillRecognition() {
        this.request = new ArrayList<>();
        this.request.add("can you");
        this.request.add("what is");
        this.request.add("tell me");
        this.request.add("how is");
        this.request.add("what's");
        this.request.add("in which");
        this.request.add("where");
        this.request.add("who");
        this.request.add("for the course");
        this.request.add("what");

        this.themes = new HashMap<>();
        this.themes.put("the weather", "Weather");
        this.themes.put("the temperature", "Weather");
        this.themes.put("the climate", "Weather");
        this.themes.put("forecast", "Weather");
        this.themes.put("music", "Spotify");
        this.themes.put("song", "Spotify");
        this.themes.put("spotify", "Spotify");
        this.themes.put("play", "Spotify");
        this.themes.put("stop", "Spotify");
        this.themes.put("pause", "Spotify");
        this.themes.put("lecture", "Canvas");
        this.themes.put("course", "Canvas");
        this.themes.put("find", "Canvas");
        this.themes.put("explain", "Wikipedia");
        this.themes.put("definition", "Wikipedia");
        this.themes.put("wikipedia", "Wikipedia");
    }

    /**
     *
     * Takes a string input, and it returns the name of the API-based skill that matches the user's query.
     *
     * If neither determineRequest nor determineTheme returns a match, then the determineSkill method returns an empty string.
     *
     * */
    public String determineSkill(String input) {
        return this.determineRequest(input);
    }

    /**
     *
     * Iterates through the request list and returns the name of the corresponding skill if the input contains one of the strings in request.
     *
     * */
    private String determineRequest(String input) {
        for (String request : this.request) {
            if(input.contains(request)) {
                return determineTheme(input);
            }
        }
        return "";
    }

    /**
     *
     * Iterates through the themes mapping and returns the name of the corresponding skill if the input contains one of the keywords in the mapping.
     *
     * */
    private String determineTheme(String input) {
        for (String theme : this.themes.keySet()) {
            if(input.contains(theme)) {
                return this.themes.get(theme);
            }
        }
        return "";
    }
}