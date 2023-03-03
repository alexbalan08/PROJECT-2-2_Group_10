package backend.Skills;

import backend.SkillWrapper;
import backend.Skills.WeatherData.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Canvas extends SkillWrapper {
    private final String APIkey = Files.readString(Path.of("./src/main/resources/canvasKey.secret"));
    private final String accessToken = "?per_page=100&access_token=" + APIkey;

    public Canvas() throws IOException {
    }

    @Override
    public void start(String matchedTemplate) {
        final String API_URL_courses = "https://canvas.maastrichtuniversity.nl/api/v1/courses";

        String output = "";
        List<JsonNode> files = new ArrayList<>();
        matchedTemplate = matchedTemplate.toLowerCase().strip();
        String[] keyWords = matchedTemplate.split(" ");
        System.out.println(matchedTemplate);
        try {
            String coursesData = response(API_URL_courses + accessToken).toLowerCase();

            ObjectMapper om = new ObjectMapper();
            JsonNode rootCourses = om.readTree(coursesData);

            for (Iterator<JsonNode> courses = rootCourses.elements(); courses.hasNext(); ) {
                JsonNode course = courses.next();
                // Look at modules inside the course
                if(course.get("name").asText().contains(keyWords[0])) {
                    final String API_URL_course_modules = API_URL_courses + "/" + course.get("id") + "/modules";
                    String courseModules = response(API_URL_course_modules + accessToken);
                    System.out.println("Course id is: " + course.get("id").asText());
                    JsonNode rootModules = om.readTree(courseModules);
                    // Look at items inside the modules
                    for (Iterator<JsonNode> modules = rootModules.elements(); modules.hasNext(); ) {
                        JsonNode module = modules.next();
                        System.out.println("The module id is: " + module.get("id").asText());
                        final String API_URL_course_module_items = module.get("items_url").asText();
                        String courseModuleItems = response(API_URL_course_module_items + accessToken);
                        JsonNode rootModuleItems = om.readTree(courseModuleItems);
                        // Look at files inside the items
                        for (Iterator<JsonNode> items = rootModuleItems.elements(); items.hasNext(); ) {
                            JsonNode item = items.next();
                            if (item.get("type").asText().equals("File")) {
                                System.out.println("Item id is: " + item.get("id").asText() + ", and type is: " + item.get("type").asText());
                                final String API_URL_course_module_item_files = item.get("url").asText();
                                String courseModuleItemFile = response(API_URL_course_module_item_files + accessToken);
                                JsonNode rootModuleItemFile = om.readTree(courseModuleItemFile);


                                String fileContent = Files.readString(Path.of("./src/main/resources/courses/" + rootModuleItemFile.get("filename")));
                                if(fileContent.contains(keyWords[1])) files.add(rootModuleItemFile);
                            }
                        }
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

        // System.out.println("InputStream: " + con.getInputStream().toString());

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
        /*Canvas c = new Canvas();
        c.start("Theoretical");*/
        String download_url = "https://canvas.maastrichtuniversity.nl/files/2329988/download?download_frd=1&verifier=td2sESVjXHBSxG7BU29DLzKNYcar7KHOgI3dZbzG";
        String saveFilePath = "./src/main/resources";
        URL url = new URL(download_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        File file = new File("./src/main/resources/newfile.pdf");
        FileOutputStream outputStream = new FileOutputStream(file/*saveFilePath*/);
        int bytesRead = -1;
        int BUFFER_SIZE = 4096;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();
    }
}
