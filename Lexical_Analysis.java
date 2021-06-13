package lego_compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Lexical_Analysis {

    private int currentPoint = -1;
    private int currentState = 0;

    private String path;
    private String code;
    ArrayList<Token> list = new ArrayList<Token>();

    private final int S_Index = 100;
    Token[] Symbol_Table = new Token[S_Index];
    int s_count = 0;

//--------------------------------------------------------------Alphabet
    private final char[] letters = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
        'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private final char[] symbols = {'(', ')', '{', '}', ';', '+', '-', '*', '/', '=', '.', '<', '>',
        '!', '"', '&', '|', '%'
    };

    private final char[] whiteSpaces = {
        '\n', '\t', ' ', '\''
    };

    private final String[] keywords = {"for", "while", "if", "else", "then", "switch", "case", "break", "else if", "elseif", "static",
        "void", "public", "default", "continue", "return", "long", "int", "String", "char", "boolean", "double", "float"
    };

    //---------------------------------------------Transsition Table
    private final int[][] TTable = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 9, 10, 12, 13, 11, 2, 4, 27, 19, 28, -1, 30, 32, 34, 25, 14, 16, 18, 37, 37, 37, 36, 40},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, -1, -1, -1, -1, -1, 3, -1, -1, -1, 38, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, -1, -1, -1, -1, -1, -1, 5, -1, -1, 39, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 17, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21, 20, 20, 20, 20},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22},
        {22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23, 24, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 26, 25, 25, 25, 25, 25, 25, 25, 25},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 29, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 35, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, -1, -1, 41, 41, 42},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 42},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}

    };

    private int nextState(int state, char ch) {
        int index = 0;
        for (int i = 0; i < letters.length; i++) {
            if (letters[i] == ch) {
                index = i;
            }
        }
        for (int i = 0; i < digits.length; i++) {
            if (digits[i] == ch) {
                index = i + letters.length;
            }
        }
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i] == ch) {
                index = i + letters.length + digits.length;
            }
        }
        for (int i = 0; i < whiteSpaces.length; i++) {
            if (whiteSpaces[i] == ch) {
                index = i + letters.length + digits.length + symbols.length;
            }
        }
        if (ch == '$') {
            index = 83;
        } else if (ch == '\'') {
            index = 84;
        }

        return TTable[state][index];
    }

    //----------------------------------------------------------
    public Token nextToken() {
        char ch;
        currentState = 0;
        String token_name = "";
        char nChar;        //khandane character badi
        int nState;        //khandane state badi
        while (true) {
            switch (currentState) {
                case 0:
                    ch = nextChar();
                    token_name = token_name + ch;
                    currentState = nextState(currentState, ch);
                    if (currentState == -1) {
                        token_name = token_name + "is not defined in Lego Language";

                        return new Token(token_name, 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");
                    }

                    break;

                case 1:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);

                    if (nState == -1) {
                        if (isInKeywords(token_name)) {
                            if (token_name.equals("boolean")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Boolean, Token.ID_Type.none, "");

                            } else if (token_name.equals("break")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Break, Token.ID_Type.none, "");

                            } else if (token_name.equals("case")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Case, Token.ID_Type.none, "");

                            } else if (token_name.equals("char")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Char, Token.ID_Type.none, "");

                            } else if (token_name.equals("continue")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Continue, Token.ID_Type.none, "");

                            } else if (token_name.equals("default")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Default, Token.ID_Type.none, "");

                            } else if (token_name.equals("double")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Double, Token.ID_Type.none, "");

                            } else if (token_name.equals("else")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Else, Token.ID_Type.none, "");

                            } else if (token_name.equals("else if")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.ElseIf, Token.ID_Type.none, "");

                            } else if (token_name.equals("elseif")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Elseif, Token.ID_Type.none, "");

                            } else if (token_name.equals("float")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Float, Token.ID_Type.none, "");

                            } else if (token_name.equals("for")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.For, Token.ID_Type.none, "");

                            } else if (token_name.equals("input")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.input, Token.ID_Type.none, "");

                            } else if (token_name.equals("if")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.IF, Token.ID_Type.none, "");

                            } else if (token_name.equals("int")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Int, Token.ID_Type.none, "");

                            } else if (token_name.equals("long")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Long, Token.ID_Type.none, "");

                            } else if (token_name.equals("public")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Public, Token.ID_Type.none, "");

                            } else if (token_name.equals("static")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Static, Token.ID_Type.none, "");

                            } else if (token_name.equals("String")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.String, Token.ID_Type.none, "");

                            } else if (token_name.equals("switch")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Switch, Token.ID_Type.none, "");

                            } else if (token_name.equals("then")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Then, Token.ID_Type.none, "");

                            } else if (token_name.equals("void")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Void, Token.ID_Type.none, "");

                            } else if (token_name.equals("while")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.While, Token.ID_Type.none, "");

                            } else if (token_name.equals("print")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.print, Token.ID_Type.none, "");

                            } else if (token_name.equals("true") || token_name.equals("false")) {
                                return new Token(token_name, 0, 0, Token.Token_Type.Boolean_literal, Token.ID_Type.none, "");

                            }
                        } else {
                            return new Token(token_name, 0, 0, Token.Token_Type.id, Token.ID_Type.none, "");

                            //id type none chon tahlil gar nahvi inkaro anjam mide
                        }

                    } else {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    }
                    break;

                case 2:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);

                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;

                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.plus, Token.ID_Type.none, "");

                    }

                    break;

                case 3:
                    return new Token(token_name, 0, 0, Token.Token_Type.plusPlus, Token.ID_Type.none, "");

                case 4:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);

                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;

                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.minus, Token.ID_Type.none, "");

                    }

                    break;

                case 5:

                    return new Token(token_name, 0, 0, Token.Token_Type.minusMinus, Token.ID_Type.none, "");

                ///--
                case 6:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);

                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.intNumber, Token.ID_Type.none, "");

                    }

                    break;

                case 7:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        return new Token(token_name + " is not Defined in Lego Language", 0, 0, Token.Token_Type.err, Token.ID_Type.none, ""); //baraye mesal 33. tarif nashode bayad bashe
                    }

                    break;

                case 8:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.floatNumber, Token.ID_Type.none, "");

                    }

                    break;

                case 9:
                    return new Token(token_name, 0, 0, Token.Token_Type.leftParentheses, Token.ID_Type.none, "");

                case 10:
                    return new Token(token_name, 0, 0, Token.Token_Type.rightParentheses, Token.ID_Type.none, "");

                case 11:
                    return new Token(token_name, 0, 0, Token.Token_Type.semicolon, Token.ID_Type.none, "");

                case 12:
                    return new Token(token_name, 0, 0, Token.Token_Type.leftAccolade, Token.ID_Type.none, "");

                case 13:
                    return new Token(token_name, 0, 0, Token.Token_Type.rightAccolade, Token.ID_Type.none, "");

                case 14:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        token_name = token_name + " is not defined in Lego Language";
                        return new Token(token_name, 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");
                    }

                    break;

                case 15:
                    return new Token(token_name, 0, 0, Token.Token_Type.and, Token.ID_Type.none, "");

                case 16:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        token_name = token_name + "is not defined in Lego Language";
                        return new Token(token_name, 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");
                    }

                    break;

                case 17:
                    return new Token(token_name, 0, 0, Token.Token_Type.or, Token.ID_Type.none, "");

                case 18:
                    return new Token(token_name, 0, 0, Token.Token_Type.mod, Token.ID_Type.none, "");

                case 19:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;

                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.divide, Token.ID_Type.none, "");

                    }

                    break;

                case 20:   // comment tak khati
                    if (watchNextChar() == '$') { //baraye zamani ke comment dar khat akhare
                        Token name20 = new Token(token_name, 0, 0, Token.Token_Type.lineComment, Token.ID_Type.none, "");

                        return name20;
                    } else {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nextState(currentState, ch);
                    }

                    break;

                case 21:
                    return new Token(token_name, 0, 0, Token.Token_Type.lineComment, Token.ID_Type.none, "");

                case 22:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nChar == '$') {  //age tahe cod */ nazanim va comment ro nabandim
                        return new Token("Multi Line Comment Missing */", 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");

                    } else {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    }

                    break;

                case 23:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nChar == '$') {  //age tahe cod / nazanim va comment ro nabandim
                        return new Token("Multi Line Comment Missing /", 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");

                    } else {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    }

                    break;

                case 24:
                    return new Token(token_name, 0, 0, Token.Token_Type.multiLineComment, Token.ID_Type.none, "");

                case 25:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nChar == '$') {  //age tahe cod " nazanim va jomle  ro nabandim
                        return new Token("String Missing \"  ", 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");

                    } else {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    }

                    break;

                case 26:
                    return new Token(token_name, 0, 0, Token.Token_Type.string, Token.ID_Type.none, "");

                case 27:
                    return new Token(token_name, 0, 0, Token.Token_Type.multiple, Token.ID_Type.none, "");

                case 28:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.equal, Token.ID_Type.none, "");

                    }

                    break;

                case 29:
                    return new Token(token_name, 0, 0, Token.Token_Type.equalTwice, Token.ID_Type.none, "");

                case 30:
                    if (watchNextChar() == '=') {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = 31;
                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.less, Token.ID_Type.none, "");

                    }

                    break;

                case 31:
                    return new Token(token_name, 0, 0, Token.Token_Type.lessEqual, Token.ID_Type.none, "");

                case 32:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.more, Token.ID_Type.none, "");

                    }

                    break;

                case 33:
                    return new Token(token_name, 0, 0, Token.Token_Type.moreEqual, Token.ID_Type.none, "");

                case 34:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        return new Token("not (!=) Missing =", 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");
                    }

                    break;

                case 35:
                    return new Token(token_name, 0, 0, Token.Token_Type.notEqual, Token.ID_Type.none, "");

                case 36:
                    return new Token(token_name, 0, 0, Token.Token_Type.$, Token.ID_Type.none, "");

                case 37:
                    token_name = "";  //bayad bargardim be state 
                    currentState = 0;

                    break;
                case 38:
                    return new Token(token_name, 0, 0, Token.Token_Type.plusEqual, Token.ID_Type.none, "");

                case 39:
                    return new Token(token_name, 0, 0, Token.Token_Type.minusEqual, Token.ID_Type.none, "");

                case 40:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nChar == '$') {
                        return new Token("Cahracter Missing \'  ", 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");

                    } else if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");
                    }

                    break;

                case 41:
                    nChar = watchNextChar();
                    nState = nextState(currentState, nChar);
                    if (nChar == '$') {
                        return new Token("Cahracter Missing \'  ", 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");

                    } else if (nState != -1) {
                        ch = nextChar();
                        token_name = token_name + ch;
                        currentState = nState;
                    } else {
                        return new Token(token_name, 0, 0, Token.Token_Type.err, Token.ID_Type.none, "");
                    }

                    break;

                case 42:
                    return new Token(token_name, 0, 0, Token.Token_Type.character, Token.ID_Type.none, "");

            }
        }
    }

    ///////////////////////////////////
    public void setValueAndType(Token name, int index) {

        //-----------------
        int b = index - 1;
        int c = index + 1;
        int d = index + 2;
        //-----------------

        if (list.get(b).getToken_type().toString().equals(Token.Token_Type.Long.toString())) {

            if (list.get(c).getToken_type().toString().equals(Token.Token_Type.equal.toString()) && list.get(d).getToken_type().toString().equals(Token.Token_Type.intNumber.toString())) {
                name.setId_type(Token.ID_Type.Long);
                name.setValue(list.get(d).getName());

            } else {
                name.setId_type(Token.ID_Type.Long);
                name.setValue(null);

            }

        } else if (list.get(b).getToken_type().toString().equals(Token.Token_Type.Int.toString())) {

            if (list.get(c).getToken_type().toString().equals(Token.Token_Type.equal.toString()) && list.get(d).getToken_type().toString().equals(Token.Token_Type.intNumber.toString())) {

                name.setId_type(Token.ID_Type.Int);
                name.setValue(list.get(d).getName());

            } else {
                name.setId_type(Token.ID_Type.Int);
                name.setValue(null);
            }

        } else if (list.get(b).getToken_type().toString().equals(Token.Token_Type.Boolean.toString())) {

            if (list.get(c).getToken_type().toString().equals(Token.Token_Type.equal.toString()) && list.get(d).getToken_type().toString().equals(Token.Token_Type.Boolean_literal.toString())) {
                name.setId_type(Token.ID_Type.Boolean);
                name.setValue(list.get(d).getName());

            } else {
                name.setId_type(Token.ID_Type.Boolean);
                name.setValue(null);

            }

        } else if (list.get(b).getToken_type().toString().equals(Token.Token_Type.Double.toString())) {

            if (list.get(c).getToken_type().toString().equals(Token.Token_Type.equal.toString()) && list.get(d).getToken_type().toString().equals(Token.Token_Type.floatNumber.toString())) {
                name.setId_type(Token.ID_Type.Double);
                name.setValue(list.get(d).getName());

            } else {
                name.setId_type(Token.ID_Type.Double);
                name.setValue(null);

            }

        } else if (list.get(b).getToken_type().toString().equals(Token.Token_Type.Float.toString())) {

            if (list.get(c).getToken_type().toString().equals(Token.Token_Type.equal.toString()) && list.get(d).getToken_type().toString().equals(Token.Token_Type.floatNumber.toString())) {
                name.setId_type(Token.ID_Type.Float);
                name.setValue(list.get(d).getName());

            } else {
                name.setId_type(Token.ID_Type.Float);
                name.setValue(null);

            }

        } else if (list.get(b).getToken_type().toString().equals(Token.Token_Type.Char.toString())) {

            if (list.get(c).getToken_type().toString().equals(Token.Token_Type.equal.toString()) && list.get(d).getToken_type().toString().equals(Token.Token_Type.character.toString())) {
                name.setId_type(Token.ID_Type.Char);
                name.setValue(list.get(d).getName());

            } else {
                name.setId_type(Token.ID_Type.Char);
                name.setValue(null);

            }

        } else if (list.get(b).getToken_type().toString().equals(Token.Token_Type.String.toString())) {

            if (list.get(c).getToken_type().toString().equals(Token.Token_Type.equal.toString()) && list.get(d).getToken_type().toString().equals(Token.Token_Type.string.toString())) {
                name.setId_type(Token.ID_Type.string);
                name.setValue(list.get(d).getName());

            } else {
                name.setId_type(Token.ID_Type.string);
                name.setValue(null);

            }

        } else {
            if (list.get(c).getToken_type().toString().equals(Token.Token_Type.equal.toString())) {
                name.setValue(list.get(d).getName());

            } else {

                name.setId_type(Token.ID_Type.none);
                name.setValue(null);

            }
        }

    }

    //----------------------------------------- 
    public void isID() {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getToken_type().toString().equals(Token.Token_Type.id.toString())) {

                setValueAndType(list.get(i), i);

                if (s_count == 0) {
                    Symbol_Table[0] = list.get(i);
                    s_count++;
                } else {
                    int j = 0;
                    for (int k = 0; k < s_count; k++) {

                        if (Symbol_Table[k].getName().equals(list.get(i).getName())) {

                            j++;
                            if (list.get(i).getValue() != null) {
                                Symbol_Table[k].setValue(list.get(i).getValue());

                                break;

                            }

                        }

                    }
                    if (j == 0) {
                        Symbol_Table[s_count] = list.get(i);
                        s_count++;
                    }

                }

            }
        }

        for (int a = 0; a < s_count; a++) {
            System.out.println(Symbol_Table[a]);
        }
    }

    //-----------------------------------nextChar
    private char nextChar() {
        char ch;
        currentPoint++;

        if (currentPoint >= code.length()) {
            return '$';  //--alamate payan code
        }
        ch = code.charAt(currentPoint);  //---index char ha das string code

        if (!(isInDigits(ch) || isInLetters(ch) || isInSymbols(ch) || isInWhiteSpaces(ch))) {
            System.out.println("character " + ch + " is not in Alphabet");
            ch = ' ';
        }

        return ch;
    }

    private char watchNextChar() {  //---nim negahi be jolo
        char ch;
        int watchNextCharPoint = currentPoint + 1;

        if (watchNextCharPoint >= code.length()) {
            return '$';
        }

        ch = code.charAt(watchNextCharPoint);

        return ch;
    }

    //------------------------------------Cheking Alphabet
    private boolean isInLetters(char ch) {
        for (int i = 0; i < letters.length; i++) {
            if (letters[i] == ch) {
                return true;
            }
        }
        return false;
    }

    private boolean isInWhiteSpaces(char ch) {
        for (int i = 0; i < whiteSpaces.length; i++) {
            if (whiteSpaces[i] == ch) {
                return true;
            }
        }
        return false;
    }

    private boolean isInDigits(char ch) {
        for (int i = 0; i < digits.length; i++) {
            if (digits[i] == ch) {
                return true;
            }
        }
        return false;
    }

    private boolean isInSymbols(char ch) {
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i] == ch) {
                return true;
            }
        }
        return false;
    }

    private boolean isInKeywords(String word) {
        for (int i = 0; i < keywords.length; i++) {
            if (word.equals(keywords[i])) {
                return true;
            }
        }
        return false;
    }

    //----------------------------------------Constructor
    public Lexical_Analysis(String path) {
        this.setPath(path);
    }
    //------------------------------------------SetPath

    private void setPath(String path) {             //----kole file ro mikhone dakhel code zakhire mikone
        code = "";
        try {
            File source = new File(path);
            Scanner scn = new Scanner(source);
            while (scn.hasNextLine()) {   //ta zamani ke khate jadid darim
                code = code + "\n" + scn.nextLine();   //code khat be khat khande mishavad
            }
            scn.close();
        } catch (FileNotFoundException e) {
            System.err.println("Source File not Found");   // err baraye khata be range ghermez
            e.printStackTrace();
        }
    }
}
