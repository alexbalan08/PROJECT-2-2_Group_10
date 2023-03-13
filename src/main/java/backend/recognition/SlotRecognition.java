package backend.recognition;

public interface SlotRecognition {

    String[] findSlot(String input);

    default String findFirst(String key, String input) {
        int index = input.indexOf(key);
        return getSlot(key, input, index);
    }

    default String findLast(String key, String input) {
        int index = input.lastIndexOf(key);
        return getSlot(key, input, index);
    }

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
