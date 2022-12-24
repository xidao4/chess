package org.sv.go;

import java.util.Vector;

public class Memento {
    //Vector chessman;
    int alreadyNum;
    int currentTurn;
    int gridNum;
    int gridLen;
    int chessmanLength;
    Chesspoint[][] map;

    public Memento(int alreadyNum, int currentTurn, int gridNum, int gridLen, int chessmanLength,Chesspoint[][] map ) {
//        this.chessman = chessman;
        this.alreadyNum = alreadyNum;
        this.currentTurn = currentTurn;
        this.gridNum = gridNum;
        this.gridLen = gridLen;
        this.chessmanLength = chessmanLength;
        this.map=map;
    }

    public Memento(){}

    public int getAlreadyNum() {
        return alreadyNum;
    }

    public void setAlreadyNum(int alreadyNum) {
        this.alreadyNum = alreadyNum;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getGridNum() {
        return gridNum;
    }

    public void setGridNum(int gridNum) {
        this.gridNum = gridNum;
    }

    public int getGridLen() {
        return gridLen;
    }

    public void setGridLen(int gridLen) {
        this.gridLen = gridLen;
    }

    public int getChessmanLength() {
        return chessmanLength;
    }

    public void setChessmanLength(int chessmanLength) {
        this.chessmanLength = chessmanLength;
    }

    public Chesspoint[][] getMap() {
        return map;
    }

    public void setMap(Chesspoint[][] map) {
        this.map = map;
    }
}
