package org.sv.go;

import java.util.HashMap;

public class SecondAI implements AI{
    public static int[][] bx;
    public static int[][] weightArray;
    HashMap<String,Integer> map = new HashMap<>();
    public SecondAI(){
        // ��һ��
        map.put("010", 20);
        map.put("020", 20);
        map.put("01", 20);
        map.put("02", 20);
        map.put("10", 20);
        map.put("20", 20);
        // �����
        map.put("0110", 100);
        map.put("0220", 100);
        map.put("011", 100);
        map.put("110", 100);
        map.put("022", 100);
        map.put("220", 100);
        // ������
        map.put("01110", 500);
        map.put("02220", 500);
        map.put("1110", 500);
        map.put("0111", 500);
        map.put("2220", 500);
        map.put("0222", 500);
        // ������
        map.put("011110", 10000);
        map.put("022220", 10000);
        map.put("01111", 10000);
        map.put("11110", 10000);
        map.put("02222", 10000);
        map.put("22220", 10000);
        // ��һ��
        map.put("012", 10);
        map.put("021", 10);
        map.put("120", 10);
        map.put("210", 10);
        // ������
        map.put("0112", 70);
        map.put("0221", 70);
        map.put("2110", 70);
        map.put("1220", 70);
        // ������
        map.put("01112", 200);
        map.put("02221", 200);
        map.put("21110", 200);
        map.put("12220", 200);
        // ������
        map.put("011112", 10000);
        map.put("022221", 10000);
        map.put("211110", 10000);
        map.put("122220", 10000);
    }
    @Override
    public Chesspoint move(ChessMap chessMap,int color) {
        bx=new int[chessMap.gridNum][chessMap.gridNum];
        weightArray=new int[chessMap.gridNum][chessMap.gridNum];
        // ���Ȩֵ����
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

        // ������ѭ�����������洢���ӵ�����
        for (int r = 0; r < bx.length; r++) {
            for (int c = 0; c < bx[r].length; c++) {
                if (bx[r][c] == 0) {// �ж��Ƿ��ǿ�λ
                    int ch = 0;// �洢��һ�γ������ӵı���
                    String chessCode = "0";// �洢ͳ��������������ı���
                    /*
                     * �������� �����������chessCodeΪ�󷽣� �����������chessCodeΪ�ҷ�
                     */
                    for (int c1 = c - 1; c1 >= 0; c1--) {
                        if (bx[r][c1] == 0) {// �ж��Ƿ��ǿ�λ
                            if (c1 + 1 == c) {// �ж��Ƿ������ڵ�
                                break;
                            } else {// �ж��Ƿ������ڵ�
                                chessCode = bx[r][c1] + chessCode;// ��¼�������������
                                break;
                            }
                        } else {// �ж��Ƿ�������
                            if (ch == 0) {// �ж��Ƿ��ǵ�һ�γ�������
                                chessCode = bx[r][c1] + chessCode;// ��¼�������������
                                ch = bx[r][c1];// �洢��һ�ε�����
                            } else if (ch == bx[r][c1]) {// �ж��Ƿ���һ����ɫ������
                                chessCode = bx[r][c1] + chessCode;// ��¼�������������
                            } else {
                                chessCode = bx[r][c1] + chessCode;// ��¼�������������
                                break;
                            }
                        }
                    }
                    // ���������������������ȡHashMap�д洢��Ȩֵ
                    Integer weight = map.get(chessCode);
                    if(null==weight){
                        //System.out.println(" get one null.....");
                    }
                    else{
                        int a;
                        a=weight.intValue();
                        // �洢�뵽Ȩֵ������
                        weightArray[r][c] += a;
                    }

                    ch = 0;// ���õ���ʼ״̬
                    chessCode = "0";// ���õ���ʼ״̬

                    /*
                     * �������� �����������chessCodeΪ�󷽣� �����������chessCodeΪ�ҷ�
                     */
                    for (int c2 = c + 1; c2 <= bx.length-2; c2++) {// �ж����ǿ�λ
                        if (bx[r][c2] == 0) {// �жϿ�λ���ڵ�
                            if (c2 - 1 == c) {
                                break;
                            } else {// �жϿ�λ�ǲ����ڵ�
                                chessCode = chessCode + bx[r][c2];// ��¼�������������
                            }
                        }
                        // �ж�������
                        else {// �ж��ǵ�һ�γ�������
                            if (ch == 0) {
                                chessCode = chessCode + bx[r][c2];// ��¼�������������
                                ch = bx[r][c2];// �����һ�γ��ֵ�����
                            } else if (ch == bx[r][c2])// �ж����ӵ���ɫһ��
                            {
                                chessCode = chessCode + bx[r][c2];
                            }// ��¼���ӵ��������
                            else {// ����������ɫ��һ��
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
                            // �洢�뵽Ȩֵ������
                            weightArray[r][c] += a;
                        }

                        ch = 0;// ���õ���ʼ״̬
                        chessCode = "0";// ���õ���ʼ״̬
                    }

                    /*
                     * �������� �����������chessCodeΪ�󷽣� �����������chessCodeΪ�ҷ�
                     */
                    for (int r1 = r - 1; r1 >= 0; r1--) {
                        if (bx[r1][c] == 0) {// �ж��Ƿ��ǿ�λ
                            if (r1 + 1 == r) {// �ж��Ƿ������ڵ�
                                break;
                            } else {// �ж��Ƿ������ڵ�
                                chessCode = bx[r1][c] + chessCode;// ��¼�������������
                                break;
                            }
                        } else {// �ж��Ƿ�������
                            if (ch == 0) {// �ж��Ƿ��ǵ�һ�γ�������
                                chessCode = bx[r1][c] + chessCode;// ��¼�������������
                                ch = bx[r1][c];// �洢��һ�ε�����
                            } else if (ch == bx[r1][c]) {// �ж��Ƿ���һ����ɫ������
                                chessCode = bx[r1][c] + chessCode;// ��¼�������������
                            } else {
                                chessCode = bx[r1][c] + chessCode;// ��¼�������������
                                break;
                            }
                        }
                    }
                    // ���������������������ȡHashMap�д洢��Ȩֵ
                    weight = map.get(chessCode);
                    if(null==weight){
                        //System.out.println(" get one null.....");
                    }
                    else{
                        int a;
                        a=weight.intValue();
                        // �洢�뵽Ȩֵ������
                        weightArray[r][c] += a;
                    }

                    ch = 0;// ���õ���ʼ״̬
                    chessCode = "0";// ���õ���ʼ״̬

                    /*
                     * �������� �����������chessCodeΪ�󷽣� �����������chessCodeΪ�ҷ�
                     */
                    for (int r2 = r + 1; r2 <= bx.length-2; r2++) {// �ж����ǿ�λ
                        if (bx[r2][c] == 0) {// �жϿ�λ���ڵ�
                            if (r2 - 1 == r) {
                                break;
                            } else {// �жϿ�λ�ǲ����ڵ�
                                chessCode = chessCode + bx[r2][c];// ��¼�������������
                            }
                        }
                        // �ж�������
                        else {// �ж��ǵ�һ�γ�������
                            if (ch == 0) {
                                chessCode = chessCode + bx[r][c];// ��¼�������������
                                ch = bx[r2][c];// �����һ�γ��ֵ�����
                            } else if (ch == bx[r2][c])// �ж����ӵ���ɫһ��
                            {
                                chessCode = chessCode + bx[r2][c];
                            }// ��¼���ӵ��������
                            else {// ����������ɫ��һ��
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
                            // �洢�뵽Ȩֵ������
                            weightArray[r][c] += a;
                        }

                        ch = 0;// ���õ���ʼ״̬
                        chessCode = "0";// ���õ���ʼ״̬
                    }

                    /*
                     * ��б���� �������������chessCodeΪ�󷽣� �������������chessCodeΪ�ҷ�
                     */
                    for (int r1 = r - 1, c1 = c - 1; c1 >= 0 && r1 >= 0; c1--, r1--) {
                        if (bx[r1][c1] == 0) {// �ж��Ƿ��ǿ�λ
                            if (c1 + 1 == c && r1 + 1 == c) {// �ж��Ƿ������ڵ�
                                break;
                            } else {// �ж��Ƿ������ڵ�
                                chessCode = bx[r1][c1] + chessCode;// ��¼�������������
                                break;
                            }
                        } else {// �ж��Ƿ�������
                            if (ch == 0) {// �ж��Ƿ��ǵ�һ�γ�������
                                chessCode = bx[r1][c1] + chessCode;// ��¼�������������
                                ch = bx[r1][c1];// �洢��һ�ε�����
                            } else if (ch == bx[r1][c1]) {// �ж��Ƿ���һ����ɫ������
                                chessCode = bx[r1][c1] + chessCode;// ��¼�������������
                            } else {
                                chessCode = bx[r1][c1] + chessCode;// ��¼�������������
                                break;
                            }
                        }
                    }
                    // ���������������������ȡHashMap�д洢��Ȩֵ
                    weight = map.get(chessCode);
                    if(null==weight){
                        //System.out.println(" get one null.....");
                    }
                    else{
                        int a;
                        a=weight.intValue();
                        // �洢�뵽Ȩֵ������
                        weightArray[r][c] += a;
                    }

                    ch = 0;// ���õ���ʼ״̬
                    chessCode = "0";// ���õ���ʼ״̬

                    /*
                     * ��б���� �������������chessCodeΪ�󷽣� �������������chessCodeΪ�ҷ�
                     */
                    for (int r1 = r + 1, c1 = c + 1; c1 <= bx.length-2 && r1 <= bx.length-2; c1++, r1++) {
                        if (bx[r1][c1] == 0) {// �ж��Ƿ��ǿ�λ
                            if (c1 - 1 == c && r1 - 1 == c) {// �ж��Ƿ������ڵ�
                                break;
                            } else {// �ж��Ƿ������ڵ�
                                chessCode = chessCode + bx[r1][c1];// ��¼�������������
                                break;
                            }
                        } else {// �ж��Ƿ�������
                            if (ch == 0) {// �ж��Ƿ��ǵ�һ�γ�������
                                chessCode = chessCode + bx[r1][c1];// ��¼�������������
                                ch = bx[r1][c1];// �洢��һ�ε�����
                            } else if (ch == bx[r1][c1]) {// �ж��Ƿ���һ����ɫ������
                                chessCode = chessCode + bx[r1][c1];// ��¼�������������
                            } else {
                                chessCode = chessCode + bx[r1][c1];// ��¼�������������
                                break;
                            }
                        }
                    }

                    /*
                     * ��б���� �������������chessCodeΪ�󷽣� �������������chessCodeΪ�ҷ�
                     */
                    for (int r1 = r - 1, c1 = c + 1; c1 <= bx.length-2 && r1 >= 0; c1++, r1--) // ��r����c
                    {
                        if (bx[r1][c1] == 0) {// �ж��Ƿ��ǿ�λ
                            if (c1 - 1 == c && r1 + 1 == c) {// �ж��Ƿ������ڵ�
                                break;
                            } else {// �ж��Ƿ������ڵ�
                                chessCode = chessCode + bx[r1][c1];// ��¼�������������
                                break;
                            }
                        } else {// �ж��Ƿ�������
                            if (ch == 0) {// �ж��Ƿ��ǵ�һ�γ�������
                                chessCode = chessCode + bx[r1][c1];// ��¼�������������
                                ch = bx[r1][c1];// �洢��һ�ε�����
                            } else if (ch == bx[r1][c1]) {// �ж��Ƿ���һ����ɫ������
                                chessCode = chessCode + bx[r1][c1];// ��¼�������������
                            } else {
                                chessCode = chessCode + bx[r1][c1];// ��¼�������������
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
                            // �洢�뵽Ȩֵ������
                            weightArray[r][c] += a;
                        }

                        ch = 0;// ���õ���ʼ״̬
                        chessCode = "0";// ���õ���ʼ״̬
                    }

                    /*
                     * ��б���� �������������chessCodeΪ�󷽣� �������������chessCodeΪ�ҷ�
                     */
                    for (int r1 = r + 1, c1 = c - 1; c1 >= 0 && r1 <= bx.length-2; c1--, r1++) // ��r����c
                    {
                        if (bx[r1][c1] == 0) {// �ж��Ƿ��ǿ�λ
                            if (c1 + 1 == c && r1 - 1 == c) {// �ж��Ƿ������ڵ�
                                break;
                            } else {// �ж��Ƿ������ڵ�
                                chessCode = bx[r1][c1] + chessCode;// ��¼�������������
                                break;
                            }
                        } else {// �ж��Ƿ�������
                            if (ch == 0) {// �ж��Ƿ��ǵ�һ�γ�������
                                chessCode = bx[r1][c1] + chessCode;// ��¼�������������
                                ch = bx[r1][c1];// �洢��һ�ε�����
                            } else if (ch == bx[r1][c1]) {// �ж��Ƿ���һ����ɫ������
                                chessCode = bx[r1][c1] + chessCode;// ��¼�������������
                            } else {
                                chessCode = bx[r1][c1] + chessCode;// ��¼�������������
                                break;
                            }
                        }
                    }
                    // ���������������������ȡHashMap�д洢��Ȩֵ
                    weight = map.get(chessCode);
                    if(null==weight){
                        //System.out.println(" get one null.....");
                    }
                    else{
                        int a;
                        a=weight.intValue();
                        // �洢�뵽Ȩֵ������
                        weightArray[r][c] += a;
                    }

                    ch = 0;// ���õ���ʼ״̬
                    chessCode = "0";// ���õ���ʼ״̬
                }
            }
        }
    }
    public Chesspoint AIcount(int color) {// ����ͳ��Ӧ�����������
        int max = -1;// ����Ȩֵ�����ֵ
        int rx = 0;// �������ֵ����������
        int cx = 0;// �������ֵ����������
        int k = 0;
        // ���ֵ������Ϊx1,y1
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
