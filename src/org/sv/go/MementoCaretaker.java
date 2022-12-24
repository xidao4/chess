package org.sv.go;

import java.util.ArrayList;
import java.util.List;

public class MementoCaretaker {
    private List<Memento> mementoList=new ArrayList<>();
    private int index=-1;
    public void goOn(ChessMap chessMap){
        index++;
        this.mementoList.add(index,chessMap.saveToMemento());
    }
    public ChessMap undo(){
        ChessMap map=new ChessMap();
        if(index>=0){
            map.restoreToMemento(this.mementoList.get(--index));
        }else{
            map=null;
        }
        return map;
    }
    public ChessMap redo(){
        ChessMap chessMap=new ChessMap();
        index++;
        if(index<this.mementoList.size()){
            chessMap.restoreToMemento(this.mementoList.get(index));
        }else{
            chessMap=null;
        }
        return chessMap;
    }

    public List<Memento> getMementoList() {
        return mementoList;
    }

    public void setMementoList(List<Memento> mementoList) {
        this.mementoList = mementoList;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
