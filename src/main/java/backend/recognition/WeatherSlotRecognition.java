package backend.recognition;

/**
 *
 * This class find slots for these examples :
 *
 * - Can you tell me about the weather in <PLACE> ?
 * - What is the weather in <PLACE> ?
 * - How is the weather in <PLACE> ?
 * - What is the weather like in <PLACE> ?
 * - What will the weather be like in <PLACE> at <TIME> ?
 * - Tell me the weather in <PLACE>
 *
 * - <PLACE> : Maastricht, Liège, Amsterdam, …
 * - <TIME> : 9 pm, 12 am, etc
 *
 * The <PLACE> will always be found. The <TIME> is optional.
 *
 * */

public class WeatherSlotRecognition implements SlotRecognition {

    public WeatherSlotRecognition() { }

    @Override
    public String[] findSlot(String input) {
        String place = "";
        String time = "";

        place = find(" in ", input);
        time = find(" at ", input);

        if(!time.equals("")) {
            String[] result = new String[2];
            result[0] = place;
            result[1] = time;
            return result;
        }
        String[] result = new String[1];
        result[0] = place;
        return result;
    }

    private String find(String key, String input) {
        int index = input.indexOf(key);
        if(index != -1) {
            String sub = input.substring(index + 4);
            index = sub.indexOf(" ");
            if(index != -1) {
                return sub.substring(0, index).trim();
            }
            return sub.replace("?", "").replace(".", "");
        }
        return "";
    }
}
