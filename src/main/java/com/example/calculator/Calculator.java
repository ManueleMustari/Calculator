package com.example.calculator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.MathContext;

import static javafx.application.Platform.exit;


public class Calculator extends Application {

    //Stage
    private TextArea textShowed;
    private VBox background;
    private HBox containerDisplay;
    private BorderPane borderPane;

    //Input
    private String first ="";
    private String second ="";
    private String op ="";

    //Boolean
    private boolean back =false;
    private boolean log =false;
    private boolean radix =false;

    //Other
    private Font font = Font.font("Arial", FontWeight.BOLD, 15);
    private String textBlack = "-fx-text-fill: black";
    private String firstColor="-fx-background-color: Orange;" + textBlack;

    //Operations
    public void sum(){
        if (first.equals("")) { second = ""; op = "";}
        else {op = "+";}
    }

    public void mult(){
        if ((!first.equals("") && second.equals("")) || !op.equals("")) { op = "x";}
        else {
            empty();}
    }

    public void div(){
        if ((!first.equals("") && second.equals("")) || !op.equals("")) {
            op = "÷";}
        else {
            empty();}
    }

    public void operationsManager(String operation){
        if (back) { back = false; }
        if (!first.equals("") && !second.equals("") && !operation.equals("") ) { noEqualPressed(operation);}
        else if(operation.equals("√") || operation.equals("log")) { radixAndPow(operation); return;}
        else if(operation.equals("+")) { sum(); }
        else if(operation.equals("^") || operation.equals("-")){ powAndSub(operation);}
        else if (operation.equals("x")){ mult(); }
        else if(operation.equals("÷")){ div(); }
        display();
    }

    private void opHandler(String pressed) {
        switch (pressed) {
            case "=" :
            {
                calcResult();
                back = true;
                op = "";
                second = "";
                break;
            }
            case "C" :
            {
                empty();
                log = false;
                radix = false;
                break;
            }
            case "." :
            {
                dot();
                break;
            }

            case "⌫":
            {
                delete();
                break;
            }
            case "±" :
            {
                changeSign();
                break;
            }
            case "+","-","x", "÷","^", "log", "√": {
                operationsManager(pressed);
                break;
            }
            default :
                digits(pressed);
        }
    }

    private void radixAndPow(String pressed){
        if (first.equals("") && second.equals("") && op.equals("") ) { first = "1"; op = pressed;}
        else if (op.equals("") && second.equals("")) { op = pressed; display();}
        else if (second.equals("")) {
            if (pressed.equals("log")) { log = true; displayLog();}
            else { radix = true; displaySqrt(); }
        }
    }

    public void powAndSub(String pressed){
        if (first.equals("")) { second = ""; op = "";  if(pressed.equals("-")){
            first = "-";}
        } else if (op.equals("x") || op.equals("÷")) { op = pressed;}
        else if (!op.equals("")) { second = pressed + second; reformat();}
        else { op = pressed; }
    }

    public void pressedLog(BigDecimal primo, BigDecimal secondo){
        switch (op) {
            case "log", "√", "^":
                break;
            case "+":
            {
                first = primo.add(base10Log(secondo)) + "";
                displayResult(primo.add(base10Log(secondo)));
                log = false;
                break;
            }
            case "-": {
                first = primo.subtract(base10Log(secondo)) + "";
                displayResult(primo.subtract(base10Log(secondo)));
                log = false;
                break;
            }
            case "x": {
                first = primo.multiply(base10Log(secondo)) + "";
                displayResult(primo.multiply(base10Log(secondo)));
                log = false;
                break;
            }
            case "÷": {
                first = primo.divide(base10Log(secondo)) + "";
                displayResult(primo.divide(base10Log(secondo)));
                log = false;
                break;
            }
            default:
                break;
        }
    }

    public void pressedRadix(BigDecimal primo, BigDecimal secondo){
        BigDecimal res;
        switch (op) {
            case "log":
                break;
            case "√":
                break;
            case "^":
                break;
            case "+": {
                res = primo.add(secondo.sqrt(new MathContext(10)));
                first = res + "";
                displayResult(res);
                radix = false;
                break;
            }
            case "-": {
                res = primo.subtract(secondo.sqrt(new MathContext(10)));
                first = res + "";
                displayResult(res);
                radix = false;
                break;
            }
            case "x": {
                res = primo.multiply(secondo.sqrt(new MathContext(10)));
                first = res + "";
                displayResult(res);
                radix = false;
                break;
            }
            case "÷": {
                res = primo.divide(secondo.sqrt(new MathContext(10)));
                first = res + "";
                displayResult(res);
                radix = false;
                break;
            }
            default:
                break;
        }
    }


