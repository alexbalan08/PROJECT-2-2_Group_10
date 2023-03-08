package backend;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SkillEditor implements ActionQuery {
    HashMap<ArrayList<String>, Method> mapFunctions = new HashMap<>();
    String query;
    String key;
    String lastSkillsAdded = "";
    String allSkillsAdded = "";
    int countMinSkillsAdded = 0;
    boolean isQueryToEditSkill;
    public HashMap.Entry<ArrayList<String>, Method> entry;
    public ArrayList<String> addSkills = new ArrayList<>();
    public ArrayList<String> showSkills = new ArrayList<>();
    public ArrayList<String> getLastSkill = new ArrayList<>();
    public ArrayList<String> getLastSkillAdded = new ArrayList<>();
    public ArrayList<String> getLastSkillsAdded = new ArrayList<>();
    public ArrayList<String> deleteAllAddedSkills = new ArrayList<>();

    public SkillEditor() throws NoSuchMethodException {
        addSkillsToHashMap();
    }

    public void addSkillsToHashMap() throws NoSuchMethodException {
        String[] addSkill = {"add skill:", "add skills:", "add this skill:", "add the following skill:", "add this following skill:", "add the following skills:", "add these following skills:", "add the skill:", "add the skills:", "add these skills:", "add:", "add this other skill:", "add these other skills:", "add another skill:", "add one more skill:", "add this one more skill:"};
        addSkills.addAll(Arrays.asList(addSkill));
        for (String addSkillOption : addSkill) {
            addSkills.add(addSkillOption);
            addSkills.add("can you " + addSkillOption);
        }

        String[] getShow = {"get", "can you get", "get me", "can you get me", "show", "can you show", "show me", "can you show me"};
        String[] the = {" the", ""};
        String[] added = {" added", " I added", " that I added", " you added", " that you added", " I told you to add", " that I told you to add"};
        String[] addedSingular = {" that has been added", " that was added"};
        String[] addedPlural = {" that have been added", " that were added"};
        String[] remove = {"remove", " please remove", "can you remove", "delete", "can you delete", " please delete"};
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
        mapFunctions.put(deleteAllAddedSkills, SkillEditor.class.getMethod("deleteAllAddedSkills"));
    }

    public void setQuery(String query) {
        this.query = query;
        for (HashMap.Entry<ArrayList<String>, Method> entry : mapFunctions.entrySet()) {
            ArrayList<String> commands = entry.getKey();
            for (String command : commands) {
                if (query.toLowerCase().contains(command)) {
                    this.entry = entry;
                    this.key = command;
                    this.isQueryToEditSkill = true;
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
        allSkillsAdded = allSkillsAdded.concat("\n" + query);
        FileWriter fw = new FileWriter(new File("./src/main/java/backend/Skills/SkillsTemplate.txt"), true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println(query);
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
       String filePath = "./src/main/java/backend/Skills/SkillsTemplate.txt";
       String skills = Files.readString(Path.of(filePath));
       skills = skills.replaceAll(allSkillsAdded.strip(), "");
       System.out.println("Text file will have:\n" + skills);
       System.out.println("The skills added are:\n" + allSkillsAdded.strip());
       PrintWriter pw = new PrintWriter(new File(filePath));
       pw.append(skills.strip().concat("\n"));
       pw.flush();
       return "All the added skills were deleted successfully.";
   }

   public String deleteLastSkillAdded() {
        return null;
   }

   public String editSkill() {
        return null;
   }

   public String editLastSkill() {
        return null;
   }
}
