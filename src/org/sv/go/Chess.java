package org.sv.go;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Chess extends JFrame {
    Chessboard goBoard = new Chessboard();//初始化外观

    public Chess() {
        new Lfr();

//        this.setTitle("棋类对战系统");                  //设置标题为“SvGO”
//        this.setLayout(new BorderLayout());     //设置布局管理器
//        this.setSize(goBoard.getSize());        //设置大小
//        this.add(goBoard, "East"); //添加棋盘面板并居中
//        this.setResizable(false);               //设置窗体为不可调整大小
//        this.setLayout(new BorderLayout());     //设置布局管理器
//        this.setSize(650, 490);    //设置窗口显示范围大小
//        this.setVisible(true);                  //设置窗体的可见性为可见
    }

    //取得宽度
    public int getWidth() {
        return goBoard.getWidth();
    }

    //取得高度
    public int getHeight() {
        return goBoard.getHeight();
    }

    public void initBoard(){
        this.setTitle("棋类对战系统");                  //设置标题为“SvGO”
        this.setLayout(new BorderLayout());     //设置布局管理器
        this.setSize(goBoard.getSize());        //设置大小
        this.add(goBoard, "East"); //添加棋盘面板并居中
        this.setResizable(false);               //设置窗体为不可调整大小
        this.setLayout(new BorderLayout());     //设置布局管理器
        this.setSize(550, 490);    //设置窗口显示范围大小  650
        this.setVisible(true);                  //设置窗体的可见性为可见

        goBoard.getPlayersInfo();
    }

    class Lfr {
        //公共静态主登陆界面框
        public JFrame frame = new JFrame("游戏入口");
        //登陆界面组件
        public JLabel label1 = new JLabel("用户名");                 //标签
        public JTextField username = new JTextField(10);            //文本框
        public JLabel label2 = new JLabel("密   码");
        public JPasswordField password = new JPasswordField(10);    //密码文本框
        public JButton Signinbtn = new JButton("登录或注册");              //按钮
        //    public static JButton registerbtn = new JButton("注册");
        public JButton visitorbtn = new JButton("游客登录");
        //    public  JButton resetbtn = new JButton("重置");
//
        public  JLabel labela = new JLabel("用户名1");
        public  JTextField usernamea = new JTextField(10);            //文本框
        public  JLabel label_a = new JLabel("密 码1");
        public  JPasswordField passworda = new JPasswordField(10);
        public  JButton Signinbtna = new JButton("登录或注册");              //按钮
        public  JButton visitorbtna = new JButton("游客登录");

        public  JLabel labelb = new JLabel("用户名2");
        public  JTextField usernameb = new JTextField(10);            //文本框
        public  JLabel label_b = new JLabel("密 码2");
        public  JPasswordField passwordb = new JPasswordField(10);
        public  JButton Signinbtnb = new JButton("登录或注册");              //按钮
        public  JButton visitorbtnb = new JButton("游客登录");

        public  JButton playersbtn = new JButton("玩家 - 玩家 模式");
        public  JButton playeraibtn = new JButton("玩家  -  AI  模式");
        public  JButton aisbtn = new JButton("  AI  -  AI  模式");

        //构造函数，创建以及初始化窗口
        public Lfr() {

            //设置窗口大小
            frame.setSize(250, 200);
            //设置按下右上角X号后关闭
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //调用函数初始化窗体的组件
            initFrame();
            //窗口居中
            frame.setLocationRelativeTo(null);
            //窗口可见
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
                        JOptionPane.showMessageDialog(null, "账号或密码错误！", "消息提示", JOptionPane.WARNING_MESSAGE);
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

//                    int userOption=JOptionPane.showConfirmDialog(null,"登录成功","消息提示",JOptionPane.OK_OPTION);
//                    if (userOption == JOptionPane.OK_OPTION) {
//                        System.err.println("是");
//                    }else {
//                        System.out.println("否");
//                    }
                        frame.setVisible(false);
                        initBoard();

                    }
                    else if (!accounts.containsKey(name)) {
                        File file = new File("accounts.txt");
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(file, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件
                        //先换一行，再写入一个Map
                        osw.write("\r\n");
                        osw.write(name + " " + pwd + " 0 0");
                        //写入完成关闭流
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

                        JOptionPane.showMessageDialog(null, "注册成功！", "消息提示", JOptionPane.WARNING_MESSAGE);
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
                        JOptionPane.showMessageDialog(null, "账号或密码错误！", "消息提示", JOptionPane.WARNING_MESSAGE);
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

                        frame.setTitle("玩家2入口");
                        initPlayer(labelb,usernameb,label_b,passwordb,Signinbtnb,visitorbtnb);

                    }
                    else if (!accounts.containsKey(name)) {
                        File file = new File("accounts.txt");
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(file, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件
                        //先换一行，再写入一个Map
                        osw.write("\r\n");
                        osw.write(name + " " + pwd + " 0 0");
                        //写入完成关闭流
                        osw.close();

                        JOptionPane.showMessageDialog(null, "注册成功！", "消息提示", JOptionPane.WARNING_MESSAGE);
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
                        JOptionPane.showMessageDialog(null, "账号或密码错误！", "消息提示", JOptionPane.WARNING_MESSAGE);
                    }
                    else if (accounts.containsKey(name) && accounts.get(name).getPwd().equals(pwd)) {
                        File playersFile = new File("players.txt");
//                        playersFile.createNewFile();
//                        BufferedWriter out = new BufferedWriter(new FileWriter(playersFile));
                        FileOutputStream fos = new FileOutputStream(playersFile, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
                        OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件
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
                        fos = new FileOutputStream(file, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件
                        //先换一行，再写入一个Map
                        osw.write("\r\n");
                        osw.write(name + " " + pwd + " 0 0");
                        //写入完成关闭流
                        osw.close();

                        JOptionPane.showMessageDialog(null, "注册成功！", "消息提示", JOptionPane.WARNING_MESSAGE);
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

                    frame.setTitle("玩家2入口");
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
                    FileOutputStream fos = new FileOutputStream(playersFile, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
                    OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件
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
                frame.setTitle("玩家1入口");
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
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));  //居中面板
            panel.add(playersbtn);
            panel.add(playeraibtn);
            panel.add(aisbtn);
            //箱式布局装入三个面板
            Box vBox = Box.createVerticalBox();
            vBox.add(panel);
            //将布局置入窗口
            frame.setContentPane(vBox);
        }

        //    public void initFrame() {
//        //定义面板封装文本框和标签
//        JPanel panel01 = new JPanel(new FlowLayout(FlowLayout.CENTER));  //居中面板
//        panel01.add(label1);
//        panel01.add(username);
//
//        JPanel panel02 = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        panel02.add(label2);
//        panel02.add(password);
//
//        //定义面板封装按钮
//        JPanel panel03 = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        panel03.add(Signinbtn);
//        panel03.add(registerbtn);
//        panel03.add(resetbtn);
//
//        //箱式布局装入三个面板
//        Box vBox = Box.createVerticalBox();
//        vBox.add(panel01);
//        vBox.add(panel02);
//        vBox.add(panel03);
//
//        //将布局置入窗口
//        frame.setContentPane(vBox);
//    }
        public void initPlayer(JLabel label1,JTextField username,JLabel label2,JTextField password,JButton Signinbtn,JButton visitorbtn) {
            //定义面板封装文本框和标签
            JPanel panel01 = new JPanel(new FlowLayout(FlowLayout.CENTER));  //居中面板
            panel01.add(label1);
            panel01.add(username);

            JPanel panel02 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel02.add(label2);
            panel02.add(password);

            //定义面板封装按钮
            JPanel panel03 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel03.add(Signinbtn);
            //panel03.add(registerbtn);
            panel03.add(visitorbtn);

            //箱式布局装入三个面板
            Box vBox = Box.createVerticalBox();
            vBox.add(panel01);
            vBox.add(panel02);
            vBox.add(panel03);

            //将布局置入窗口
            frame.setContentPane(vBox);

            //窗口居中
            frame.setLocationRelativeTo(null);
            //窗口可见
            frame.setVisible(true);
        }

    }
    // 主函数，开始下棋程序
    public static void main(String[] args) {
        Chess svo = new Chess();

    }
}
