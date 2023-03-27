package backend.recognition.user;

import backend.recognition.SlotRecognition;
import utils.StringUtils;

import java.util.*;

/**
 *
 * Class that represents a skill found in the SkillTemplate.txt file.
 *
 * The List "questions" contains all questions accepted for a skill.
 * The Map "actions" contains all the slots accepted in the key, and the answer based on slots in the value.
 * The String "answer" represent the template of the answer.
 * The String "error" represent the template of the error.
 *
 * */

public class SkillTemplate {

    private final List<String> questions;
    private final Map<String, String> actions;

    private final String answer;
    private final String error;

    private final SlotRecognition slotRecognition;

    public SkillTemplate(List<String> questions, Map<String, String> actions, String answer, String error) {
        this.questions = questions;
        this.actions = actions;
        this.answer = answer;
        this.error = error;
        this.slotRecognition = new TemplateSlotRecognition();
    }

    /**
     *
     * Takes a question as input and tries to find a matching template question from the questions list.
     * If a match is found, the method uses the SlotRecognition interface to find slots in both the input and the matching template question.
     * Then, the method tries to find a matching action for the slots by calling the findAction method, which checks each key in the actions map to see if it contains all the slots.
     *
     * If a matching action is found, the method uses the getAnswerTemplate method to generate a response using the answer template, inserting the result of the action and the slot values.
     * If a matching action is not found, the method uses the getErrorTemplate method to generate an error response using the error template and the slot values.
     * If no match is found between the input question and any of the template questions, an empty string is returned.
     *
     * */
    public String findAnswer(String question) {
        for (String next : this.questions) {
            if (StringUtils.areSimilarSentences(question, next)) {
                List<String> slots = this.slotRecognition.findSlot(question + " / " + next);
                String result = this.findAction(slots);
                if (!Objects.equals(result, "")) {
                    return getAnswerTemplate(result, slots);
                } else {
                    return getErrorTemplate(slots);
                }
            }
        }
        return "";
    }

    /**
     *
     * Responsible for determining which action should be taken based on the slots found in the user's input.
     *
     * It loops through each action in the actions map and counts how many of the slots are present in the action's key.
     * If the count equals the number of slots found, it returns the action's value (the corresponding action to take).
     * Otherwise, it returns an empty string.
     *
     * */
    private String findAction(List<String> slots) {
        for (String key : this.actions.keySet()) {
            int count = 0;
            for (String slot : slots) {
                if (key.toLowerCase(Locale.ROOT).contains(slot.toLowerCase(Locale.ROOT))) {
                    count++;
                }
            }
            if (count == slots.size()) {
                return this.actions.get(key);
            }
        }
        return "";
    }

    /**
     *
     * Replaces the <ANSWER> placeholder in the answer template with the result obtained from findAction method
     * and then replaces all the slot placeholders in the template with the corresponding slot values obtained from findSlot method.
     *
     * */
    private String getAnswerTemplate(String result, List<String> slots) {
        String template = this.answer.replace("<ANSWER>", result);
        for (String slot : slots) {
            template = replaceSlot(template, slot);
        }
        return template;
    }

    /**
     *
     * Replaces all the slot placeholders in the error template with the corresponding slot values obtained from findSlot method.
     *
     * */
    private String getErrorTemplate(List<String> slots) {
        String template = this.error;
        for (String slot : slots) {
            template = replaceSlot(template, slot);
        }
        return template;
    }

    /**
     *
     * Helper method used to replace a placeholder in the answer or error template with the actual slot value found by the SlotRecognition implementation.
     * The method takes two parameters: template, which is a string containing the answer or error template, and slot, which is a string representing the slot value to be inserted into the template.
     *
     * */
    private String replaceSlot(String template, String slot) {
        return template.substring(0, template.indexOf("<")) + slot + template.substring(template.indexOf(">") + 1);
    }
}
