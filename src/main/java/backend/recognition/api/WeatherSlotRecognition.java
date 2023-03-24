package backend.recognition.api;

import backend.recognition.SlotRecognition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    public List<String> findSlot(String input) {
        String place = findFirst(" in ", input);
        if(Objects.equals(place, "")) {
            return new ArrayList<>(Arrays.asList(findFirst(" at ", input), findLast(" at ", input)));
        } else {
            return new ArrayList<>(Arrays.asList(place, findFirst(" at ", input)));
        }
    }

    private String[] getSlotsInArray(String place, String time) {
        if(!time.equals("") && !Objects.equals(place, time)) {
            return new String[] {place, time};
        }
        return new String[] {place};
    }
}
