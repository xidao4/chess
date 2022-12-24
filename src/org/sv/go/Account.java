package org.sv.go;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {
    String name;
    String pwd;
    int winNum;
    int loseNum;

    public Account(String name, String pwd, int winNum, int loseNum) {
        this.name = name;
        this.pwd = pwd;
        this.winNum = winNum;
        this.loseNum = loseNum;
    }
    public Account(String name){
        this.name=name;
    }
    public Account(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getWinNum() {
        return winNum;
    }

    public void setWinNum(int winNum) {
        this.winNum = winNum;
    }

    public int getLoseNum() {
        return loseNum;
    }

    public void setLoseNum(int loseNum) {
        this.loseNum = loseNum;
    }
}
