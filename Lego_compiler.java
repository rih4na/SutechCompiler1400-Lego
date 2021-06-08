package lego_compiler;

import java.util.Scanner;

public class Lego_compiler {

    public static void main(String[] args) {
        String file;
        Scanner scn = new Scanner(System.in);
        System.out.println("welcome to Lego Compiler");
        System.out.println("please enter your  file address  : ");
        file=scn.next();
        Lexical_Analysis obj = new Lexical_Analysis(file);
        
        Token token;
        do{
            token = obj.nextToken();
            System.out.println(" [" + token.getName() + " , " + token.getToken_type() + " ]");
            if(token.getName().equals("$"))
                break;
        }
        while(true);

    }

}

