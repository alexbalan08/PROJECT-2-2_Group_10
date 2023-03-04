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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

public class Canvas extends SkillWrapper {
    private final String APIkey = Files.readString(Path.of("./src/main/resources/canvasKey.secret"));
    private final String accessToken = "?per_page=100&access_token=" + APIkey;

    public Canvas() throws IOException {
    }

    @Override
    public void start(String matchedTemplate) {
        final String API_URL_courses = "https://canvas.maastrichtuniversity.nl/api/v1/courses";

        String output = "";
        boolean topicFound = false;
        List<JsonNode> files = new ArrayList<>();
        matchedTemplate = matchedTemplate.toLowerCase().strip();
        String[] keyWords = matchedTemplate.split(" ");
        System.out.println(matchedTemplate);
        try {
            String coursesData = response(API_URL_courses + accessToken);

            ObjectMapper om = new ObjectMapper();
            JsonNode rootCourses = om.readTree(coursesData);

            for (Iterator<JsonNode> courses = rootCourses.elements(); courses.hasNext(); ) {
                JsonNode course = courses.next();
                // Look at modules inside the course
                if(course.get("name").asText().toLowerCase().contains(keyWords[0])) {
                    output = output.concat("For the course " + course.get("name").asText() + " you can find the topic \'" + keyWords[1] + "\' in:\n");
                    //outputs.add("MODULE 1");
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
                        boolean topicInModuleFound = false;
                        // Look at files inside the items
                        for (Iterator<JsonNode> items = rootModuleItems.elements(); items.hasNext(); ) {
                            JsonNode item = items.next();
                            if (item.get("type").asText().equals("File")) {
                                System.out.println("Item id is: " + item.get("id").asText() + ", and type is: " + item.get("type").asText());
                                final String API_URL_course_module_item_file = item.get("url").asText();
                                String courseModuleItemFile = response(API_URL_course_module_item_file + accessToken);
                                JsonNode rootModuleItemFile = om.readTree(courseModuleItemFile);

                                if (rootModuleItemFile.get("content-type").asText().equals("application/pdf")) {
                                    String download_url = rootModuleItemFile.get("url").asText();
                                    String pathName = "./src/main/resources/courses/" + rootModuleItemFile.get("filename").asText() + ".pdf";
                                    ArrayList<Integer> numberSlides = getSlidesWithTopic(keyWords[1], download_url, pathName);
                                    if(!numberSlides.isEmpty()) {
                                        if (!topicFound) topicFound = true;
                                        if (!topicInModuleFound) {
                                            topicInModuleFound = true;
                                            output = output.concat("\nModule \'" + module.get("name").asText() + ":\n");
                                        }
                                        output = output.concat("\'" + rootModuleItemFile.get("filename").asText() + "\': slide(s) ");
                                        for (Integer numberSlide : numberSlides) {
                                            if (numberSlides.get(numberSlides.size() - 1) != numberSlide) output = output.concat(numberSlide + ", ");
                                            else output = output.concat(numberSlide + "\n");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    outputs.add(output);
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

    // Returns String of content inside an API_URL
    public static String response(String API_URL) throws IOException {
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

    public static ArrayList<Integer> getSlidesWithTopic(String topic, String download_url, String pathName) throws IOException {
        ArrayList<Integer> slidesWithTopic = new ArrayList<>();

        // Save a pdf file with the content of the lecture slides
        URL url = new URL(download_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream inputStream = con.getInputStream();
        File file = new File(pathName);
        FileOutputStream outputStream = new FileOutputStream(file);
        int bytesRead = -1;
        int BUFFER_SIZE = 4096;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();

        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // Find slides that contain the topic
        for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
            StringWriter outputStreamSlide = new StringWriter();
            pdfStripper.setStartPage(pageNumber);
            pdfStripper.setEndPage(pageNumber);
            pdfStripper.writeText(document, outputStreamSlide);
            if (outputStreamSlide.toString().toLowerCase().contains(topic)) {
                System.out.println("Topic is in slide: " + pageNumber);
                slidesWithTopic.add(pageNumber);
            }
        }
        document.close();

        return slidesWithTopic;
    }

    public static void main(String[] args) throws IOException {
        /*Canvas c = new Canvas();
        c.start("Theoretical");*/

        /*ObjectMapper om = new ObjectMapper();
        String urlFile = "https://canvas.maastrichtuniversity.nl/api/v1/courses/11856/files/2328213?per_page=100&access_token=15183~uSW7dxlpiDnZicfuGzgzZGPoESt8XFeM8Ub5EKfYg9ygIARDS4Y3iaxUKwKDWC56";
        String courseModuleItemFile = response(urlFile);
        JsonNode rootModuleItemFile = om.readTree(courseModuleItemFile);
        System.out.println("Is the content type of pdf? " + rootModuleItemFile.get("content-type").asText().equals("application/pdf"));*/
    }
}
