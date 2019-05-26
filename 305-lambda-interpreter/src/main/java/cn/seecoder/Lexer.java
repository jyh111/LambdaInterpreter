package cn.seecoder;


public class Lexer{

    public String source;
    public int index;
    public TokenType token;
    public String tokenvalue;

    public Lexer(String s){
        index = 0;
        source = s;
        nextToken();
    }
    //get next token
    private TokenType nextToken(){
        //write your code here
        return null;
    }

    // get next char
    private char nextChar(){
        //write your code here
        return '\0';
    }


    //check token == t
    public boolean next(TokenType t){
        //write your code here
        return true;
    }

    //assert matching the token type, and move next token
    public void match(TokenType t){
        //write your code here
    }

    //skip token  and move next token
    public boolean skip(TokenType t){
        //write your code here
        return true;
    }


}
