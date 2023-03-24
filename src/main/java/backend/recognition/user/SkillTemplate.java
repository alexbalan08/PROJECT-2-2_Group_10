package backend.recognition.user;

import backend.recognition.SlotRecognition;
import utils.StringUtils;

import java.util.*;

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
        this.slotRecognition = new UserSlotRecognition();
    }

    public String findAnswer(String question) {
        for (String next : this.questions) {
            if (StringUtils.areSimilarSentences(question, next)) {
                String[] slots = this.slotRecognition.findSlot(question + " / " + next);
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

    private String findAction(String[] slots) {
        for (String key : this.actions.keySet()) {
            int count = 0;
            for (String slot : slots) {
                if (key.toLowerCase(Locale.ROOT).contains(slot.toLowerCase(Locale.ROOT))) {
                    count++;
                }
            }
            if (count == slots.length) {
                return this.actions.get(key);
            }
        }
        return "";
    }

    private String getAnswerTemplate(String result, String[] slots) {
        String template = this.answer.replace("<ANSWER>", result);
        for (String slot : slots) {
            template = replaceSlot(template, slot);
        }
        return template;
    }

    private String getErrorTemplate(String[] slots) {
        String template = this.error;
        for (String slot : slots) {
            template = replaceSlot(template, slot);
        }
        return template;
    }

    private String replaceSlot(String template, String slot) {
        int firstIndex = template.indexOf("<");
        int secondIndex = template.indexOf(">") + 1;
        return template.substring(0, firstIndex) + slot + template.substring(secondIndex);
    }
}
