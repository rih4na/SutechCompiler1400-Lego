
package lego_compiler;


public class Token {
    public enum Token_Type {
        Long,Int,string,Char,Boolean,Double,Float, print, input ,id, plus, intNumber, floatNumber, minus,mod,minusMinus,plusPlus,and,or,
        leftParentheses, rightParentheses, semicolon, leftAccolade, rightAccolade,
        divide, lineComment, multiLineComment, String, multiple, equal,
        equalTwice, less, lessEqual, more , moreEqual, notEqual, $, err,For,While,IF,Else,Then,Switch,Case,Break,ElseIf,Elseif,Static,Void,Public,Default,
        Continue
    }
    public enum ID_Type { Long,Int,string,Char,Boolean,Double,Float,none}

    private String name;
    private int blockkNum;
    private int blockOrder;
    private Token_Type token_type;
    private ID_Type id_type;

 public Token(String name, int blkNum, int blkOrder, Token_Type token_type, ID_Type id_type){
        this.name = name;
        this.blockkNum = blkNum;
        this.blockOrder = blkOrder;
        this.token_type = token_type;
        this.id_type = id_type;
    }

    
    public String getName() {
        return name;
    }

   
    public void setName(String name) {
        this.name = name;
    }

    
    public int getBlockkNum() {
        return blockkNum;
    }

    
    public void setBlockkNum(int blockkNum) {
        this.blockkNum = blockkNum;
    }

    
    public int getBlockOrder() {
        return blockOrder;
    }

    
    public void setBlockOrder(int blockOrder) {
        this.blockOrder = blockOrder;
    }

    
    public Token_Type getToken_type() {
        return token_type;
    }

    
    public void setToken_type(Token_Type token_type) {
        this.token_type = token_type;
    }

    
    public ID_Type getId_type() {
        return id_type;
    }

    
    public void setId_type(ID_Type id_type) {
        this.id_type = id_type;
    }
   
    
}
