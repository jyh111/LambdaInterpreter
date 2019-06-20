package cn.seecoder;


import javax.swing.*;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class Lexer{

    public String source;
    private int index;
    public TokenType token;
    private JTextArea area;
    public boolean isValid;

    public void setIndex(int index){
        this.index=index;
    }

    public int getIndex(){
        return this.index;
    }

    public Lexer(String s){
        isValid = true;
        index = 0;
        source = s.replace(" ","");
        if(!checkValid()){
            isValid = false;
            System.exit(1);
        }
        System.out.println(getTokenType(String.valueOf(getChar())));
        for(int i=0;i<source.length();i++){
            System.out.println(nextToken());
        }
        index = 0;
    }

    public Lexer(String s, JTextArea area){
        isValid = true;
        this.area = area;
        index = 0;
        source = s.replace(" ","");
        if(!checkValid()){
            isValid = false;
        }else {
        System.out.println(getTokenType(String.valueOf(getChar())));
        for(int i=0;i<source.length();i++){
            System.out.println(nextToken());
            }
        }
        index = 0;
    }

    private boolean checkValid(){
        ArrayList<String> invalidInput = new ArrayList<>();
        int numOfLparen = 0;
        int numOfRparen = 0;
        index = 0;
        token=getTokenType(String.valueOf(getChar()));
        if(token==TokenType.WRONG){
            invalidInput.add(String.valueOf(getChar()));
        }else if(token==TokenType.LPAREN){
            numOfLparen++;
        }else if(token==TokenType.RPAREN){
            numOfRparen++;
        }
        for(int i=0;i<source.length();i++){
            token = nextToken();
            if(token==TokenType.WRONG){
                invalidInput.add(String.valueOf(getChar()));
            }else if(token==TokenType.LPAREN){
                numOfLparen++;
            }else if(token==TokenType.RPAREN){
                numOfRparen++;
            }
        }
        //check invalid char
        if(invalidInput.size()!=0){
            System.out.println("Grammar Error!");
            System.out.println("Invalid Charset:"+invalidInput);
            if(area!=null){
                area.append("Grammar Error!\n");
                area.append("Invalid Charset:"+invalidInput+"\n");
            }
            index = 0;
            return false;
        }
        //check paren
        if(numOfLparen!=numOfRparen){
            System.out.println("Grammar Error!");
            System.out.println("Unpaired parens, please check again.");
            if(area!=null){
                area.append("Grammar Error!\n");
                area.append("Unpaired parens, please check again.\n");
            }
            index = 0;
            return false;
        }

        index = 0; //返回原始状态
        return true;
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
            tokenType = TokenType.WRONG;
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
        if(index>=source.length()){
            return 0;
        }else {
        return source.charAt(index);
        }
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
        }else{
            System.out.println("Grammar error!");
            System.out.println("Invalid term,please pay attention to the "+(index+1)+"th char or chars near it.");
            if(area!=null){
                area.append("Grammar error!\n");
                area.append("Invalid term,please pay attention to the "+(index+1)+"th char or chars near it.\n");
            }
            isValid = false;
        }
        return false;
    }

    public static void main(String[] args){
        String s = "((\\n.\\f.\\x.f(nfx))(\\f.\\x.x))";
        Lexer lexer = new Lexer(s);
    }
}
