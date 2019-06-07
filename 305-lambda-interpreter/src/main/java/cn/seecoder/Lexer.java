package cn.seecoder;


import java.util.ArrayList;
import java.util.regex.Pattern;
import javafx.scene.input;

public class Lexer{

    public String source;
    private int index;
    public TokenType token;
    public String tokenvalue;

    public void setIndex(int index){
        this.index=index;
    }

    public int getIndex(){
        return this.index;
    }

    public Lexer(String s){
        index = 0;
        source = s.replace(" ","");
        System.out.println(getTokenType(String.valueOf(s.charAt(0))));
        for(int i=0;i<source.length();i++){
            System.out.println(nextToken());
        }
        index = 0;
    }

    public TokenType getTokenType(String token){
        TokenType tokenType = TokenType.EOF;
        if(token.equals("(")){
            tokenType = TokenType.LPAREN;
        }else if(token.equals(")")){
            tokenType = TokenType.RPAREN;
        }else if(token.equals(".")){
            tokenType = TokenType.DOT;
        }else if(Pattern.matches("[a-zA-Z]",token)){
            tokenType = TokenType.LCID;
        }else if(token.equals("\\")){
            tokenType = TokenType.LAMBDA;
        }else {
            assert true:"invalid input";
        }
        return  tokenType;
    }

    //get next token
    private TokenType nextToken(){
        index++;
        if(index>=source.length())
            return TokenType.EOF;
        String token = String.valueOf(source.charAt(index));
        return getTokenType(token);
    }

    private TokenType previousToken(){
        index--;
        if(index<0)
            return null;
        String token = String.valueOf(source.charAt(index));
        return getTokenType(token);
    }

    // get next char
    public char nextChar(){
        index++;
        token=getTokenType(String.valueOf(getChar()));
        if(index>=source.length())
            return 0;
        else
            return source.charAt(index);
    }

    public char getChar(){
        if(index>source.length()){
            return 0;
        }else {
        return source.charAt(index);
        }
    }

    public boolean previous(TokenType t){
        boolean result = false;
        if(previousToken()==t){
            result = true;
        }
        return result;
    }


    //check token == t
    public boolean next(TokenType t){
        boolean result = false;
        if(nextToken()==t){
            if(index<source.length()){
                token = getTokenType(String.valueOf(getChar()));
            }else {
                token = null;
            }
            result = true;
        }
        return result;
    }

    //assert matching the token type, and move next token
    public boolean match(TokenType t){
        index--;
        if(nextToken()==t)
            return true;
        else
            return false;
    }

    //skip token  and move next token
    public boolean skip(TokenType t){
        index--;
        if(nextToken()==t){
            index++;
            if(index<source.length()){
            token = getTokenType(String.valueOf(getChar()));
            }else {
                token = null;
            }
            return true;
        }else
            return false;
    }

    public static void main(String[] args){
        String s = "((\\n.\\f.\\x.f(nfx))(\\f.\\x.x))";
        Lexer lexer = new Lexer(s);
    }
}
