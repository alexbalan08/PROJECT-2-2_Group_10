package backend.recognition.api;

import backend.recognition.SlotRecognition;

import java.util.ArrayList;
import java.util.List;
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

    /**
     *
     * Takes an input string and tries to extract relevant information (slots) from it.
     *
     * In this case, it looks for information related to location or place, using the words "about", "of" or "is" as markers.
     * It first looks for a theme using the "about" keyword, then "of", and finally "is".
     * If none of these are found, it returns an empty string as the subject.
     * It then returns a list containing the subject as its only element.
     *
     * */
    @Override
    public List<String> findSlot(String input) {
        boolean isItWikipediaSkill = false;
        String inputTemp = input.toLowerCase();
        if (inputTemp.contains("can you explain to me what is "))
            isItWikipediaSkill = true;
        else if (inputTemp.contains("what is the definition of "))
            isItWikipediaSkill = true;
        else if (inputTemp.contains("what does wikipedia say about "))
            isItWikipediaSkill = true;
        if(!isItWikipediaSkill)
            return null;
        String subject = findTheme(" about ", input);
        if(Objects.equals(subject, "")) {
            subject = findTheme(" of ", input);
            if(Objects.equals(subject, "")) {
                subject = findTheme(" is ", input);
            }
        }
        return new ArrayList<>(List.of(subject));
    }

    /**
     *
     * It returns the theme that appears after the key keyword in the input.
     *
     * */
    private String findTheme(String key, String input) {
        int index = input.indexOf(key);
        if(index != -1) {
            return cleanTheme(input.substring(index + key.length()));
        }
        return "";
    }

    /**
     *
     * Clean up the extracted theme from the user input before using it.
     *
     * */
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
