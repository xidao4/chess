package org.sv.go;

import java.util.HashMap;

public class SecondAI implements AI{
    public static int[][] bx;
    public static int[][] weightArray;
    HashMap<String,Integer> map = new HashMap<>();
    public SecondAI(){
        // 活一连
        map.put("010", 20);
        map.put("020", 20);
        map.put("01", 20);
        map.put("02", 20);
        map.put("10", 20);
        map.put("20", 20);
        // 活二连
        map.put("0110", 100);
        map.put("0220", 100);
        map.put("011", 100);
        map.put("110", 100);
        map.put("022", 100);
        map.put("220", 100);
        // 活三连
        map.put("01110", 500);
        map.put("02220", 500);
        map.put("1110", 500);
        map.put("0111", 500);
        map.put("2220", 500);
        map.put("0222", 500);
        // 活四连
        map.put("011110", 10000);
        map.put("022220", 10000);
        map.put("01111", 10000);
        map.put("11110", 10000);
        map.put("02222", 10000);
        map.put("22220", 10000);
        // 死一连
        map.put("012", 10);
        map.put("021", 10);
        map.put("120", 10);
        map.put("210", 10);
        // 死二连
        map.put("0112", 70);
        map.put("0221", 70);
        map.put("2110", 70);
        map.put("1220", 70);
        // 死三连
        map.put("01112", 200);
        map.put("02221", 200);
        map.put("21110", 200);
        map.put("12220", 200);
        // 死四连
        map.put("011112", 10000);
        map.put("022221", 10000);
        map.put("211110", 10000);
        map.put("122220", 10000);
    }
    @Override
    public Chesspoint move(ChessMap chessMap,int color) {
        bx=new int[chessMap.gridNum][chessMap.gridNum];
        weightArray=new int[chessMap.gridNum][chessMap.gridNum];
        // 清空权值数组
        for (int r = 0; r < weightArray.length; r++) {
            for (int c = 0; c < weightArray.length; c++) {
                weightArray[r][c] = 0;
            }
        }
        WZQAI(chessMap);
        Chesspoint p=AIcount(color);
        chessMap.map[p.x][p.y]=p;
        chessMap.alreadyNum++;
        chessMap.currentTurn=1-color;
        return p;
    }
    public void WZQAI(ChessMap chessMap) {
        for(int i=0;i<bx.length;i++){
            for(int j=0;j<bx.length;j++){
                if(chessMap.map[i][j]==null){
                    bx[i][j]=0;
                }else if(chessMap.map[i][j].color==Chesspoint.black){
                    bx[i][j]=1;
                }else if(chessMap.map[i][j].color==Chesspoint.white){
                    bx[i][j]=2;
                }
            }
        }

        // 这两个循环遍历整个存储棋子的数组
        for (int r = 0; r < bx.length; r++) {
            for (int c = 0; c < bx[r].length; c++) {
                if (bx[r][c] == 0) {// 判断是否是空位
                    int ch = 0;// 存储第一次出现棋子的变量
                    String chessCode = "0";// 存储统计棋子相连情况的变量
                    /*
                     * 横向向左 左面的棋子在chessCode为左方， 右面的棋子在chessCode为右方
                     */
                    for (int c1 = c - 1; c1 >= 0; c1--) {
                        if (bx[r][c1] == 0) {// 判断是否是空位
                            if (c1 + 1 == c) {// 判断是否是相邻的
                                break;
                            } else {// 判断是否不是相邻的
                                chessCode = bx[r][c1] + chessCode;// 记录棋子相连的情况
                                break;
                            }
                        } else {// 判断是否是棋子
                            if (ch == 0) {// 判断是否是第一次出现棋子
                                chessCode = bx[r][c1] + chessCode;// 记录棋子相连的情况
                                ch = bx[r][c1];// 存储第一次的棋子
                            } else if (ch == bx[r][c1]) {// 判断是否是一样颜色的棋子
                                chessCode = bx[r][c1] + chessCode;// 记录棋子相连的情况
                            } else {
                                chessCode = bx[r][c1] + chessCode;// 记录棋子相连的情况
                                break;
                            }
                        }
                    }
                    // 根据棋子相连的情况，获取HashMap中存储的权值
                    Integer weight = map.get(chessCode);
                    if(null==weight){
                        //System.out.println(" get one null.....");
                    }
                    else{
                        int a;
                        a=weight.intValue();
                        // 存储入到权值数组中
                        weightArray[r][c] += a;
                    }

                    ch = 0;// 重置到初始状态
                    chessCode = "0";// 重置到初始状态

                    /*
                     * 横向向右 左面的棋子在chessCode为左方， 右面的棋子在chessCode为右方
                     */
                    for (int c2 = c + 1; c2 <= bx.length-2; c2++) {// 判断是是空位
                        if (bx[r][c2] == 0) {// 判断空位相邻的
                            if (c2 - 1 == c) {
                                break;
                            } else {// 判断空位是不相邻的
                                chessCode = chessCode + bx[r][c2];// 记录棋子相连的情况
                            }
                        }
                        // 判断是棋子
                        else {// 判断是第一次出现棋子
                            if (ch == 0) {
                                chessCode = chessCode + bx[r][c2];// 记录棋子相连的情况
                                ch = bx[r][c2];// 储存第一次出现的棋子
                            } else if (ch == bx[r][c2])// 判断棋子的颜色一样
                            {
                                chessCode = chessCode + bx[r][c2];
                            }// 记录棋子的连接情况
                            else {// 相邻棋子颜色不一样
                                chessCode = chessCode + bx[r][c2];
                                break;
                            }
                        }
                        weight = map.get(chessCode);
                        if(null==weight){
                            //System.out.println(" get one null.....");
                        }
                        else{
                            int a;
                            a=weight.intValue();
                            // 存储入到权值数组中
                            weightArray[r][c] += a;
                        }

                        ch = 0;// 重置到初始状态
                        chessCode = "0";// 重置到初始状态
                    }

                    /*
                     * 竖向向上 上面的棋子在chessCode为左方， 下面的棋子在chessCode为右方
                     */
                    for (int r1 = r - 1; r1 >= 0; r1--) {
                        if (bx[r1][c] == 0) {// 判断是否是空位
                            if (r1 + 1 == r) {// 判断是否是相邻的
                                break;
                            } else {// 判断是否不是相邻的
                                chessCode = bx[r1][c] + chessCode;// 记录棋子相连的情况
                                break;
                            }
                        } else {// 判断是否是棋子
                            if (ch == 0) {// 判断是否是第一次出现棋子
                                chessCode = bx[r1][c] + chessCode;// 记录棋子相连的情况
                                ch = bx[r1][c];// 存储第一次的棋子
                            } else if (ch == bx[r1][c]) {// 判断是否是一样颜色的棋子
                                chessCode = bx[r1][c] + chessCode;// 记录棋子相连的情况
                            } else {
                                chessCode = bx[r1][c] + chessCode;// 记录棋子相连的情况
                                break;
                            }
                        }
                    }
                    // 根据棋子相连的情况，获取HashMap中存储的权值
                    weight = map.get(chessCode);
                    if(null==weight){
                        //System.out.println(" get one null.....");
                    }
                    else{
                        int a;
                        a=weight.intValue();
                        // 存储入到权值数组中
                        weightArray[r][c] += a;
                    }

                    ch = 0;// 重置到初始状态
                    chessCode = "0";// 重置到初始状态

                    /*
                     * 竖向向下 上面的棋子在chessCode为左方， 下面的棋子在chessCode为右方
                     */
                    for (int r2 = r + 1; r2 <= bx.length-2; r2++) {// 判断是是空位
                        if (bx[r2][c] == 0) {// 判断空位相邻的
                            if (r2 - 1 == r) {
                                break;
                            } else {// 判断空位是不相邻的
                                chessCode = chessCode + bx[r2][c];// 记录棋子相连的情况
                            }
                        }
                        // 判断是棋子
                        else {// 判断是第一次出现棋子
                            if (ch == 0) {
                                chessCode = chessCode + bx[r][c];// 记录棋子相连的情况
                                ch = bx[r2][c];// 储存第一次出现的棋子
                            } else if (ch == bx[r2][c])// 判断棋子的颜色一样
                            {
                                chessCode = chessCode + bx[r2][c];
                            }// 记录棋子的连接情况
                            else {// 相邻棋子颜色不一样
                                chessCode = chessCode + bx[r2][c];
                                break;
                            }
                        }
                        weight = map.get(chessCode);
                        if(null==weight){
                            //System.out.println(" get one null.....");
                        }
                        else{
                            int a;
                            a=weight.intValue();
                            // 存储入到权值数组中
                            weightArray[r][c] += a;
                        }

                        ch = 0;// 重置到初始状态
                        chessCode = "0";// 重置到初始状态
                    }

                    /*
                     * 左斜向上 左上面的棋子在chessCode为左方， 右下面的棋子在chessCode为右方
                     */
                    for (int r1 = r - 1, c1 = c - 1; c1 >= 0 && r1 >= 0; c1--, r1--) {
                        if (bx[r1][c1] == 0) {// 判断是否是空位
                            if (c1 + 1 == c && r1 + 1 == c) {// 判断是否是相邻的
                                break;
                            } else {// 判断是否不是相邻的
                                chessCode = bx[r1][c1] + chessCode;// 记录棋子相连的情况
                                break;
                            }
                        } else {// 判断是否是棋子
                            if (ch == 0) {// 判断是否是第一次出现棋子
                                chessCode = bx[r1][c1] + chessCode;// 记录棋子相连的情况
                                ch = bx[r1][c1];// 存储第一次的棋子
                            } else if (ch == bx[r1][c1]) {// 判断是否是一样颜色的棋子
                                chessCode = bx[r1][c1] + chessCode;// 记录棋子相连的情况
                            } else {
                                chessCode = bx[r1][c1] + chessCode;// 记录棋子相连的情况
                                break;
                            }
                        }
                    }
                    // 根据棋子相连的情况，获取HashMap中存储的权值
                    weight = map.get(chessCode);
                    if(null==weight){
                        //System.out.println(" get one null.....");
                    }
                    else{
                        int a;
                        a=weight.intValue();
                        // 存储入到权值数组中
                        weightArray[r][c] += a;
                    }

                    ch = 0;// 重置到初始状态
                    chessCode = "0";// 重置到初始状态

                    /*
                     * 左斜向下 左上面的棋子在chessCode为左方， 右下面的棋子在chessCode为右方
                     */
                    for (int r1 = r + 1, c1 = c + 1; c1 <= bx.length-2 && r1 <= bx.length-2; c1++, r1++) {
                        if (bx[r1][c1] == 0) {// 判断是否是空位
                            if (c1 - 1 == c && r1 - 1 == c) {// 判断是否是相邻的
                                break;
                            } else {// 判断是否不是相邻的
                                chessCode = chessCode + bx[r1][c1];// 记录棋子相连的情况
                                break;
                            }
                        } else {// 判断是否是棋子
                            if (ch == 0) {// 判断是否是第一次出现棋子
                                chessCode = chessCode + bx[r1][c1];// 记录棋子相连的情况
                                ch = bx[r1][c1];// 存储第一次的棋子
                            } else if (ch == bx[r1][c1]) {// 判断是否是一样颜色的棋子
                                chessCode = chessCode + bx[r1][c1];// 记录棋子相连的情况
                            } else {
                                chessCode = chessCode + bx[r1][c1];// 记录棋子相连的情况
                                break;
                            }
                        }
                    }

                    /*
                     * 右斜向上 左下面的棋子在chessCode为左方， 右上面的棋子在chessCode为右方
                     */
                    for (int r1 = r - 1, c1 = c + 1; c1 <= bx.length-2 && r1 >= 0; c1++, r1--) // 行r，列c
                    {
                        if (bx[r1][c1] == 0) {// 判断是否是空位
                            if (c1 - 1 == c && r1 + 1 == c) {// 判断是否是相邻的
                                break;
                            } else {// 判断是否不是相邻的
                                chessCode = chessCode + bx[r1][c1];// 记录棋子相连的情况
                                break;
                            }
                        } else {// 判断是否是棋子
                            if (ch == 0) {// 判断是否是第一次出现棋子
                                chessCode = chessCode + bx[r1][c1];// 记录棋子相连的情况
                                ch = bx[r1][c1];// 存储第一次的棋子
                            } else if (ch == bx[r1][c1]) {// 判断是否是一样颜色的棋子
                                chessCode = chessCode + bx[r1][c1];// 记录棋子相连的情况
                            } else {
                                chessCode = chessCode + bx[r1][c1];// 记录棋子相连的情况
                                break;
                            }
                        }

                        weight = map.get(chessCode);
                        if(null==weight){
                            //System.out.println(" get one null.....");
                        }
                        else{
                            int a;
                            a=weight.intValue();
                            // 存储入到权值数组中
                            weightArray[r][c] += a;
                        }

                        ch = 0;// 重置到初始状态
                        chessCode = "0";// 重置到初始状态
                    }

                    /*
                     * 右斜向下 左下面的棋子在chessCode为左方， 右上面的棋子在chessCode为右方
                     */
                    for (int r1 = r + 1, c1 = c - 1; c1 >= 0 && r1 <= bx.length-2; c1--, r1++) // 行r，列c
                    {
                        if (bx[r1][c1] == 0) {// 判断是否是空位
                            if (c1 + 1 == c && r1 - 1 == c) {// 判断是否是相邻的
                                break;
                            } else {// 判断是否不是相邻的
                                chessCode = bx[r1][c1] + chessCode;// 记录棋子相连的情况
                                break;
                            }
                        } else {// 判断是否是棋子
                            if (ch == 0) {// 判断是否是第一次出现棋子
                                chessCode = bx[r1][c1] + chessCode;// 记录棋子相连的情况
                                ch = bx[r1][c1];// 存储第一次的棋子
                            } else if (ch == bx[r1][c1]) {// 判断是否是一样颜色的棋子
                                chessCode = bx[r1][c1] + chessCode;// 记录棋子相连的情况
                            } else {
                                chessCode = bx[r1][c1] + chessCode;// 记录棋子相连的情况
                                break;
                            }
                        }
                    }
                    // 根据棋子相连的情况，获取HashMap中存储的权值
                    weight = map.get(chessCode);
                    if(null==weight){
                        //System.out.println(" get one null.....");
                    }
                    else{
                        int a;
                        a=weight.intValue();
                        // 存储入到权值数组中
                        weightArray[r][c] += a;
                    }

                    ch = 0;// 重置到初始状态
                    chessCode = "0";// 重置到初始状态
                }
            }
        }
    }
    public Chesspoint AIcount(int color) {// 电脑统计应该下棋的坐标
        int max = -1;// 储存权值的最大值
        int rx = 0;// 储存最大值处的行坐标
        int cx = 0;// 储存最大值处的列坐标
        int k = 0;
        // 最大值的坐标为x1,y1
        for (int r = 0; r < weightArray.length; r++) {
            for (int c = 0; c < weightArray.length; c++) {
                if (weightArray[r][c] > max) {
                    System.out.println("weightArray=" + weightArray[r][c]);
                    max = weightArray[r][c];
                    rx = r;
                    cx = c;
                } else {
                    continue;
                }
            }
        }
        return new Chesspoint(rx,cx,color);
    }
}
