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



// THIS CLASS IS JUST A SCRATCH. IGNORE IT

import java.util.*;

public class cfgPlayground {
    Map<String, String> request;
    Map<String, String> actions;
    List<String> queries;

    public cfgPlayground() {
        this.request = new HashMap<>();
        this.request.put("can you", "action");
        this.request.put("what is", "query");

        this.actions = new HashMap<>();
        this.actions.put("search for", "query");
        this.actions.put("tell me", "query");

        this.queries = new ArrayList<>();
        this.queries.add("weather");
        this.queries.add("music");
    }

    public String determineSkill(String input) {
        return this.determineRequest(input);
    }

    private String determineRequest(String input) {
        for (String request : this.request.keySet()) {
            if(input.contains(request)) {
                return switch (this.request.get(request)) {
                    case "action" -> determineAction(input);
                    case "query" -> determineQuery(input);
                    default -> "";
                };
            }
        }
        return "";
    }

    private String determineAction(String input) {
        for (String action : this.actions.keySet()) {
            if(input.contains(action)) {
                if(Objects.equals(this.actions.get(action), "query")) {
                    return determineQuery(input);
                }
            }
        }
        return "";
    }

    private String determineQuery(String input) {
        for (String query : this.queries) {
            if(input.contains(query)) {
                if(query.equals("music")) {
                    return "Spotify";
                }
                return query.substring(0, 1).toUpperCase() + query.substring(1);
            }
        }
        return "";
    }
}