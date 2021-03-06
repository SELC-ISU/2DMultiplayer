package com.example.a2dgame;

import android.content.Context;
import android.util.Log;

import java.util.Random;


public class ComputerPlayer {

    private static String TAG = "COMPUTER_PLAYER";

    private String[][] gameBoard;
    private Context context;
    private String symbol;
    private int moveNum;
    private Random rand;
    private int xPrev, yPrev;

    /**
     *
     * Creates a new computer player
     *
     * @param cntxt
     * @param sym X or O
     * @param gmBoard playing tictactoe board
     */
    public ComputerPlayer(Context cntxt, String sym, String[][] gmBoard){

        symbol = sym;
        context = cntxt;
        gameBoard = gmBoard;

        moveNum = 0;

        rand = new Random();

    }

    /**
     * Called to have the computer make a move and then make the move visible on the board
     * @return if succesfull move
     */
    public boolean placeMove(){
        boolean out = pickMove();

        if(out){
            ((MainActivity)context).setBoardPos(xPrev,yPrev,symbol);
        }

        return false;
    }

    /**
     * Called by placeMove, this does the actual picking of where the computer will choose
     * @return if successful
     */
    private boolean pickMove(){

        int randNum,x,y;

        if(moveNum == 0){
            while(true) {
                randNum = rand.nextInt(4);

                if (randNum == 0) {
                   x = 0;
                   y =0;
                } else if (randNum == 1) {      //This is the first move for the computer
                    x = 2;
                    y =0;
                } else if (randNum == 2) {
                    x = 0;
                    y =2;
                } else {
                    x = 2;
                    y =2;
                }

                if(isPosEmpty(x,y)){
                    gameBoard[x][y] = symbol;
                    xPrev = x;
                    yPrev = y;
                    moveNum++;
                    return true;
                }
            }

        }
        else if(canBlockWin()){

            gameBoard[xPrev][yPrev] = symbol;  //Now it is possible for the opponent or comp to win
            moveNum++;                          //so check to see if a block or win is possible
            return true;

        }
        else if(moveNum == 1 || moveNum == 2){

            if(acrossOpen()) {
                Log.d(TAG,"Accross Open");
                x = Math.abs(xPrev - 2);
                y = Math.abs(yPrev - 2);
                gameBoard[x][y] = symbol;   //this checks if the cornering strategy is open
                xPrev = x;
                yPrev = y;
                moveNum++;
                return true;
            }
            else {

                randNum = rand.nextInt(2);
                if (randNum == 1) {
                    x = Math.abs(xPrev - 2);
                    Log.d(TAG,"Checking pos: " + x + ", " + yPrev);
                    if (isPosEmpty(x, yPrev)) {
                        gameBoard[x][yPrev] = symbol;   //this is chooses for the computer if it cannot corner
                        xPrev = x;
                        moveNum++;
                        return true;
                    }
                    else if (isPosEmpty(yPrev, x)) {
                        gameBoard[yPrev][x] = symbol;   //this is chooses for the computer if it cannot corner
                        xPrev = yPrev;
                        yPrev = x;
                        moveNum++;
                        return true;
                    }
                } else {
                    y = Math.abs(yPrev - 2);
                    Log.d(TAG,"Checking pos: " + xPrev + ", " + y);
                    if (isPosEmpty(xPrev, y)) {
                        gameBoard[xPrev][y] = symbol;
                        yPrev = y;
                        moveNum++;
                        return true;
                    }
                    else if (isPosEmpty(y, xPrev)) {
                        gameBoard[y][xPrev] = symbol;   //this is chooses for the computer if it cannot corner
                        yPrev = xPrev;
                        xPrev = y;
                        moveNum++;
                        return true;
                    }
                }

            }

        }
        else {

            while(true) {
                x = rand.nextInt(3);
                y = rand.nextInt(3);

                if(isPosEmpty(x,y)){
                    gameBoard[x][y] = symbol;   //this just picks a random move because all strategies are out the window
                    xPrev = x;
                    yPrev = y;
                    moveNum++;
                    return true;
                }
            }
        }
        Log.d(TAG, "Should not be here, failed to place");
        return false;
    }

