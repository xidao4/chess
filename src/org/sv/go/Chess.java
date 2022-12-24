package org.sv.go;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Chess extends JFrame {
    Chessboard goBoard = new Chessboard();//��ʼ�����

    public Chess() {
        new Lfr();

//        this.setTitle("�����սϵͳ");                  //���ñ���Ϊ��SvGO��
//        this.setLayout(new BorderLayout());     //���ò��ֹ�����
//        this.setSize(goBoard.getSize());        //���ô�С
//        this.add(goBoard, "East"); //���������岢����
//        this.setResizable(false);               //���ô���Ϊ���ɵ�����С
//        this.setLayout(new BorderLayout());     //���ò��ֹ�����
//        this.setSize(650, 490);    //���ô�����ʾ��Χ��С
//        this.setVisible(true);                  //���ô���Ŀɼ���Ϊ�ɼ�
    }

    //ȡ�ÿ��
    public int getWidth() {
        return goBoard.getWidth();
    }

    //ȡ�ø߶�
    public int getHeight() {
        return goBoard.getHeight();
    }

    public void initBoard(){
        this.setTitle("�����սϵͳ");                  //���ñ���Ϊ��SvGO��
        this.setLayout(new BorderLayout());     //���ò��ֹ�����
        this.setSize(goBoard.getSize());        //���ô�С
        this.add(goBoard, "East"); //���������岢����
        this.setResizable(false);               //���ô���Ϊ���ɵ�����С
        this.setLayout(new BorderLayout());     //���ò��ֹ�����
        this.setSize(550, 490);    //���ô�����ʾ��Χ��С  650
        this.setVisible(true);                  //���ô���Ŀɼ���Ϊ�ɼ�

        goBoard.getPlayersInfo();
    }

    class Lfr {
        //������̬����½�����
        public JFrame frame = new JFrame("��Ϸ���");
        //��½�������
        public JLabel label1 = new JLabel("�û���");                 //��ǩ
        public JTextField username = new JTextField(10);            //�ı���
        public JLabel label2 = new JLabel("��   ��");
        public JPasswordField password = new JPasswordField(10);    //�����ı���
        public JButton Signinbtn = new JButton("��¼��ע��");              //��ť
        //    public static JButton registerbtn = new JButton("ע��");
        public JButton visitorbtn = new JButton("�ο͵�¼");
        //    public  JButton resetbtn = new JButton("����");
//
        public  JLabel labela = new JLabel("�û���1");
        public  JTextField usernamea = new JTextField(10);            //�ı���
        public  JLabel label_a = new JLabel("�� ��1");
        public  JPasswordField passworda = new JPasswordField(10);
        public  JButton Signinbtna = new JButton("��¼��ע��");              //��ť
        public  JButton visitorbtna = new JButton("�ο͵�¼");

        public  JLabel labelb = new JLabel("�û���2");
        public  JTextField usernameb = new JTextField(10);            //�ı���
        public  JLabel label_b = new JLabel("�� ��2");
        public  JPasswordField passwordb = new JPasswordField(10);
        public  JButton Signinbtnb = new JButton("��¼��ע��");              //��ť
        public  JButton visitorbtnb = new JButton("�ο͵�¼");

        public  JButton playersbtn = new JButton("��� - ��� ģʽ");
        public  JButton playeraibtn = new JButton("���  -  AI  ģʽ");
        public  JButton aisbtn = new JButton("  AI  -  AI  ģʽ");

        //���캯���������Լ���ʼ������
        public Lfr() {

            //���ô��ڴ�С
            frame.setSize(250, 200);
            //���ð������Ͻ�X�ź�ر�
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //���ú�����ʼ����������
            initFrame();
            //���ھ���
            frame.setLocationRelativeTo(null);
            //���ڿɼ�
            frame.setVisible(true);

            playersbtn.addActionListener(new initPlayers());
            playeraibtn.addActionListener(new initPlayerAI());
            aisbtn.addActionListener(new initAIs());
            Signinbtn.addActionListener(new SignIn());
            visitorbtn.addActionListener(new Visitor());

            Signinbtna.addActionListener(new SignIna());
            Signinbtnb.addActionListener(new SignInb());
            visitorbtna.addActionListener(new Visitora());
            visitorbtnb.addActionListener(new Visitorb());
        }

        private class SignIn implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                String name = username.getText();
                String pwd = password.getText();
                try {
                    File accountsFile = new File("accounts.txt");
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(accountsFile));
                    BufferedReader br = new BufferedReader(reader);
                    Map<String, Account> accounts = new HashMap<>();
                    while (true) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] player = line.split(" ");
                        Account acc=new Account(player[0],player[1],Integer.parseInt(player[2]),Integer.parseInt(player[3]));
                        accounts.put(player[0],acc);
                    }
                    if (accounts.containsKey(name) && !accounts.get(name).getPwd().equals(pwd)) {
                        JOptionPane.showMessageDialog(null, "�˺Ż��������", "��Ϣ��ʾ", JOptionPane.WARNING_MESSAGE);
                    }
                    else if (accounts.containsKey(name) && accounts.get(name).getPwd().equals(pwd)) {
                        File playersFile = new File("players.txt");
                        playersFile.createNewFile();
                        BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
                        Account a=accounts.get(name);
                        out.write(name + " " +a.getPwd()+" "+a.getWinNum()+" "+a.getLoseNum());
                        out.write("\r\n");
                        out.write("ai");
                        out.flush();
                        out.close();

//                    int userOption=JOptionPane.showConfirmDialog(null,"��¼�ɹ�","��Ϣ��ʾ",JOptionPane.OK_OPTION);
//                    if (userOption == JOptionPane.OK_OPTION) {
//                        System.err.println("��");
//                    }else {
//                        System.out.println("��");
//                    }
                        frame.setVisible(false);
                        initBoard();

                    }
                    else if (!accounts.containsKey(name)) {
                        File file = new File("accounts.txt");
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(file, true);//���ﹹ�췽������һ������true,��ʾ���ļ�ĩβ׷��д��
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//ָ����UTF-8��ʽд���ļ�
                        //�Ȼ�һ�У���д��һ��Map
                        osw.write("\r\n");
                        osw.write(name + " " + pwd + " 0 0");
                        //д����ɹر���
                        osw.close();

//                        accounts.put(name, new Account(name,pwd,0, 0));
//                        File playersFile = new File("players.txt");
//                        playersFile.createNewFile();
//                        BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
//                        Account a= accounts.get(name);
//                        out.write(name + " " +a.getPwd()+" "+a.getWinNum()+" "+a.getLoseNum());
//                        out.write("\r\n");
//                        out.write("ai");
//                        out.flush();
//                        out.close();

                        JOptionPane.showMessageDialog(null, "ע��ɹ���", "��Ϣ��ʾ", JOptionPane.WARNING_MESSAGE);
//                        frame.setVisible(false);
//                        initBoard();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        }

        private class SignIna implements ActionListener{
            public void actionPerformed(ActionEvent evt) {
                String name = usernamea.getText();
                String pwd = passworda.getText();
                try {
                    File accountsFile = new File("accounts.txt");
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(accountsFile));
                    BufferedReader br = new BufferedReader(reader);
                    Map<String, Account> accounts = new HashMap<>();
                    while (true) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] player = line.split(" ");
                        Account acc=new Account(player[0],player[1],Integer.parseInt(player[2]),Integer.parseInt(player[3]));
                        accounts.put(player[0],acc);
                    }
                    if (accounts.containsKey(name) && !accounts.get(name).getPwd().equals(pwd)) {
                        JOptionPane.showMessageDialog(null, "�˺Ż��������", "��Ϣ��ʾ", JOptionPane.WARNING_MESSAGE);
                    }
                    else if (accounts.containsKey(name) && accounts.get(name).getPwd().equals(pwd)) {
                        File playersFile = new File("players.txt");
                        playersFile.createNewFile();
                        BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
                        Account a=accounts.get(name);
                        out.write(name + " " +a.getPwd()+" "+a.getWinNum()+" "+a.getLoseNum());
                        out.write("\r\n");

                        out.flush();
                        out.close();

                        frame.setTitle("���2���");
                        initPlayer(labelb,usernameb,label_b,passwordb,Signinbtnb,visitorbtnb);

                    }
                    else if (!accounts.containsKey(name)) {
                        File file = new File("accounts.txt");
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(file, true);//���ﹹ�췽������һ������true,��ʾ���ļ�ĩβ׷��д��
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//ָ����UTF-8��ʽд���ļ�
                        //�Ȼ�һ�У���д��һ��Map
                        osw.write("\r\n");
                        osw.write(name + " " + pwd + " 0 0");
                        //д����ɹر���
                        osw.close();

                        JOptionPane.showMessageDialog(null, "ע��ɹ���", "��Ϣ��ʾ", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        }

        private class SignInb implements ActionListener{
            public void actionPerformed(ActionEvent evt) {
                String name = usernameb.getText();
                String pwd = passwordb.getText();
                try {
                    File accountsFile = new File("accounts.txt");
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(accountsFile));
                    BufferedReader br = new BufferedReader(reader);
                    Map<String, Account> accounts = new HashMap<>();
                    while (true) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] player = line.split(" ");
                        Account acc=new Account(player[0],player[1],Integer.parseInt(player[2]),Integer.parseInt(player[3]));
                        accounts.put(player[0],acc);
                    }
                    if (accounts.containsKey(name) && !accounts.get(name).getPwd().equals(pwd)) {
                        JOptionPane.showMessageDialog(null, "�˺Ż��������", "��Ϣ��ʾ", JOptionPane.WARNING_MESSAGE);
                    }
                    else if (accounts.containsKey(name) && accounts.get(name).getPwd().equals(pwd)) {
                        File playersFile = new File("players.txt");
//                        playersFile.createNewFile();
//                        BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
                        FileOutputStream fos = new FileOutputStream(playersFile, true);//���ﹹ�췽������һ������true,��ʾ���ļ�ĩβ׷��д��
                        OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");//ָ����UTF-8��ʽд���ļ�
                        Account a=accounts.get(name);
                        out.write(name + " " +a.getPwd()+" "+a.getWinNum()+" "+a.getLoseNum());
                        out.write("\r\n");
                        out.flush();
                        out.close();
                        frame.setVisible(false);
                        initBoard();
                    }
                    else if (!accounts.containsKey(name)) {
                        File file = new File("accounts.txt");
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(file, true);//���ﹹ�췽������һ������true,��ʾ���ļ�ĩβ׷��д��
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//ָ����UTF-8��ʽд���ļ�
                        //�Ȼ�һ�У���д��һ��Map
                        osw.write("\r\n");
                        osw.write(name + " " + pwd + " 0 0");
                        //д����ɹر���
                        osw.close();

                        JOptionPane.showMessageDialog(null, "ע��ɹ���", "��Ϣ��ʾ", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        }

        private class Visitor implements ActionListener{
            public void actionPerformed(ActionEvent evt) {
                try{
                    File playersFile = new File("players.txt");
                    playersFile.createNewFile();
                    BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
                    out.write( "visitor password 0 0");
                    out.write("\r\n");
                    out.write("ai");
                    out.flush();
                    out.close();

                    frame.setVisible(false);
                    initBoard();
                }catch (IOException e){
                    throw new RuntimeException(e);
                }

            }
        }

        private class Visitora implements ActionListener{
            public void actionPerformed(ActionEvent evt) {
                try{
                    File playersFile = new File("players.txt");
                    playersFile.createNewFile();
                    BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
                    out.write( "visitor password 0 0");
                    out.write("\r\n");
                    out.flush();
                    out.close();

                    frame.setTitle("���2���");
                    initPlayer(labelb,usernameb,label_b,passwordb,Signinbtnb,visitorbtnb);
                }catch (IOException e){
                    throw new RuntimeException(e);
                }

            }
        }

        private class Visitorb implements ActionListener{
            public void actionPerformed(ActionEvent evt) {
                try{
                    File playersFile = new File("players.txt");
//                    playersFile.createNewFile();
//                    BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
                    FileOutputStream fos = new FileOutputStream(playersFile, true);//���ﹹ�췽������һ������true,��ʾ���ļ�ĩβ׷��д��
                    OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");//ָ����UTF-8��ʽд���ļ�
                    out.write( "visitor password 0 0");
                    out.flush();
                    out.close();

                    frame.setVisible(false);
                    initBoard();
                }catch (IOException e){
                    throw new RuntimeException(e);
                }

            }
        }

        private class initPlayers implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                frame.setTitle("���1���");
                initPlayer(labela,usernamea,label_a,passworda,Signinbtna,visitorbtna);
            }
        }

        private class initAIs implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                try{
                    File playersFile = new File("players.txt");
                    playersFile.createNewFile();
                    BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
                    out.write("ai");
                    out.write("\r\n");
                    out.write("ai");
                    out.flush();
                    out.close();

                    frame.setVisible(false);
                    initBoard();
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        }

        private class initPlayerAI implements ActionListener {
            public void actionPerformed(ActionEvent evt) {
                initPlayer(label1,username,label2,password,Signinbtn,visitorbtn);
            }
        }

        public void initFrame() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));  //�������
            panel.add(playersbtn);
            panel.add(playeraibtn);
            panel.add(aisbtn);
            //��ʽ����װ���������
            Box vBox = Box.createVerticalBox();
            vBox.add(panel);
            //���������봰��
            frame.setContentPane(vBox);
        }

        //    public void initFrame() {
