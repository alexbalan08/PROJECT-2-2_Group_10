package UI;

import backend.DA;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class CUI {
    public final DA assistant;
    Scanner input;
    CUI() throws Exception {
       assistant = new DA();
       input = new Scanner(System.in);
       launch();
    }
    private void launch() throws IOException, InvocationTargetException, IllegalAccessException {
        boolean exited = false;
        while (!exited){
            System.out.println("What can I help you with?");
            String query = input.nextLine();
            assistant.startQuery(query);
        }
    }

    public static void main(String[] args) throws Exception {
       new CUI();
    }
}
