package backend.Skills;

import backend.SkillWrapper;
import backend.Skills.WeatherData.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class Canvas extends SkillWrapper {
    private final String APIkey = Files.readString(Path.of("./src/main/resources/canvasKey.secret"));
    private final String accessToken = "?access_token=" + APIkey;

    public Canvas() throws IOException {
    }

    @Override
    public void start(String matchedTemplate) {
        final String API_URL_courses = "https://canvas.maastrichtuniversity.nl/api/v1/courses";

        String output = "";
        matchedTemplate = matchedTemplate.toLowerCase();
        try {
            String coursesData = response(API_URL_courses + accessToken).toLowerCase();

            ObjectMapper om = new ObjectMapper();
            JsonNode rootCourses = om.readTree(coursesData);

            for (Iterator<JsonNode> courses = rootCourses.elements(); courses.hasNext(); ) {
                JsonNode course = courses.next();
                if(course.get("name").asText().contains(matchedTemplate)) {
                    final String API_URL_course_modules = API_URL_courses + "/" + course.get("id") + "/modules";
                    String courseModules = response(API_URL_course_modules + accessToken);
                    System.out.println("Course id is: " + course.get("id").asText());
                    JsonNode rootModules = om.readTree(courseModules);
                    for (Iterator<JsonNode> modules = rootModules.elements(); modules.hasNext(); ) {
                        JsonNode module = modules.next();
                        System.out.println("The module id is: " + module.get("id").asText());
                    }
                    break;
                } else if (!courses.hasNext()) {
                    outputs.add("Sorry, this course can not be found.");
                    return;
                }
            }


            // System.out.println("CourseId: " + courseId);

            //ObjectMapper om = new ObjectMapper();
            //WeatherData WD = om.readValue(response.toString(), WeatherData.class);
            //outputs.add("At the moment, in "+ city + ", it's "+ WD.getMain().getTemp()+ "°C.\nFeels like: "+WD.getMain().getFeels_like()+"°C.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String response(String API_URL) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        return response.toString();
    }

    public static void main(String[] args) throws IOException {
        Canvas c = new Canvas();
        c.start("Theoretical Computer Science");
    }
}
