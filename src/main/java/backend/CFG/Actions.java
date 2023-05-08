package backend.CFG;

import java.util.Map;

public class Actions {
    private final Map<String, Map<String, String>> actions;

    public Actions(Map<String, Map<String, String>> actions) {
        this.actions = actions;
    }

    public String findAnswer(String type, String input) {
        Map<String, String> secondMap = this.actions.get(type);
        if(secondMap != null) {
            for(var entry : secondMap.entrySet()) {
                if(keysElementsAreInInput(entry.getKey(), input)) {
                    return entry.getValue().trim();
                }
            }
        }
        return "I dont' know.";
    }

    private boolean keysElementsAreInInput(String key, String input) {
        var split = key.trim().split(" ");
        int counter = 0;
        for (String next : split) {
            if(input.contains(next)) {
                counter++;
            }
        }
        return counter == split.length;
    }
}
