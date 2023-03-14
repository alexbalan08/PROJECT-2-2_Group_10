package backend.recognition.api;

import backend.recognition.SlotRecognition;

import java.util.Objects;

/**
 *
 * This class find slots for these examples :
 *
 * - In which lecture slides of <COURSE> can you find <TOPIC> ?
 * - For the course <COURSE> can you find <TOPIC> ?
 * - Where can I find <TOPIC> in <COURSE> ?
 * - Can you find <TOPIC> in the course <COURSE> ?
 *
 * - <COURSE> : A course from UM
 * - <TOPIC> : A topic from a course
 *
 * - RESULT = [<COURSE>, <TOPIC>]
 *
 * <COURSE> can be found after the words "course", "of" and "in".
 * <TOPIC> is found after the word "find".
 *
 * */

public class CanvasSlotRecognition implements SlotRecognition {

    public CanvasSlotRecognition() { }

    @Override
    public String[] findSlot(String input) {
        String course = findCourse(input);
        String topic = findFirst(" find ", input);

        if(Objects.equals(course, "") || Objects.equals(topic, "")) {
            return new String[] { "course", "topic" };
        }
        return new String[] { course, topic };
    }

    private String findCourse(String input) {
        String course = findFirst(" course ", input);
        if(Objects.equals(course, "")) {
            course = findFirst(" of ", input);
            if(Objects.equals(course, "")) {
                course = findFirst(" in ", input);
            }
        }
        return course;
    }
}
