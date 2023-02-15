package com.dacs.digitalassistent;

import backend.DA;

import java.util.Scanner;

public class CUI {
    public final DA assistant;
    Scanner input;
    CUI(){
       assistant = new DA();
       input = new Scanner(System.in);
       launch();
    }
    private void launch() {
        boolean exited = false;
        while (!exited){
            System.out.println("What can I help you with?");
            String query = input.nextLine();
            assistant.startQuery(query);

        }
    }

    public static void main(String[] args) {
        CUI tester = new CUI();
    }
}
