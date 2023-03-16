package backend.recognition.user;

import backend.recognition.SlotRecognition;

public class UserSlotRecognition implements SlotRecognition {

    public UserSlotRecognition() { }

    @Override
    public String[] findSlot(String input) {
        String question = getInformation(input, true);
        input = getInformation(input, false);

        int index = question.indexOf("<");
        String questionSlot = getSlotForQuestion(question);
        String inputSlot = getSlotForInput(input, index);

        question = question.substring(index + questionSlot.length() + 1);
        input = input.substring(index + inputSlot.length() + 1);

        index = question.indexOf("<");
        if(index != -1) {
            return new String[] { inputSlot, getSlotForInput(input, index) };
        } else {
            return new String[] { inputSlot };
        }
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
