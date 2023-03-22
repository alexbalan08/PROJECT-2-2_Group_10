package backend.recognition.api;

import backend.recognition.SlotRecognition;

import java.util.Objects;

/**
 *
 * This class find slots for these examples :
 *
 * - Can you explain to me what is <SUBJECT> ?
 * - What is the definition of <SUBJECT> ?
 * - What does Wikipedia say about <SUBJECT> ?
 *
 * - RESULT : [<SUBJECT>]
 * */

public class WikipediaSlotRecognition implements SlotRecognition {

    public WikipediaSlotRecognition() { }

    @Override
    public String[] findSlot(String input) {
        String subject = findTheme(" about ", input);
        if(Objects.equals(subject, "")) {
            subject = findTheme(" of ", input);
            if(Objects.equals(subject, "")) {
                subject = findTheme(" is ", input);
            }
        }
        return new String[] { subject };
    }

    private String findTheme(String key, String input) {
        int index = input.indexOf(key);
        if(index != -1) {
            return cleanTheme(input.substring(index + key.length()));
        }
        return "";
    }

    private String cleanTheme(String theme) {
        theme = theme.replace("?", "").replace(".", "")
                .replace("\n", "").replace(" ", "%20");
        int lastOccurrence = theme.lastIndexOf("%20");
        if (lastOccurrence != -1 && theme.substring(0, lastOccurrence).contains("%20")) {
            theme = theme.substring(0, lastOccurrence) + theme.substring(lastOccurrence + 3);
        }
        return theme.trim();
    }
}
