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
public class cfgPlayground {
    public static void main(String[] args) {
        String input = "play classical music";
        // regex syntax
        if(input.matches("play (rock|pop|jazz|classical) music")){
            System.out.println("Playing");
        }
    }
}