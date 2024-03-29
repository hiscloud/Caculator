
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Junyu Lu
 */
class GUI extends JFrame {
    private JPanel panel;
    private JLabel label;
    private JButton button;
    private JTextField text;

    //constructor
    public GUI() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        buildPanel();
        add(panel);
        setVisible(true);
    }
    //build the panel and components
    public void buildPanel() {
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        label = new JLabel();
        label.setText("write your expressions here");
        text = new JTextField(30);
        text.setText("(1.2   +2.3   )*3.85   ^4.5");
        button = new JButton("Calculate");
        button.addActionListener(new ButtonListener());
        panel.add(label);
        panel.add(text);
        panel.add(button);
    }
// adjust any format of the expression to the correct ones with " " between each number and operators
    public static String inputManipulation(String raw){
        String noSpace="";
        //get rid of any spaces
        for (int i=0;i<raw.length();i++){
            if (raw.charAt(i)!=' '){
                noSpace+=raw.charAt(i);
                }
            }
 
        //now add a space between any numbers and operators
        String goal="";
        for(int i=0;i<noSpace.length();i++){
                goal+=noSpace.charAt(i);
                boolean addSpace=true;
                //6.          45     .9
                if(i<noSpace.length()-1)
                if (Character.isDigit(noSpace.charAt(i))||noSpace.charAt(i)=='.'){
                   if ( (Character.isDigit(noSpace.charAt(i+1)))||(noSpace.charAt(i+1)=='.')){
                        addSpace=false;
                       }
                }
                if (addSpace)
                      goal+=' ';
            }
        
        return goal;
        }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String input = text.getText();
            // error check below
            boolean leastOneDigit=false;
            for(int i=0;i<input.length();i++){
                    char a=input.charAt(i);
                    if(!Character.isDigit(a)&&a!='+'&&a!='-'&&a!='*'&&a!='/'&&a!='^'&& a!='(' &&a!=')'&& a!=' '&&a!='.'){
                        JOptionPane.showMessageDialog(null,"The character "+ a + " is invailable");
                        return;
                        }
                    ///////////////////////////////////////////////////////////////////////
                    if(i>=1 && a==0 && input.charAt(i-1)=='/'){
                        JOptionPane.showMessageDialog(null,"0 can't be the divisor!");
                        }
                    ///////////////////////////////////////////////////////////////////////
                    if(Character.isDigit(a)){
                        leastOneDigit=true;
                        }
                }
            if (!leastOneDigit){
                JOptionPane.showMessageDialog(null,"At least one number in the expression!");
                return;
                }
            String modifiedInput;
            modifiedInput=inputManipulation(input);
         
            FullCalculator calc = new FullCalculator();
            calc.processInput(modifiedInput);
        }
    }
}

class Token {

    public static final int UNKNOWN = -1;
    public static final int NUMBER = 0;
    public static final int OPERATOR = 1;
    public static final int LEFT_PARENTHESIS = 2;
    public static final int RIGHT_PARENTHESIS = 3;

    private int type;
    private double value;
    private char operator;
    private int precedence;

    public Token() {
        type = UNKNOWN;
    }

    public Token(String contents) {
        switch (contents) {
            case "+":
                type = OPERATOR;
                operator = contents.charAt(0);
                precedence = 1;
                break;
            case "-":
                type = OPERATOR;
                operator = contents.charAt(0);
                precedence = 1;
                break;
            case "*":
                type = OPERATOR;
                operator = contents.charAt(0);
                precedence = 2;
                break;
            case "/":
                type = OPERATOR;
                operator = contents.charAt(0);
                precedence = 2;
                break;
            case "^":
                type = OPERATOR;
                operator = contents.charAt(0);
                precedence = 3;
                break;
            case "(":
                type = LEFT_PARENTHESIS;
                break;
            case ")":
                type = RIGHT_PARENTHESIS;
                break;
            default:
                type = NUMBER;
                try {
                    value = Double.parseDouble(contents);
                } catch (Exception ex) {
                    type = UNKNOWN;
                }
        }
    }

    public Token(double x) {
        type = NUMBER;
        value = x;
    }

    int getType() {
        return type;
    }

