/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantMessaging;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseListener;
import InstantMessaging.InstantMessaging.ShadowPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.jdesktop.swingx.prompt.PromptSupport;

/**
 *
 * @author Süleyman
 */
public class ClientLoginScreen extends JFrame {

    private JTextField txtUserName;
    private JPasswordField txtPass;
    private JButton btnLogin, btnClose, btnMinimize, btnRegister;
    private Color titleBg;
    JLabel lblLogin;

    public ClientLoginScreen() {
        createForm();
    }

    //<editor-fold defaultstate="collapsed" desc="Creating the form">
    private void createForm() {
        //Settings of content pane
        setContentPane(new ShadowPane());
        ShadowPane sp = (ShadowPane) getContentPane();
        sp.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        // <editor-fold defaultstate="collapsed" desc="Creating Title Bar">
        titleBg = new Color(44, 189, 165);
        Border empt = BorderFactory.createEmptyBorder();
        JPanel pnlTitle = new JPanel();
        pnlTitle.setLayout(null);
        pnlTitle.setPreferredSize(new Dimension(490, 35));
        pnlTitle.setBackground(titleBg);
        sp.add(pnlTitle);
        JLabel title = new JLabel("Instant Messaging App");
        title.setSize(new Dimension(150, 24));
        title.setLocation(10, 5);
        title.setForeground(Color.WHITE);
        pnlTitle.add(title);
        Color clrHover = new Color(31, 176, 165);
        btnClose = new JButton();
        btnClose.setSize(30, 35);
        btnClose.setText("X");
        btnClose.setLocation(460, 0);
        btnClose.setBackground(titleBg);
        btnClose.setBorder(empt);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnClose.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnClose.setBackground(clrHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnClose.setBackground(titleBg);
            }

        });
        btnClose.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        pnlTitle.add(btnClose);
        btnMinimize = new JButton();
        btnMinimize.setSize(30, 35);
        btnMinimize.setText("-");
        btnMinimize.setLocation(430, 0);
        btnMinimize.setBackground(titleBg);
        btnMinimize.setBorder(empt);
        btnMinimize.setForeground(Color.WHITE);
        btnMinimize.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btnMinimize.setFocusPainted(false);
        btnMinimize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setState(Frame.ICONIFIED);
            }
        });
        btnMinimize.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnMinimize.setBackground(clrHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnMinimize.setBackground(titleBg);
            }

        });
        pnlTitle.add(btnMinimize);
        //</editor-fold>
        //Creating background
        JLayeredPane jLp = new JLayeredPane();
        jLp.setPreferredSize(new Dimension(490, 455));
        sp.add(jLp);
        JPanel pnl1 = new JPanel();
        pnl1.setSize(new Dimension(490, 150));
        pnl1.setBackground(new Color(0, 150, 136));
        jLp.add(pnl1, 1);
        JPanel pnl2 = new JPanel();
        pnl2.setSize(new Dimension(490, 305));
        pnl2.setBackground(new Color(214, 219, 216));
        pnl2.setLocation(0, 150);
        jLp.add(pnl2, 1);
        //Creating login screen
        ShadowPane pnlGnl = new ShadowPane();
        pnlGnl.setSize(new Dimension(430, 380));
        pnlGnl.setBackground(new Color(246, 248, 248));
        pnlGnl.setLocation(30, 20);
        pnlGnl.setLayout(null);
        jLp.add(pnlGnl, 0);
        JLabel lblWc = new JLabel("Welcome to App");
        lblWc.setSize(265, 35);
        lblWc.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
        lblWc.setForeground(new Color(32,192,70));
        int width = pnlGnl.getWidth();
        int height = pnlGnl.getHeight();
        int x = (width - lblWc.getWidth()) / 2;
        int y = 60;
        lblWc.setLocation(x, y);
        pnlGnl.add(lblWc);
        txtUserName = new JTextField();
        txtUserName.setSize(260, 40);
        txtUserName.setBackground(pnlGnl.getBackground());
        txtUserName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        x = (width - txtUserName.getWidth()) / 2;
        y = ((height - (txtUserName.getHeight() * 2)) / 2) - 30;
        PromptSupport.setPrompt("Username", txtUserName);
        txtUserName.setLocation(x, y);
        Border brd = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 136));
        txtUserName.setBorder(brd);
        pnlGnl.add(txtUserName);
        txtPass = new JPasswordField();
        txtPass.setBackground(pnlGnl.getBackground());
        txtPass.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        txtPass.setSize(260, 40);
        x = (width - txtPass.getWidth()) / 2;
        y = (height - txtPass.getHeight()) / 2;
        PromptSupport.setPrompt("Password", txtPass);
        txtPass.setLocation(x, y);
        txtPass.setBorder(brd);
        pnlGnl.add(txtPass);
        btnLogin = new JButton();
        btnLogin.setText("LOGIN");
        btnLogin.setSize(80, 40);
        btnLogin.setBorder(empt);
        Color btnBg = new Color(0, 150, 136);
        btnLogin.setBackground(btnBg);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 18));
        x = ((width - btnLogin.getWidth()) / 2) - 63;
        y = ((height - txtPass.getHeight()) / 2) + txtPass.getHeight() + 10;
        btnLogin.setLocation(x, y);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        btnLogin.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }

        });
        btnLogin.setBorder(new RoundedBorder(10));
        btnLogin.setFocusPainted(false);
        pnlGnl.add(btnLogin);
        btnRegister = new JButton();
        btnRegister.setText("REGISTER");
        btnRegister.setSize(120, 40);
        btnRegister.setBorder(empt);
        btnRegister.setBackground(btnBg);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBorder(new RoundedBorder(10));
        btnRegister.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 18));
        btnRegister.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }

        });
        x = ((width - btnRegister.getWidth()) / 2) + 42;
        y = ((height - txtPass.getHeight()) / 2) + txtPass.getHeight() + 10;
        btnRegister.setLocation(x, y);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        btnRegister.setFocusPainted(false);
        pnlGnl.add(btnRegister);
        lblLogin = new JLabel();
        lblLogin.setSize(200, 30);
        x = ((350 - lblLogin.getWidth()) / 2);
        y = 100;
        lblLogin.setLocation(x, y);
        lblLogin.setText("");
        lblLogin.setForeground(new Color(255, 0, 51));
        pnlGnl.add(lblLogin);
        FrameDragListener frameDragListener = new FrameDragListener(this);
        pnlTitle.addMouseListener(frameDragListener);
        pnlTitle.addMouseMotionListener(frameDragListener);
    }//</editor-fold>

    //login action
    private void login() {
        Client c = new Client("127.0.0.1", 6427, new User(txtUserName.getText(), txtPass.getText()), this);
        c.start();
    }

    //<editor-fold defaultstate="collapsed" desc="Button shape">
    private static class RoundedBorder implements Border {

        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Move events">
    public static class FrameDragListener extends MouseAdapter {

        private final JFrame frame;
        private Point mouseDownCompCoords = null;

        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDownCompCoords = null;
        }

        public void mousePressed(MouseEvent e) {
            mouseDownCompCoords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        }
    }//</editor-fold>

}
