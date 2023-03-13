package backend;

import javafx.scene.control.TextArea;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillEditor implements ActionQuery {
    HashMap<ArrayList<String>, Method> mapFunctions = new HashMap<>();
    String skillsFilePath = "./src/main/java/backend/Skills/SkillsTemplate.txt";
    TextArea textArea;
    int maxNewLinesTextArea;
    int textHeight;
    String query;
    String key;
    String lastSkillsAdded = "";
    String originalSkillsTemplate;
    int countMinSkillsAdded = 0;
    boolean isQueryToEditSkill;
    public HashMap.Entry<ArrayList<String>, Method> entry;
    public ArrayList<String> addSkills = new ArrayList<>();
    public ArrayList<String> showSkills = new ArrayList<>();
    public ArrayList<String> getLastSkill = new ArrayList<>();
    public ArrayList<String> getLastSkillAdded = new ArrayList<>();
    public ArrayList<String> getLastSkillsAdded = new ArrayList<>();
    public ArrayList<String> deleteAllAddedSkills = new ArrayList<>();
    public ArrayList<String> deleteLastAddedSkill = new ArrayList<>();
    public ArrayList<String> editLastSkill = new ArrayList<>();
    public ArrayList<String> editSkill = new ArrayList<>();

    public SkillEditor(TextArea textArea, int maxNewLinesTextArea, int textHeight) throws NoSuchMethodException, IOException {
        this.textArea = textArea;
        this.maxNewLinesTextArea = maxNewLinesTextArea;
        this.textHeight = textHeight;
        addSkillsToHashMap();
        originalSkillsTemplate = Files.readString(Path.of(skillsFilePath));
    }

    public void addSkillsToHashMap() throws NoSuchMethodException {
        String[] addSkill = {"add skill:", "add skills:", "add this skill:", "add the following skill:", "add this following skill:", "add the following skills:", "add these following skills:", "add the skill:", "add the skills:", "add these skills:", "add:", "add this other skill:", "add these other skills:", "add another skill:", "add one more skill:", "add this one more skill:"};
        addSkills.addAll(Arrays.asList(addSkill));
        for (String addSkillOption : addSkill) {
            addSkills.add(addSkillOption);
            addSkills.add("can you " + addSkillOption);
        }

        String[] getShow = {"get", "can you get", "get me", "can you get me", "show", "can you show", "show me", "can you show me"};
        String[] remove = {"remove", "please remove", "can you remove", "delete", "can you delete", "please delete"};
        String[] edit = {"edit", "can I edit", "may I edit", "let me edit", "please let me edit", "change", "can I change", "may I change", "let me change", "please let me change"};
        String[] the = {" the", ""};
        String[] added = {" added", " I added", " that I added", " you added", " that you added", " I told you to add", " that I told you to add"};
        String[] addedSingular = {" that has been added", " that was added"};
        String[] addedPlural = {" that have been added", " that were added"};
        String[] skills = {" skills", " the skills", " all skills", " all the skills"};

        for (String getShowOption : getShow) {
            for (String skillsOption : skills) {
                showSkills.add(getShowOption + skillsOption);
            }
        }

        for (String removeOption : remove) {
            for (String skillsOption : skills) {
                deleteAllAddedSkills.add(removeOption + " the " + skillsOption);
                deleteAllAddedSkills.add(removeOption + skillsOption);
                deleteAllAddedSkills.add(removeOption + " added skills");
                deleteAllAddedSkills.add(removeOption + " the added skills");
                deleteAllAddedSkills.add(removeOption + " all added skills");
                deleteAllAddedSkills.add(removeOption + " all the added skills");
            }
        }

        for (String removeOption : remove) {
            for (String theOption : the) {
                for (String addedOption : added) {
                    deleteLastAddedSkill.add(removeOption + theOption + " skill" + addedOption);
                    deleteLastAddedSkill.add(removeOption + theOption + " last skill" + addedOption);
                }
                for (String addedOption : addedPlural) {
                    deleteLastAddedSkill.add(removeOption + theOption + " skill" + addedOption);
                    deleteLastAddedSkill.add(removeOption + theOption + " last skill" + addedOption);
                }
                deleteLastAddedSkill.add(removeOption + theOption + " added skill");
                deleteLastAddedSkill.add(removeOption + theOption + " last added skill");
            }
        }

        for (String editOption : edit) {
            for (String theOption : the) {
                editLastSkill.add(editOption + theOption + " last skill");
                for (String addedOption : added) {
                    editLastSkill.add(editOption + theOption + " last skill" + addedOption);
                }
            }
        }

        for (String editOption : edit) {
            for (String theOption : the) {
                editSkill.add(editOption + theOption + " skill \\d+");
                editSkill.add(editOption + theOption + " skill \\d+ from the text file");
                for (String addedOption : added) {
                    editSkill.add(editOption + theOption + " skill \\d+" + addedOption);
                }
            }
        }

        for (String getShowOption : getShow) {
            for (String theOption : the) {
                for (String add : added) {
                    getLastSkill.add(getShowOption + theOption + " last skill");
                    getLastSkillAdded.add(getShowOption + theOption + " last skill" + add);
                    getLastSkillAdded.add(getShowOption + theOption + "skill" + add);
                }
                for (String add : addedSingular) {
                    getLastSkill.add(getShowOption + theOption + " last skill");
                    getLastSkillAdded.add(getShowOption + theOption + " last skill" + add);
                }
            }
        }

        for (String showSkill : showSkills) {
            String show = showSkill.substring(0, showSkill.lastIndexOf(" ") + 1);
            String skill = showSkill.substring(showSkill.lastIndexOf(" ") + 1);
            for (String add : added) {
                getLastSkillsAdded.add(showSkill.concat(add));
                getLastSkillsAdded.add(show + "last " + skill + add);
            }
            for (String add : addedPlural) {
                getLastSkillsAdded.add(showSkill.concat(add));
                getLastSkillsAdded.add(show + "last " + skill + add);
            }
            getLastSkillsAdded.add(show + "last " + skill);
        }

        mapFunctions.put(addSkills, SkillEditor.class.getMethod("addSkill"));
        mapFunctions.put(showSkills, SkillEditor.class.getMethod("getSkills"));
        mapFunctions.put(getLastSkill, SkillEditor.class.getMethod("getLastSkill"));
        mapFunctions.put(getLastSkillsAdded, SkillEditor.class.getMethod("getLastSkillsAdded"));
        mapFunctions.put(getLastSkillAdded, SkillEditor.class.getMethod("getLastSkillAdded"));
        mapFunctions.put(deleteLastAddedSkill, SkillEditor.class.getMethod("deleteLastAddedSkill"));
        mapFunctions.put(deleteAllAddedSkills, SkillEditor.class.getMethod("deleteAllAddedSkills"));
        mapFunctions.put(editLastSkill, SkillEditor.class.getMethod("editLastSkill"));
        mapFunctions.put(editSkill, SkillEditor.class.getMethod("editSkill"));
    }

    public void setQuery(String query) {
        this.query = query;
        for (HashMap.Entry<ArrayList<String>, Method> entry : mapFunctions.entrySet()) {
            ArrayList<String> commands = entry.getKey();
            for (String command : commands) {
                if (command.contains("\\d+")) {
                    if (Pattern.compile(command).matcher(query).find()) {
                        this.entry = entry;
                        this.key = command;
                        this.isQueryToEditSkill = true;
                    }
                } else {
                    if (query.toLowerCase().contains(command)) {
                        this.entry = entry;
                        this.key = command;
                        this.isQueryToEditSkill = true;
                    }
                }
            }
        }
    }

    public boolean isQueryToEditSkill() {
        return isQueryToEditSkill;
    }

    public String startQuery(String query) throws InvocationTargetException, IllegalAccessException {
        if (entry == null) System.out.println("Check if the user wants to edit a skill");
        return (String) mapFunctions.get(entry.getKey()).invoke(this);
    }

    // Adds the skill to the text file and returns a String saying if adding the skill to the SkillsTemplate was successful
    public String addSkill() throws IOException {
        String command = query.substring(0, query.indexOf("\n"));
        boolean addMoreThanOneSkill = command.contains("skills");
        query = query.replace(command, "");
        countMinSkillsAdded++;
        lastSkillsAdded = lastSkillsAdded.concat("\n" + query);
        FileWriter fw = new FileWriter(new File("./src/main/java/backend/Skills/SkillsTemplate.txt"), true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.print(query + "\n");
        pw.flush();
        pw.close();
        bw.close();
        fw.close();
        if (!addMoreThanOneSkill) return "The skill has been added";
        countMinSkillsAdded += 2;
        lastSkillsAdded = query;
        return "The skills have been added";
    }

   public String getSkills() throws IOException {
        return Files.readString(Path.of("./src/main/java/backend/Skills/SkillsTemplate.txt"));
   }

   public String getLastSkill() throws IOException {
       String skillsTemplateText = getSkills();
       String lastSkill;
       if (skillsTemplateText.strip().contains("\n")) lastSkill = skillsTemplateText.strip().substring(skillsTemplateText.strip().lastIndexOf("\n\n") + 2);
       else lastSkill = skillsTemplateText;
       return lastSkill;
   }

   public String getLastSkillAdded() throws IOException {
        String lastSkill = getLastSkill();
        if (lastSkillsAdded.contains(lastSkill)) return lastSkill;
        return "No skills have been recently added.";
   }

   public String getLastSkillsAdded() {
        if(countMinSkillsAdded == 0) return "No skills have been recently added.";
        else if (countMinSkillsAdded == 1) {
            return "One skill has been added:\n".concat(lastSkillsAdded.strip());
        }
        return lastSkillsAdded.strip();
   }

   public String deleteAllAddedSkills() throws IOException {
       if(countMinSkillsAdded == 0) return "No skills have been recently added.";
       writeToSkillsFile(originalSkillsTemplate);
       countMinSkillsAdded = 0;
       lastSkillsAdded = "";
       return "All the added skills were deleted successfully.";
   }

   public String deleteLastAddedSkill() throws IOException {
       if(countMinSkillsAdded == 0) return "No skills have been recently added.";
       String textWithDeletedSkill = getSkills().substring(0, getSkills().indexOf(getLastSkillAdded().strip()));
       writeToSkillsFile(textWithDeletedSkill.strip().concat("\n"));
       countMinSkillsAdded--;
       return "The last added skill was deleted successfully.";
   }

   public void writeToSkillsFile(String text) throws FileNotFoundException {
       PrintWriter pw = new PrintWriter(new File(skillsFilePath));
       pw.append(text);
       pw.flush();
   }

   public String editSkill() throws IOException {
        Matcher matcher = Pattern.compile("\\d+").matcher(query);
        int skillNumber = 0;
        if (matcher.find()) {
            skillNumber = Integer.parseInt(matcher.group());
        }
        String[] skills = getSkills().split("\n\n");
        String skill = skills[skillNumber - 1].strip();
        if (!lastSkillsAdded.contains(skill)) return "Sorry, you can not edit a skill you have not added yourself.";
        addToTextArea("add the skill:\n" + skill);
        writeToSkillsFile(getSkills().replaceAll("\n\n" + skill, "").replaceAll("\n\n\n", "\n\n"));
        return "You can now edit the skill.";
   }

   public String editLastSkill() throws IOException {
       if(countMinSkillsAdded == 0) return "Sorry, you can not edit a skill you have not added yourself.";
       addToTextArea("add the skill:\n" + getLastSkillAdded().strip());
       deleteLastAddedSkill();
       return "You can now edit the skill.";
   }

   public void addToTextArea(String text) {
        textArea.setText(text);
        int numberOfLines = text.split("\n").length;
        System.out.println("Number of lines: " + numberOfLines);
        if (numberOfLines > maxNewLinesTextArea) textArea.setMaxHeight((textHeight + (textHeight * 1.5)) + maxNewLinesTextArea * (textHeight + 3));
        else textArea.setMaxHeight((textHeight + (textHeight * 1.5)) + (numberOfLines - 1) * (textHeight + 3));
   }
}