    public void calcResult(){ //equal pressed
        if (second.equals("")) { displayResult(new BigDecimal(first));}
        else{
            BigDecimal primo = new BigDecimal(first);
            BigDecimal secondo = new BigDecimal(second);
            if (log) { pressedLog(primo,secondo); }
            else if (radix) { pressedRadix(primo,secondo);}
            else {
                switch (op) {
                    case "log": {
                        first = primo.multiply(base10Log(secondo)) + "";
                        displayResult(primo.multiply(base10Log(secondo)));
                        break;
                    }
                    case "√": {
                        BigDecimal multiply = primo.multiply(secondo.sqrt(new MathContext(10)));
                        first = multiply + "";
                        displayResult(multiply);
                        break;
                    }
                    case "^": {
                        first = pow(primo, secondo) + "";
                        displayResult(pow(primo, secondo));
                        break;
                    }
                    case "+": {
                        first = primo.add(secondo) + "";
                        displayResult(primo.add(secondo));
                        break;
                    }
                    case "-": {
                        first = primo.subtract(secondo) + "";
                        displayResult(primo.subtract(secondo));
                        break;
                    }
                    case "x": {
                        first = primo.multiply(secondo) + "";
                        displayResult(primo.multiply(secondo));
                        break;
                    }
                    case "÷": {
                        if (secondo.equals(new BigDecimal("0"))) {
                            displayInfinity();
                        } else {
                            first = primo.divide(secondo) + "";
                            displayResult(primo.divide(secondo));
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    //Graphics
    private void setAll(){

        borderPane =new BorderPane();
        background =new VBox();
        borderPane.setMinSize(250,450);
        background.setStyle("-fx-background-color: black");
        background.setAlignment(Pos.CENTER);
        background.setSpacing(10);

        Button btnLog=setNewButton("log");
        Button btnSquare=setNewButton("√");
        Button btnPi=setNewButton("π");
        Button btnPow=setNewButton("^");
        Button btnChangeSign= setNewButton("±");
        Button btnC= setNewButton("C");
        Button btnDel= setNewButton("⌫");
        Button btnSum= setNewButton("+");
        Button btn1= setNewButton("1");
        Button btn2= setNewButton("2");
        Button btn3= setNewButton("3");
        Button btnMinus= setNewButton("-");
        Button btn4= setNewButton("4");
        Button btn5= setNewButton("5");
        Button btn6= setNewButton("6");
        Button btnMul= setNewButton("x");
        Button btn7= setNewButton("7");
        Button btn8= setNewButton("8");
        Button btn9= setNewButton("9");
        Button btnDiv= setNewButton("÷");
        Button btn0= setNewButton("0");
        Button btnPt= setNewButton(".");
        Button btnUgual= setNewButton("=");

        Button btnExit= new Button("Exit");
        btnExit.setMinSize(50,50);
        btnExit.setStyle("-fx-background-color: #d00000;" + "-fx-text-fill: white");
        btnExit.setFont(font);
        btnExit.setOnAction(e ->
                exit()
        );

        btnLog.setStyle(firstColor);
        btnSquare.setStyle(firstColor);
        btnPi.setStyle(firstColor);
        btnPow.setStyle(firstColor);
        btnSum.setStyle(firstColor);
        btnDel.setStyle(firstColor);
        btnC.setStyle(firstColor);
        btnChangeSign.setStyle(firstColor);
        btnMinus.setStyle(firstColor);
        btnMul.setStyle(firstColor);
        btnDiv.setStyle(firstColor);
        btnUgual.setStyle(firstColor);

        HBox row0 =new HBox();
        row0.setSpacing(10);
        row0.setAlignment(Pos.CENTER);
        HBox row1 =new HBox();
        row1.setSpacing(10);
        row1.setAlignment(Pos.CENTER);
        HBox row2 =new HBox();
        row2.setSpacing(10);
        row2.setAlignment(Pos.CENTER);
        HBox row3 =new HBox();
        row3.setSpacing(10);
        row3.setAlignment(Pos.CENTER);
        HBox row4 =new HBox();
        row4.setSpacing(10);
        row4.setAlignment(Pos.CENTER);
        HBox row5 =new HBox();
        row5.setSpacing(10);
        row5.setAlignment(Pos.CENTER);

        row0.getChildren().addAll(btnExit,btnLog,btnPi,btnSquare);
        row1.getChildren().addAll(btnC,btnDel,btnPow,btnSum);
        row2.getChildren().addAll(btn7,btn8,btn9,btnMinus);
        row3.getChildren().addAll(btn4,btn5,btn6,btnMul);
        row4.getChildren().addAll(btn1,btn2,btn3,btnDiv);
        row5.getChildren().addAll(btnChangeSign,btn0,btnPt,btnUgual);

        textShowed =new TextArea();
        textShowed.setMaxHeight(10);
        textShowed.setMaxWidth(200);
        textShowed.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        containerDisplay =new HBox(textShowed);
        containerDisplay.setAlignment(Pos.CENTER);
        containerDisplay.setStyle("-fx-background-color: black;");
        containerDisplay.setPadding(new Insets( 20, 0, 0, 0 ));

        background.getChildren().addAll(row0, row1, row2, row3, row4, row5);
    }

    public void dot(){
        if (first.length() >= 1 && op.equals("")) {
            if (!first.contains(".")) {first = first + ".";}
        }
        else if (second.length() >= 1 && !second.contains(".")) { second = second + "."; }
        display();
    }

    public void delete(){
        if (second.length() >= 1) { second = second.substring(0, second.length() - 1);}
        else if (!op.equals("")) { op = "";}
        else if (first.length() >= 1) { first = first.substring(0, first.length() - 1);}
        else {  empty(); }
        display();
    }

    public void changeSign(){
        String segno = "-";
        if (first.equals("") || op.equals("")) {
            first = segno.concat(first);
            if (first.startsWith("--")) first = first.substring(2);
            else if (first.startsWith("+-") || first.startsWith("--")) { first = "-".concat(first.substring(2));}
        }
        else { second = segno.concat(second); reformat();}
        display();
    }
    public void digits(String pressed){
        if(pressed.equals("π")){ pressed= String.valueOf(Math.PI);}
        if (back) { first = pressed; op = ""; second = ""; back =false;}
        else if(log){ second = second.concat(pressed); displayLog();}
        else if(radix){ second = second.concat(pressed); displaySqrt(); return;}
        else if (first.equals("") || op.equals("")) { first = first.concat(pressed);}
        else { second = second.concat(pressed);}
        display();
    }

    private void displayLog() {
        this.textShowed.setText(first +" "+ op +" "+"log "+ second);
    }

    private void displaySqrt() {
        this.textShowed.setText(first +" "+ op +" "+"√ "+ second);
    }

    private void reformat() {
        if((op + second).startsWith("--")){
            op ="+";
        }else if((op + second).startsWith("+-") || first.startsWith("--")){
            op ="-";
        }else if((op + second).startsWith("++")){
            op ="+";
        }
        second = second.substring(1);
    }

    private void display(){
        if(first.equals("") && second.equals("") && op.equals("")){
            empty();
        }else {
            this.textShowed.setText(first + " " + op + " " + second);
        }
    }

    private void displayResult(BigDecimal result){
        this.textShowed.setText(result+"");
    }

    private void empty(){
        first ="";
        op ="";
        second ="";
        this.textShowed.setText("0");
    }

    private void displayInfinity(){
        first ="";
        op ="";
        second ="";
        this.textShowed.setText("Infinity");
    }

    private Button setNewButton(String text){
        Button btn=new Button(text);
        btn.setMinSize(50,50);
        btn.setStyle("-fx-font-weight: bold;" + "-fx-background-color: white;" + textBlack);
        btn.setFont(font);
        btn.setOnAction(e -> opHandler(text));
        return btn;
    }

    public void noEqualPressed(String who){
        BigDecimal primo = new BigDecimal(first);
        BigDecimal secondo = new BigDecimal(second);
        switch (op) {
            case "log":
            {
                first = primo.multiply(base10Log(secondo)) + "";
                displayResult(primo.multiply(base10Log(secondo)));
                op = who;
                break;
            }
            case "√":
            {
                BigDecimal multiply = primo.multiply(secondo.sqrt(new MathContext(10)));
                first = multiply + "";
                displayResult(multiply);
                op = who;
                break;
            }
            case "+":
            {
                displayResult(primo.add(secondo));
                first = primo.add(secondo) + "";
                op = who;
                break;
            }
            case "-":
            {
                displayResult(primo.subtract(secondo));
                first = primo.subtract(secondo)+ "";
                op = who;
                break;
            }

            case "x":
            {
                displayResult(primo.multiply(secondo));
                first = primo.multiply(secondo) + "";
                op = who;
                break;
            }
            case "÷":
            {
                if (secondo.equals(new BigDecimal("0"))) {
                    displayInfinity();
                } else {
                    displayResult(primo.divide(secondo));
                    first = primo.divide(secondo) + "";
                    op = who;
                }
                break;
            }
            default:
                break;
        }
        second = "";
    }

    public static BigDecimal base10Log(BigDecimal op){
        Double opDouble=Double.valueOf(op+"");
        opDouble=Math.log10(opDouble);
        return new BigDecimal(opDouble+"");
    }
    public static BigDecimal pow(BigDecimal a,BigDecimal b){ //performs a^b
        Double op1=Double.valueOf(a+"");
        Double op2=Double.valueOf(b+"");
        Double output;
        output=Math.pow(op1,op2);
        return new BigDecimal(output+"");
    }

    @Override
    public void start(Stage stage) {
        Stage myStage = stage;
        setAll();
        borderPane.setCenter(background);
        borderPane.setTop(containerDisplay);
        Scene scene = new Scene(borderPane);
        myStage.setScene(scene);
        myStage.setResizable(false);
        myStage.show();
        empty();
    }

    public static void main(String[] args) {
        launch();
    }
}