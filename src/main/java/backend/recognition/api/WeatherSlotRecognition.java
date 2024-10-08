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
    boolean hardCoded;

    public WeatherSlotRecognition(boolean hardCoded) {
        this.hardCoded = hardCoded;
    }

    /**
     *
     * Takes an input string and tries to extract relevant information (slots) from it.
     *
     * In this case, it looks for information related to location or place, using the words "in" or "at" as markers.
     * If it finds "in" in the input string, it assumes that the location information follows it, and then tries to find "at" to get the time information.
     * Otherwise, it looks for "at" and assumes that the time information follows it. It returns a list of two strings: the location or place, and the time information.
     * If it cannot find any relevant information, it returns an empty list.
     *
     * */
    @Override
    public List<String> findSlot(String input) {
        if (hardCoded) {
            boolean isItWeatherSkill = false;
            String inputTemp = input.toLowerCase();
            if (inputTemp.contains("can you tell me about the weather in "))
                isItWeatherSkill = true;
            else if (inputTemp.contains("what is the weather in "))
                isItWeatherSkill = true;
            else if (inputTemp.contains("how is the weather in "))
                isItWeatherSkill = true;
            else if (inputTemp.contains("what is the weather like in "))
                isItWeatherSkill = true;
            if(!isItWeatherSkill)
                return null;
        }

        String place = input.substring(input.indexOf("\'") + 1, input.lastIndexOf("\'"));
        if(Objects.equals(place, "")) {
            return new ArrayList<>(Arrays.asList(findFirst(" at ", input), findLast(" at ", input)));
        } else {
            return new ArrayList<>(Arrays.asList(place, findFirst(" at ", input)));
        }
    }
}
