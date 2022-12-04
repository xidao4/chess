package org.sv.go;


class Chesspoint {
    public static int black = 0, white = 1;
    int x, y;
    int color;//������ʶ�������ʲô��ɫ��.

    public Chesspoint(int i, int j, int c)//����Chesspoint������������3������i,j,c
    {
        x = i;
        y = j;
        color = c;
    }

    public String toString()//����x,y��λ�ú���ɫ
    {
        String c = (color == black ? "black" : "white");
        return "[" + x + "," + y + "]:" + c;
    }
}
