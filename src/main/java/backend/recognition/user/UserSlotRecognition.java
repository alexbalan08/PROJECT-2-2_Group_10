package backend.recognition.user;

public class UserSlotRecognition {

    public UserSlotRecognition() { }

    public String[] getSlots(String input, String question) {
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
}
