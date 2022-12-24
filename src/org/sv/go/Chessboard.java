package org.sv.go;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;



public class Chessboard extends JPanel {
    //  默认的棋盘方格长度及数目
    public static final int defaultGridLen = 27, defaultGridNum = 15;  //52   8       22 19

    /**利用Vector保存所有已下的棋子,包括在棋盘上的所有棋子和被踢掉的，
     **若某一次落子没有造成踢子，包括所有被这个棋子提掉的棋子及这个棋子本身.
     **Vector 类可以实现可增长的对象数组。与数组一样，它包含可以使用整数索引进行访问的组件。
     **Vector 的大小可以根据需要增大或缩小，以适应创建 Vector 后进行添加或移除项的操作。
     */
    private ChessMap chessMap;
//    private Vector chessMap.chessman;
//    private int chessMap.alreadyNum;             // 已下数目
//    private int chessMap.currentTurn;            // 轮到谁下
//    private int chessMap.gridNum, chessMap.gridLen;       // 方格长度及数目 9 46，13 32，19 22
//    private int chessMap.chessmanLength;         // 棋子的直径 41，28，19
//    private Chesspoint[][] chessMap.map;         // 在棋盘上的所有棋子  及以上
    private Image offScreen;            //用来绘制棋盘
    private Graphics offGrid;           //用来绘制方格和棋子
    private int size;                   // 棋盘的宽度及高度 444
    private int top = 13, left = 22;    // 棋盘上边及左边的边距
    //Point类表示 (x,y) 坐标空间中的位置的点，以整数精度指定。
    private Point mouseClick;           // 鼠标的位置，即map数组中的下标 null
    private ControlPanel controlPanel;  // 控制面板
    private Strategy strategy=new FiveStrategy();
    private MementoCaretaker mementoCaretaker;

    private Account account1=new Account();
    private Account account2=new Account();

    private int mode; //0 人人 1 人机 2 机机
    private FirstAI firstai;
    private SecondAI secondai;

    //获得控制板的距离
    public int getWidth() {
        return size + controlPanel.getWidth() + 35;
    }

    public int getHeight() {
        return size;
    }

