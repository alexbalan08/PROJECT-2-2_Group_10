package backend.recognition;

public interface SlotRecognition {

    String[] findSlot(String input);

    default String find(String key, String input) {
        int index = input.indexOf(key);
        if(index != -1) {
            String sub = input.substring(index + key.length());
            index = sub.indexOf(" ");
            if(index != -1) {
                return sub.substring(0, index).trim();
            }
            return sub.replace("?", "").replace(".", "");
        }
        return "";
    }
}
