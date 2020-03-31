package com.example.a2dgame;

public enum GameType {

    BEST_OF_FIVE(3,5), BEST_OF_THREE(2,3), SINGLE(1,1);

    public final int totalGames;
    public final int mercyNum;

    private GameType(int m, int t){

        this.totalGames = t;
        this.mercyNum = m;

    }

    public static GameType enumOfTotalGames(int val){

        for(GameType item:values()){
            if(item.totalGames == val){
                return item;
            }
        }

        return null;

    }
    public static GameType enumOfMercyNum(int val){

        for(GameType item:values()){
            if(item.mercyNum == val){
                return item;
            }
        }

        return null;

    }

    public static GameType enumOf(int m, int t){

        for(GameType item:values()){
            if(item.totalGames == t){
                if(item.mercyNum == m)
                    return item;
                else
                    return null;
            }
        }

        return null;

    }

}
