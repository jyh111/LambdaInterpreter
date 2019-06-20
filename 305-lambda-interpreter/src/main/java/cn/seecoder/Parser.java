package cn.seecoder;

import java.util.ArrayList;

public class Parser {
    Lexer lexer;
    int numOfLambda=0;

    public Parser(Lexer l){
        lexer = l;
    }

    public AST parse(){
        AST ast = term(new ArrayList<String>());



//        System.out.println(lexer.match(TokenType.EOF));
        return ast;
    }


    private AST term(ArrayList<String> ctx){
       if(lexer.match(TokenType.LAMBDA)){
                numOfLambda++;
                String tempIdentifier = String.valueOf(lexer.nextChar());
                ctx.add(tempIdentifier);
                lexer.skip(TokenType.LCID);
                lexer.skip(TokenType.DOT);
                AST body = term(ctx);
                int tempValue = ctx.size()-1-ctx.lastIndexOf(tempIdentifier);
                ctx.remove(ctx.lastIndexOf(tempIdentifier));
                return new Abstraction(new Identifier(tempIdentifier,tempValue),body);
        }else {
           return application(ctx);
       }

    }




    private AST application(ArrayList<String> ctx){
        Application application = new Application();
        application.setLhs(atom(ctx));
        while (true){
            application.setRhs(atom(ctx));
            if(application.getRhs()==null)
                return application.getLhs();
            else {
                application.setLhs(new Application(application.getLhs(),application.getRhs()));
            }
        }
    }

    private AST atom(ArrayList<String> ctx){
        if(lexer.match(TokenType.LCID)){
            String tempIdentifier = String.valueOf(lexer.getChar());
            int identifierValue = ctx.size()-1-ctx.lastIndexOf(tempIdentifier);
            lexer.skip(TokenType.LCID);
            return new Identifier(tempIdentifier,identifierValue);
        }else if(lexer.match(TokenType.LPAREN)){
            lexer.skip(TokenType.LPAREN);
            return term(ctx);
        }else if(lexer.match(TokenType.RPAREN)){
            lexer.skip(TokenType.RPAREN);
            return null;
        }else if(lexer.match(TokenType.EOF)){
            return null;
        }else if(lexer.match(TokenType.LAMBDA)){
            return term(ctx);
        }else if(lexer.match(null)){
            lexer.skip(null);
            return atom(ctx);
        }
        return null;
    }

    public static void main(String[] args){
        Lexer lexer = new Lexer("(\\x.\\(xxx))(\\x.xxx)");
        Parser parser = new Parser(lexer);
        System.out.println(parser.parse().toString());
    }
}
