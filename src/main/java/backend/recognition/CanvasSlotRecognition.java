package backend.recognition;

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
 * */
public class CanvasSlotRecognition implements SlotRecognition {

    public CanvasSlotRecognition() { }

    @Override
    public String[] findSlot(String input) {
        String course = find(" course ", input);
        if(Objects.equals(course, "")) {
            course = find(" of ", input);
            if(Objects.equals(course, "")) {
                course = find(" in ", input);
            }
        }
        String topic = find(" find ", input);

        if(Objects.equals(course, "") || Objects.equals(topic, "")) {
            return new String[] {"course", "topic"};
        }
        return new String[] {course, topic};
    }
}
