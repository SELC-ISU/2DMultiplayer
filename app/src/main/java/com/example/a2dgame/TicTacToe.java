package com.example.a2dgame;

import android.content.Context;

public class TicTacToe extends Thread{

    private Context context;
    boolean doublePlayer;
    boolean opponentTurn;

    public TicTacToe(Context context){  //this will be used in MainActivity like, TicTacToe ttt = new TicTacToe(MainActivity.this);
        this.context = context;
    }

    public void run(){

        if(doublePlayer == true && opponentTurn == true) {

            while (((MainActivity) context).newGameMessage == false) ;
            ((MainActivity) context).newGameMessage = false;
            String message = ((MainActivity) context).gameMessage;

        }
        if(doublePlayer == true && opponentTurn == false) {

            String message = getOwnWhatver();
            ((MainActivity) context).write(message,MainActivity.GAME_STR);

        }
        //playing the rest of the game until it looks for more input
    }

    private String getOwnWhatver() {
        String msg = "";
        return msg;
    }
}
