package com.example.a2dgame;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.*;
import java.util.Scanner;

import androidx.appcompat.app.AppCompatActivity;

public class TicTacToe extends AppCompatActivity{

    private static String TAG = "TICTACTOE";

    private ImageView image;

    public final static String GAME_LOSE = "youLost";
    public final static String GAME_WIN = "youWon";
    public final static String GAME_CLEAR = "Clear:";
    public final static String GAME_CONT = "Normal:";
    public final static String GAME_DRAW = "DRAW:";

    private Context context;
    boolean doublePlayer;
    boolean opponentTurn = true;
    String [][] gameTracker = new String[3][3];
    int cell;
    int cellRow;
    int cellColumn;
    String symbol = "X";
    int score = 0;
    int oppScore = 0;
    int numGames;
    private ComputerPlayer player;

    public TicTacToe(Context context, boolean setTo, boolean oppTurn){  //this will be used in MainActivity like, TicTacToe ttt = new TicTacToe(MainActivity.this);
        this.context = context;
        doublePlayer = setTo;

        if(oppTurn == true){
            symbol = "X";
        }
        else{
            symbol = "O";
        }

        if(!doublePlayer){
            player = new ComputerPlayer(context,"O",gameTracker);
            Log.d(TAG,"Computer Created");
        }

        opponentTurn = oppTurn;
        System.out.println("opponent turn in constructor: " + opponentTurn);
        Game game = new Game();
        game.start();

        for(int i = 0; i <=2; i++){
            for(int j = 0; j<=2; j++){
                gameTracker[i][j] = "";
            }
        }

    }



    public class Game extends Thread {

        public Game(){

        }

        public void run() {
            while (true) {
                //System.out.println("running run outside");
                //System.out.println("Double player in outside run: " + doublePlayer);
                //System.out.println("Opponent turn in outside run: " + opponentTurn);

                if (doublePlayer == true && opponentTurn == true) {
                    //when it is the other person's turn (listening for new message)
                    System.out.println("running run inside oppTurn true");
                    Log.d(TAG, "We wait for client to pick");

                    while (((MainActivity) context).newGameMessage == false) ;

                    ((MainActivity) context).newGameMessage = false;


                } else if (doublePlayer == false && opponentTurn == true) {
                    Log.d(TAG, "We wait for computer to pick");
                    player.placeMove(); //computer picked moved RGHT HERE
                    Log.d(TAG, "Computer Picked"); //do all opponent winning checks and anything that you would do for a person

                    if(!isAtADraw()){
                        if(checkIfWonSpecific(getOpponentSymbol())){
                            //oppScore++;
                            ((MainActivity) context).scoreBox.setText(getScoreStatement());
                            ((MainActivity) context).clearGrid();
                            clearArray();

                        }/*else if(checkIfWonSpecific(getSymbol())){
                            score++;
                            ((MainActivity) context).scoreBox.setText(getScoreStatement());
                            ((MainActivity) context).clearGrid();
                            clearArray();

                        }*/


                    }else{
                        ((MainActivity) context).clearGrid();
                        clearArray();
                    }


                    changeOppTurn();

                }else if(doublePlayer == false && opponentTurn == false){

                    //onCellTouchSinglePlayer

                } else if (doublePlayer == true && opponentTurn == false) {
                    //when it is your turn (sending out selected cell)
                    Log.d(TAG, "We wait for Host to pick");
                    System.out.println("running run inside oppTurn false");



                }

                //playing the rest of the game until it looks for more input
            }
        }

    }



    public String getSymbol(){

        return symbol;
    }

    protected String getOpponentSymbol(){
        if(symbol.equals("O")){
            return "X";
        }else if(symbol.equals("X")){
            return "O";
        }
        return "Oh shit";
    }

    public void modifyGameTrackerClick(int row, int col){

        gameTracker[row][col] = symbol;

    }

    //checks to see if there are three in a row

