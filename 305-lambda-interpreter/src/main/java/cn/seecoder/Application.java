package cn.seecoder;

public class Application extends AST{
    private AST lhs;//左树
    private AST rhs;//右树

    Application(AST l, AST s){
        lhs = l;
        rhs = s;
    }

    public void setLhs(AST lhs){
        this.lhs=lhs;
    }

    public void setRhs(AST rhs){
        this.rhs = rhs;
    }

    public AST getLhs(){
        return this.lhs;
    }

    public AST getRhs(){
        return this.rhs;
    }

    public String toString(){
        if(rhs==null){
            return lhs.toString();
        }else {
        return "("+lhs.toString()+" "+rhs.toString()+")";
        }
    }
}
