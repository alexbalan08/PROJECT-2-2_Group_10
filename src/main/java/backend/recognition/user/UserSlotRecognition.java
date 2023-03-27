package backend.recognition.user;

import backend.recognition.SlotRecognition;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This code snippet is a class UserSlotRecognition that implements the SlotRecognition interface.
 *
 * */

public class UserSlotRecognition implements SlotRecognition {

    public UserSlotRecognition() { }

    /**
     *
     * The findSlot method is implemented in the class to extract slot values from a natural language text input based on a set of template questions.
     * The method splits the input into two parts, one containing the template question and another containing the answer to the question.
     *
     * The method then iterates over the template questions, identified by angle brackets (< and >), and extracts the corresponding slot values from the answer.
     * The method returns a List of all the extracted slot values.
     *
     * */
    @Override
    public List<String> findSlot(String input) {
        String question = getInformation(input, true);
        input = getInformation(input, false);

        List<String> result = new ArrayList<>();

        int index = question.indexOf("<");
        while (index != -1) {
            String questionSlot = getSlotForQuestion(question);
            question = question.substring(index + questionSlot.length() + 1).trim();

            String inputSlot = getSlotForInput(input, index, getNextWord(question));
            input = input.substring(index + inputSlot.length() + 1).trim();

            result.add(inputSlot);
            index = question.indexOf("<");
        }
        return result;
    }

    /**
     *
     * Helper method that extracts the slot value from a template question, identified by angle brackets (< and >).
     *
     * */
    private String getSlotForQuestion(String question) {
        if (!question.contains("<") || !question.contains(">"))
            return "";
        return question.substring(question.indexOf("<"), question.indexOf(">") + 1);
    }

    /**
     *
     * Helper method that extracts the slot value from the input answer, based on the position of the corresponding
     * template question and the next word in the template question.
     *
     * */
    private String getSlotForInput(String input, int index, String nextWord) {
        String sub = input.substring(index);
        index = sub.indexOf(nextWord) - 1;
        return sub.substring(0, index);
    }

    /**
     *
     * Helper method that extracts the next word in a template question, used by the "getSlotForInput" method to
     * determine the end of the slot value in the input answer.
     *
     * */
    private String getNextWord(String question) {
        int index = question.indexOf(" ");
        if(index == -1) {
            return "?";
        }
        return question.substring(0, index);
    }

    /**
     *
     * Helper method that splits the input into the template question and answer parts,
     * based on the position of a forward slash (/).
     *
     * */
    private String getInformation(String input, boolean question) {
        int index = input.indexOf("/");
        if(!question) {
            return input.substring(0, index).trim();
        }
        return input.substring(index + 1).trim();
    }
}
