package backend.recognition;

import java.util.Objects;

/**
 *
 * This class find slots for these examples :
 *
 * - Can you explain me what is <SUBJECT> ?
 * - What is the definition of <SUBJECT> ?
 * - What does Wikipedia says about <SUBJECT> ?
 *
 * - RESULT : [<SUBJECT>]
 * */

public class WikipediaSlotRecognition implements SlotRecognition {

    public WikipediaSlotRecognition() { }

    @Override
    public String[] findSlot(String input) {
        String subject = findFirst(" about ", input);
        if(Objects.equals(subject, "")) {
            subject = findFirst(" of ", input);
            if(Objects.equals(subject, "")) {
                subject = findFirst(" is ", input);
            }
        }
        return new String[] { subject };
    }
}
