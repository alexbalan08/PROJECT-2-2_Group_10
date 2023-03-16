package backend;

// Examples provided by ChatGBT:

//<start> ::= <greeting> <request> | <goodbye>
//<greeting> ::= "Hello" | "Hi" | "Hey"
//<request> ::= "Can you" <action> "?" | "What is" <subject> "?"
//<action> ::= "search for" <query> | "tell me about" <query>
//<query> ::= "weather" | "news" | "sports"
//<subject> ::= "the weather" | "the news" | "sports"
//<goodbye> ::= "Goodbye" | "See you later" | "Bye"

//<question> ::= "what is the weather like" <location> "today"
//<location> ::= "in" <city>
//<city> ::= "New York" | "Los Angeles" | "Chicago" | "Houston" | "Philadelphia" | "Phoenix" | "San Antonio" | "San Diego"


//<command> ::= "what is" <expression>
//<expression> ::= <number> <operator> <number>
//<number> ::= <digit> | <digit> <number>
//<digit> ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
//<operator> ::= "+" | "-" | "*" | "/"

//<command> ::= "play" <song>
//<songs> ::= "pop" | "rock" | "jazz" | "classical"



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import UI.HelloApplication;

// THIS CLASS IS JUST A SCRATCH. IGNORE IT
public class cfgPlayground {
    public static String CFG(String input) throws IOException {
        int max = 0;
        int count = 0;
        ArrayList<String> keywords = new ArrayList<>();
        ArrayList<String> userkeys = new ArrayList<>();
        ArrayList<String[]> outputkeys = new ArrayList<>();
        ArrayList<String> actions = new ArrayList<>();
        String question = "";
        String answer = "";
        Scanner reader = new Scanner(new File("C:\\Users\\simon\\Downloads\\PROJECT-2-2_Group_10-main-2\\src\\main\\java\\backend\\Skills\\SkillsTemplate.txt"));
            while(reader.hasNextLine()) {
               String word = reader.nextLine();
             if(word.contains("Slot ")) {
                keywords.add(word.replaceAll("Slot |<.*?> ",""));
               }
             if(word.contains("Question ")) {
                 question = word.replaceAll("Question | <.*?>| at", "");
             }
             if(word.contains("Action ")) {
                 actions.add(word.replaceAll("Action |<.*?> ",""));
             }
            }
            reader.close();

        for(int i = 0; i < keywords.size(); i++) {
            if(input.contains(question) && input.contains(keywords.get(i))) {
                userkeys.add(keywords.get(i));
            }
        }

        for(int i = 0; i < actions.size(); i++) {
          for(int j = 0; j < userkeys.size(); j++) {
              if(actions.get(i).contains(userkeys.get(j))) {
                  count++;
              }
              if(count > max) {
                  max = count;
                  answer = actions.get(i);

              }
          }
          count = 0;
        }
        if(max == 0) {
            System.out.println(question);
            return(actions.get(actions.size()-1));
        }
        else {
            System.out.println(question);
            return(answer);
        }
    }
}