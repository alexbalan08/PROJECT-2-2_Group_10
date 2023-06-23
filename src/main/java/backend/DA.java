package backend;

import backend.CFG.CFG;
import backend.CFG.CFGReader;
import backend.Skills.*;
import backend.recognition.LanguageModel;
import backend.recognition.SkillRecognition;
import backend.recognition.SlotRecognition;
import backend.recognition.api.*;
import backend.recognition.user.SkillTemplate;
import backend.recognition.user.SkillTemplateReader;
import backend.recognition.user.TemplateSkillRecognition;
import utils.StringUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DA implements ActionQuery {
    boolean hardCoded = true;
    private final Map<SkillWrapper, SlotRecognition> skills;
    private final SkillRecognition apiSkillRecognition;
    private final SkillTemplateReader fileReader;
    private final LanguageModel languageModel;
    private List<SkillTemplate> skillTemplates;
    private final SkillEditor skillEditor;

    private boolean useBert;

    private final CFG cfg;

    public DA() throws Exception {
        this.skills = new HashMap<>();
        this.addSkill(new Weather(), new WeatherSlotRecognition(hardCoded));
        this.addSkill(new Spotify(), new SpotifySlotRecognition(hardCoded));
        this.addSkill(new Canvas(), new CanvasSlotRecognition(hardCoded));
        this.addSkill(new Wikipedia(), new WikipediaSlotRecognition(hardCoded));

        this.fileReader = new SkillTemplateReader("./src/main/java/backend/Skills/SkillsTemplate.txt");
        this.skillTemplates = fileReader.getSkillTemplates();

        this.apiSkillRecognition = new ApiSkillRecognition();

        this.languageModel = new LanguageModel();
        this.skillEditor = new SkillEditor();

        String cfgFile = "./src/main/java/backend/CFG/CFG.txt";
        this.cfg = new CFG(cfgFile);
        this.useBert = true;
    }

    private void addSkill(SkillWrapper skill, SlotRecognition slotRecognition) {
        this.skills.put(skill, slotRecognition);
        System.out.println(skill.getClass().getSimpleName() + " wrapper loaded successfully!");
    }

    public String startQuery(String query) throws IOException, InvocationTargetException, IllegalAccessException {
        this.skillEditor.setQuery(query);
        if (this.skillEditor.isQueryToEditSkill()) {
            String answer = this.skillEditor.startQuery(query);
            System.out.println("THE ANSWER IS: " + answer);
            this.skillTemplates = fileReader.getSkillTemplates();
            this.cfg.readFile();
            return answer;
        } else {
            query = query.replace("\n", "").trim();
            return doSkill(query);
        }
    }

    private String doSkill(String query) {
        StringBuilder output = new StringBuilder();

        try {
            // CHECK IF USER ASK SOME EXAMPLE OF QUESTIONS
            if (StringUtils.areSimilarSentences(query, "Can you give me some examples of questions ?", 0.8)) {
                output.append(this.cfg.getExamplesOfQuestions());
            }

            // CHECK IF THE USER ASK FOR TEMPLATE TO ADD SKILL
            if (StringUtils.areSimilarSentences(query, "Can you show me the template to add a skill ?", 0.8)) {
                output.append(this.cfg.getCFGSKillTemplate());
            }

            // CHECK IF THE USER WANT TO USE BERT
            if (StringUtils.areSimilarSentences(query, "Activate BERT model", 0.8) || StringUtils.areSimilarSentences(query, "Deactivate BERT model", 0.8)) {
                useBert = !useBert;
                if(useBert) {
                    output.append("BERT model is now activated");
                } else {
                    output.append("BERT model is now deactivated");
                }
            }

            // CHECH IF BERT CAN ANSWER
            if(useBert && output.isEmpty()) {
                output.append(this.cfg.getAnswerForBERTModel(query));
            }

            // CHECK IF THE CFG KNOW THE SKILL
            if (output.isEmpty()) {
                output.append(this.cfg.getAnswer(query.toLowerCase()));
            }

            // THEN CHECK IF IT'S AN API SKILL WANTED
            if (output.isEmpty() || output.toString().equals("I don't know.") || output.toString().equals("I don't know...")) {
                output = new StringBuilder();
                String determinedSkill = this.apiSkillRecognition.determineSkill(query.toLowerCase(Locale.ROOT));
                for (SkillWrapper skill : this.skills.keySet()) {
                    if (skill.getClass().getSimpleName().equals(determinedSkill)) {
                        List<String> slots = this.skills.get(skill).findSlot(query);
                        if (slots == null)
                            break;
                        skill.start(slots);
                        output.append(skill.getResponse());
                        break;
                    }
                }
            }

            if (output.isEmpty() || output.toString().equals("I don't know.") || output.toString().equals("I don't know...")) {
                output = new StringBuilder();
                String determinedSkill = languageModel.determineSkill(query);
                if (determinedSkill.equals("Random"))
                    return "Sorry, I'm not sure I understood ...";
                for (SkillWrapper skill : this.skills.keySet()) {
                    if (skill.getClass().getSimpleName().equals(determinedSkill)) {
                        List<String> slots = languageModel.findSlot(query);
                        output.append(languageModel.botResponse(slots));
                        if (slots != null && !slots.isEmpty() && !slots.get(0).equals("") && slots.get(0) != null) {
                            skill.start(slots);
                            output.append(skill.getResponse());
                        } else if (slots.isEmpty())
                            break;
                    }
                }
            }

            if (!output.isEmpty()) {
                return output.toString();
            } else {
                return "Sorry, I don't know how I can answer to this ...";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, that confused me ...";
        }
    }
}
