package cn.seecoder;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Interpreter {
    Parser parser;
    AST astAfterParser;
    public JTextArea text;
    private JTextField field;
    private Interpreter interpreter;

    public Interpreter(Parser p){
        parser = p;
        astAfterParser = p.parse();
        //System.out.println("After parser:"+astAfterParser.toString());
    }

    public Interpreter(){

    }


    private  boolean isAbstraction(AST ast){
        return ast instanceof Abstraction;
    }
    private  boolean isApplication(AST ast){
        return ast instanceof Application;
    }
    private  boolean isIdentifier(AST ast){
        return ast instanceof Identifier;
    }

    public void setUpGui(){
        JFrame frame = new JFrame("Lambda Interpreter");
        JLabel label = new JLabel("Lambda expression:");
        JPanel inputPanel = new JPanel();
        JPanel outputPanel = new JPanel();
        JPanel rsPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        field = new JTextField(30);
        text = new JTextArea(15,30);
        text.setLineWrap(true);

        Font labelFont = new Font("serif",Font.BOLD,14);
        label.setPreferredSize(new Dimension(450,40));
        label.setFont(labelFont);

        text.setFont(labelFont);

        field.addActionListener(new InputListener());

        JButton resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(100,30));
        resetButton.setFont(labelFont);
        resetButton.addActionListener(new ResetButtonListener());

        JButton submitButton = new JButton("OK");
        submitButton.setPreferredSize(new Dimension(100,30));
        submitButton.setFont(labelFont);
        submitButton.addActionListener(new InputListener());

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(labelFont);
        clearButton.setPreferredSize(new Dimension(100,30));
        clearButton.addActionListener(new ClearButtonListener());

        JScrollPane scroller = new JScrollPane(text);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        inputPanel.add(label);
        inputPanel.add(field);
        inputPanel.add(rsPanel);
        inputPanel.setLayout(new BoxLayout(inputPanel,BoxLayout.Y_AXIS));

        rsPanel.add(resetButton);
        rsPanel.add(submitButton);

        outputPanel.add(scroller);

        buttonPanel.add(clearButton);

        frame.getContentPane().add(BorderLayout.NORTH,inputPanel);
        frame.getContentPane().add(BorderLayout.CENTER,outputPanel);
        frame.getContentPane().add(BorderLayout.SOUTH,buttonPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450,500);
        frame.setVisible(true);
    }

    class ResetButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            field.setText("");
        }
    }

    class ClearButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent event){
            text.setText("");
        }
    }

    class InputListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent event){
            if(field.getText()!=null&&field.getText().trim().length()!=0){
                Lexer lexer = new Lexer(field.getText(),text);
                Parser parser = new Parser(lexer);
                if(!lexer.isValid){
                    text.append("\n");
                    field.setText("");
                    return;
                }
                interpreter = new Interpreter(parser);
                text.append(field.getText()+"\n");
                field.setText("");
                AST result = interpreter.eval();
                text.append("=" + result.toString()+"\n\n");
            }
        }
    }



    public AST eval(){

        return evalAST(astAfterParser);
    }

    private   AST evalAST(AST ast){
        while (true){
            if(isValue(ast))
                return ast;
            if(isApplication(ast)){
                ast = (Application)ast;
                if(isValue(((Application) ast).getLhs())&&isValue(((Application) ast).getRhs())){
                    if(((Application) ast).getRhs()==null)
                        return ((Application) ast).getLhs();
                    ast = substitute(((Abstraction)((Application) ast).getLhs()).getBody(),((Application) ast).getRhs());
                }else if(isValue(((Application) ast).getLhs())){
                    ((Application) ast).setRhs(evalAST(((Application) ast).getRhs()));
                    if(text!=null){
                        text.append("="+ast.toString()+"\n");
                    }
                }else{
                    ((Application) ast).setLhs(evalAST(((Application) ast).getLhs()));
                }
            }else if(isAbstraction(ast)){
                ast = (Abstraction)ast;
                ((Abstraction) ast).setBody(evalAST(((Abstraction) ast).getBody()));
            }
            if(text!=null){
            text.append(ast.toString());
            }
        }
    }
    private AST substitute(AST node,AST value){
        return shift(-1,subst(node,shift(1,value,0),0),0);
    }

    /**
     *  value替换node节点中的变量：
     *  如果节点是Applation，分别对左右树替换；
     *  如果node节点是abstraction，替入node.body时深度得+1；
     *  如果node是identifier，则替换De Bruijn index值等于depth的identifier（替换之后value的值加深depth）

     *@param value 替换成为的value
     *@param node 被替换的整个节点
     *@param depth 外围的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */
    private AST subst(AST node, AST value, int depth){
        if(isApplication(node)){
            node = (Application)node;
            if(((Application) node).getRhs()==null){
                return subst(((Application) node).getLhs(),value,depth);
            }else {
            return new Application(subst(((Application) node).getLhs(),value,depth),subst(((Application) node).getRhs(),value,depth));
            }
        }else if(isAbstraction(node)){
            node = (Abstraction)node;
            return new Abstraction(((Abstraction) node).getParam(),subst(((Abstraction) node).getBody(),value,depth+1));

        }else{
            node = (Identifier)node;
            if(((Identifier) node).value.equals(String.valueOf(depth))){
                return shift(depth,value,0);
            }else {
                return node;
            }
        }
    }

    /**

     *  De Bruijn index值位移
     *  如果节点是Applation，分别对左右树位移；
     *  如果node节点是abstraction，新的body等于旧node.body位移by（from得+1）；
     *  如果node是identifier，则新的identifier的De Bruijn index值如果大于等于from则加by，否则加0（超出内层的范围的外层变量才要shift by位）.

        *@param by 位移的距离
     *@param node 位移的节点
     *@param from 内层的深度

             
     *@return AST
     *@exception  (方法有异常的话加)


     */

    private AST shift(int by, AST node,int from){
        if(isApplication(node)){
            node = (Application)node;
            return new Application(shift(by,((Application) node).getLhs(),from),shift(by,((Application) node).getRhs(),from));
        }else if(isAbstraction(node)){
            node = (Abstraction)node;
            return new Abstraction(((Abstraction) node).getParam(),shift(by,((Abstraction) node).getBody(),from+1));
        }else{
            node = (Identifier)node;
            int temp = ((Identifier)node).getDebruin();
            return new Identifier(((Identifier)node).name,temp+(temp>=from?by:0));
        }

    }


    private boolean isValue(AST node){
        boolean result = false;
        if(node instanceof  Identifier)
            result = true;
        else if(node instanceof Abstraction){
            node = (Abstraction)node;
            if(isValue(((Abstraction) node).getBody())){
                result = true;
            }
        }else if(node instanceof Application){
            node = (Application)node;
            if(((Application) node).getLhs() instanceof Abstraction){
                return false;
            }
            if(isValue(((Application) node).getLhs())&&isValue(((Application) node).getRhs())){
                result = true;
            }
        }else if(node == null){
            result = true;
        }
        return result;
    }


    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m "+SUCC+") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n"+PRED+"m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x."+FALSE+")"+TRUE+")";
    static String LEQ = "(\\m.\\n."+ISZERO+"("+SUB+"m n))";
    static String EQ = "(\\m.\\n."+AND+"("+LEQ+"m n)("+LEQ+"n m))";
    static String MAX = "(\\m.\\n."+IF+"("+LEQ+" m n)n m)";
    static String MIN = "(\\m.\\n."+IF+"("+LEQ+" m n)m n)";

    private static String app(String func, String x){
        return "(" + func + x + ")";
    }
    private static String app(String func, String x, String y){
        return "(" +  "(" + func + x +")"+ y + ")";
    }
    private static String app(String func, String cond, String x, String y){
        return "(" +"("+"(" + func + cond +")"+ x+")" + y + ")";
    }

    public static void main(String[] args) {
        // write your code here


        String[] sources = {
                "((\\f.f) a)",
                ZERO,//0
                ONE,//1
                TWO,//2
                THREE,//3
                app(PLUS, ZERO, ONE),//4
                app(PLUS, TWO, THREE),//5
                app(POW, TWO, TWO),//6
                app(PRED, ONE),//7
                app(PRED, TWO),//8
                app(SUB, FOUR, TWO),//9
                app(AND, TRUE, TRUE),//10
                app(AND, TRUE, FALSE),//11
                app(AND, FALSE, FALSE),//12
                app(OR, TRUE, TRUE),//13
                app(OR, TRUE, FALSE),//14
                app(OR, FALSE, FALSE),//15
                app(NOT, TRUE),//16
                app(NOT, FALSE),//17
                app(IF, TRUE, TRUE, FALSE),//18
                app(IF, FALSE, TRUE, FALSE),//19
                app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
                app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
                app(ISZERO, ZERO),//22
                app(ISZERO, ONE),//23
                app(LEQ, THREE, TWO),//24
                app(LEQ, TWO, THREE),//25
                app(EQ, TWO, FOUR),//26
                app(EQ, FIVE, FIVE),//27
                app(MAX, ONE, TWO),//28
                app(MAX, FOUR, TWO),//29
                app(MIN, ONE, TWO),//30
                app(MIN, FOUR, TWO),//31
        };

        new Interpreter().setUpGui();


/*
        for(int i=0 ; i<sources.length; i++) {


            String source = sources[i];

            System.out.println(i+":"+source);

            Lexer lexer = new Lexer(source);

            Parser parser = new Parser(lexer);

            Interpreter interpreter = new Interpreter(parser);

            AST result = interpreter.eval();

            System.out.println(i+":" + result.toString());

        }

 */

    }
}
