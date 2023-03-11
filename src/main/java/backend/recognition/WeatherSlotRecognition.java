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
 * - <PLACE> : Maastricht, Liege, Amsterdam, ect
 * - <TIME> : 9pm, 12am, etc
 *
 * - RESULT = [<PLACE>, optional <TIME>]
 *
 * */

public class WeatherSlotRecognition implements SlotRecognition {

    public WeatherSlotRecognition() { }

    @Override
    public String[] findSlot(String input) {
        String place = find(" in ", input);
        String time = find(" at ", input);

        if(!time.equals("")) {
            return new String[] {place, time};
        }
        return new String[] {place};
    }
}
