package backend.recognition.user;

import backend.recognition.SlotRecognition;

import java.util.ArrayList;
import java.util.List;

public class UserSlotRecognition implements SlotRecognition {

    public UserSlotRecognition() { }

    @Override
    public String[] findSlot(String input) {
        String question = getInformation(input, true);
        input = getInformation(input, false);

        List<String> result = new ArrayList<>();

        int index = question.indexOf("<");
        while (index != -1) {
            String questionSlot = getSlotForQuestion(question);
            String inputSlot = getSlotForInput(input, index);

            question = question.substring(index + questionSlot.length() + 1);
            input = input.substring(index + inputSlot.length() + 1);

            result.add(inputSlot);
            index = question.indexOf("<");
        }
        return result.toArray(new String[0]);
    }

    private String getSlotForQuestion(String question) {
        return question.substring(question.indexOf("<"), question.indexOf(">") + 1);
    }

    private String getSlotForInput(String input, int index) {
        String sub = input.substring(index);
        index = sub.indexOf(" ");
        return sub.substring(0, index);
    }

    private String getInformation(String input, boolean question) {
        int index = input.indexOf("/");
        if(!question) {
            return input.substring(0, index).trim();
        }
        return input.substring(index + 1).trim();
    }
}