//        //��������װ�ı���ͱ�ǩ
//        JPanel panel01 = new JPanel(new FlowLayout(FlowLayout.CENTER));  //�������
//        panel01.add(label1);
//        panel01.add(username);
//
//        JPanel panel02 = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        panel02.add(label2);
//        panel02.add(password);
//
//        //��������װ��ť
//        JPanel panel03 = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        panel03.add(Signinbtn);
//        panel03.add(registerbtn);
//        panel03.add(resetbtn);
//
//        //��ʽ����װ���������
//        Box vBox = Box.createVerticalBox();
//        vBox.add(panel01);
//        vBox.add(panel02);
//        vBox.add(panel03);
//
//        //���������봰��
//        frame.setContentPane(vBox);
//    }
        public void initPlayer(JLabel label1,JTextField username,JLabel label2,JTextField password,JButton Signinbtn,JButton visitorbtn) {
            //��������װ�ı���ͱ�ǩ
            JPanel panel01 = new JPanel(new FlowLayout(FlowLayout.CENTER));  //�������
            panel01.add(label1);
            panel01.add(username);

            JPanel panel02 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel02.add(label2);
            panel02.add(password);

            //��������װ��ť
            JPanel panel03 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel03.add(Signinbtn);
            //panel03.add(registerbtn);
            panel03.add(visitorbtn);

            //��ʽ����װ���������
            Box vBox = Box.createVerticalBox();
            vBox.add(panel01);
            vBox.add(panel02);
            vBox.add(panel03);

            //���������봰��
            frame.setContentPane(vBox);

            //���ھ���
            frame.setLocationRelativeTo(null);
            //���ڿɼ�
            frame.setVisible(true);
        }

    }
    // ����������ʼ�������
    public static void main(String[] args) {
        Chess svo = new Chess();

    }
}
