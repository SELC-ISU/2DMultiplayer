package com.example.a2dgame;

public enum GameType {

    BEST_OF_FIVE(5), BEST_OF_THREE(3), SINGLE(1);

    public final int totalGames;

    private GameType(int t){

        this.totalGames = t;

    }

    public static GameType enumOf(int val){

        for(GameType item:values()){
            if(item.totalGames == val){
                return item;
            }
        }

        return null;

    }



}
