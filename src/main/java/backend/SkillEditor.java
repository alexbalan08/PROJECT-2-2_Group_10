package backend;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class SkillEditor implements ActionQuery {
    HashMap<ArrayList<String>, Method> mapFunctions = new HashMap<>();
    String CFGSkillsFilePath = "./src/main/java/backend/CFG/CFG.txt";
    String query;
    String key;
    String lastSkillsAdded = "";
    boolean isQueryToEditSkill;
    public HashMap.Entry<ArrayList<String>, Method> entry;
    public ArrayList<String> addSkills = new ArrayList<>();
    public ArrayList<String> addCFGSkills = new ArrayList<>();
    public ArrayList<String> deleteCFGSkills = new ArrayList<>();

    public SkillEditor() throws NoSuchMethodException, IOException {
        addSkillsToHashMap();
    }

    public void addSkillsToHashMap() throws NoSuchMethodException {
        String[] addSkill = {"add skill:", "add skills:", "add this skill:", "add the following skill:", "add this following skill:", "add the following skills:", "add these following skills:", "add the skill:", "add the skills:", "add these skills:", "add:", "add this other skill:", "add these other skills:", "add another skill:", "add one more skill:", "add this one more skill:"};
        addSkills.addAll(Arrays.asList(addSkill));
        for (String addSkillOption : addSkill) {
            addSkills.add(addSkillOption);
            addSkills.add("can you " + addSkillOption);
        }

        String[] addCFGSkill = {"add cfg skill:", "add cfg skills:", "add this cfg skill:", "add the following cfg skill:", "add this following cfg skill:", "add the following cfg skills:", "add these following cfg skills:", "add the cfg skill:", "add the cfg skills:", "add these cfg skills:", "add cfg:", "add this other cfg skill:", "add these other cfg skills:", "add another cfg skill:", "add one more cfg skill:", "add this one more cfg skill:"};
        addCFGSkills.addAll(Arrays.asList(addCFGSkill));
        for (String addCFGSkillOption : addCFGSkill) {
            addCFGSkills.add(addCFGSkillOption);
            addCFGSkills.add("can you " + addCFGSkillOption);
        }

        String[] deleteCFGSkill = {"remove cfg skill:", "remove cfg skills:", "delete cfg skill:", "delete cfg skills:", "remove skill:", "remove skills:", "delete skill:", "delete skills:"};
        deleteCFGSkills.addAll(Arrays.asList(deleteCFGSkill));
        for (String deleteCFGSkillOption : deleteCFGSkill) {
            deleteCFGSkills.add(deleteCFGSkillOption);
            deleteCFGSkills.add("can you " + deleteCFGSkillOption);
        }

        mapFunctions.put(addSkills, SkillEditor.class.getMethod("addCFGSkill"));
        mapFunctions.put(addCFGSkills, SkillEditor.class.getMethod("addCFGSkill"));
        mapFunctions.put(deleteCFGSkills, SkillEditor.class.getMethod("deleteCFGSkill"));
    }

    public void setQuery(String query) {
        this.query = query;
        this.isQueryToEditSkill = false;
        for (HashMap.Entry<ArrayList<String>, Method> entry : mapFunctions.entrySet()) {
            ArrayList<String> commands = entry.getKey();
            for (String command : commands) {
                if (command.contains("\\d+")) {
                    String[] commandSeparated = command.split(Pattern.quote("\\d+"));
                    if (Pattern.compile(command).matcher(query.toLowerCase()).find()) {
                        boolean doesQueryMatchToCommand = true;
                        // RECENTLY ADDED
                        for (String commandPart : commandSeparated) {
                            if (!query.toLowerCase().contains(commandPart)) {
                                doesQueryMatchToCommand = false;
                                break;
                            }
                        }
                        if (doesQueryMatchToCommand) {
                            this.entry = entry;
                            this.key = command;
                            this.isQueryToEditSkill = true;
                        }
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
        if (entry == null)
            return "Check if the user wants to edit a skill";
        return (String) mapFunctions.get(entry.getKey()).invoke(this);
    }

    public String addCFGSkill() {
        String file = rewriteFile();
        try (FileWriter fw = new FileWriter(this.CFGSkillsFilePath, false)) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                try (PrintWriter pw = new PrintWriter(bw)) {
                    String command = query.substring(0, query.indexOf("\n"));
                    boolean addMoreThanOneSkill = command.contains("skills");
                    query = query.replace(command, "");
                    query = query.replace(" : ", " ").replace(": ", " ").replace(":", "");
                    lastSkillsAdded = lastSkillsAdded.concat("\n" + query);
                    pw.print(file + query + "\n");

                    if (!addMoreThanOneSkill)
                        return "The skill has been added";

                    lastSkillsAdded = query;
                    return "The skills have been added";
                }
            } catch (IOException io) {
                return "Error with the BufferedWriter";
            }
        } catch (IOException io) {
            return "Error with the FileWriter";
        }
    }

    public String rewriteFile() {
        String input = "";
        try {
            FileReader fr = new FileReader("./src/main/java/backend/CFG/CFG.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            String command = query.substring(0, query.indexOf("\n"));
            query = query.replace(command, "");
            String type = query.substring(0, query.indexOf("\nR"));
            query = query.replace(type, "");
            type = type.replace("\nType :  ", "");
            String[] types = type.split("\\|");

            while ((line = br.readLine()) != null) {
                input += line + System.lineSeparator();
                if (line.startsWith("Rule <ACTION>")) {
                    for (String typ : types) {
                        typ = typ.substring(typ.indexOf(":") + 1).replace("?", "").trim();
                        if (!typ.contains("<")) {
                            typ = "<" + typ.toUpperCase() + ">";
                        }
                        input = input.replace(line, line.trim() + " | " + typ);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input;
    }

    public String deleteCFGSkill() {
        String command = query.substring(query.lastIndexOf(":") + 1).trim();
        List<String> toDelete = this.getElementsToDelete(command, getAllSkills());
        String newFile = this.deleteSkillInFile(toDelete);
        this.rewriteFile(newFile);
        return "Skills " + toDelete + " are now deleted.";
    }

    public String addCFGSkillTemplateType() {
        return "\nType : ";
    }

    public String addCFGSkillTemplateRulesAndActions(String type) {
        return "Rule : <" + type + ">\nAction : <" + type + "> *  * ";
    }

    public String addAllSkillsToDelete() {
        return "Remove the skill that you want to delete :\n" + this.getAllSkills();
    }

    private String getAllSkills() {
        String result = "";
        try (BufferedReader br = new BufferedReader(new FileReader(this.CFGSkillsFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("Rule <ACTION>")) {
                    line = line.substring(line.indexOf(">") + 1).trim();
                    return line;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while trying to get all skills : " + this.CFGSkillsFilePath);
        }
        return result;
    }

    private List<String> getElementsToDelete(String input, String skill) {
        String[] skillSplit = skill.split("\\|");
        List<String> result = new ArrayList<>();
        for(String next : skillSplit) {
            if(!input.contains(next.trim())) {
                result.add(next.trim());
            }
        }
        return result;
    }

    private String deleteSkillInFile(List<String> toDelete) {
        try (BufferedReader br = new BufferedReader(new FileReader(this.CFGSkillsFilePath))) {
            String input = "";
            String line;
            boolean toContinue = false;
            while ((line = br.readLine()) != null) {
                if(!toContinue && line.startsWith("Rule <ACTION>")) {
                    for (String next : toDelete) {
                        line = line.replace(next, "").trim();
                        if (line.endsWith("|")) {
                            line = line.substring(0, line.length() - 1);
                        }
                        line = line.replace("|  |", " | ").replace("| |", " | ").replace("||", " | ");
                    }
                    toContinue = true;
                }
                for (String next : toDelete) {
                    if(line.contains(next)) {
                        line = "";
                        break;
                    }
                }

                if(!line.equals("")) {
                    input += line + "\n";
                }
            }
            return input;
        } catch (IOException e) {
            System.out.println("Error while trying to delete skills : " + this.CFGSkillsFilePath);
            return "";
        }
    }

    private void rewriteFile(String newFile) {
        try (FileWriter fw = new FileWriter(this.CFGSkillsFilePath, false)) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                try (PrintWriter pw = new PrintWriter(bw)) {
                    pw.println(newFile);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while trying to delete skills in the file " + this.CFGSkillsFilePath);
        }
    }
}