package com.example.a2dgame;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.*;
import java.util.Scanner;
import android.view.*;

import androidx.appcompat.app.AppCompatActivity;

public class TicTacToe extends AppCompatActivity{

    private ImageView image;

    private Context context;
    boolean doublePlayer;
    boolean opponentTurn = true;
    String [][] gameTracker = new String[3][3];
    int cell;
    int rowChanged;
    int colChanged;
    int cellRow;
    int cellColumn;
    String symbol = "X";
    int score = 0;
    int oppScore = 0;


    public TicTacToe(Context context, boolean setTo, boolean oppTurn){  //this will be used in MainActivity like, TicTacToe ttt = new TicTacToe(MainActivity.this);
        this.context = context;
        doublePlayer = setTo;
        opponentTurn = oppTurn;
        System.out.println("opponent turn in constructor: " + opponentTurn);
        Game game = new Game();
        game.start();

    }

    public class Game extends Thread {

        public Game(){

        }

        public void run() {
            System.out.println("running run outside");
            System.out.println("Double player in outside run: " + doublePlayer);
            System.out.println("Opponent turn in outside run: " + opponentTurn);
            if (doublePlayer == true && opponentTurn == true) {
                System.out.println("running run inside oppTurn true");
                //symbol will switch each time it becomes a different player's turn
                //symbol = "X";

                while (((MainActivity) context).newGameMessage == false) ;
                ((MainActivity) context).newGameMessage = false;
                String message = ((MainActivity) context).gameMessage;
                Scanner sc = new Scanner(message);
                symbol = sc.next();
                cellRow = sc.nextInt();
                cellColumn = sc.nextInt();
                cell = toCellNumber(cellRow, cellColumn);
                gameTracker[cellRow][cellColumn] = symbol;


            }
            if (doublePlayer == true && opponentTurn == false) {
                System.out.println("running run inside oppTurn false");
                //symbol will switch each time it becomes a different player's turn
                //symbol = "O";
                String message = getOwnMessage();
                ((MainActivity) context).write(message, MainActivity.GAME_STR);

            }

            //playing the rest of the game until it looks for more input
        }

    }

    protected String getOwnMessage() {
        String msg = symbol + " " + rowChanged + " " + colChanged;
        return msg;
    }

    /**
     * converts the coordinates from the incoming message into the cell number
     * will hopefully make determining which cell image to change easier
     */
    private int toCellNumber(int row, int column) {
        int tempCell = -1;

        if (row == 0) {
            if (column == 0) {
                tempCell = 1;
            } else if (column == 1) {
                tempCell = 2;
            } else if (column == 2) {
                tempCell = 3;
            }
        } else if (row == 1) {
            if (column == 0) {
                tempCell = 4;
            } else if (column == 1) {
                tempCell = 5;
            } else if (column == 2) {
                tempCell = 6;
            }
        } else if (row == 2) {
            if (column == 0) {
                tempCell = 7;
            } else if (column == 1) {
                tempCell = 8;
            } else if (column == 2) {
                tempCell = 9;
            }
        }

        return tempCell;
    }

    public String getSymbol(){

        return symbol;
    }

    public void modifyGameTrackerClick(int cellNum){

        if(cellNum == 1){
            gameTracker[0][0] = symbol;
            rowChanged = 0;
            colChanged = 0;
        }else if(cellNum == 2){
            gameTracker[0][1] = symbol;
            rowChanged = 0;
            colChanged = 1;
        }else if(cellNum == 3){
            gameTracker[0][2] = symbol;
            rowChanged = 0;
            colChanged = 2;
        }else if(cellNum == 4){
            gameTracker[1][0] = symbol;
            rowChanged = 1;
            colChanged = 0;
        }else if(cellNum == 5){
            gameTracker[1][1] = symbol;
            rowChanged = 1;
            colChanged = 1;
        }else if(cellNum == 6){
            gameTracker[1][2] = symbol;
            rowChanged = 1;
            colChanged = 2;
        }else if(cellNum == 7){
            gameTracker[2][0] = symbol;
            rowChanged = 2;
            colChanged = 0;
        }else if(cellNum == 8){
            gameTracker[2][1] = symbol;
            rowChanged = 2;
            colChanged = 1;
        }else if(cellNum == 9){
            gameTracker[2][2] = symbol;
            rowChanged = 2;
            colChanged = 2;
        }

    }

    //checks to see if the game has been won
    public boolean checkIfWon(){
        if(gameTracker[0][0].equals(gameTracker[0][1]) && gameTracker[0][0].equals(gameTracker[0][2])){
            //if first row is all equal
            return true;
        }else if(gameTracker[1][0].equals(gameTracker[1][1]) && gameTracker[1][0].equals(gameTracker[1][2])){
            //if second row is all equal
            return true;
        }else if(gameTracker[2][0].equals(gameTracker[2][1]) && gameTracker[2][0].equals(gameTracker[2][2])){
            //if third row is all equal
            return true;
        }else if(gameTracker[0][0].equals(gameTracker[1][0]) && gameTracker[0][0].equals(gameTracker[2][0])){
            //if first column is all equal
            return true;
        }else if(gameTracker[0][1].equals(gameTracker[1][1]) && gameTracker[0][1].equals(gameTracker[2][1])){
            //if middle column is all equal
            return true;
        }else if(gameTracker[0][2].equals(gameTracker[1][2]) && gameTracker[0][2].equals(gameTracker[2][2])){
            //check if third column is all equal
            return true;
        }else if(gameTracker[0][0].equals(gameTracker[1][1]) && gameTracker[0][0].equals(gameTracker[2][2])){
            //if diagonal starting in top left is all equal
            return true;
        }else if(gameTracker[0][2].equals(gameTracker[1][1]) && gameTracker[0][2].equals(gameTracker[2][0])){
            //if diagonal starting in top right is all equal
            return true;
        }


        return false;
    }

    protected void changeOppTurn(){

        opponentTurn = false;
    }

    protected void setDoublePlayer(boolean set){
        doublePlayer = set;
        System.out.println("double player value in end of setDoublePlayer" + doublePlayer);
    }

    protected boolean getOpponentTurn(){
        return opponentTurn;
    }

    protected void changeSymbol(){
        symbol = "O";
    }

    protected void incrementScore(){
        score++;
    }

    protected void incrementOppScore(){
        oppScore++;
    }

    protected int getScore(){
        return score;
    }

    protected int getOppScore(){
        return oppScore;
    }

    protected String getScoreStatement(){
        return "Score: " + score + " - " + oppScore;
    }

    protected void afterClick(int n){
        //String message = ((MainActivity) context).gameMessage;
        //Scanner sc = new Scanner(message);

        modifyGameTrackerClick(n);

        /*
        if(checkIfWon()){
            //do stuff
            incrementScore();
            ((MainActivity) context).write("incrementScore", MainActivity.MICELLANEOUS_STR);
        }
        */
        //String message = getOwnMessage();
        //((MainActivity) context).write(message, MainActivity.GAME_STR);

    }

}
