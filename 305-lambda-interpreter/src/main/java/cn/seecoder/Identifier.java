package cn.seecoder;

public class Identifier extends AST {

    String name; //名字
    String value;//De Bruijn index值

    public Identifier(String n,int v){

        name = n;
        value = String.valueOf(v);
    }

    public int getDebruin(){
        return Integer.valueOf(value);
    }

    public void setDebruin(int value){
        this.value = String.valueOf(value);
    }

    public Identifier(String n){
        name= n;
        value = String.valueOf(0);
    }

    public String toString(){

        return value;
    }
}
