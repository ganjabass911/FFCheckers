package checkers;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

public class CheckerFrame extends JFrame implements ActionListener{
    JButton stB=new JButton("Начало игры");
    JPanel gmP=new StartPanel();
  
    CheckerFrame(){
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this); //изменение внешнего вида frame
        }
        catch (Exception e) {
           //нет необходимости обрабатывать исключение, поскольку оно влияет только на внешний вид
        }
        setupGUI();
       // new PlaySound("src//sounds//Start.wav").start();
    }

    private void setupGUI() {
        setLayout(null);
        gmP.setBounds(0,0,508,401);//400,401
        //gmP.imageUpdate(ne, WIDTH, WIDTH, WIDTH, WIDTH, WIDTH)
        add(gmP);
        stB.setHorizontalAlignment(SwingConstants.LEADING);
        //stB.setIcon(new ImageIcon(getClass().getResource("/images/checker.png")));
//        stB.setIcon(new ImageIcon(getClass().getResource("images/checker.png")));
        stB.setBackground(Color.LIGHT_GRAY);
        stB.setCursor(new Cursor(Cursor.HAND_CURSOR));
        stB.setBounds(154,420,200,60);
        stB.setFont(new Font("Times new roman",Font.BOLD,20));
        stB.addActionListener(this);
        stB.setFocusPainted(false);
        add(stB);

        this.setIconImage(new ImageIcon(getClass().getResource("/images/icon.jpg")).getImage());

        setSize(508,520);
        setLocation((int)getToolkit().getScreenSize().getWidth()/2-254,(int)getToolkit().getScreenSize().getHeight()/2-310);
        setResizable(false);
        setVisible(true);
        setTitle("Шашки");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equalsIgnoreCase("Начало игры")){
            ((JButton)e.getSource()).setText("Новая игра");
            //new PlaySound("src//sounds//button.wav").start();
            gmP=new Checkers();
            gmP.setBounds(0,0,508,401);
            this.setContentPane(gmP);
        }
    }
}
