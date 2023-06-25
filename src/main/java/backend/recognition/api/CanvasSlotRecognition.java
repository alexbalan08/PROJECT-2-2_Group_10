package backend.recognition.api;

import backend.recognition.SlotRecognition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * This class find slots for these examples :
 *
 * - In which lecture slides of <COURSE> can you find '<TOPIC>' ?
 * - For the course <COURSE> can you find '<TOPIC>' ?
 * - Where can I find '<TOPIC>' in <COURSE> ?
 * - Can you find '<TOPIC>' in the course <COURSE> ?
 *
 * - <COURSE> : A course from UM
 * - <TOPIC> : A topic from a course
 *
 * - RESULT = [<COURSE>, <TOPIC>]
 *
 * <COURSE> can be found after the words "course", "of" and "in".
 * <TOPIC> is found inside quotes.
 *
 * */

public class CanvasSlotRecognition implements SlotRecognition {
    boolean hardCoded;
    String[] courses = {"calculus", "neuroscience", "data structures", "databases", "discrete math", "graph theory", "human computer interaction", "ict", "computer science 1", "computer science 2", "data science", "algebra", "logic", "machine learning", "mathematical modelling", "numerical math", "probability and statistic", "project 1-1", "project 1-2", "project 2-1", "project 2-2", "reasoning", "statistical analysis", "software", "theoretical", "linear programming"};

    public CanvasSlotRecognition(boolean hardCoded) {
        this.hardCoded = hardCoded;
    }

    @Override
    public List<String> findSlot(String input) {
        if (hardCoded) {
            boolean isItCanvasSkill = false;
            String inputTemp = input.toLowerCase();
            if (inputTemp.contains("in which lecture slides of") && inputTemp.contains("can you find '"))
                isItCanvasSkill = true;
            else if (inputTemp.contains("for the course") && inputTemp.contains("can you find '"))
                isItCanvasSkill = true;
            else if (inputTemp.contains("where can I find '") && inputTemp.contains("' in "))
                isItCanvasSkill = true;
            else if (inputTemp.contains("can you find '") && inputTemp.contains("' in the course "))
                isItCanvasSkill = true;
            if(!isItCanvasSkill)
                return null;
        }
        String course = findCourse(input);
        String topic = "";
        if (input.contains("\'"))
            topic = input.substring(input.indexOf("\'") + 1);
        if (topic.contains("\'"))
            topic = topic.substring(0, topic.indexOf("\'")).toLowerCase();
        else
            topic = "";

        if(Objects.equals(course, "") || Objects.equals(topic, "")) {
            return new ArrayList<>(Arrays.asList("course", "topic"));
        }
        return new ArrayList<>(Arrays.asList(course, topic));
    }

    private String findCourse(String input) {
        for (String course : courses) {
            if (input.toLowerCase().contains(course))
                return course;
        }
        return "";
    }
}
