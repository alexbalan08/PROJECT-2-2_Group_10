package UI;

import backend.DA;

import java.io.IOException;
import java.util.Scanner;

public class CUI {
    public final DA assistant;
    Scanner input;
    CUI() throws IOException {
       assistant = new DA();
       input = new Scanner(System.in);
       launch();
    }
    private void launch() throws IOException {
        boolean exited = false;
        while (!exited){
            System.out.println("What can I help you with?");
            String query = input.nextLine();
            assistant.startQuery(query);

        }
    }

    public static void main(String[] args) throws IOException {
       new CUI();
    }
}
