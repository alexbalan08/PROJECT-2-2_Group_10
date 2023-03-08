package backend;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class SkillEditor implements ActionQuery {
    HashMap<ArrayList<String>, Method> mapFunctions = new HashMap<>();
    String query;
    String key;
    String lastSkillsAdded = "";
    int countMinSkillsAdded = 0;
    boolean isQueryToEditSkill;
    public HashMap.Entry<ArrayList<String>, Method> entry;
    public ArrayList<String> addSkills = new ArrayList<>();
    public ArrayList<String> showSkills = new ArrayList<>();
    public ArrayList<String> getLastSkill = new ArrayList<>();
    public ArrayList<String> getLastSkillAdded = new ArrayList<>();
    public ArrayList<String> getLastSkillsAdded = new ArrayList<>();

    public SkillEditor() throws NoSuchMethodException {
        addSkillsToHashMap();
    }

    public void addSkillsToHashMap() throws NoSuchMethodException {
        addSkills.add("add skill:");
        addSkills.add("add skills:");
        addSkills.add("add this skill");
        addSkills.add("add the following skill:");
        addSkills.add("add this following skill:");
        addSkills.add("add the following skills:");
        addSkills.add("add these following skills:");
        addSkills.add("add the skill:");
        addSkills.add("add the skills:");
        addSkills.add("add these skills:");
        addSkills.add("add:");
        addSkills.add("add this other skill:");
        addSkills.add("add these other skills:");
        addSkills.add("add another skill:");
        addSkills.add("add one more skill:");
        addSkills.add("add this one more skill:");

        String[] getShow = {"get", "get me", "show", "show me"};
        String[] the = {" the", ""};
        String[] added = {" added", " I added", " that I added", " you added", " that you added", " that has been added", " that was added"};

        for (String item : getShow) {
            showSkills.add(item + " skills");
            showSkills.add(item + " the skills");
            showSkills.add(item + " all skills");
            showSkills.add(item + " all the skills");
        }

        for (String value : getShow) {
            for (String s : the) {
                for (String add : added) {
                    getLastSkill.add(value + s + " last skill");
                    getLastSkillAdded.add(value + s + " last skill" + add);
                }
            }
        }

        for (String showSkill : showSkills) {
            getLastSkillsAdded.add(showSkill.concat(" added"));
            getLastSkillsAdded.add(showSkill.concat(" that I added"));
            getLastSkillsAdded.add(showSkill.concat(" that you added"));
            getLastSkillsAdded.add(showSkill.concat(" that have been added"));
            getLastSkillsAdded.add(showSkill.concat(" that were added"));
            String show = showSkill.substring(0, showSkill.lastIndexOf(" ") + 1);
            String skill = showSkill.substring(showSkill.lastIndexOf(" ") + 1);
            getLastSkillsAdded.add(show + "last " + skill);
            getLastSkillsAdded.add(show + "last " + skill + " added");
            getLastSkillsAdded.add(show + "last " + skill + " that I added");
            getLastSkillsAdded.add(show + "last " + skill + " that you added");
            getLastSkillsAdded.add(show + "last " + skill + " that have been added");
            getLastSkillsAdded.add(show + "last " + skill + " that were added");
        }

        mapFunctions.put(addSkills, SkillEditor.class.getMethod("addSkill"));
        mapFunctions.put(showSkills, SkillEditor.class.getMethod("getSkills"));
        mapFunctions.put(getLastSkill, SkillEditor.class.getMethod("getLastSkill"));
        mapFunctions.put(getLastSkillsAdded, SkillEditor.class.getMethod("getLastSkillsAdded"));
        mapFunctions.put(getLastSkillAdded, SkillEditor.class.getMethod("getLastSkillAdded"));
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
        lastSkillsAdded = lastSkillsAdded.concat(query);
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
            return "One skill has been added:\n".concat(lastSkillsAdded);
        }
        return lastSkillsAdded;
   }

   public String deleteAllAddedSkills() {
       if(countMinSkillsAdded == 0) return "No skills have been recently added.";
       return null;
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