    public boolean checkIfWon(){

        if(gameTracker[0][0].equals(gameTracker[0][1]) && gameTracker[0][0].equals(gameTracker[0][2]) && (gameTracker[0][0].equals("X") || gameTracker[0][0].equals("O"))){
            //if first row is all equal and X or O (not "")
            return true;
        }else if(gameTracker[1][0].equals(gameTracker[1][1]) && gameTracker[1][0].equals(gameTracker[1][2]) && (gameTracker[1][0].equals("X") || gameTracker[1][0].equals("O"))){
            //if second row is all equal and X or O (not "")
            return true;
        }else if(gameTracker[2][0].equals(gameTracker[2][1]) && gameTracker[2][0].equals(gameTracker[2][2]) && (gameTracker[2][0].equals("X") || gameTracker[2][0].equals("O"))){
            //if third row is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][0].equals(gameTracker[1][0]) && gameTracker[0][0].equals(gameTracker[2][0]) && (gameTracker[0][0].equals("X") || gameTracker[0][0].equals("O"))){
            //if first column is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][1].equals(gameTracker[1][1]) && gameTracker[0][1].equals(gameTracker[2][1]) && (gameTracker[0][1].equals("X") || gameTracker[0][1].equals("O"))){
            //if middle column is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][2].equals(gameTracker[1][2]) && gameTracker[0][2].equals(gameTracker[2][2]) && (gameTracker[0][2].equals("X") || gameTracker[0][2].equals("O"))){
            //check if third column is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][0].equals(gameTracker[1][1]) && gameTracker[0][0].equals(gameTracker[2][2]) && (gameTracker[0][0].equals("X") || gameTracker[0][0].equals("O"))){
            //if diagonal starting in top left is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][2].equals(gameTracker[1][1]) && gameTracker[0][2].equals(gameTracker[2][0]) && (gameTracker[0][2].equals("X") || gameTracker[0][2].equals("O"))){
            //if diagonal starting in top right is all equal and X or O (not "")
            return true;
        }else {
            return false;
        }
    }

    public boolean checkIfWonSpecific(String temp){

        if(gameTracker[0][0].equals(gameTracker[0][1]) && gameTracker[0][0].equals(gameTracker[0][2]) && (gameTracker[0][0].equals(temp))){
            //if first row is all equal and X or O (not "")
            return true;
        }else if(gameTracker[1][0].equals(gameTracker[1][1]) && gameTracker[1][0].equals(gameTracker[1][2]) && (gameTracker[1][0].equals(temp))){
            //if second row is all equal and X or O (not "")
            return true;
        }else if(gameTracker[2][0].equals(gameTracker[2][1]) && gameTracker[2][0].equals(gameTracker[2][2]) && (gameTracker[2][0].equals(temp))){
            //if third row is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][0].equals(gameTracker[1][0]) && gameTracker[0][0].equals(gameTracker[2][0]) && (gameTracker[0][0].equals(temp))){
            //if first column is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][1].equals(gameTracker[1][1]) && gameTracker[0][1].equals(gameTracker[2][1]) && (gameTracker[0][1].equals(temp))){
            //if middle column is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][2].equals(gameTracker[1][2]) && gameTracker[0][2].equals(gameTracker[2][2]) && (gameTracker[0][2].equals(temp))){
            //check if third column is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][0].equals(gameTracker[1][1]) && gameTracker[0][0].equals(gameTracker[2][2]) && (gameTracker[0][0].equals(temp))){
            //if diagonal starting in top left is all equal and X or O (not "")
            return true;
        }else if(gameTracker[0][2].equals(gameTracker[1][1]) && gameTracker[0][2].equals(gameTracker[2][0]) && (gameTracker[0][2].equals(temp))){
            //if diagonal starting in top right is all equal and X or O (not "")
            return true;
        }else {
            return false;
        }
    }

    protected void changeOppTurn(){

        opponentTurn = false;
    }

    //i couldn't get .toString to work
    protected String getGameTracker(){
        return "Row 1: " + gameTracker[0][0] + gameTracker[0][1] + gameTracker[0][2] + " Row 2: " + gameTracker[1][0] + gameTracker[1][1] + gameTracker[1][2]
                +  " Row 3: " + gameTracker[2][0] + gameTracker[2][1] + gameTracker[2][2];
    }

    protected void setOpponentTurnTrue(){
        opponentTurn = true;
    }


    protected boolean getOpponentTurn(){
        return opponentTurn;
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
        if(checkPlayerVictory()){
            return "Game over. You won!";
        }else if(checkOpponentVictory()){
            return "Game over. You lost!";
        }else {
            return "Score: " + score + " - " + oppScore;
        }
    }

    protected void afterClick(int r, int c){

        modifyGameTrackerClick(r,c);
        setOpponentTurnTrue();

    }

    protected void clearArray(){
        for(int i = 0; i <=2; i++){
            for(int j = 0; j<=2; j++){
                gameTracker[i][j] = "";
            }
        }
        //System.out.println(getGameTracker());
    }

    protected void setNumGames(int n){
        numGames = n;
    }

