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
        System.out.println("LEXICAL ANALYSIS");
        do{
            token = obj.nextToken();
            obj.list.add(token);
            System.out.println(" [" + token.getName() + " , " + token.getToken_type() + " ]");
            if(token.getName().equals("$"))
                break;
        }
        while(true);
        System.out.println("Do You Want to See Sybmol Table ?[Y/N]");
        String S1=scn.next();
        if(S1.equalsIgnoreCase("y") || S1.equalsIgnoreCase("yes") ){
        
        System.out.println("=========================================");
        obj.isID();
        }

    }
    //Lexical_Analysis obj = new Lexical_Analysis("C:\\Users\\LENOVO\\Desktop\\lego.txt");

}