    /**
     * returns the symbol of the opponent or the computer
     * @param isMine whose sym you want
     * @return symbol
     */
    public String getSymbol(boolean isMine){
        if(isMine)
            return symbol;
        else {
            if(symbol == "X")
                return "O";
            else
                return "X";
        }
    }

    /**
     * checks to see if a piece is already there
     * @param x
     * @param y
     * @return
     */
    private boolean isPosEmpty(int x, int y){
        if(gameBoard[x][y].isEmpty() || gameBoard[x][y] == null){
            return true;
        }
        return false;
    }

    /**
     * checks to see if the position accross from the previous move is ope
     * This is to check if the cornering strategy is open
     * @return
     */
    private boolean acrossOpen(){
        if(isPosEmpty(Math.abs(xPrev-2),Math.abs(yPrev-2))){
            return true;
        }
        return false;
    }

    /**
     * This checks if there is an option to win, or if there is a way to block
     * the oppenent from winning
     * @return
     */
    private boolean canBlockWin(){

        int xCountR = 0, oCountR = 0, xCountC = 0, oCountC = 0;

        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){

                if(j == 0){
                    xCountC = 0;
                    oCountC = 0;
                    xCountR = 0;
                    oCountR = 0;
                }

                if(gameBoard[i][j].equals("X"))
                    xCountR++;
                else if(gameBoard[i][j].equals("O"))
                    oCountR++;

                if(gameBoard[j][i].equals("X"))
                    xCountC++;
                else if(gameBoard[j][i].equals("O"))
                    oCountC++;

                if(j == 2) {
                    if ((xCountC == 2 && oCountC == 0) || (xCountC == 0 && oCountC == 2)) {
                        if (isPosEmpty(0, i)) {
                            xPrev = 0;
                            yPrev = i;
                            return true;
                        } else if (isPosEmpty(1, i)) {
                            xPrev = 1;
                            yPrev = i;
                            return true;
                        } else if (isPosEmpty(2, i)) {
                            xPrev = 2;
                            yPrev = i;
                            return true;
                        }
                    }
                    if ((xCountR == 2 && oCountR == 0) || (xCountR == 0 && oCountR == 2)) {
                        if (isPosEmpty(i, 0)) {
                            xPrev = i;
                            yPrev = 0;
                            return true;
                        } else if (isPosEmpty(i, 1)) {
                            xPrev = i;
                            yPrev = 1;
                            return true;
                        } else if (isPosEmpty(i, 2)) {
                            xPrev = i;
                            yPrev = 2;
                            return true;
                        }

                    }
                }
            }
        }

        if(gameBoard[1][1].equals(getSymbol(false)) || gameBoard[1][1].equals(getSymbol(true))){
            String tempSym = gameBoard[1][1];
            if(gameBoard[2][2].equals(tempSym)){
                xPrev = 0;
                yPrev = 0;
                return true;
            }
            else if(gameBoard[0][2].equals(tempSym)){
                xPrev = 2;
                yPrev = 0;
                return true;                        //this checks to see if there is a chance on the angles
            }
            else if(gameBoard[2][0].equals(tempSym)){
                xPrev = 0;
                yPrev = 2;
                return true;
            }
            else if(gameBoard[0][0].equals(tempSym)){
                xPrev = 2;
                yPrev = 2;
                return true;
            }
        }
        else{
            if(gameBoard[0][0].equals(gameBoard[2][2]) || gameBoard[0][2].equals(gameBoard[2][0])){
                xPrev = 1;
                yPrev = 1;
                return true;
            }
        }
        return false;
    }

    /**
     * Allows for the game to switch between pieces for the players
     * @param sym
     */
    public void changeSymbol(String sym){
        symbol = sym;
    }

    /**
     * This resets the move count so the computer can go back to its strategy
     */
    public void resetMoves(){

        yPrev = 0;
        xPrev = 0;
        moveNum = 0;
    }

}
