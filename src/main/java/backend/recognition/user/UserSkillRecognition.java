package backend.recognition.user;

import backend.recognition.SkillRecognition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class UserSkillRecognition implements SkillRecognition {

    private final String fileURL;
    private final UserSlotRecognition slotRecognition;

    public UserSkillRecognition() {
        this.fileURL = "./src/main/java/backend/Skills/SkillsTemplate.txt";
        this.slotRecognition = new UserSlotRecognition();
    }

    /*
     * Which lectures are there on Monday at 9 ?
     * What is the capital of Belgium ?
     * Which transport do I take to go to Liege ?
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
            e.printStackTrace();
        }
        return "";
    }

    private String findQuestion(String input, String line, BufferedReader br) throws IOException {
        // FIND A QUESTION THAT MATCH THE INPUT
        String question = line.substring(line.indexOf(":") + 2).trim();
        if(isThisQuestion(input, question)) {
            String answer = findAnswer(input, question, br);
            if(!answer.equals("")) {
                return answer;
            }
        }
        return "";
    }

    private String findAnswer(String input, String question, BufferedReader br) throws IOException {
        // CHECK IF IT KNOWS <SLOT>
        String answer = "";
        BufferedReader newBr = null;
        String[] slots = this.slotRecognition.getSlots(input, question);
        if(slots.length > 0) {
            // FIND ANSWER
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

    // Levenshtein distance, from Chat GPT
    private boolean isThisQuestion(String input, String question) {
        int n = input.length();
        int m = question.length();
        int[][] dp = new int[n + 1][m + 1];

        if (n == 0 || m == 0) {
            return false;
        }

        for (int i = 1; i <= n; i++) {
            dp[i][0] = i;
        }

        for (int j = 1; j <= m; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = input.charAt(i - 1) == question.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        int distance = dp[n][m];
        int maxLength = Math.max(n, m);
        double similarity = (double) (maxLength - distance) / maxLength;
        return similarity >= 0.6;
    }


}