    //绘制棋盘外观
    public Chessboard() {

        try{
            File accountsFile=new File("players.txt");
            InputStreamReader reader=new InputStreamReader(new FileInputStream(accountsFile));
            BufferedReader br=new BufferedReader(reader);

            String[] info1=br.readLine().split(" ");
            if(!info1[0].equals("ai")){
                account1=new Account(info1[0],info1[1],Integer.parseInt(info1[2]),Integer.parseInt(info1[3]));
            }else{
                account1=new Account("ai");
            }
            String[] info2=br.readLine().split(" ");
            if(!info2[0].equals("ai")){
                account2=new Account(info2[0],info2[1],Integer.parseInt(info2[2]),Integer.parseInt(info2[3]));
            }else{
                account2=new Account("ai");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        chessMap=new ChessMap();
        chessMap.gridNum = defaultGridNum;                       //方格数目为19
        chessMap.gridLen = defaultGridLen;                       //方格长度为22
        chessMap.chessmanLength = chessMap.gridLen * 9 / 10;              //棋子直径为22*9/10
        size = 2 * left + chessMap.gridNum * chessMap.gridLen;            //正方形棋盘边长为2*13+19*22
        addMouseListener(new PlayChess());              //注册鼠标监听器,监听鼠标按下事件
        addMouseMotionListener(new mousePosition());    //注册鼠标监听器,监听鼠标移动事件
        setLayout(new BorderLayout());                  //设置布局模式
        controlPanel = new ControlPanel();  //创建控制面板
        add(controlPanel, "West");            //添加"控制面板",为"西"
        setSize(getWidth(), size);                      //设置宽度和大小
        mementoCaretaker=new MementoCaretaker();
        strategy.startGame();                                    //开始游戏

        controlPanel.setLabel();
    }

    public void getPlayersInfo(){
        try{
            File accountsFile=new File("players.txt");
            InputStreamReader reader=new InputStreamReader(new FileInputStream(accountsFile));
            BufferedReader br=new BufferedReader(reader);

            String[] info1=br.readLine().split(" ");
            if(!info1[0].equals("ai")){
                account1=new Account(info1[0],info1[1],Integer.parseInt(info1[2]),Integer.parseInt(info1[3]));
            }else{
                account1=new Account("ai"); //ai vs ai
                this.mode=2;
                firstai=new FirstAI();
                secondai=new SecondAI();
            }
            String[] info2=br.readLine().split(" ");
            if(!info2[0].equals("ai")){
                account2=new Account(info2[0],info2[1],Integer.parseInt(info2[2]),Integer.parseInt(info2[3]));
                this.mode=0;  //human vs human
            }else{
                account2=new Account("ai");
                if(this.mode!=2){  //human vs ai
                    this.mode=1;
                    secondai=new SecondAI();
                    //controlPanel.
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controlPanel.setLabel();

        controlPanel.addPlayersInfo();

//        if(mode==2){
//            new FiveStrategy().aivsai();
//        }
    }



    public void addNotify() {
        //创建按钮的同位体。按钮的同位体允许应用程序更改按钮的外观。而不更改其功能。
        super.addNotify();
        //创建一幅用于双缓冲的可在屏幕外绘制的图象
        offScreen = createImage(size, size);
        //为offScreen组件创建图形的上下文
        offGrid = offScreen.getGraphics();
    }
    public void paint(Graphics g) {
        //将颜色选取器的当前颜色设置为指定的 RGB 颜色。即设置画笔颜色
        offGrid.setColor(new Color(180, 150, 100));
        offGrid.fillRect(0, 0, size, size);

        /***画出棋盘格子***/
        //设置画笔颜色为黑色
        offGrid.setColor(Color.black);
        for (int i = 0; i < chessMap.gridNum+1 ; i++) {
            int x1 = left + i * chessMap.gridLen+5;            //13+i*22
            int x2 = x1;
            int y1 = top;                           //top=13(前面已定义)
            int y2 = top + chessMap.gridNum * chessMap.gridLen;       //13+i*22
            //画竖线，在画布中心绘制直线（使用当前画笔颜色在（x1,y1)和（x2,y2)间画一条线段
            offGrid.drawLine(x1, y1, x2, y2);

            x1 = left+5;
            x2 = left + chessMap.gridNum * chessMap.gridLen+5;
            y1 = top + i * chessMap.gridLen;
            y2 = y1;
            //画横线，在画布中心绘制直线（使用当前画笔颜色在（x1,y1)和（x2,y2)间画一条线段
            offGrid.drawLine(x1, y1, x2, y2);
        }
        /***这是通过画线的方式将棋盘绘制出来***/


        /***画出棋子***/
        for (int i = 0; i < chessMap.gridNum + 1; i++) {
            for (int j = 0; j < chessMap.gridNum + 1; j++) {
                //前面定义Chesspoint[][] chessMap.map;即在棋盘上的所有棋子
                if (chessMap.map[i][j] == null)
                    continue;
                //给棋子设置相应的颜色
                offGrid.setColor(chessMap.map[i][j].color == Chesspoint.black ? Color.black : Color.white);
                //在指定区域绘制圆形
                offGrid.fillOval(left + i * chessMap.gridLen - chessMap.chessmanLength / 2,
                        top + j * chessMap.gridLen - chessMap.chessmanLength / 2, chessMap.chessmanLength, chessMap.chessmanLength);
            }
        }

        /***画出鼠标的位置，即下一步将要下的位置***/
        if (mouseClick != null) {
            //设置画笔颜色
            /***应该是他少减一个数着具体的也没改好,这先用-3*defaultGridLen***/
            offGrid.setColor(chessMap.currentTurn == Chesspoint.black ? Color.gray : new Color(200, 200, 250));
            //使用当前颜色填充外接指定矩形框的椭圆。
            offGrid.fillOval(left + mouseClick.x * chessMap.gridLen - chessMap.chessmanLength / 2,
                    top + mouseClick.y * chessMap.gridLen - chessMap.chessmanLength / 2, chessMap.chessmanLength, chessMap.chessmanLength);
        }
        //把画面一次性画出
        g.drawImage(offScreen, 80, 0, this);
    }
    // 更新棋盘
    public void update(Graphics g) {
        paint(g);//绘制
    }

    /***下棋子,这是对鼠标按下事件的处理类,是内部类***/
    class PlayChess extends MouseAdapter { // 放一颗棋子
        //鼠标按键在组件上按下时调用
        public void mousePressed(MouseEvent evt) {
            int xoff = left / 2;
            int yoff = top / 2;

            /***程序中的那个棋子与鼠标不对位的漏洞,很有可能是这里和下边鼠标事件的X坐标出现了问题
             应该是少减一个数,具体的也没改好,这先用-3*defaultGridLen,位置大概也正确了***/
            //getX()返回事件相对于源组件的水平 x 坐标。
            int x = (evt.getX() - xoff - 3 * defaultGridLen) / chessMap.gridLen;
            //getY()返回事件相对于源组件的水平 y 坐标。
            int y = (evt.getY() - yoff) / chessMap.gridLen;
            if (x < 0 || x > chessMap.gridNum || y < 0 || y > chessMap.gridNum)
                return;//返回空

            //在void函数中可以用一个不带值的return来结束程序
            if (chessMap.map[x][y] != null)
                return;

            /***清除多余的棋子***/
//            if (chessMap.alreadyNum < chessMap.chessman.size()) {
//                int size = chessMap.chessman.size();
//                for (int i = size - 1; i >= chessMap.alreadyNum; i--)
//                    chessMap.chessman.removeElementAt(i);//从此向量中移除i变量
//            }

            Chesspoint goPiece = new Chesspoint(x, y, chessMap.currentTurn);

            chessMap.map[x][y] = goPiece;
            //将棋子添加到chessman中
            //chessMap.chessman.addElement(goPiece);
            //已下棋子数目自加
            chessMap.alreadyNum++;

            if (chessMap.currentTurn == Chesspoint.black) {
                chessMap.currentTurn = Chesspoint.white;
            } else {
                chessMap.currentTurn = Chesspoint.black;
            }

            if(strategy.playChess(x,y,goPiece)){
                return;
            }

            mouseClick = null;
            // 更新控制面板
            controlPanel.setLabel();
            //更新标签
        }

        public void mouseExited(MouseEvent evt) {// 鼠标退出时，清除将要落子的位置
            mouseClick = null;
            repaint();//重绘
        }
    }

    private class Strategy{
        //棋局结束则返回TRUE，继续进行则返回FALSE
        public boolean playChess(int x, int y, Chesspoint goPiece){return false;}
        public void startGame(){
            //chessMap.chessman = new Vector(); //chessman定义为一种向量
            chessMap.alreadyNum = 0;//chessMap.alreadyNum 初始植为零
            chessMap.map = new Chesspoint[chessMap.gridNum + 1][chessMap.gridNum + 1];//map定义为一个二维数组,用来存放所有棋子
            chessMap.currentTurn = Chesspoint.black;//到黑子下
            controlPanel.setLabel();//更新控制面板的标签
            repaint();//重绘组件
        }
    }
    private class GoStrategy extends Strategy{
        public boolean playChess(int x, int y, Chesspoint goPiece){
            //***判断在[x,y]落子后，是否可以提掉对方的子
            take(x, y);
            //***判断是否挤死了自己，若是则已落的子无效
            if (allDead(goPiece).size() != 0) {
                chessMap.map[x][y] = null;
                repaint();//重绘此组件。
                controlPanel.setMsg("无效下棋");//控制面板提示"无效下棋"
                //***back***
                //chessMap.chessman.removeElement(goPiece);//移除棋子
                chessMap.alreadyNum--;//已下棋子数目自减
                if (chessMap.currentTurn == Chesspoint.black) {
                    chessMap.currentTurn = Chesspoint.white;//轮到白子下
                } else {
                    chessMap.currentTurn = Chesspoint.black;//轮到黑子下
                }
            }else{
                mementoCaretaker.goOn(chessMap);
            }
            return false;
        }
        public void take(int x, int y) {
            Chesspoint goPiece;
            if ((goPiece = chessMap.map[x][y]) == null) {
                return;
            }
            int color = goPiece.color;
            //取得棋子四周围的几个子
            Vector v = around(goPiece);
            for (int l = 0; l < v.size(); l++) {
                //elementAt()返回指定索引处的组件。
                Chesspoint q = (Chesspoint) (v.elementAt(l));
                if (q.color == color)
                    continue;
                //若颜色不同，取得和q连在一起的所有已死的子，
                //若没有已死的子则返回一个空的Vector
                Vector dead = allDead(q);
                //移去所有已死的子
                removeAll(dead);
                //如果踢子，则保存所有被踢掉的棋子
//                if (dead.size() != 0) {
//                    Object obj = chessMap.chessman.elementAt(chessMap.alreadyNum - 1);
//                    if (obj instanceof Chesspoint) {
//                        goPiece = (Chesspoint) (chessMap.chessman.elementAt(chessMap.alreadyNum - 1));
//                        dead.addElement(goPiece);
//                    } else {
//                        Vector vector = (Vector) obj;
//                        for (int i = 0; i < vector.size(); i++) {
//                            dead.addElement(vector.elementAt(i));
//                        }
//                    }
//                    // 更新Vector chessman中的第num个元素
//                    chessMap.chessman.setElementAt(dead, chessMap.alreadyNum - 1);
//                }
            }
            repaint();
        }
        //判断在[x,y]落子后，是否可以踢掉对方的子
        //以下这两个数组是用来定位一下棋子四周的坐标
        public int[] xdir = {0, 0, 1, -1};
        public int[] ydir = {1, -1, 0, 0};

        //判断棋子周围是否有空白
        public boolean aroundBlank(Chesspoint goPiece) {
            for (int l = 0; l < xdir.length; l++) {
                int x1 = goPiece.x + xdir[l];
                int y1 = goPiece.y + ydir[l];
                //xdir与ydir的取值是xdir={ 0, 0, 1, -1 }; ydir = { 1, -1, 0, 0 };也就是当前棋子的四周
                if (x1 < 0 || x1 > chessMap.gridNum || y1 < 0 || y1 > chessMap.gridNum)
                    continue;
                if (chessMap.map[x1][y1] == null)
                    return true;//当发现有空白时就返回一个TRUE
            }
            return false;
        }

        //取得棋子四周围的几个子
        public Vector around(Chesspoint goPiece) {
            Vector v = new Vector();
            for (int l = 0; l < xdir.length; l++) {
                int x1 = goPiece.x + xdir[l];
                int y1 = goPiece.y + ydir[l];
                //xdir与ydir的取值是xdir={ 0, 0, 1, -1 }; ydir = { 1, -1, 0, 0 };也就是当前棋子的四周
                if (x1 < 0 || x1 > chessMap.gridNum || y1 < 0 || y1 > chessMap.gridNum
                        || chessMap.map[x1][y1] == null)
                    continue;
                v.addElement(chessMap.map[x1][y1]);//将map[x1][y1]组件添加到此v的末尾。
            }
            return v;
        }

        //取得连在一起的所有已死的子
        public Vector allDead(Chesspoint q) {
            Vector v = new Vector();
            v.addElement(q);//将q组件添加到此v的末尾。
            int count = 0;
            //true时执行循环语句
            while (true) {
                int origsize = v.size();
                for (int i = count; i < origsize; i++) {
                    Chesspoint goPiece = (Chesspoint) (v.elementAt(i));
                    if (aroundBlank(goPiece))
                        return new Vector();
                    Vector around = around(goPiece);
                    for (int j = 0; j < around.size(); j++) {
                        Chesspoint a = (Chesspoint) (around.elementAt(j));
                        if (a.color != goPiece.color)
                            continue;
                        if (v.indexOf(a) < 0)//indexOf(a)，返回a中第一次出现处的索引。
                            v.addElement(a);//将a组件添加到此v的末尾。
                    }
                }
                if (origsize == v.size())
                    break;
                else
                    count = origsize;
            }
            return v;
        }

        // 从棋盘上移去中棋子
        public void removeAll(Vector v) {
            for (int i = 0; i < v.size(); i++) {
                Chesspoint q = (Chesspoint) (v.elementAt(i));//返回i处的组件赋给q。
                chessMap.map[q.x][q.y] = null;
            }
            repaint();//重绘此组件。
        }
    }
    private class FiveStrategy extends Strategy{
        private Chesspoint firstStep(){
            Chesspoint p1=firstai.move(chessMap,Chesspoint.black);
            repaint();
            return p1;
        }
        private Chesspoint aiStep(AI ai,int color){
            Chesspoint p=ai.move(chessMap,color);
            repaint();
            checkWin(p.x,p.y);
            return p;
        }
        public void aivsai()  {
            Chesspoint p1=firstai.move(chessMap,Chesspoint.black);
            repaint();

            Chesspoint p2;
            while(!checkWin(p1.x,p1.y)){
                int tmp=0;
                while(tmp<1147483647){
                    tmp++;
                }

                p2=secondai.move(chessMap,Chesspoint.white);
                repaint();
                if(checkWin(p2.x,p2.y)){
                    break;
                }

                tmp=0;
                while(tmp<100000){
                    tmp++;
                }

                p1=firstai.move(chessMap,Chesspoint.black);
                repaint();
            }


        }
        public boolean playChess(int x, int y, Chesspoint goPiece){
            mementoCaretaker.goOn(chessMap);
            if(checkWin(x,y)) return true;
            if(mode==1){
                Chesspoint cp=secondai.move(chessMap,Chesspoint.white);
                mementoCaretaker.goOn(chessMap);
                repaint();
                boolean ret= checkWin(cp.x,cp.y);
                //chessMap.currentTurn=Chesspoint.black;
                return ret;
            }
            return false;
        }
        private boolean checkWin(int x,int y){
            //当前行
            int minX=x-4;
            int maxX=x+4;
            if(minX<1) minX=1;
            if(maxX>Chessboard.this.chessMap.gridNum) maxX=Chessboard.this.chessMap.gridNum;
            int sum=0;
            for (int i=minX;i<=maxX;i++){
                if(Chessboard.this.chessMap.map[i][y]==null){
                    continue;
                }
                int piece=Chessboard.this.chessMap.map[i][y].color;
                if(piece==1-Chessboard.this.chessMap.currentTurn){
                    sum++;
                    if(sum==5) {
                        if (chessMap.currentTurn==1-Chesspoint.black){
                            controlPanel.setMsg("黑子获胜！");
                            if(!account1.getName().equals("ai")){
                                account1.setWinNum(account1.getWinNum()+1);
                            }
                            if(!account2.getName().equals("ai")){
                                account2.setLoseNum(account2.getLoseNum()+1);
                            }
                            JOptionPane.showMessageDialog(null,"黑子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                            strategy.startGame();
                        }else{
                            controlPanel.setMsg("白子获胜！");
                            JOptionPane.showMessageDialog(null,"白子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                            strategy.startGame();
                        }
                        return true;
                    }
                }else{
                    sum=0;
                }
            }
            //当前列
            int minY=y-4;
            int maxY = y+4;
            if (minY<0) minY=0;
            if(maxY>Chessboard.this.chessMap.gridNum) maxY=Chessboard.this.chessMap.gridNum;
            sum=0;
            for (int i=minY; i<=maxY; i++) {
                if(Chessboard.this.chessMap.map[x][i]==null){
                    continue;
                }
                int piece=Chessboard.this.chessMap.map[x][i].color;
                if(piece==1-Chessboard.this.chessMap.currentTurn){
                    sum++;
                    if(sum==5){
                        if (chessMap.currentTurn==1-Chesspoint.black){
                            controlPanel.setMsg("黑子获胜！");
                            if(!account1.getName().equals("ai")){
                                account1.setWinNum(account1.getWinNum()+1);
                            }
                            if(!account2.getName().equals("ai")){
                                account2.setLoseNum(account2.getLoseNum()+1);
                            }
                            JOptionPane.showMessageDialog(null,"黑子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                            strategy.startGame();

                        }else{
                            controlPanel.setMsg("白子获胜！");
                            JOptionPane.showMessageDialog(null,"白子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                            strategy.startGame();
                        }
                        return true;
                    }
                }else{
                    sum=0;
                }
            }
            //左斜
            sum=0;
            for(int i=-4;i<=4;i++){
                if(x+i>0 && x+i<=Chessboard.this.chessMap.gridNum && y+i>=0 && y+i<=Chessboard.this.chessMap.gridNum) {
                    if(Chessboard.this.chessMap.map[x+i][y+i]==null){
                        continue;
                    }
                    int piece=Chessboard.this.chessMap.map[x+i][y+i].color;
                    if(piece==1-Chessboard.this.chessMap.currentTurn){
                        sum++;
                        if(sum==5){
                            if (chessMap.currentTurn==1-Chesspoint.black){
                                controlPanel.setMsg("黑子获胜！");
                                if(!account1.getName().equals("ai")){
                                    account1.setWinNum(account1.getWinNum()+1);
                                }
                                if(!account2.getName().equals("ai")){
                                    account2.setLoseNum(account2.getLoseNum()+1);
                                }
                                JOptionPane.showMessageDialog(null,"黑子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                                strategy.startGame();
                            }else{
                                controlPanel.setMsg("白子获胜！");
                                JOptionPane.showMessageDialog(null,"白子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                                strategy.startGame();
                            }
                            return true;
                        }
                    }else{
                        sum=0;
                    }
                }
            }
            //右斜
            sum=0;
            for (int i=-4;i<=4;i++){
                if(x+i>0 && x+i<=Chessboard.this.chessMap.gridNum && y-i>=0 && y-i<=Chessboard.this.chessMap.gridNum){
                    if(Chessboard.this.chessMap.map[x+i][y-i]==null){
                        continue;
                    }
                    int piece=Chessboard.this.chessMap.map[x+i][y-i].color;
                    if(piece==1-Chessboard.this.chessMap.currentTurn){
                        sum++;
                        if(sum==5){
                            if (chessMap.currentTurn==1-Chesspoint.black){
                                controlPanel.setMsg("黑子获胜！");
                                if(!account1.getName().equals("ai")){
                                    account1.setWinNum(account1.getWinNum()+1);
                                }
                                if(!account2.getName().equals("ai")){
                                    account2.setLoseNum(account2.getLoseNum()+1);
                                }
                                JOptionPane.showMessageDialog(null,"黑子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                                strategy.startGame();
                            }else{
                                controlPanel.setMsg("白子获胜！");
                                JOptionPane.showMessageDialog(null,"白子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                                strategy.startGame();
                            }
                            return true;
                        }
                    }else{
                        sum=0;
                    }
                }
            }
            return false;
        }
    }
    private class BlackWhiteStrategy extends Strategy{
        public boolean playChess(int x, int y, Chesspoint goPiece){
            //先检查落子位置是否合法
            if(move(x,y,goPiece.color,false)){
                mementoCaretaker.goOn(chessMap);
                int otherColor;
                if(goPiece.color==Chesspoint.black){
                    otherColor=Chesspoint.white;
                }else{
                    otherColor=Chesspoint.black;
                }
                if(checkPass(otherColor)){
                    if(checkPass(goPiece.color)){
                        repaint();
                        whoWin();
                        return true;
                    }
                }
            }else{
                chessMap.map[x][y] = null;
                repaint();//重绘此组件。
                controlPanel.setMsg("无效下棋");//控制面板提示"无效下棋"
                //***back***
                //chessMap.chessman.removeElement(goPiece);//移除棋子
                chessMap.alreadyNum--;//已下棋子数目自减
                if (chessMap.currentTurn == Chesspoint.black) {
                    chessMap.currentTurn = Chesspoint.white;//轮到白子下
                } else {
                    chessMap.currentTurn = Chesspoint.black;//轮到黑子下
                }


                int otherColor;
                if(goPiece.color==Chesspoint.black){
                    otherColor=Chesspoint.white;
                }else{
                    otherColor=Chesspoint.black;
                }
                if(checkPass(otherColor)){
                    if(checkPass(goPiece.color)){
                        repaint();
                        whoWin();
                        return true;
                    }
                }


            }
            return false;
        }
        public void startGame(){
            //chessMap.chessman = new Vector(); //chessman定义为一种向量
            chessMap.alreadyNum = 0;//chessMap.alreadyNum 初始植为零
            chessMap.map = new Chesspoint[chessMap.gridNum + 1][chessMap.gridNum + 1];//map定义为一个二维数组,用来存放所有棋子

            int coordinate=Chessboard.this.chessMap.gridNum / 2;
            Chesspoint pb1= new Chesspoint(coordinate,coordinate,Chesspoint.black);
            //Chessboard.this.chessMap.chessman.addElement(pb1);
            chessMap.map[coordinate][coordinate]=pb1;
            Chesspoint pw1= new Chesspoint(coordinate,coordinate+1,Chesspoint.white);
            //Chessboard.this.chessMap.chessman.addElement(pw1);
            chessMap.map[coordinate][coordinate+1]=pw1;
            Chesspoint pb2= new Chesspoint(coordinate+1,coordinate+1,Chesspoint.black);
            //Chessboard.this.chessMap.chessman.addElement(pb2);
            chessMap.map[coordinate+1][coordinate+1]=pb2;
            Chesspoint pw2= new Chesspoint(coordinate+1,coordinate,Chesspoint.white);
            //Chessboard.this.chessMap.chessman.addElement(pw2);
            chessMap.map[coordinate+1][coordinate]=pw2;


            chessMap.currentTurn = Chesspoint.black;//到黑子下
            controlPanel.setLabel();//更新控制面板的标签
            controlPanel.setBlackWhiteSize();
            repaint();//重绘组件
        }
        public boolean checkPass(int color){
            for (int i=0;i<=chessMap.gridNum;i++){
                for(int j=0;j<=chessMap.gridNum;j++){
                    if (chessMap.map[i][j]==null&&move(i,j,color,true)){
                        return false;
                    }
                }
            }
            return true;
        }
        public boolean move(int i,int j, int color, boolean checkOnly){
            int[][] directions={{1, 0}, {1, 1}, {1, -1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};
            boolean moveSuccess=false;
            for(int[] dir : directions){
                boolean canmove=false;
                int x=j;
                int y=i;
                int directionX=dir[0];
                int directionY=dir[1];
                while(true){
                    x+=directionX;
                    y+=directionY;
                    if(x<0 || x>chessMap.gridNum || y<0 || y>chessMap.gridNum){
                        canmove=false;
                        break;
                    }
                    int otherColor;
                    if(color==Chesspoint.black){
                        otherColor=Chesspoint.white;
                    }else{
                        otherColor=Chesspoint.black;
                    }
                    if(Chessboard.this.chessMap.map[y][x]!=null && Chessboard.this.chessMap.map[y][x].color==otherColor){
                        canmove=true;
                    }else if(Chessboard.this.chessMap.map[y][x]!=null && Chessboard.this.chessMap.map[y][x].color==color){
                        break;
                    }else if(Chessboard.this.chessMap.map[y][x]!=null && Chessboard.this.chessMap.map[y][x].color==0){
                        canmove=false;
                        break;
                    }
                }
                if(canmove){
                    moveSuccess=true;
                    while(true&&!checkOnly){
                        x-=directionX;
                        y-=directionY;
                        Chessboard.this.chessMap.map[y][x]=new Chesspoint(y,x,color);
                        if(x==j&&y==i){
                            break;
                        }
                    }
                }
            }
            return moveSuccess;
        }
        public void whoWin(){
            int blackCnt=0;
            int whiteCnt=0;
            for(int i=0;i<=chessMap.gridNum;i++){
                for(int j=0;j<=chessMap.gridNum;j++){
                    if(chessMap.map[i][j]==null){

                    }else if(chessMap.map[i][j].color==Chesspoint.black){
                        blackCnt+=1;
                    }else if(chessMap.map[i][j].color==Chesspoint.white){
                        whiteCnt+=1;
                    }
                }
            }
            if (blackCnt>whiteCnt){
                if(!account1.getName().equals("ai")){
                    account1.setWinNum(account1.getWinNum()+1);
                }
                if(!account2.getName().equals("ai")){
                    account2.setLoseNum(account2.getLoseNum()+1);
                }
                controlPanel.setMsg("黑子获胜！");
                JOptionPane.showMessageDialog(null,"黑子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                strategy.startGame();
            }else if(whiteCnt>blackCnt){
                if(!account2.getName().equals("ai")){
                    account2.setWinNum(account2.getWinNum()+1);
                }
                if(!account1.getName().equals("ai")){
                    account1.setLoseNum(account1.getLoseNum()+1);
                }
                controlPanel.setMsg("白子获胜！");
                JOptionPane.showMessageDialog(null,"白子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
                strategy.startGame();
            }else{
                controlPanel.setMsg("黑白平手！");
                JOptionPane.showMessageDialog(null,"黑白平手！","消息提示",JOptionPane.WARNING_MESSAGE);
                strategy.startGame();
            }
        }
    }

    private class mousePosition extends MouseMotionAdapter {// 取得将要落子的位置

        public void mouseMoved(MouseEvent evt) {
            int xoff = left / 2;
            int yoff = top / 2;
            /***这里也是上边说的棋子不对位的漏洞所对应的代码,这也放一个-3*defaultGridLen***/
            int x = (evt.getX() - xoff - 3 * defaultGridLen) / chessMap.gridLen;
            int y = (evt.getY() - yoff) / chessMap.gridLen;

            //在void函数中可以用一个不带值的return来结束程序
            if (x < 0 || x > chessMap.gridNum || y < 0 || y > chessMap.gridNum) {
                return;
            }
            if (chessMap.map[x][y] != null) {
                return;
            }

            mouseClick = new Point(x, y);//鼠标位置为（x,y)
            repaint();//重绘此组件。
        }
    }



    //悔棋
//    public void back() {
//        if (chessMap.alreadyNum == 0) {
//            controlPanel.setMsg("无子可悔");//调用controlPanel的消息方法,在标签上输出"无子可悔"
//            return;
//        }
//        Object obj = chessMap.chessman.elementAt(--chessMap.alreadyNum);
//        //instanceof是Java的一个二元操作符，和==，>，<是同一类东西。由于它是由字母组成的，所以也是Java的保留关键字。
//        //它的作用是测试它左边的对象是否是它右边的类的实例，返回boolean类型的数据。
//        if (obj instanceof Chesspoint) {
//            Chesspoint goPiece = (Chesspoint) obj;
//            chessMap.map[goPiece.x][goPiece.y] = null;
//            chessMap.currentTurn = goPiece.color;
//        } else {
//            Vector v = (Vector) obj;
//            for (int i = 0; i < v.size(); i++) {
//                Chesspoint q = (Chesspoint) (v.elementAt(i));
//                if (i == v.size() - 1) {
//                    chessMap.map[q.x][q.y] = null;
//                    int index = chessMap.chessman.indexOf(v);//返回v处的组件赋给index。
//                    //setElementAt(Object, int)将此列表指定 index 处的组件设置为指定的对象。
//                    chessMap.chessman.setElementAt(q, index);
//                    chessMap.currentTurn = q.color;
//                } else {
//                    chessMap.map[q.x][q.y] = q;
//                }
//            }
//        }
//        controlPanel.setLabel();// // 更新控制面板
//        repaint();//重绘此组件。
//    }

    //围棋虚着
    public void skip(){
        if(Chessboard.this.strategy instanceof FiveStrategy) {
            JOptionPane.showMessageDialog(null,"五子棋无法虚着！","消息提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        //alreadyNum不变
        if (chessMap.currentTurn == Chesspoint.black) {
            chessMap.currentTurn = Chesspoint.white;
        } else {
            chessMap.currentTurn = Chesspoint.black;
        }
        mouseClick = null;
        // 更新控制面板
        controlPanel.setLabel();
        //更新标签
    }

    //投负
    public void giveIn(){
        if(strategy instanceof BlackWhiteStrategy){
            JOptionPane.showMessageDialog(null,"黑白棋无法投负，请点重开！","消息提示",JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (chessMap.currentTurn==Chesspoint.black){
            controlPanel.setMsg("白子获胜！");
            JOptionPane.showMessageDialog(null,"白子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
            this.strategy.startGame();
        }else{
            controlPanel.setMsg("黑子获胜！");
            JOptionPane.showMessageDialog(null,"黑子获胜！","消息提示",JOptionPane.WARNING_MESSAGE);
            this.strategy.startGame();
        }
    }


    public void undo(){
        ChessMap chessmap=mementoCaretaker.undo();
        //this.chessMap.map=new Chesspoint[this.chessMap.gridNum+1][this.chessMap.gridNum+1];
        this.chessMap=chessmap;
//        for(Object point:this.chessMap.chessman){
//            Chesspoint p=(Chesspoint)point;
//            this.chessMap.map[p.x][p.y]=p;
//        }
        if(chessmap==null){
            JOptionPane.showMessageDialog(null,"已回退到第一步，请下棋","消息提示",JOptionPane.WARNING_MESSAGE);	//消息对话框
        }else{
            repaint();
        }
    }
    public void redo(){
        ChessMap chessmap=mementoCaretaker.redo();
        this.chessMap=chessmap;
        //this.chessMap.map=new Chesspoint[this.chessMap.gridNum+1][this.chessMap.gridNum+1];
//        for(Object point:this.chessMap.chessman){
//            Chesspoint p=(Chesspoint)point;
//            this.chessMap.map[p.x][p.y]=p;
//        }
        if(chessmap==null){
            JOptionPane.showMessageDialog(null,"无子可恢复，请继续下棋","消息提示",JOptionPane.WARNING_MESSAGE);	//消息对话框
        }else{
            repaint();
        }
    }

    //保存局面
    public void save(Account account){
        try{
            File f = new File("save_"+account.name+".txt");
            f.createNewFile();
            BufferedWriter out=new BufferedWriter(new FileWriter(f));
            List<Memento> mementoList=mementoCaretaker.getMementoList();
            for (Memento memento: mementoList){
                for(int i=0;i<= memento.gridNum;i++){
                    for (int j=0;j<memento.gridNum;j++){
                        if(memento.map[i][j]!=null){
                            out.write(memento.map[i][j].toString()+"\r\n");
                        }
                    }
                }
                out.write(memento.alreadyNum+"\r\n"+memento.currentTurn+"\r\n"+memento.gridNum+"\r\n"+memento.gridLen+"\r\n"+memento.chessmanLength+"\r\n");
            }
            out.flush();
            out.close();
            JOptionPane.showMessageDialog(null,"保存成功！","消息提示",JOptionPane.WARNING_MESSAGE);	//消息对话框
//            controlPanel.setMsg("保存成功");
        }catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println(this.chessMap.map);
    }

    public void recover(Account account){
        try{
            String pathname="save_"+account.name+".txt";
            File filename=new File(pathname);
            InputStreamReader reader=new InputStreamReader(new FileInputStream(filename));
            BufferedReader br=new BufferedReader(reader);

            boolean hasLine=true;
            List<Memento> mementoList=new ArrayList<>();
            Memento memento=new Memento();
            while(true){
                ArrayList<Chesspoint> points=new ArrayList<>();
                String line;
                while(true){
                    line=br.readLine();
                    if(line==null||line.equals("")){
                        hasLine=false;
                        break;
                    }
//                    System.out.println(line);
                    if(line.charAt(0)!='['){
                        break;
                    }
                    String[] info = line.split(":");
                    int color=0;
                    if(info[1].charAt(0)=='w')
                        color=1;
                    String[] coordinates=info[0].substring(1,info[0].length()-1).split(",");
                    Chesspoint p= new Chesspoint(Integer.valueOf(coordinates[0]),Integer.valueOf(coordinates[1]),color);
                    points.add(p);
                }

                if(hasLine){
                    int alreadyNum=Integer.valueOf(line);
                    int currentTurn=Integer.valueOf(br.readLine());
                    int gridNum=Integer.valueOf(br.readLine());
                    int gridLen=Integer.valueOf(br.readLine());
                    int chessmanLength=Integer.valueOf(br.readLine());
                    Chesspoint[][] map=new Chesspoint[this.chessMap.gridNum+1][this.chessMap.gridNum+1];
                    for(Chesspoint p:points){
                        map[p.x][p.y]=p;
                    }
                    memento=new Memento(alreadyNum,currentTurn,gridNum,gridLen,chessmanLength,map);
                    mementoList.add(memento);
                }else{
                    break;
                }
            }
            this.mementoCaretaker.setMementoList(mementoList);
            this.mementoCaretaker.setIndex(mementoList.size()-1);
            this.chessMap=new ChessMap(memento.getAlreadyNum(),memento.getCurrentTurn(),memento.getGridNum(),memento.getGridLen(),memento.getChessmanLength(),memento.getMap());
            //JOptionPane.showMessageDialog(null,"覆盖并恢复成功！","消息提示",JOptionPane.WARNING_MESSAGE);	//消息对话框
            repaint();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    //控制面板类
    class ControlPanel extends Panel {
        protected Label lblTurn = new Label("", Label.CENTER);//创建标签对象
        //protected Label lblNum = new Label("", Label.CENTER);//创建标签对象
        protected Label lblMsg = new Label("", Label.CENTER);//创建标签对象
        protected Label lblSize = new Label("",Label.CENTER);
        protected Choice choice = new Choice();//创建一个新的选择菜单。
        //protected Button back = new Button("悔棋");//创建"悔棋"按钮
        protected Button start = new Button("重开");//创建"重新开局"按钮
        protected Button skip = new Button("虚着");
        protected Button giveIn = new Button("投负");

        protected Button undo = new Button("上一步");
        protected Button redo = new Button("下一步");

        protected Button chooseSize = new Button("大小");
        protected Label player1 = new Label("黑方:",Label.CENTER);
        protected Label player2 = new Label("白方:",Label.CENTER);
        protected Label win1 = new Label("胜:/负:",Label.CENTER);
        protected Label win2 = new Label("胜:/负:",Label.CENTER);
        protected Button save1 = new Button("保存");
        protected Button recover1 = new Button("恢复");
        protected Button save2 = new Button("保存");
        protected Button recover2 = new Button("恢复");
        protected Button startAI1 = new Button("落黑子");
        protected Button startAI2 = new Button("落白子");

        public int getWidth() {
            return 30;//返回组件的当前宽度45。
        }

        public int getHeight() {
            return size;//返回组件的当前高度size。
        }

        //选择棋盘的大小
        public ControlPanel() {
            setSize(this.getWidth(), this.getHeight());//设置控制面板大小
            setLayout(new GridLayout(20, 1, 0, 10));//设置布局管理器
            setLabel();//设置标签

            choice.add("五子棋");
            choice.add("围棋");
            choice.add("黑白棋");
            choice.addItemListener(new SetGameType());//在选择按钮中添加监听器
            add(choice);
            add(lblTurn);//添加lblTurn标签对象
            //add(lblNum);//添加lblNum标签对象
            add(lblSize);
            add(chooseSize);
            add(start);//添加开局按钮
            add(lblMsg);//添加lblMsg标签对象
//            add(back);//添加“悔棋”按钮
            add(giveIn);//投负
            add(skip);//围棋虚着
            add(undo);
            add(redo);


            //back.addActionListener(new BackChess());//给悔棋按钮,添加事件监听器
            start.addActionListener(new BackChess());//给重新开始按钮,添加事件监听器
            skip.addActionListener(new ForwardChess());
            giveIn.addActionListener(new ForwardChess());
            save1.addActionListener(new ManageChess());
            recover1.addActionListener(new ManageChess());
            save2.addActionListener(new ManageChess());
            recover2.addActionListener(new ManageChess());
            chooseSize.addActionListener(new SetBoardSize());
            undo.addActionListener(new MementoBtn());
            redo.addActionListener(new MementoBtn());
            startAI1.addActionListener(new startAIs());
            startAI2.addActionListener(new startAIs());
            setBackground(new Color(120, 120, 200));//设置背景颜色
        }

        public void addPlayersInfo(){
            add(player1);
            add(win1);
            if(!account1.getName().equals("ai")){
                add(save1);//保存局面
                add(recover1);
            }else{
                add(startAI1);
            }
            add(player2);
            add(win2);
            if(!account2.getName().equals("ai")){
                add(save2);//保存局面
                add(recover2);
            }else if(mode==2){
                add(startAI2);
            }
        }

        public Insets getInsets() {
            return new Insets(5, 5, 5, 5);
        }

        private class startAIs implements ActionListener{
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource() == startAI1){
                    new FiveStrategy().aiStep(firstai,Chesspoint.black);
                }
                else if(evt.getSource() == startAI2){
                    new FiveStrategy().aiStep(secondai,Chesspoint.white);
                }

            }
        }

        //悔棋或重开（棋局后退）
        private class BackChess implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
//                if (evt.getSource() == back)//如果鼠标点击"悔棋"按钮，则返回上一步（悔棋）
//                    Chessboard.this.back();
                if (evt.getSource() == start)//如果鼠标点击“重新开始”按钮，则重新开局
                    Chessboard.this.strategy.startGame();
            }
        }
        //虚着或投负（棋局前进）
        private class ForwardChess implements ActionListener{
            public void actionPerformed(ActionEvent evt){
                if (evt.getSource()==skip){
                    Chessboard.this.skip();
                } else if (evt.getSource()==giveIn) {
                    Chessboard.this.giveIn();
                }
            }
        }
        //保存或恢复局面（棋局管理）
        private class ManageChess implements ActionListener{
            public void actionPerformed(ActionEvent evt){
                if (evt.getSource()==save1) {
                    Chessboard.this.save(account1);
                }
                else if (evt.getSource()==recover1) {
                    Chessboard.this.recover(account1);
                }
                else if (evt.getSource()==save2) {
                    Chessboard.this.save(account2);
                }
                else if (evt.getSource()==recover2) {
                    Chessboard.this.recover(account2);
                }
            }
        }

        private class MementoBtn implements ActionListener{
            public void actionPerformed(ActionEvent evt){
                if(evt.getSource()==undo){
                    undo();
                }else if (evt.getSource()==redo){
                    redo();
                }
            }
        }

//        private class Settings implements ActionListener{
//            public void actionPerformed(ActionEvent evt){
//                if (evt.getSource()==chooseSize) {
//                    String size = JOptionPane.showInputDialog(null,"请棋盘大小，范围是[8,19]：","输入",JOptionPane.WARNING_MESSAGE);		//输入对话框
//                    System.out.println(size);
//                }
//            }
//        }

        //设置棋盘大小
//        private class SetBoardSize implements ItemListener {
        private class SetBoardSize implements ActionListener {
//            public void itemStateChanged(ItemEvent evt) {
            public void actionPerformed(ActionEvent evt) {
                String s = JOptionPane.showInputDialog(null,"请棋盘大小，范围是[8,19]：","输入",JOptionPane.WARNING_MESSAGE);		//输入对话框
                int rects=Integer.parseInt(s);
                if(rects<3 || rects>19){
                    JOptionPane.showMessageDialog(null,"棋盘大小不符合要求！","消息提示",JOptionPane.WARNING_MESSAGE);	//消息对话框
                    return;
                }
//                String s = (String) (evt.getItem());
//                int rects = Integer.parseInt(s.substring(0, 2).trim());//这是重新计算所要格子数.
                if(strategy instanceof BlackWhiteStrategy){
                    rects-=1;
                }
                if (rects != Chessboard.this.chessMap.gridNum) {
                    /**这里出现了错误,致使出现了能放大棋盘而不能缩小的错误//这里原来是gridLen * defaultGridNum,
                    现在将其改为defaultGridLen * defaultGridNum就可以了,主要是这是在计算棋盘长度时出现了错误*/
                    Chessboard.this.chessMap.gridLen = (defaultGridLen * defaultGridNum) / rects;
                    Chessboard.this.chessMap.chessmanLength = chessMap.gridLen * 9 / 10;
                    Chessboard.this.chessMap.gridNum = rects;
                    System.out.println(Chessboard.this.chessMap.gridLen);
                    System.out.println(Chessboard.this.chessMap.gridNum);
                    Chessboard.this.strategy.startGame();
                }
            }
        }

        private class SetGameType implements ItemListener{
            public void itemStateChanged(ItemEvent evt) {
                String s = (String) (evt.getItem());
                if("围棋".equals(s)){
                    Chessboard.this.strategy=new GoStrategy();
                    changeSize(19);
                }else if("五子棋".equals(s)){
                    Chessboard.this.strategy=new FiveStrategy();
                    changeSize(19);
                }else if("黑白棋".equals(s)){
                    int rects=7;

//                String s = (String) (evt.getItem());
//                int rects = Integer.parseInt(s.substring(0, 2).trim());//这是重新计算所要格子数.
                    if (rects != Chessboard.this.chessMap.gridNum) {
                        /**这里出现了错误,致使出现了能放大棋盘而不能缩小的错误//这里原来是gridLen * defaultGridNum,
                         现在将其改为defaultGridLen * defaultGridNum就可以了,主要是这是在计算棋盘长度时出现了错误*/
                        Chessboard.this.chessMap.gridLen = (defaultGridLen * defaultGridNum) / rects;
                        Chessboard.this.chessMap.chessmanLength = chessMap.gridLen * 9 / 10;
                        Chessboard.this.chessMap.gridNum = rects;
//                        System.out.println(Chessboard.this.chessMap.gridLen);
//                        System.out.println(Chessboard.this.chessMap.gridNum);
//                        Chessboard.this.startGame();
                    }
                    Chessboard.this.strategy=new BlackWhiteStrategy();

                }
                Chessboard.this.strategy.startGame();
            }
            public void changeSize(int rects){
                if (rects != Chessboard.this.chessMap.gridNum) {
                    /**这里出现了错误,致使出现了能放大棋盘而不能缩小的错误//这里原来是gridLen * defaultGridNum,
                     现在将其改为defaultGridLen * defaultGridNum就可以了,主要是这是在计算棋盘长度时出现了错误*/
                    Chessboard.this.chessMap.gridLen = (defaultGridLen * defaultGridNum) / rects;
                    Chessboard.this.chessMap.chessmanLength = chessMap.gridLen * 9 / 10;
                    Chessboard.this.chessMap.gridNum = rects;
                }
            }
        }

        // 待下方的颜色与步数
        public void setLabel() {
            //如果待下方是黑子，则显示“轮到黑子”，否则显示“轮到白子”
            lblTurn.setText(Chessboard.this.chessMap.currentTurn == Chesspoint.black ? "轮到黑子" : "轮到白子 ");
            //如果待下方是黑子,则棋子颜色为黑色，否则为白色
            lblTurn.setForeground(Chessboard.this.chessMap.currentTurn == Chesspoint.black ? Color.black : Color.white);
            //每下一步，步数加1
//            lblNum.setText("第 " + (Chessboard.this.chessMap.alreadyNum + 1) + " 手");
            //如果待下方是黑子，则该棋子的前景颜色为黑色，否则为白色
            //lblNum.setForeground(Chessboard.this.chessMap.currentTurn == Chesspoint.black ? Color.black : Color.white);
            //将该标签设置为空文本
            lblMsg.setText("");
            if(strategy instanceof BlackWhiteStrategy){
                setBlackWhiteSize();
            }else{
                lblSize.setText(Chessboard.this.chessMap.gridNum+"×"+Chessboard.this.chessMap.gridNum);
            }
            player1.setText("黑方:"+account1.getName());
            win1.setText("胜:"+account1.getWinNum()+"/负:"+account1.getLoseNum());
            player2.setText("白方:"+account2.getName());
            win2.setText("胜:"+account2.getWinNum()+"/负:"+account2.getLoseNum());
        }
        public void setBlackWhiteSize(){
            int lbl=Chessboard.this.chessMap.gridNum+1;
            lblSize.setText(lbl +"×"+lbl );
        }

        public void setMsg(String msg) {// 提示信息
            lblMsg.setText(msg);
        }
    }
}