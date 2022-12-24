package org.sv.go;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;



public class Chessboard extends JPanel {
    //  Ĭ�ϵ����̷��񳤶ȼ���Ŀ
    public static final int defaultGridLen = 27, defaultGridNum = 15;  //52   8       22 19

    /**����Vector�����������µ�����,�����������ϵ��������Ӻͱ��ߵ��ģ�
     **��ĳһ������û��������ӣ��������б����������������Ӽ�������ӱ���.
     **Vector �����ʵ�ֿ������Ķ������顣������һ��������������ʹ�������������з��ʵ������
     **Vector �Ĵ�С���Ը�����Ҫ�������С������Ӧ���� Vector �������ӻ��Ƴ���Ĳ�����
     */
    private ChessMap chessMap;
//    private Vector chessMap.chessman;
//    private int chessMap.alreadyNum;             // ������Ŀ
//    private int chessMap.currentTurn;            // �ֵ�˭��
//    private int chessMap.gridNum, chessMap.gridLen;       // ���񳤶ȼ���Ŀ 9 46��13 32��19 22
//    private int chessMap.chessmanLength;         // ���ӵ�ֱ�� 41��28��19
//    private Chesspoint[][] chessMap.map;         // �������ϵ���������  ������
    private Image offScreen;            //������������
    private Graphics offGrid;           //�������Ʒ��������
    private int size;                   // ���̵Ŀ�ȼ��߶� 444
    private int top = 13, left = 22;    // �����ϱ߼���ߵı߾�
    //Point���ʾ (x,y) ����ռ��е�λ�õĵ㣬����������ָ����
    private Point mouseClick;           // ����λ�ã���map�����е��±� null
    private ControlPanel controlPanel;  // �������
    private Strategy strategy=new FiveStrategy();
    private MementoCaretaker mementoCaretaker;

    private Account account1=new Account();
    private Account account2=new Account();

    private int mode; //0 ���� 1 �˻� 2 ����
    private FirstAI firstai;
    private SecondAI secondai;

    //��ÿ��ư�ľ���
    public int getWidth() {
        return size + controlPanel.getWidth() + 35;
    }

    public int getHeight() {
        return size;
    }

