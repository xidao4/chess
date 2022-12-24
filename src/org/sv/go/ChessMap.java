package org.sv.go;

import java.util.Vector;

public class ChessMap {
//    Vector chessman;
    int alreadyNum;
    int currentTurn;
    int gridNum;
    int gridLen;
    int chessmanLength;
    Chesspoint[][] map;

    public ChessMap(int alreadyNum, int currentTurn, int gridNum, int gridLen, int chessmanLength, Chesspoint[][] map) {
        this.alreadyNum = alreadyNum;
        this.currentTurn = currentTurn;
        this.gridNum = gridNum;
        this.gridLen = gridLen;
        this.chessmanLength = chessmanLength;
        this.map = map;
    }

    public ChessMap(){

    }

    public void restoreToMemento(Memento m){
//        this.chessman=m.getChessman();
        this.alreadyNum=m.getAlreadyNum();
        this.currentTurn=m.getCurrentTurn();
        this.gridNum=m.getGridNum();
        this.gridLen=m.getGridLen();
        this.chessmanLength=m.getChessmanLength();
        this.map=m.getMap();
    }
    public Memento saveToMemento(){
//        Vector chessman=new Vector();
//        for(Object p: this.chessman){
//            chessman.addElement(p);
//        }
        Chesspoint[][] map=new Chesspoint[this.gridNum+1][this.gridNum+1];
        for(int i=0;i<gridNum+1;i++){
            for(int j=0;j<gridNum+1;j++){
                map[i][j]=this.map[i][j];
            }
        }
        return new Memento(this.alreadyNum,this.currentTurn,this.gridNum,this.gridLen,this.chessmanLength,map);
    }

}
