package backend.recognition.api;

import backend.recognition.SlotRecognition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * This class find slots for these examples :
 *
 * - What is the music at the moment ?
 * - What is the music playing ?
 * - What song does Spotify play?
 * - Can you <ACTION> this song "<TITLE>" ?
 * - Can you <ACTION> "<TITLE>" ?
 * - <ACTION> "<TITLE>"
 *
 * - <ACTION> : Play | Pause | Stop | Resume | Replay
 * - <TITLE> : title of one music, has to be between quotation marks
 *
 * - RESULT = [<ACTION>, optional <TITLE>]
 *
 * If there is no <TITLE>, that means that the user wants to know the music that is playing.
 *      --> this class will return "info" as <ACTION>.
 * If there is a <TITLE>, this class will search for the wanted action.
 *
 * */

public class SpotifySlotRecognition implements SlotRecognition {

    private final List<String> actions;

    public SpotifySlotRecognition() {
        this.actions = new ArrayList<>();
        this.actions.add("play");
        this.actions.add("stop");
        this.actions.add("pause");
        this.actions.add("resume");
        this.actions.add("replay");
    }

    /**
     *
     * Extract the title and the action from the input.
     *
     * */
    @Override
    public List<String> findSlot(String input) {
        String title = findTitle(input);
        if(!title.equals("")) {
            return new ArrayList<>(Arrays.asList(findAction(input, title), title));
        }
        return new ArrayList<>(List.of("info"));
    }

    private String findAction(String input, String title) {
        for(String action : this.actions) {
            if(input.contains(action) && !title.contains(action)) {
                return action;
            }
        }
        return "info";
    }

    private String findTitle(String input) {
        return input.substring(input.indexOf("\'") + 1, input.lastIndexOf("\'"));
    }
}
