package backend.Skills;

import backend.SkillWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class Canvas extends SkillWrapper {
    private final String APIkey = Files.readString(Path.of("./src/main/resources/canvasKey.secret"));
    private final String accessToken = "?per_page=100&access_token=" + APIkey;
    final String API_URL_courses = "https://canvas.maastrichtuniversity.nl/api/v1/courses";
    ObjectMapper om = new ObjectMapper();

    public Canvas() throws IOException { }

    @Override
    public void start(List<String> slots) {
        /*if (!Objects.equals(slots[0], "course")) {
            String course = slots[0];
            String topic = slots[1];
            outputs.add(find(course, topic));
        } else {
            outputs.add("Sorry, I didn't understand the course or the topic. Try another one.");
        }*/
        outputs.add(find(slots.get(0), slots.get(1)));
    }

    private String find(String courseName, String topic) {
        try {
            String output = "";
            String coursesData = response(API_URL_courses + accessToken);
            JsonNode rootCourses = om.readTree(coursesData);

            for (Iterator<JsonNode> courses = rootCourses.elements(); courses.hasNext(); ) {
                JsonNode course = courses.next();
                // Look at modules inside the course
                if (course.get("name").asText().toLowerCase().contains(courseName)) {
                    output = output.concat("For the course \'" + course.get("name").asText() + "\' you can find the topic \'" + topic + "\' in:\n");
                    File courseFolder = new File("./src/main/resources/courses/" + course.get("name").asText());
                    if (!courseFolder.exists()) {
                        downloadUrlFiles(course, courseFolder);
                    }
                    output = output.concat(allSlidesWithTopic(courseFolder, topic));
                    return output;
                } else if (!courses.hasNext()) {
                    return "Sorry, I can't find this course.";
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return "Oh, this service had an error.";
        }
        return "";
    }

    private void downloadUrlFiles(JsonNode course, File courseFolder) throws IOException {
        courseFolder.mkdir();
        final String API_URL_course_modules = API_URL_courses + "/" + course.get("id") + "/modules";
        String courseModules = response(API_URL_course_modules + accessToken);
        JsonNode rootModules = om.readTree(courseModules);

        // Look at items inside the modules
        for (Iterator<JsonNode> modules = rootModules.elements(); modules.hasNext(); ) {
            JsonNode module = modules.next();
            String moduleName = module.get("name").asText().replace(":", "");
            File moduleFolder = new File(courseFolder.getPath() + "/" + moduleName);
            moduleFolder.mkdir();
            System.out.println("The module directory \'" + module.get("name").asText() + "\' has been created? " + moduleFolder.exists());
            String courseModuleItems = response(module.get("items_url").asText() + accessToken);
            JsonNode rootModuleItems = om.readTree(courseModuleItems);

            // Look at files inside the items
            for (Iterator<JsonNode> items = rootModuleItems.elements(); items.hasNext(); ) {
                JsonNode item = items.next();
                if (item.get("type").asText().equals("File")) {
                    String courseModuleItemFile = response(item.get("url").asText() + accessToken);
                    JsonNode rootModuleItemFile = om.readTree(courseModuleItemFile);
                    if (rootModuleItemFile.get("content-type").asText().equals("application/pdf")) {
                        String download_url = rootModuleItemFile.get("url").asText();
                        String pathName = moduleFolder.getPath() + "/" + rootModuleItemFile.get("filename").asText();
                        savePdfFile(download_url, pathName);
                    }
                }
            }
        }
    }

    // Returns String of content inside an API_URL
    private static String response(String API_URL) throws IOException {
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

    // Save a pdf file (with the lecture slides content) having the download_url and saving it in the pathName
    private static void savePdfFile(String download_url, String pathName) throws IOException {
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
    }

    // List of slides of one Lecture containing the topic
    private static ArrayList<Integer> getFileSlidesWithTopic(String topic, File file) throws IOException {
        ArrayList<Integer> slidesWithTopic = new ArrayList<>();

        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        // Find slides that contain the topic
        for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
            StringWriter outputStreamSlide = new StringWriter();
            pdfStripper.setStartPage(pageNumber);
            pdfStripper.setEndPage(pageNumber);
            pdfStripper.writeText(document, outputStreamSlide);
            if (outputStreamSlide.toString().toLowerCase().contains(topic)) {
                slidesWithTopic.add(pageNumber);
            }
        }
        document.close();

        return slidesWithTopic;
    }

    // String containing slides of all lectures containing the topic
    private static String allSlidesWithTopic(File courseFolder, String topic) throws IOException {
        String output = "";
        boolean topicFound = false;
        File[] courseFolderModules = courseFolder.listFiles();
        for (File courseFolderModule : courseFolderModules) {
            boolean topicInModuleFound = false;
            File[] courseFolderModuleFiles = courseFolderModule.listFiles();
            for (File courseFolderModuleFile : courseFolderModuleFiles) {
                ArrayList<Integer> numberSlides = getFileSlidesWithTopic(topic, courseFolderModuleFile);
                if (!numberSlides.isEmpty()) {
                    if (!topicFound) topicFound = true;
                    if (!topicInModuleFound) {
                        topicInModuleFound = true;
                        output = output.concat("\nModule \'" + courseFolderModule.getName() + ":\n");
                    }
                    output = output.concat("\'" + courseFolderModuleFile.getName() + "\': slide(s) ");
                    output = outputFileSlidesWithTopic(output, numberSlides);
                }
            }
        }
        if (!topicFound) output = output.concat("No lecture slide contains the topic \'" + topic + "\'");

        return output;
    }

    // String concatenating slides of a file containing the topic
    private static String outputFileSlidesWithTopic(String output, ArrayList<Integer> numberSlides) {
        Integer previousNumberSlide = numberSlides.get(0);
        Integer lastConsecutiveSlide = numberSlides.get(0);
        output = output.concat(lastConsecutiveSlide.toString());

        for (Integer numberSlide : numberSlides) {
            if (Objects.equals(numberSlide, numberSlides.get(0))) {
                if (numberSlides.size() == 1) output = output.concat("\n");
                continue;
            }
            boolean lastNumberSlide = Objects.equals(numberSlides.get(numberSlides.size() - 1), numberSlide);
            if (numberSlide != lastConsecutiveSlide + 1) {
                if (lastConsecutiveSlide.equals(previousNumberSlide) || lastConsecutiveSlide == previousNumberSlide + 1) {
                    if (lastConsecutiveSlide == previousNumberSlide + 1)
                        output = output.concat(", " + lastConsecutiveSlide);
                    output = output.concat(", " + numberSlide);
                } else {
                    output = output.concat("-" + lastConsecutiveSlide + ", " + numberSlide);
                }
                previousNumberSlide = numberSlide;
            } else if (lastNumberSlide) output = output.concat("-" + numberSlide);
            lastConsecutiveSlide = numberSlide;
            if (lastNumberSlide) output = output.concat("\n");
        }
        return output;
    }
}