    protected boolean checkVictory(){
        System.out.println("Num games in checkVictory: " + numGames);
        System.out.println(getScoreStatement());
        System.out.println("Opp score in checkVictory: " + oppScore);
        System.out.println("Score in checkVictory: " + score);
        if(score  > (numGames / 2) ||  oppScore > (numGames / 2)){
            return true;
        }else{
            return false;
        }
    }

    protected boolean checkOpponentVictory(){
        if(oppScore > (numGames / 2)){
            return true;
        }else{
            return false;
        }
    }

    protected boolean checkPlayerVictory(){
        if(score > (numGames / 2)){
            return true;
        }else{
            return false;
        }
    }

    protected boolean isAtADraw(){
        if(!checkVictory() && checkIfAllFull()){
            return true;
        }else{
            return false;
        }
    }

    protected boolean checkIfAllFull(){
        for(int i = 0; i <=2; i++){
            for(int j = 0; j<=2; j++){
                if(gameTracker[i][j].equals("")){
                    return false;
                }
            }
        }
        return true;
    }

    protected void reverseSymbol(){
        if(symbol.equals("X")){
            symbol = "O";
        }else if(symbol.equals("O")){
            symbol = "X";
        }
    }

    protected void onCellTouchDouble(TextView cellNum, int r, int c){
        Log.d(TAG,"USER pick position");
        System.out.println("Beginning of onCellTouchDouble");
        if(!getOpponentTurn() && !checkVictory()) {
            //cellNum.setText(getSymbol());
            ((MainActivity)context).setBoardPos(r,c,getSymbol());
            afterClick(r,c);

            String pos = r+","+c;
            String check = "nothing";

            if(!isAtADraw()) {
                if (checkIfWon()) {
                    System.out.println("winner bitch");
                    score++;
                    ((MainActivity) context).scoreBox.setText(getScoreStatement());
                    System.out.println("Score after win: " + getScore());
                    //scoreBox.setText(ttt.getScoreStatement());
                    if (checkPlayerVictory()) {
                        //scoreBox.setText("Game over. You Won!");
                        System.out.println("Writing: youLost");
                        ((MainActivity) context).write(GAME_LOSE, MainActivity.GAME_STR);
                        check = GAME_LOSE;
                        System.out.println("Should be done writing youLost");
                    } else if (checkOpponentVictory()) {
                        //scoreBox.setText("Game over. You Lost!");
                        ((MainActivity) context).write(GAME_WIN, MainActivity.GAME_STR);
                        check = GAME_WIN;
                    }
                    clearArray();
                    ((MainActivity) context).clearGrid();
                    ((MainActivity) context).write(GAME_CLEAR+pos+"."+check, MainActivity.GAME_STR);
                } else {
                    Log.d(TAG,"Continueing Play");
                    if(doublePlayer)
                        ((MainActivity) context).write(GAME_CONT+pos, MainActivity.GAME_STR);
                    else
                        setOpponentTurnTrue();
                }
            }else{

                clearArray();
                ((MainActivity) context).clearGrid();


                ((MainActivity) context).switchToGameScreenLayout();
                reverseSymbol();
                ((MainActivity) context).write(GAME_DRAW, MainActivity.GAME_STR);
            }
        }
    }

    public void onCellTouchSingle(TextView cellNum, int r, int c) {
        if (!checkVictory()) {
            ((MainActivity) context).setBoardPos(r, c, getSymbol());
            afterClick(r, c);


            if (!isAtADraw()) {
                if (checkIfWonSpecific(getSymbol())) {
                    score++;
                    ((MainActivity) context).scoreBox.setText(getScoreStatement());

                    clearArray();
                    ((MainActivity) context).clearGrid();

                } /*else if (checkIfWonSpecific(getOpponentSymbol())) {
                    oppScore++;
                    ((MainActivity) context).scoreBox.setText(getScoreStatement());

                    clearArray();
                    ((MainActivity) context).clearGrid();

                }*/


            } else {
                clearArray();
                ((MainActivity) context).clearGrid();

            }

        }

        if (checkPlayerVictory()) {
            ((MainActivity) context).scoreBox.setText(getScoreStatement());
            clearArray();
            ((MainActivity) context).clearGrid();

        }

        setOpponentTurnTrue();

    }

    public void onCellTouchMain(TextView cellNum, int r, int c){
        if(doublePlayer){
            onCellTouchDouble(cellNum, r, c);
        }else{
            onCellTouchSingle(cellNum, r, c);
        }
    }

}
