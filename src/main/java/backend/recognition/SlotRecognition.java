package backend.recognition;

import java.util.List;

/**
 *
 * This interface can be implemented by a class that performs slot recognition, which is the task of identifying certain types of information in a natural language input.
 * The "findFirst" and "findLast" methods are useful for finding slots associated with specific keywords in the input.
 *
 * */
public interface SlotRecognition {

    List<String> findSlot(String input);

    /**
     *
     * The "findFirst" method takes two arguments: "key" and "input".
     * It finds the first occurrence of "key" in "input" and returns the slot associated with it.
     * The slot is obtained by calling the private method "getSlot".
     *
     * */
    default String findFirst(String key, String input) {
        int index = input.indexOf(key);
        return getSlot(key, input, index);
    }

    /**
     *
     * The "findLast" method takes two arguments: "key" and "input".
     * It finds the last occurrence of "key" in "input" and returns the slot associated with it.
     * The slot is obtained by calling the private method "getSlot".
     *
     * */
    default String findLast(String key, String input) {
        int index = input.lastIndexOf(key);
        return getSlot(key, input, index);
    }

    /**
     *
     * It returns the slot associated with "key" in "input", which is the substring that appears between "key" and the next space character.
     * If there is no space character after key, it removes certain punctuation characters from the substring before returning it.
     *
     * */
    private String getSlot(String key, String input, int index) {
        if(index != -1) {
            String sub = input.substring(index + key.length());
            index = sub.indexOf(" ");
            if(index != -1) {
                return sub.substring(0, index).trim();
            }
            return sub.replace("?", "").replace(".", "").replace("\n", "");
        }
        return "";
    }
}
