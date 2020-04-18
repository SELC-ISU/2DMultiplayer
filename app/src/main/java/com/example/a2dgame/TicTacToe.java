package com.example.a2dgame;

import android.content.Context;
import android.widget.ImageView;
import android.widget.*;
import java.util.Scanner;
import android.view.*;

import androidx.appcompat.app.AppCompatActivity;

public class TicTacToe extends AppCompatActivity{

    private ImageView image;

    private Context context;
    boolean doublePlayer;
    boolean opponentTurn;
    char [][] tracker = new char[3][3];
    int cell;
    int cellRow;
    int cellColumn;
    String symbol;

    private ImageView x1;
    private ImageView x2;
    private ImageView x3;
    private ImageView x4;
    private ImageView x5;
    private ImageView x6;
    private ImageView x7;
    private ImageView x8;
    private ImageView x9;

    private ImageView o1;
    private ImageView o2;
    private ImageView o3;
    private ImageView o4;
    private ImageView o5;
    private ImageView o6;
    private ImageView o7;
    private ImageView o8;
    private ImageView o9;


    public TicTacToe(Context context){  //this will be used in MainActivity like, TicTacToe ttt = new TicTacToe(MainActivity.this);
        this.context = context;
        Game game = new Game();
        image = findViewById(R.id.ONum3);
        game.start();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public class Game extends Thread {

        public Game(){

        }

        public void run() {

            if (doublePlayer == true && opponentTurn == true) {

                //symbol will switch each time it becomes a different player's turn
                symbol = "X";

                while (((MainActivity) context).newGameMessage == false) ;
                ((MainActivity) context).newGameMessage = false;
                String message = ((MainActivity) context).gameMessage;
                Scanner sc = new Scanner(message);
                symbol = sc.next();
                cellRow = sc.nextInt();
                cellColumn = sc.nextInt();
                cell = toCellNumber(cellRow, cellColumn);


            }
            if (doublePlayer == true && opponentTurn == false) {

                //symbol will switch each time it becomes a different player's turn
                symbol = "O";

                String message = getOwnWhatver();
                ((MainActivity) context).write(message, MainActivity.GAME_STR);

            }
            //playing the rest of the game until it looks for more input
        }

        private String getOwnWhatver() {
            String msg = "";
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
    }


public String getSymbol(){
        return symbol;
}

}
//mA.write(positionOfPeice, MainActivity.GAME_STR);
