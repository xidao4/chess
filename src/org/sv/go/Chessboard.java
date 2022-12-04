package org.sv.go;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;


public class Chessboard extends JPanel {
    //  Ĭ�ϵ����̷��񳤶ȼ���Ŀ
    public static final int defaultGridLen = 22, defaultGridNum = 19;

    /**����Vector�����������µ�����,�����������ϵ��������Ӻͱ��ߵ��ģ�
     **��ĳһ������û��������ӣ��������б����������������Ӽ�������ӱ���.
     **Vector �����ʵ�ֿ������Ķ������顣������һ��������������ʹ�������������з��ʵ������
     **Vector �Ĵ�С���Ը�����Ҫ�������С������Ӧ���� Vector �������ӻ��Ƴ���Ĳ�����
     */
    private Vector chessman;
    private int alreadyNum;             // ������Ŀ
    private int currentTurn;            // �ֵ�˭��
    private int gridNum, gridLen;       // ���񳤶ȼ���Ŀ 9 46��13 32��19 22
    private int chessmanLength;         // ���ӵ�ֱ�� 41��28��19
    private Chesspoint[][] map;         // �������ϵ���������  ������
    private Image offScreen;            //������������
    private Graphics offGrid;           //�������Ʒ��������
    private int size;                   // ���̵Ŀ�ȼ��߶� 444
    private int top = 13, left = 13;    // �����ϱ߼���ߵı߾�
    //Point���ʾ (x,y) ����ռ��е�λ�õĵ㣬����������ָ����
    private Point mouseClick;           // ����λ�ã���map�����е��±� null
    private ControlPanel controlPanel;  // �������
    private Strategy strategy=new GoStrategy();

    //��ÿ��ư�ľ���
    public int getWidth() {
        return size + controlPanel.getWidth() + 35;
    }

    public int getHeight() {
        return size;
    }

