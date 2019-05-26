package cn.seecoder;

import java.util.ArrayList;

public class Parser {
    Lexer lexer;

    public Parser(Lexer l){
        lexer = l;
    }

    public AST parse(){

        AST ast = term(new ArrayList<>());
//        System.out.println(lexer.match(TokenType.EOF));
        return ast;
    }

    private AST term(ArrayList<String> ctx){
        // write your code here

        return null;
    }
    private AST application(ArrayList<String> ctx){
        // write your code here
        return null;
    }
    private AST atom(ArrayList<String> ctx){
        // write your code here
        return  null;

    }
}
