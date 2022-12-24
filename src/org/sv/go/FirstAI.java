package org.sv.go;

public class FirstAI implements AI{
    @Override
    public Chesspoint move(ChessMap chessMap,int color){
        int x,y;
        while(true){
            x=(int)(Math.random()*(chessMap.gridNum+1));
            y=(int)(Math.random()*(chessMap.gridNum+1));
            if (chessMap.map[x][y]==null){
                break;
            }
        }
        chessMap.map[x][y]=new Chesspoint(x,y,color);
        chessMap.alreadyNum++;
        chessMap.currentTurn=1-color;
        return chessMap.map[x][y];
    }
}