    //�����������
    public Chessboard() {
        gridNum = defaultGridNum;                       //������ĿΪ19
        gridLen = defaultGridLen;                       //���񳤶�Ϊ22
        chessmanLength = gridLen * 9 / 10;              //����ֱ��Ϊ22*9/10
        size = 2 * left + gridNum * gridLen;            //���������̱߳�Ϊ2*13+19*22
        addMouseListener(new PlayChess());              //ע����������,������갴���¼�
        addMouseMotionListener(new mousePosition());    //ע����������,��������ƶ��¼�
        setLayout(new BorderLayout());                  //���ò���ģʽ
        controlPanel = new ControlPanel();              //�����������
        setSize(getWidth(), size);                      //���ÿ�Ⱥʹ�С
        add(controlPanel, "West");            //���"�������",Ϊ"��"
        startGame();                                    //��ʼ��Ϸ
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
        for (int i = 0; i < gridNum + 1; i++) {
            int x1 = left + i * gridLen;            //13+i*22
            int x2 = x1;
            int y1 = top;                           //top=13(ǰ���Ѷ���)
            int y2 = top + gridNum * gridLen;       //13+i*22
            //�����ߣ��ڻ������Ļ���ֱ�ߣ�ʹ�õ�ǰ������ɫ�ڣ�x1,y1)�ͣ�x2,y2)�仭һ���߶�
            offGrid.drawLine(x1, y1, x2, y2);

            x1 = left;
            x2 = left + gridNum * gridLen;
            y1 = top + i * gridLen;
            y2 = y1;
            //�����ߣ��ڻ������Ļ���ֱ�ߣ�ʹ�õ�ǰ������ɫ�ڣ�x1,y1)�ͣ�x2,y2)�仭һ���߶�
            offGrid.drawLine(x1, y1, x2, y2);
        }
        /***����ͨ�����ߵķ�ʽ�����̻��Ƴ���***/


        /***��������***/
        for (int i = 0; i < gridNum + 1; i++) {
            for (int j = 0; j < gridNum + 1; j++) {
                //ǰ�涨��Chesspoint[][] map;���������ϵ���������
                if (map[i][j] == null)
                    continue;
                //������������Ӧ����ɫ
                offGrid.setColor(map[i][j].color == Chesspoint.black ? Color.black : Color.white);
                //��ָ���������Բ��
                offGrid.fillOval(left + i * gridLen - chessmanLength / 2,
                        top + j * gridLen - chessmanLength / 2, chessmanLength, chessmanLength);
            }
        }

        /***��������λ�ã�����һ����Ҫ�µ�λ��***/
        if (mouseClick != null) {
            //���û�����ɫ
            /***Ӧ�������ټ�һ�����ž����Ҳû�ĺ�,������-3*defaultGridLen***/
            offGrid.setColor(currentTurn == Chesspoint.black ? Color.gray : new Color(200, 200, 250));
            //ʹ�õ�ǰ��ɫ������ָ�����ο����Բ��
            offGrid.fillOval(left + mouseClick.x * gridLen - chessmanLength / 2,
                    top + mouseClick.y * gridLen - chessmanLength / 2, chessmanLength, chessmanLength);
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
            int x = (evt.getX() - xoff - 3 * defaultGridLen) / gridLen;
            //getY()�����¼������Դ�����ˮƽ y ���ꡣ
            int y = (evt.getY() - yoff) / gridLen;
            if (x < 0 || x > gridNum || y < 0 || y > gridNum)
                return;//���ؿ�

            //��void�����п�����һ������ֵ��return����������
            if (map[x][y] != null)
                return;

            /***������������***/
            if (alreadyNum < chessman.size()) {
                int size = chessman.size();
                for (int i = size - 1; i >= alreadyNum; i--)
                    chessman.removeElementAt(i);//�Ӵ��������Ƴ�i����
            }

            Chesspoint goPiece = new Chesspoint(x, y, currentTurn);

            map[x][y] = goPiece;
            //��������ӵ�chessman��
            chessman.addElement(goPiece);
            //����������Ŀ�Լ�
            alreadyNum++;

            if(strategy.playChess(x,y,goPiece)){
                return;
            }
//            //***�ж���[x,y]���Ӻ��Ƿ��������Է�����
//            take(x, y);
//            //***�ж��Ƿ������Լ������������������Ч
//            if (allDead(goPiece).size() != 0) {
//                map[x][y] = null;
//                repaint();//�ػ�������
//                controlPanel.setMsg("��Ч����");//���������ʾ"��Ч����"
//                //***back***
//                chessman.removeElement(goPiece);//�Ƴ�����
//                alreadyNum--;//����������Ŀ�Լ�
//                if (currentTurn == Chesspoint.black) {
//                    currentTurn = Chesspoint.white;//�ֵ�������
//                } else {
//                    currentTurn = Chesspoint.black;//�ֵ�������
//                }
//                return;
//            }

            if (currentTurn == Chesspoint.black) {
                currentTurn = Chesspoint.white;
            } else {
                currentTurn = Chesspoint.black;
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
        public boolean playChess(int x, int y, Chesspoint goPiece){return false;}
    }
    private class GoStrategy extends Strategy{
        public boolean playChess(int x, int y, Chesspoint goPiece){
            //***�ж���[x,y]���Ӻ��Ƿ��������Է�����
            take(x, y);
            //***�ж��Ƿ������Լ������������������Ч
            if (allDead(goPiece).size() != 0) {
                map[x][y] = null;
                repaint();//�ػ�������
                controlPanel.setMsg("��Ч����");//���������ʾ"��Ч����"
                //***back***
                chessman.removeElement(goPiece);//�Ƴ�����
                alreadyNum--;//����������Ŀ�Լ�
                if (currentTurn == Chesspoint.black) {
                    currentTurn = Chesspoint.white;//�ֵ�������
                } else {
                    currentTurn = Chesspoint.black;//�ֵ�������
                }
//                return false;
            }
            return false;
        }
    }
    private class FiveStrategy extends Strategy{
        public boolean playChess(int x, int y, Chesspoint goPiece){
            //��ǰ��
            int minX=x-4;
            int maxX=x+4;
            if(minX<1) minX=1;
            if(maxX>Chessboard.this.gridNum) maxX=Chessboard.this.gridNum;
            int sum=0;
            for (int i=minX;i<=maxX;i++){
                if(Chessboard.this.map[i][y]==null){
                    continue;
                }
                int piece=Chessboard.this.map[i][y].color;
                if(piece==Chessboard.this.currentTurn){
                    sum++;
                    if(sum==5) {
                        if (currentTurn==Chesspoint.black){
                            controlPanel.setMsg("���ӻ�ʤ��");
                            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                            startGame();
                        }else{
                            controlPanel.setMsg("���ӻ�ʤ��");
                            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                            startGame();
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
            if(maxY>Chessboard.this.gridNum) maxY=Chessboard.this.gridNum;
            sum=0;
            for (int i=minY; i<=maxY; i++) {
                if(Chessboard.this.map[x][i]==null){
                    continue;
                }
                int piece=Chessboard.this.map[x][i].color;
                if(piece==Chessboard.this.currentTurn){
                    sum++;
                    if(sum==5){
                        if (currentTurn==Chesspoint.black){
                            controlPanel.setMsg("���ӻ�ʤ��");
                            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                            startGame();
                        }else{
                            controlPanel.setMsg("���ӻ�ʤ��");
                            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                            startGame();
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
                if(x+i>0 && x+i<=Chessboard.this.gridNum && y+i>=0 && y+i<=Chessboard.this.gridNum) {
                    if(Chessboard.this.map[x+i][y+i]==null){
                        continue;
                    }
                    int piece=Chessboard.this.map[x+i][y+i].color;
                    if(piece==Chessboard.this.currentTurn){
                        sum++;
                        if(sum==5){
                            if (currentTurn==Chesspoint.black){
                                controlPanel.setMsg("���ӻ�ʤ��");
                                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                                startGame();
                            }else{
                                controlPanel.setMsg("���ӻ�ʤ��");
                                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                                startGame();
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
                if(x+i>0 && x+i<=Chessboard.this.gridNum && y-i>=0 && y-i<=Chessboard.this.gridNum){
                    if(Chessboard.this.map[x+i][y-i]==null){
                        continue;
                    }
                    int piece=Chessboard.this.map[x+i][y-i].color;
                    if(piece==Chessboard.this.currentTurn){
                        sum++;
                        if(sum==5){
                            if (currentTurn==Chesspoint.black){
                                controlPanel.setMsg("���ӻ�ʤ��");
                                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                                startGame();
                            }else{
                                controlPanel.setMsg("���ӻ�ʤ��");
                                JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
                                startGame();
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

    private class mousePosition extends MouseMotionAdapter {// ȡ�ý�Ҫ���ӵ�λ��

        public void mouseMoved(MouseEvent evt) {
            int xoff = left / 2;
            int yoff = top / 2;
            /***����Ҳ���ϱ�˵�����Ӳ���λ��©������Ӧ�Ĵ���,��Ҳ��һ��-3*defaultGridLen***/
            int x = (evt.getX() - xoff - 3 * defaultGridLen) / gridLen;
            int y = (evt.getY() - yoff) / gridLen;

            //��void�����п�����һ������ֵ��return����������
            if (x < 0 || x > gridNum || y < 0 || y > gridNum) {
                return;
            }
            if (map[x][y] != null) {
                return;
            }

            mouseClick = new Point(x, y);//���λ��Ϊ��x,y)
            repaint();//�ػ�������
        }
    }

    //�ж���[x,y]���Ӻ��Ƿ�����ߵ��Է�����
    //����������������������λһ���������ܵ�����
    public static int[] xdir = {0, 0, 1, -1};
    public static int[] ydir = {1, -1, 0, 0};

    public void take(int x, int y) {
        Chesspoint goPiece;
        if ((goPiece = map[x][y]) == null) {
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
            if (dead.size() != 0) {
                Object obj = chessman.elementAt(alreadyNum - 1);
                if (obj instanceof Chesspoint) {
                    goPiece = (Chesspoint) (chessman.elementAt(alreadyNum - 1));
                    dead.addElement(goPiece);
                } else {
                    Vector vector = (Vector) obj;
                    for (int i = 0; i < vector.size(); i++) {
                        dead.addElement(vector.elementAt(i));
                    }
                }
                // ����Vector chessman�еĵ�num��Ԫ��
                chessman.setElementAt(dead, alreadyNum - 1);
            }
        }
        repaint();
    }

    //�ж�������Χ�Ƿ��пհ�
    public boolean aroundBlank(Chesspoint goPiece) {
        for (int l = 0; l < xdir.length; l++) {
            int x1 = goPiece.x + xdir[l];
            int y1 = goPiece.y + ydir[l];
            //xdir��ydir��ȡֵ��xdir={ 0, 0, 1, -1 }; ydir = { 1, -1, 0, 0 };Ҳ���ǵ�ǰ���ӵ�����
            if (x1 < 0 || x1 > gridNum || y1 < 0 || y1 > gridNum)
                continue;
            if (map[x1][y1] == null)
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
            if (x1 < 0 || x1 > gridNum || y1 < 0 || y1 > gridNum
                    || map[x1][y1] == null)
                continue;
            v.addElement(map[x1][y1]);//��map[x1][y1]�����ӵ���v��ĩβ��
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
            map[q.x][q.y] = null;
        }
        repaint();//�ػ�������
    }

    //����
    public void back() {
        if (alreadyNum == 0) {
            controlPanel.setMsg("���ӿɻ�");//����controlPanel����Ϣ����,�ڱ�ǩ�����"���ӿɻ�"
            return;
        }
        Object obj = chessman.elementAt(--alreadyNum);
        //instanceof��Java��һ����Ԫ����������==��>��<��ͬһ�ණ����������������ĸ��ɵģ�����Ҳ��Java�ı����ؼ��֡�
        //���������ǲ�������ߵĶ����Ƿ������ұߵ����ʵ��������boolean���͵����ݡ�
        if (obj instanceof Chesspoint) {
            Chesspoint goPiece = (Chesspoint) obj;
            map[goPiece.x][goPiece.y] = null;
            currentTurn = goPiece.color;
        } else {
            Vector v = (Vector) obj;
            for (int i = 0; i < v.size(); i++) {
                Chesspoint q = (Chesspoint) (v.elementAt(i));
                if (i == v.size() - 1) {
                    map[q.x][q.y] = null;
                    int index = chessman.indexOf(v);//����v�����������index��
                    //setElementAt(Object, int)�����б�ָ�� index �����������Ϊָ���Ķ���
                    chessman.setElementAt(q, index);
                    currentTurn = q.color;
                } else {
                    map[q.x][q.y] = q;
                }
            }
        }
        controlPanel.setLabel();// // ���¿������
        repaint();//�ػ�������
    }

    //Χ������
    public void skip(){
        if(Chessboard.this.strategy instanceof FiveStrategy) {
            JOptionPane.showMessageDialog(null,"�������޷����ţ�","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
            return;
        }
        //alreadyNum����
        if (currentTurn == Chesspoint.black) {
            currentTurn = Chesspoint.white;
        } else {
            currentTurn = Chesspoint.black;
        }
        mouseClick = null;
        // ���¿������
        controlPanel.setLabel();
        //���±�ǩ
    }

    //Ͷ��
    public void giveIn(){
        if (currentTurn==Chesspoint.black){
            controlPanel.setMsg("���ӻ�ʤ��");
            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
            startGame();
        }else{
            controlPanel.setMsg("���ӻ�ʤ��");
            JOptionPane.showMessageDialog(null,"���ӻ�ʤ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);
            startGame();
        }
    }

    //�������
    public void save(){
        try{
            File f = new File("manageChess.txt");
            f.createNewFile();
            BufferedWriter out=new BufferedWriter(new FileWriter(f));
            out.write(this.chessman.toString()+"\r\n"+this.alreadyNum+"\r\n"+this.currentTurn+"\r\n"+this.gridNum+"\r\n"+this.gridLen+"\r\n"+this.chessmanLength);
            //out.write(this.map.toString());
            out.flush();
            out.close();
            JOptionPane.showMessageDialog(null,"����ɹ���","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);	//��Ϣ�Ի���
//            controlPanel.setMsg("����ɹ�");
        }catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println(this.map);
    }
    
    public void recover(){
        try{
            String pathname="manageChess.txt";
            File filename=new File(pathname);
            InputStreamReader reader=new InputStreamReader(new FileInputStream(filename));
            BufferedReader br=new BufferedReader(reader);

            this.chessman=new Vector();
            String[] lst=br.readLine().substring(1).split(", ");

            for(String point : lst){
                String[] info = point.split(":");
                int color=0;
                if(info[1].charAt(0)=='w')
                    color=1;
                String[] coordinates=info[0].substring(1,info[0].length()-1).split(",");
                Chesspoint p= new Chesspoint(Integer.valueOf(coordinates[0]),Integer.valueOf(coordinates[1]),color);
                chessman.addElement(p);
            }
            this.alreadyNum=Integer.valueOf(br.readLine());
            this.currentTurn=Integer.valueOf(br.readLine());
            this.gridNum=Integer.valueOf(br.readLine());
            this.gridLen=Integer.valueOf(br.readLine());
            this.chessmanLength=Integer.valueOf(br.readLine());
            this.map=new Chesspoint[this.gridNum+1][this.gridNum+1];
            for(Object point:this.chessman){
                Chesspoint p=(Chesspoint)point;
                map[p.x][p.y]=p;
            }
            JOptionPane.showMessageDialog(null,"���ǲ��ָ��ɹ���","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);	//��Ϣ�Ի���
            repaint();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }


//    //������ٴ�ǰ��
//    public void forward() {
//        if (alreadyNum == chessman.size()) {
//            controlPanel.setMsg("����ǰ��");//����controlPanel����Ϣ����"����ǰ��"
//            return;
//        }
//        Object obj = chessman.elementAt(alreadyNum++);
//        Chesspoint goPiece;
//        //instanceof��Java��һ����Ԫ����������==��>��<��ͬһ�ණ����������������ĸ��ɵģ�����Ҳ��Java�ı����ؼ��֡�
//        //���������ǲ�������ߵĶ����Ƿ������ұߵ����ʵ��������boolean���͵����ݡ�
//        if (obj instanceof Chesspoint) {
//            goPiece = (Chesspoint) (obj);
//            map[goPiece.x][goPiece.y] = goPiece;
//        } else {
//            Vector v = (Vector) obj;
//            goPiece = (Chesspoint) (v.elementAt(v.size() - 1));//����v.size() - 1)�����������goPiece��
//            map[goPiece.x][goPiece.y] = goPiece;
//        }
//        if (goPiece.color == Chesspoint.black) {
//            currentTurn = Chesspoint.white;//��������
//        } else {
//            currentTurn = Chesspoint.black;//��������
//        }
//        take(goPiece.x, goPiece.y);//����
//        controlPanel.setLabel();//���¿������
//        repaint();//�ػ����
//    }

    //���¿�ʼ��Ϸ
    public void startGame() {
        chessman = new Vector(); //chessman����Ϊһ������
        alreadyNum = 0;//alreadyNum ��ʼֲΪ��
        map = new Chesspoint[gridNum + 1][gridNum + 1];//map����Ϊһ����ά����,���������������
        currentTurn = Chesspoint.black;//��������
        controlPanel.setLabel();//���¿������ı�ǩ
        repaint();//�ػ����
    }

    //���������
    class ControlPanel extends Panel {
        protected Label lblTurn = new Label("", Label.CENTER);//������ǩ����
        protected Label lblNum = new Label("", Label.CENTER);//������ǩ����
        protected Label lblMsg = new Label("", Label.CENTER);//������ǩ����
        protected Label lblSize = new Label("",Label.CENTER);
        protected Choice choice = new Choice();//����һ���µ�ѡ��˵���
        protected Button back = new Button("�� ��");//����"����"��ť
        protected Button start = new Button("���¿���");//����"���¿���"��ť
        protected Button skip = new Button("�� ��");
        protected Button giveIn = new Button("Ͷ ��");
        protected Button save = new Button("�������");
        protected Button recover = new Button("�ָ�����");
        protected Button chooseSize = new Button("���̴�С");

        public int getWidth() {
            return 45;//��������ĵ�ǰ���45��
        }

        public int getHeight() {
            return size;//��������ĵ�ǰ�߶�size��
        }

        //ѡ�����̵Ĵ�С
        public ControlPanel() {
            setSize(this.getWidth(), this.getHeight());//���ÿ�������С
            setLayout(new GridLayout(12, 1, 0, 10));//���ò��ֹ�����
            setLabel();//���ñ�ǩ
            choice.add("Χ��");
            choice.add("������");
            choice.addItemListener(new SetGameType());//��ѡ��ť����Ӽ�����
            add(choice);
            add(lblTurn);//���lblTurn��ǩ����
            add(lblNum);//���lblNum��ǩ����
            add(start);//��ӿ��ְ�ť

            add(lblSize);
            add(chooseSize);
            add(lblMsg);//���lblMsg��ǩ����
            add(save);//�������
            add(recover);
            add(back);//��ӡ����塱��ť
            add(giveIn);//Ͷ��
            add(skip);//Χ������

            back.addActionListener(new BackChess());//�����尴ť,����¼�������
            start.addActionListener(new BackChess());//�����¿�ʼ��ť,����¼�������
            skip.addActionListener(new ForwardChess());
            giveIn.addActionListener(new ForwardChess());
            save.addActionListener(new ManageChess());
            recover.addActionListener(new ManageChess());
            chooseSize.addActionListener(new SetBoardSize());
            setBackground(new Color(120, 120, 200));//���ñ�����ɫ
        }

        public Insets getInsets() {
            return new Insets(5, 5, 5, 5);
        }

        //������ؿ�����ֺ��ˣ�
        private class BackChess implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource() == back)//��������"����"��ť���򷵻���һ�������壩
                    Chessboard.this.back();
                else if (evt.getSource() == start)//�������������¿�ʼ����ť�������¿���
                    Chessboard.this.startGame();
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
                if (evt.getSource()==save) {
                    Chessboard.this.save();
                }
                else if (evt.getSource()==recover) {
                    Chessboard.this.recover();
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
                if(rects<8 || rects>19){
                    JOptionPane.showMessageDialog(null,"���̴�С������Ҫ��","��Ϣ��ʾ",JOptionPane.WARNING_MESSAGE);	//��Ϣ�Ի���
                    return;
                }
//                String s = (String) (evt.getItem());
//                int rects = Integer.parseInt(s.substring(0, 2).trim());//�������¼�����Ҫ������.
                if (rects != Chessboard.this.gridNum) {
                    /**��������˴���,��ʹ�������ܷŴ����̶�������С�Ĵ���//����ԭ����gridLen * defaultGridNum,
                    ���ڽ����ΪdefaultGridLen * defaultGridNum�Ϳ�����,��Ҫ�������ڼ������̳���ʱ�����˴���*/
                    Chessboard.this.gridLen = (defaultGridLen * defaultGridNum) / rects;
                    Chessboard.this.chessmanLength = gridLen * 9 / 10;
                    Chessboard.this.gridNum = rects;
                    Chessboard.this.startGame();
                }
            }
        }

        private class SetGameType implements ItemListener{
            public void itemStateChanged(ItemEvent evt) {
                String s = (String) (evt.getItem());
                if("Χ��".equals(s)){
                    Chessboard.this.strategy=new GoStrategy();
                }else if("������".equals(s)){
                    Chessboard.this.strategy=new FiveStrategy();
                }
                startGame();
            }
        }

        // ���·�����ɫ�벽��
        public void setLabel() {
            //������·��Ǻ��ӣ�����ʾ���ֵ����ӡ���������ʾ���ֵ����ӡ�
            lblTurn.setText(Chessboard.this.currentTurn == Chesspoint.black ? "�ֵ�����" : "�ֵ����� ");
            //������·��Ǻ���,��������ɫΪ��ɫ������Ϊ��ɫ
            lblTurn.setForeground(Chessboard.this.currentTurn == Chesspoint.black ? Color.black : Color.white);
            //ÿ��һ����������1
            lblNum.setText("�� " + (Chessboard.this.alreadyNum + 1) + " ��");
            //������·��Ǻ��ӣ�������ӵ�ǰ����ɫΪ��ɫ������Ϊ��ɫ
            lblNum.setForeground(Chessboard.this.currentTurn == Chesspoint.black ? Color.black : Color.white);
            //���ñ�ǩ����Ϊ���ı�
            lblMsg.setText("");
            lblSize.setText("��С"+Chessboard.this.gridNum+"��"+Chessboard.this.gridNum);
        }

        public void setMsg(String msg) {// ��ʾ��Ϣ
            lblMsg.setText(msg);
        }
    }
}