    //�����������
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
        chessMap.gridNum = defaultGridNum;                       //������ĿΪ19
        chessMap.gridLen = defaultGridLen;                       //���񳤶�Ϊ22
        chessMap.chessmanLength = chessMap.gridLen * 9 / 10;              //����ֱ��Ϊ22*9/10
        size = 2 * left + chessMap.gridNum * chessMap.gridLen;            //���������̱߳�Ϊ2*13+19*22
        addMouseListener(new PlayChess());              //ע����������,������갴���¼�
        addMouseMotionListener(new mousePosition());    //ע����������,��������ƶ��¼�
        setLayout(new BorderLayout());                  //���ò���ģʽ
        controlPanel = new ControlPanel();  //�����������
        add(controlPanel, "West");            //���"�������",Ϊ"��"
        setSize(getWidth(), size);                      //���ÿ�Ⱥʹ�С
        mementoCaretaker=new MementoCaretaker();
        strategy.startGame();                                    //��ʼ��Ϸ

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
        //������ť��ͬλ�塣��ť��ͬλ������Ӧ�ó�����İ�ť����ۡ����������书�ܡ�
        super.addNotify();
        //����һ������˫����Ŀ�����Ļ����Ƶ�ͼ��
        offScreen = createImage(size, size);
        //ΪoffScreen�������ͼ�ε�������
        offGrid = offScreen.getGraphics();
    }
    public void paint(Graphics g) {
        //����ɫѡȡ���ĵ�ǰ��ɫ����Ϊָ���� RGB ��ɫ�������û�����ɫ
        offGrid.setColor(new Color(180, 150, 100));
        offGrid.fillRect(0, 0, size, size);

        /***�������̸���***/
        //���û�����ɫΪ��ɫ
        offGrid.setColor(Color.black);
        for (int i = 0; i < chessMap.gridNum+1 ; i++) {
            int x1 = left + i * chessMap.gridLen+5;            //13+i*22
            int x2 = x1;
            int y1 = top;                           //top=13(ǰ���Ѷ���)
            int y2 = top + chessMap.gridNum * chessMap.gridLen;       //13+i*22
            //�����ߣ��ڻ������Ļ���ֱ�ߣ�ʹ�õ�ǰ������ɫ�ڣ�x1,y1)�ͣ�x2,y2)�仭һ���߶�
            offGrid.drawLine(x1, y1, x2, y2);

            x1 = left+5;
            x2 = left + chessMap.gridNum * chessMap.gridLen+5;
            y1 = top + i * chessMap.gridLen;
            y2 = y1;
            //�����ߣ��ڻ������Ļ���ֱ�ߣ�ʹ�õ�ǰ������ɫ�ڣ�x1,y1)�ͣ�x2,y2)�仭һ���߶�
            offGrid.drawLine(x1, y1, x2, y2);
        }
        /***����ͨ�����ߵķ�ʽ�����̻��Ƴ���***/


        /***��������***/
        for (int i = 0; i < chessMap.gridNum + 1; i++) {
            for (int j = 0; j < chessMap.gridNum + 1; j++) {
                //ǰ�涨��Chesspoint[][] chessMap.map;���������ϵ���������
                if (chessMap.map[i][j] == null)
                    continue;
                //������������Ӧ����ɫ
                offGrid.setColor(chessMap.map[i][j].color == Chesspoint.black ? Color.black : Color.white);
                //��ָ���������Բ��
                offGrid.fillOval(left + i * chessMap.gridLen - chessMap.chessmanLength / 2,
                        top + j * chessMap.gridLen - chessMap.chessmanLength / 2, chessMap.chessmanLength, chessMap.chessmanLength);
            }
        }

        /***��������λ�ã�����һ����Ҫ�µ�λ��***/
        if (mouseClick != null) {
            //���û�����ɫ
            /***Ӧ�������ټ�һ�����ž����Ҳû�ĺ�,������-3*defaultGridLen***/
            offGrid.setColor(chessMap.currentTurn == Chesspoint.black ? Color.gray : new Color(200, 200, 250));
            //ʹ�õ�ǰ��ɫ������ָ�����ο����Բ��
            offGrid.fillOval(left + mouseClick.x * chessMap.gridLen - chessMap.chessmanLength / 2,
                    top + mouseClick.y * chessMap.gridLen - chessMap.chessmanLength / 2, chessMap.chessmanLength, chessMap.chessmanLength);
        }
        //�ѻ���һ���Ի���
        g.drawImage(offScreen, 80, 0, this);
    }
    // ��������
    public void update(Graphics g) {
        paint(g);//����
    }

    /***������,���Ƕ���갴���¼��Ĵ�����,���ڲ���***/
    class PlayChess extends MouseAdapter { // ��һ������
        //��갴��������ϰ���ʱ����
        public void mousePressed(MouseEvent evt) {
            int xoff = left / 2;
            int yoff = top / 2;

            /***�����е��Ǹ���������겻��λ��©��,���п�����������±�����¼���X�������������
             Ӧ�����ټ�һ����,�����Ҳû�ĺ�,������-3*defaultGridLen,λ�ô��Ҳ��ȷ��***/
            //getX()�����¼������Դ�����ˮƽ x ���ꡣ
            int x = (evt.getX() - xoff - 3 * defaultGridLen) / chessMap.gridLen;
            //getY()�����¼������Դ�����ˮƽ y ���ꡣ
            int y = (evt.getY() - yoff) / chessMap.gridLen;
            if (x < 0 || x > chessMap.gridNum || y < 0 || y > chessMap.gridNum)
                return;//���ؿ�

            //��void�����п�����һ������ֵ��return����������
            if (chessMap.map[x][y] != null)
                return;

            /***������������***/
//            if (chessMap.alreadyNum < chessMap.chessman.size()) {
//                int size = chessMap.chessman.size();
//                for (int i = size - 1; i >= chessMap.alreadyNum; i--)
//                    chessMap.chessman.removeElementAt(i);//�Ӵ��������Ƴ�i����
//            }

            Chesspoint goPiece = new Chesspoint(x, y, chessMap.currentTurn);

            chessMap.map[x][y] = goPiece;
            //��������ӵ�chessman��
            //chessMap.chessman.addElement(goPiece);
            //����������Ŀ�Լ�
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
            // ���¿������
            controlPanel.setLabel();
            //���±�ǩ
        }

        public void mouseExited(MouseEvent evt) {// ����˳�ʱ�������Ҫ���ӵ�λ��
            mouseClick = null;
            repaint();//�ػ�
        }
    }

    private class Strategy{
        //��ֽ����򷵻�TRUE�����������򷵻�FALSE
        public boolean playChess(int x, int y, Chesspoint goPiece){return false;}
        public void startGame(){
            //chessMap.chessman = new Vector(); //chessman����Ϊһ������
            chessMap.alreadyNum = 0;//chessMap.alreadyNum ��ʼֲΪ��
            chessMap.map = new Chesspoint[chessMap.gridNum + 1][chessMap.gridNum + 1];//map����Ϊһ����ά����,���������������
            chessMap.currentTurn = Chesspoint.black;//��������
            controlPanel.setLabel();//���¿������ı�ǩ
            repaint();//�ػ����
        }
    }
    private class GoStrategy extends Strategy{
        public boolean playChess(int x, int y, Chesspoint goPiece){
            //***�ж���[x,y]���Ӻ��Ƿ��������Է�����
            take(x, y);
            //***�ж��Ƿ������Լ������������������Ч
            if (allDead(goPiece).size() != 0) {
                chessMap.map[x][y] = null;
                repaint();//�ػ�������
                controlPanel.setMsg("��Ч����");//���������ʾ"��Ч����"
                //***back***
                //chessMap.chessman.removeElement(goPiece);//�Ƴ�����
                chessMap.alreadyNum--;//����������Ŀ�Լ�
                if (chessMap.currentTurn == Chesspoint.black) {
                    chessMap.currentTurn = Chesspoint.white;//�ֵ�������
                } else {
                    chessMap.currentTurn = Chesspoint.black;//�ֵ�������
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
            //ȡ����������Χ�ļ�����
            Vector v = around(goPiece);
            for (int l = 0; l < v.size(); l++) {
                //elementAt()����ָ���������������
                Chesspoint q = (Chesspoint) (v.elementAt(l));
                if (q.color == color)
                    continue;
                //����ɫ��ͬ��ȡ�ú�q����һ��������������ӣ�
                //��û�����������򷵻�һ���յ�Vector
                Vector dead = allDead(q);
                //��ȥ������������
                removeAll(dead);
                //������ӣ��򱣴����б��ߵ�������
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
//                    // ����Vector chessman�еĵ�num��Ԫ��
//                    chessMap.chessman.setElementAt(dead, chessMap.alreadyNum - 1);
//                }
            }
            repaint();
        }
        //�ж���[x,y]���Ӻ��Ƿ�����ߵ��Է�����
        //����������������������λһ���������ܵ�����
        public int[] xdir = {0, 0, 1, -1};
        public int[] ydir = {1, -1, 0, 0};

        //�ж�������Χ�Ƿ��пհ�
        public boolean aroundBlank(Chesspoint goPiece) {
            for (int l = 0; l < xdir.length; l++) {
                int x1 = goPiece.x + xdir[l];
                int y1 = goPiece.y + ydir[l];
                //xdir��ydir��ȡֵ��xdir={ 0, 0, 1, -1 }; ydir = { 1, -1, 0, 0 };Ҳ���ǵ�ǰ���ӵ�����
                if (x1 < 0 || x1 > chessMap.gridNum || y1 < 0 || y1 > chessMap.gridNum)
                    continue;
                if (chessMap.map[x1][y1] == null)
                    return true;//�������пհ�ʱ�ͷ���һ��TRUE
            }
            return false;
        }

        //ȡ����������Χ�ļ�����
        public Vector around(Chesspoint goPiece) {
            Vector v = new Vector();
            for (int l = 0; l < xdir.length; l++) {
                int x1 = goPiece.x + xdir[l];
                int y1 = goPiece.y + ydir[l];
                //xdir��ydir��ȡֵ��xdir={ 0, 0, 1, -1 }; ydir = { 1, -1, 0, 0 };Ҳ���ǵ�ǰ���ӵ�����
                if (x1 < 0 || x1 > chessMap.gridNum || y1 < 0 || y1 > chessMap.gridNum
                        || chessMap.map[x1][y1] == null)
                    continue;
                v.addElement(chessMap.map[x1][y1]);//��map[x1][y1]�����ӵ���v��ĩβ��
            }
            return v;
        }

        //ȡ������һ���������������
        public Vector allDead(Chesspoint q) {
            Vector v = new Vector();
            v.addElement(q);//��q�����ӵ���v��ĩβ��
            int count = 0;
            //trueʱִ��ѭ�����
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
                        if (v.indexOf(a) < 0)//indexOf(a)������a�е�һ�γ��ִ���������
                            v.addElement(a);//��a�����ӵ���v��ĩβ��
                    }
                }
                if (origsize == v.size())
                    break;
                else
                    count = origsize;
            }
            return v;
        }

        // ����������ȥ������
        public void removeAll(Vector v) {
            for (int i = 0; i < v.size(); i++) {
                Chesspoint q = (Chesspoint) (v.elementAt(i));//����i�����������q��
                chessMap.map[q.x][q.y] = null;
            }
            repaint();//�ػ�������
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
            //��ǰ��
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
                            controlPanel.setMsg("���ӻ�ʤ��");
                            if(!account1.getName().equals("ai")){
                                account1.setWinNum(account1.getWinNum()+1);
                            }
                            if(!account2.getName().equals("ai")){
                                account2.setLoseNum(account2.getLoseNum()+1);
                            }
                            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                            strategy.startGame();
                        }else{
                            controlPanel.setMsg("���ӻ�ʤ��");
                            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                            strategy.startGame();
                        }
                        return true;
                    }
                }else{
                    sum=0;
                }
            }
            //��ǰ��
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
                            controlPanel.setMsg("���ӻ�ʤ��");
                            if(!account1.getName().equals("ai")){
                                account1.setWinNum(account1.getWinNum()+1);
                            }
                            if(!account2.getName().equals("ai")){
                                account2.setLoseNum(account2.getLoseNum()+1);
                            }
                            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                            strategy.startGame();

                        }else{
                            controlPanel.setMsg("���ӻ�ʤ��");
                            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                            strategy.startGame();
                        }
                        return true;
                    }
                }else{
                    sum=0;
                }
            }
            //��б
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
                                controlPanel.setMsg("���ӻ�ʤ��");
                                if(!account1.getName().equals("ai")){
                                    account1.setWinNum(account1.getWinNum()+1);
                                }
                                if(!account2.getName().equals("ai")){
                                    account2.setLoseNum(account2.getLoseNum()+1);
                                }
                                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                                strategy.startGame();
                            }else{
                                controlPanel.setMsg("���ӻ�ʤ��");
                                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                                strategy.startGame();
                            }
                            return true;
                        }
                    }else{
                        sum=0;
                    }
                }
            }
            //��б
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
                                controlPanel.setMsg("���ӻ�ʤ��");
                                if(!account1.getName().equals("ai")){
                                    account1.setWinNum(account1.getWinNum()+1);
                                }
                                if(!account2.getName().equals("ai")){
                                    account2.setLoseNum(account2.getLoseNum()+1);
                                }
                                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                                strategy.startGame();
                            }else{
                                controlPanel.setMsg("���ӻ�ʤ��");
                                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
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
            //�ȼ������λ���Ƿ�Ϸ�
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
                repaint();//�ػ�������
                controlPanel.setMsg("��Ч����");//���������ʾ"��Ч����"
                //***back***
                //chessMap.chessman.removeElement(goPiece);//�Ƴ�����
                chessMap.alreadyNum--;//����������Ŀ�Լ�
                if (chessMap.currentTurn == Chesspoint.black) {
                    chessMap.currentTurn = Chesspoint.white;//�ֵ�������
                } else {
                    chessMap.currentTurn = Chesspoint.black;//�ֵ�������
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
            //chessMap.chessman = new Vector(); //chessman����Ϊһ������
            chessMap.alreadyNum = 0;//chessMap.alreadyNum ��ʼֲΪ��
            chessMap.map = new Chesspoint[chessMap.gridNum + 1][chessMap.gridNum + 1];//map����Ϊһ����ά����,���������������

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


            chessMap.currentTurn = Chesspoint.black;//��������
            controlPanel.setLabel();//���¿������ı�ǩ
            controlPanel.setBlackWhiteSize();
            repaint();//�ػ����
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
                controlPanel.setMsg("���ӻ�ʤ��");
                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                strategy.startGame();
            }else if(whiteCnt>blackCnt){
                if(!account2.getName().equals("ai")){
                    account2.setWinNum(account2.getWinNum()+1);
                }
                if(!account1.getName().equals("ai")){
                    account1.setLoseNum(account1.getLoseNum()+1);
                }
                controlPanel.setMsg("���ӻ�ʤ��");
                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                strategy.startGame();
            }else{
                controlPanel.setMsg("�ڰ�ƽ�֣�");
                JOptionPane.showMessageDialog(null,"�ڰ�ƽ�֣�","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                strategy.startGame();
            }
        }
    }

    private class mousePosition extends MouseMotionAdapter {// ȡ�ý�Ҫ���ӵ�λ��

        public void mouseMoved(MouseEvent evt) {
            int xoff = left / 2;
            int yoff = top / 2;
            /***����Ҳ���ϱ�˵�����Ӳ���λ��©������Ӧ�Ĵ���,��Ҳ��һ��-3*defaultGridLen***/
            int x = (evt.getX() - xoff - 3 * defaultGridLen) / chessMap.gridLen;
            int y = (evt.getY() - yoff) / chessMap.gridLen;

            //��void�����п�����һ������ֵ��return����������
            if (x < 0 || x > chessMap.gridNum || y < 0 || y > chessMap.gridNum) {
                return;
            }
            if (chessMap.map[x][y] != null) {
                return;
            }

            mouseClick = new Point(x, y);//���λ��Ϊ��x,y)
            repaint();//�ػ�������
        }
    }



    //����
//    public void back() {
//        if (chessMap.alreadyNum == 0) {
//            controlPanel.setMsg("���ӿɻ�");//����controlPanel����Ϣ����,�ڱ�ǩ�����"���ӿɻ�"
//            return;
//        }
//        Object obj = chessMap.chessman.elementAt(--chessMap.alreadyNum);
//        //instanceof��Java��һ����Ԫ����������==��>��<��ͬһ�ණ����������������ĸ��ɵģ�����Ҳ��Java�ı����ؼ��֡�
//        //���������ǲ�������ߵĶ����Ƿ������ұߵ����ʵ��������boolean���͵����ݡ�
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
//                    int index = chessMap.chessman.indexOf(v);//����v�����������index��
//                    //setElementAt(Object, int)�����б�ָ�� index �����������Ϊָ���Ķ���
//                    chessMap.chessman.setElementAt(q, index);
//                    chessMap.currentTurn = q.color;
//                } else {
//                    chessMap.map[q.x][q.y] = q;
//                }
//            }
//        }
//        controlPanel.setLabel();// // ���¿������
//        repaint();//�ػ�������
//    }

    //Χ������
    public void skip(){
        if(Chessboard.this.strategy instanceof FiveStrategy) {
            JOptionPane.showMessageDialog(null,"�������޷����ţ�","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
            return;
        }
        //alreadyNum����
        if (chessMap.currentTurn == Chesspoint.black) {
            chessMap.currentTurn = Chesspoint.white;
        } else {
            chessMap.currentTurn = Chesspoint.black;
        }
        mouseClick = null;
        // ���¿������
        controlPanel.setLabel();
        //���±�ǩ
    }

    //Ͷ��
    public void giveIn(){
        if(strategy instanceof BlackWhiteStrategy){
            JOptionPane.showMessageDialog(null,"�ڰ����޷�Ͷ��������ؿ���","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (chessMap.currentTurn==Chesspoint.black){
            controlPanel.setMsg("���ӻ�ʤ��");
            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
            this.strategy.startGame();
        }else{
            controlPanel.setMsg("���ӻ�ʤ��");
            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(null,"�ѻ��˵���һ����������","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);	//��Ϣ�Ի���
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
            JOptionPane.showMessageDialog(null,"���ӿɻָ������������","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);	//��Ϣ�Ի���
        }else{
            repaint();
        }
    }

    //�������
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
            JOptionPane.showMessageDialog(null,"����ɹ���","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);	//��Ϣ�Ի���
//            controlPanel.setMsg("����ɹ�");
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
            //JOptionPane.showMessageDialog(null,"���ǲ��ָ��ɹ���","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);	//��Ϣ�Ի���
            repaint();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    //���������
    class ControlPanel extends Panel {
        protected Label lblTurn = new Label("", Label.CENTER);//������ǩ����
        //protected Label lblNum = new Label("", Label.CENTER);//������ǩ����
        protected Label lblMsg = new Label("", Label.CENTER);//������ǩ����
        protected Label lblSize = new Label("",Label.CENTER);
        protected Choice choice = new Choice();//����һ���µ�ѡ��˵���
        //protected Button back = new Button("����");//����"����"��ť
        protected Button start = new Button("�ؿ�");//����"���¿���"��ť
        protected Button skip = new Button("����");
        protected Button giveIn = new Button("Ͷ��");

        protected Button undo = new Button("��һ��");
        protected Button redo = new Button("��һ��");

        protected Button chooseSize = new Button("��С");
        protected Label player1 = new Label("�ڷ�:",Label.CENTER);
        protected Label player2 = new Label("�׷�:",Label.CENTER);
        protected Label win1 = new Label("ʤ:/��:",Label.CENTER);
        protected Label win2 = new Label("ʤ:/��:",Label.CENTER);
        protected Button save1 = new Button("����");
        protected Button recover1 = new Button("�ָ�");
        protected Button save2 = new Button("����");
        protected Button recover2 = new Button("�ָ�");
        protected Button startAI1 = new Button("�����");
        protected Button startAI2 = new Button("�����");

        public int getWidth() {
            return 30;//��������ĵ�ǰ���45��
        }

        public int getHeight() {
            return size;//��������ĵ�ǰ�߶�size��
        }

        //ѡ�����̵Ĵ�С
        public ControlPanel() {
            setSize(this.getWidth(), this.getHeight());//���ÿ�������С
            setLayout(new GridLayout(20, 1, 0, 10));//���ò��ֹ�����
            setLabel();//���ñ�ǩ

            choice.add("������");
            choice.add("Χ��");
            choice.add("�ڰ���");
            choice.addItemListener(new SetGameType());//��ѡ��ť����Ӽ�����
            add(choice);
            add(lblTurn);//���lblTurn��ǩ����
            //add(lblNum);//���lblNum��ǩ����
            add(lblSize);
            add(chooseSize);
            add(start);//��ӿ��ְ�ť
            add(lblMsg);//���lblMsg��ǩ����
//            add(back);//��ӡ����塱��ť
            add(giveIn);//Ͷ��
            add(skip);//Χ������
            add(undo);
            add(redo);


            //back.addActionListener(new BackChess());//�����尴ť,����¼�������
            start.addActionListener(new BackChess());//�����¿�ʼ��ť,����¼�������
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
            setBackground(new Color(120, 120, 200));//���ñ�����ɫ
        }

        public void addPlayersInfo(){
            add(player1);
            add(win1);
            if(!account1.getName().equals("ai")){
                add(save1);//�������
                add(recover1);
            }else{
                add(startAI1);
            }
            add(player2);
            add(win2);
            if(!account2.getName().equals("ai")){
                add(save2);//�������
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

        //������ؿ�����ֺ��ˣ�
        private class BackChess implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
//                if (evt.getSource() == back)//��������"����"��ť���򷵻���һ�������壩
//                    Chessboard.this.back();
                if (evt.getSource() == start)//�������������¿�ʼ����ť�������¿���
                    Chessboard.this.strategy.startGame();
            }
        }
        //���Ż�Ͷ�������ǰ����
        private class ForwardChess implements ActionListener{
            public void actionPerformed(ActionEvent evt){
                if (evt.getSource()==skip){
                    Chessboard.this.skip();
                } else if (evt.getSource()==giveIn) {
                    Chessboard.this.giveIn();
                }
            }
        }
        //�����ָ����棨��ֹ���
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
//                    String size = JOptionPane.showInputDialog(null,"�����̴�С����Χ��[8,19]��","����",JOptionPane.WARNING_MESSAGE);		//����Ի���
//                    System.out.println(size);
//                }
//            }
//        }

        //�������̴�С
//        private class SetBoardSize implements ItemListener {
        private class SetBoardSize implements ActionListener {
//            public void itemStateChanged(ItemEvent evt) {
            public void actionPerformed(ActionEvent evt) {
                String s = JOptionPane.showInputDialog(null,"�����̴�С����Χ��[8,19]��","����",JOptionPane.WARNING_MESSAGE);		//����Ի���
                int rects=Integer.parseInt(s);
                if(rects<3 || rects>19){
                    JOptionPane.showMessageDialog(null,"���̴�С������Ҫ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);	//��Ϣ�Ի���
                    return;
                }
//                String s = (String) (evt.getItem());
//                int rects = Integer.parseInt(s.substring(0, 2).trim());//�������¼�����Ҫ������.
                if(strategy instanceof BlackWhiteStrategy){
                    rects-=1;
                }
                if (rects != Chessboard.this.chessMap.gridNum) {
                    /**��������˴���,��ʹ�������ܷŴ����̶�������С�Ĵ���//����ԭ����gridLen * defaultGridNum,
                    ���ڽ����ΪdefaultGridLen * defaultGridNum�Ϳ�����,��Ҫ�������ڼ������̳���ʱ�����˴���*/
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
                if("Χ��".equals(s)){
                    Chessboard.this.strategy=new GoStrategy();
                    changeSize(19);
                }else if("������".equals(s)){
                    Chessboard.this.strategy=new FiveStrategy();
                    changeSize(19);
                }else if("�ڰ���".equals(s)){
                    int rects=7;

//                String s = (String) (evt.getItem());
//                int rects = Integer.parseInt(s.substring(0, 2).trim());//�������¼�����Ҫ������.
                    if (rects != Chessboard.this.chessMap.gridNum) {
                        /**��������˴���,��ʹ�������ܷŴ����̶�������С�Ĵ���//����ԭ����gridLen * defaultGridNum,
                         ���ڽ����ΪdefaultGridLen * defaultGridNum�Ϳ�����,��Ҫ�������ڼ������̳���ʱ�����˴���*/
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
                    /**��������˴���,��ʹ�������ܷŴ����̶�������С�Ĵ���//����ԭ����gridLen * defaultGridNum,
                     ���ڽ����ΪdefaultGridLen * defaultGridNum�Ϳ�����,��Ҫ�������ڼ������̳���ʱ�����˴���*/
                    Chessboard.this.chessMap.gridLen = (defaultGridLen * defaultGridNum) / rects;
                    Chessboard.this.chessMap.chessmanLength = chessMap.gridLen * 9 / 10;
                    Chessboard.this.chessMap.gridNum = rects;
                }
            }
        }

        // ���·�����ɫ�벽��
        public void setLabel() {
            //������·��Ǻ��ӣ�����ʾ���ֵ����ӡ���������ʾ���ֵ����ӡ�
            lblTurn.setText(Chessboard.this.chessMap.currentTurn == Chesspoint.black ? "�ֵ�����" : "�ֵ����� ");
            //������·��Ǻ���,��������ɫΪ��ɫ������Ϊ��ɫ
            lblTurn.setForeground(Chessboard.this.chessMap.currentTurn == Chesspoint.black ? Color.black : Color.white);
            //ÿ��һ����������1
//            lblNum.setText("�� " + (Chessboard.this.chessMap.alreadyNum + 1) + " ��");
            //������·��Ǻ��ӣ�������ӵ�ǰ����ɫΪ��ɫ������Ϊ��ɫ
            //lblNum.setForeground(Chessboard.this.chessMap.currentTurn == Chesspoint.black ? Color.black : Color.white);
            //���ñ�ǩ����Ϊ���ı�
            lblMsg.setText("");
            if(strategy instanceof BlackWhiteStrategy){
                setBlackWhiteSize();
            }else{
                lblSize.setText(Chessboard.this.chessMap.gridNum+"��"+Chessboard.this.chessMap.gridNum);
            }
            player1.setText("�ڷ�:"+account1.getName());
            win1.setText("ʤ:"+account1.getWinNum()+"/��:"+account1.getLoseNum());
            player2.setText("�׷�:"+account2.getName());
            win2.setText("ʤ:"+account2.getWinNum()+"/��:"+account2.getLoseNum());
        }
        public void setBlackWhiteSize(){
            int lbl=Chessboard.this.chessMap.gridNum+1;
            lblSize.setText(lbl +"��"+lbl );
        }

        public void setMsg(String msg) {// ��ʾ��Ϣ
            lblMsg.setText(msg);
        }
    }
}