    double getValue() {
        return value;
    }

    int getPrecedence() {
        return precedence;
    }

    Token operate(double a, double b) {
        double result = 0;
        switch (operator) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                result = a / b;
                break;
            case '^':
                result = Math.pow(a, b);
                break;

        }
        return new Token(result);
    }
}

class TokenStack {

    /**
     * Member variables *
     */
    private ArrayList<Token> tokens;

    /**
     * Constructors *
     */
    public TokenStack() {
        tokens = new ArrayList<Token>();
    }

    /**
     * Accessor methods *
     */
    public boolean isEmpty() {
        return tokens.size() == 0;
    }

    public Token top() {
        return tokens.get(tokens.size() - 1);
    }

    /**
     * Mutator methods *
     */
    public void push(Token t) {
        tokens.add(t);
    }

    public void pop() {
        tokens.remove(tokens.size() - 1);
    }
}

class FullCalculator {

    private TokenStack operatorStack;
    private TokenStack valueStack;
    private boolean error;

    public FullCalculator() {
        operatorStack = new TokenStack();
        valueStack = new TokenStack();
        error = false;
    }

    private void processOperator(Token t) {
        Token A = null, B = null;
        if (valueStack.isEmpty()) {
            JOptionPane.showMessageDialog(null,"Expression error.");
            error = true;
            return;
        } else {
            B = valueStack.top();
            valueStack.pop();
        }
        if (valueStack.isEmpty()) {
            JOptionPane.showMessageDialog(null,"Expression error.");
            error = true;
            return;
        } else {
            A = valueStack.top();
            valueStack.pop();
        }
        Token R = t.operate(A.getValue(), B.getValue());
        valueStack.push(R);
    }

    public void processInput(String input) {
        // The tokens that make up the input
        String[] parts = input.split(" ");
        Token[] tokens = new Token[parts.length];
        for (int n = 0; n < parts.length; n++) {
            tokens[n] = new Token(parts[n]);
        }

        // Main loop - process all input tokens
        for (int n = 0; n < tokens.length; n++) {
            Token nextToken = tokens[n];
            if (nextToken.getType() == Token.NUMBER) {
                valueStack.push(nextToken);
            } else if (nextToken.getType() == Token.OPERATOR) {
                if (operatorStack.isEmpty() || nextToken.getPrecedence() > operatorStack.top().getPrecedence()) {
                    operatorStack.push(nextToken);
                } else {
                    while (!operatorStack.isEmpty() && nextToken.getPrecedence() <= operatorStack.top().getPrecedence()) {
                        Token toProcess = operatorStack.top();
                        operatorStack.pop();
                        processOperator(toProcess);
                    }
                    operatorStack.push(nextToken);
                }
            } else if (nextToken.getType() == Token.LEFT_PARENTHESIS) {
                operatorStack.push(nextToken);
            } else if (nextToken.getType() == Token.RIGHT_PARENTHESIS) {
                while (!operatorStack.isEmpty() && operatorStack.top().getType() == Token.OPERATOR) {
                    Token toProcess = operatorStack.top();
                    operatorStack.pop();
                    processOperator(toProcess);
                }
                if (!operatorStack.isEmpty() && operatorStack.top().getType() == Token.LEFT_PARENTHESIS) {
                    operatorStack.pop();
                } else {
                    JOptionPane.showMessageDialog(null,"Error: unbalanced parenthesis.");
                    error = true;
                }
            }

        }
        // Empty out the operator stack at the end of the input
        while (!operatorStack.isEmpty() && operatorStack.top().getType() == Token.OPERATOR) {
            Token toProcess = operatorStack.top();
            operatorStack.pop();
            processOperator(toProcess);
        }
        // Print the result if no error has been seen.
        if (error == false) {
            Token result = valueStack.top();
            valueStack.pop();
            if (!operatorStack.isEmpty() || !valueStack.isEmpty()) {
                JOptionPane.showMessageDialog(null,"operator error");
            } else {
                JOptionPane.showMessageDialog(null, "The result of "+ input+" is\n "+result.getValue());

            }
        }
    }

    public static void main(String[] args) {
        GUI sb = new GUI();
    }
}
