package backend.recognition.user;

import backend.recognition.SkillRecognition;
import utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/*
 * Which lectures are there on Monday at 9 ?
 * What is the capital of Belgium ?
 * Which transport do I take to go to Liege ?
 * */

public class UserSkillRecognition implements SkillRecognition {

    private final String fileURL;
    private final UserSlotRecognition slotRecognition;

    public UserSkillRecognition() {
        this.fileURL = "./src/main/java/backend/Skills/SkillsTemplate.txt";
        this.slotRecognition = new UserSlotRecognition();
    }

    /**
     *
     * Function that read the file "SkillsTemplate.txt" line by line.
     * It will search first for a question by using the line starting by the key word : Question.
     * For each question, it will search for a similitude with the input thanks to the function "findQuestion".
     *
     * This function returns a String :
     * - Empty if a question or an answer is not found.
     * - Not empty if a question and an answer is found.
     *
     * */
    @Override
    public String determineSkill(String input) {
        try (BufferedReader br = new BufferedReader(new FileReader(this.fileURL))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("Question :")) {
                    String result = findQuestion(input, line, br);
                    if(!result.equals("")) {
                        return result;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error with the file : " + this.fileURL);
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     * This function return an answer, if a question is found by their similitude, with the function "findAnswer".
     * Otherwise, it will return an empty String.
     *
     * */
    private String findQuestion(String input, String line, BufferedReader br) throws IOException {
        String question = line.substring(line.indexOf(":") + 2).trim();
        if(StringUtils.areSimilarSentences(input, question)) {
            String answer = findAnswer(input, question, br);
            if(!answer.equals("")) {
                return answer;
            }
        }
        return "";
    }

    /**
     *
     * This function will first determine the Slots from the input, thanks to (User)SlotRecognition.
     * If there are found, it will continue to read the file line by line.
     *
     * If a line start by "Action" :
     * - it will try to find an answer, based of the slots, thanks to the function "findAction".
     *   If an answer is found, it will break the "while" and return an answer in a template, thanks to "findTemplate".
     *
     * If a line start with "Answer" :
     * - That means that the file doesn't include an answer for the given slots.
     *   This will return an error template, thanks to "findTemplate".
     *
     * */
    private String findAnswer(String input, String question, BufferedReader br) throws IOException {
        String answer = "";
        BufferedReader newBr = null;
        String[] slots = this.slotRecognition.findSlot(input + " / " + question);
        if(slots.length > 0) {
            String line = "";
            while (!Objects.equals(line = br.readLine(), null)) {
                if(line.startsWith("Action")) {
                    answer = findAction(line, slots);
                    if(!answer.equals("")) {
                        break;
                    }
                } else if(line.startsWith("Answer")){
                    break;
                }
                newBr = br;
            }
        }
        if(newBr != null) {
            return findTemplate(slots, answer, newBr);
        } else {
            return findTemplate(slots, answer, br);
        }
    }

    /**
     *
     * This function will try to find an answer, based on the given slots and line.
     * It's called if the line start with "Action".
     *
     * If the line contains all the slots, that's mean that this line is the answer (without format) and return
     * a substring, with only the response.
     * Else, it returns an empty String.
     *
     * */
    private String findAction(String line, String[] slots) {
        int count = 0;
        for(String slot : slots) {
            if(line.contains(slot)) {
                count++;
            }
        }
        if(count == slots.length) {
            int index = line.lastIndexOf(":");
            return line.substring(index + 2);
        }
        return "";
    }

    /**
     *
     * This function is called to return a formatted answer.
     *
     * It will search the lines that start with "Answer" (if the paramater "answer" is not empty) or "Error" (else).
     * If a template is found, il will replace the <SLOT> in the template and return it.
     *
     * */
    private String findTemplate(String[] slots, String answer, BufferedReader br) throws IOException {
        String line = "";
        while (!Objects.equals(line = br.readLine(), null)) {
            String template = line.substring(line.indexOf(":") + 2);
            if(!Objects.equals(answer, "")) {
                if(line.startsWith("Answer")) {
                    template = template.replace("<ANSWER>", answer);
                    for(String slot : slots) {
                        template = replaceSlot(template, slot);
                    }
                    return template;
                }
            } else {
                if(line.startsWith("Error")) {
                    for(String slot : slots) {
                        template = replaceSlot(template, slot);
                    }
                    return template;
                }
            }
        }
        return "";
    }

    private String replaceSlot(String template, String slot) {
        int firstIndex = template.indexOf("<");
        int secondIndex = template.indexOf(">") + 1;
        return template.substring(0, firstIndex) + slot + template.substring(secondIndex);
    }
}
