package cn.seecoder;

public class Abstraction extends AST {
    Identifier param;//变量
    AST body;//表达式

    Abstraction(Identifier p, AST b){
        param = p;
        body = b;
    }

    public Identifier getParam(){
        return this.param;
    }

    public AST getBody(){
        return this.body;
    }

    public void setBody(AST body){
        this.body=body;
    }

    public void setParam(Identifier param){
        this.param = param;
    }

    public String toString(){
        String result = "";
        result += "\\."+body.toString();
        return result;
    }
}
