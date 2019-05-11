package InstantMessaging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import InstantMessaging.InstantMessaging.ShadowPane;
import javax.swing.*;
import javax.swing.border.Border;

public class ChatScreen extends JFrame {

    //To make a better GUI for lists.
    // <editor-fold defaultstate="collapsed" desc="CellRenderer">
    class MessageCellRenderer extends JLabel implements ListCellRenderer {

        JPanel pnlItem;
        JPanel pnlContain;
        JPanel pnlMessage;
        JLabel lblMessage;

        public MessageCellRenderer() {
            setOpaque(true);
            pnlItem = new JPanel();
            pnlItem.setBackground(new Color(229, 221, 213));
            pnlItem.setSize(540, 30);
            pnlContain = new JPanel();
            pnlContain.setBackground(new Color(229, 221, 213));
            pnlMessage = new JPanel(new FlowLayout(FlowLayout.LEFT));
            lblMessage = new JLabel();
            lblMessage.setMaximumSize(new Dimension(540, 20));
            lblMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            pnlMessage.add(lblMessage);
            pnlContain.add(pnlMessage);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Message m = (Message) value;
            lblMessage.setText(m.getContent());
            lblMessage.validate();
            lblMessage.setHorizontalAlignment(JLabel.LEFT);

            if (m.getType() == Message.SYSTEM) {
                lblMessage.setForeground(new Color(94, 91, 79));
                pnlMessage.setBackground(new Color(254, 243, 196));
                pnlItem.setLayout(new FlowLayout(FlowLayout.CENTER));
                pnlContain.setLayout(new FlowLayout(FlowLayout.CENTER));
                pnlContain.setPreferredSize(new Dimension(540, pnlContain.getPreferredSize().height));
            } else if (current.getIdUser() == m.getSourceId()) {
                lblMessage.setForeground(new Color(38, 38, 38));
                pnlMessage.setBackground(new Color(220, 248, 198));
                pnlItem.setLayout(new FlowLayout(FlowLayout.RIGHT));
                pnlContain.setLayout(new FlowLayout(FlowLayout.RIGHT));
            } else if (friend.getIdUser() == m.getSourceId()) {
                lblMessage.setForeground(new Color(38, 38, 38));
                pnlMessage.setBackground(new Color(255, 255, 255));
                pnlItem.setLayout(new FlowLayout(FlowLayout.LEFT));
                pnlContain.setLayout(new FlowLayout(FlowLayout.LEFT));
            } else if (m.getType() == Message.HISTORY) {
                pnlMessage.setBackground(new Color(225, 242, 251));
                lblMessage.setHorizontalAlignment(JLabel.CENTER);
                pnlItem.setLayout(new FlowLayout(FlowLayout.CENTER));
                pnlContain.setLayout(new FlowLayout(FlowLayout.CENTER));
                pnlContain.setPreferredSize(new Dimension(540, pnlContain.getPreferredSize().height));
                lblMessage.setForeground(new Color(102, 121, 131));
            }
            pnlItem.add(pnlContain);

            return pnlItem;
        }

    }// </editor-fold>  

    User current, friend;
    private final ClientForm cf;
    private JTextArea txtMessage;
    private JButton btnSend;
    private SimpleDateFormat sdf;
    private JScrollPane spChat;
    private JButton btnMinimize, btnClose;
    private JLayeredPane lpMsg;
    private JList lstChat;
    private ShadowPane sp;
    JLabel lblStatus;
    DefaultListModel<Message> lstChatMsgs;

    //If a current user chooses a friend from onlines list,
    //This constructor is used.
    public ChatScreen(User current, User friend, ClientForm cf) {
        this.current = current;
        this.friend = friend;
        this.cf = cf;
        createForm();
        Message today = new Message();
        today.setContent("TODAY");
        today.setType(Message.HISTORY);
        lstChatMsgs.addElement(today);
        lstChat.setModel(lstChatMsgs);
    }

    //If chat screen is not opened, however, a friend sends a message to current user,
    //This constructor is used.
    public ChatScreen(User current, User friend, ClientForm cf, Message msg) {
        this.current = current;
        this.cf = cf;
        this.friend = friend;
        createForm();
        Message today = new Message();
        today.setContent("TODAY");
        today.setType(Message.HISTORY);
        lstChatMsgs.addElement(today);
        lstChatMsgs.addElement(msg);
        lstChat.setModel(lstChatMsgs);
    }

    // <editor-fold defaultstate="collapsed" desc="Creating Form">
    private void createForm() {
        setContentPane(new ShadowPane());
        sp = (ShadowPane) getContentPane();
        sp.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        // <editor-fold defaultstate="collapsed" desc="Creating Title Bar">
        Color titleBg = new Color(44, 189, 165);
        Border empt = BorderFactory.createEmptyBorder();
        JPanel pnlTitle = new JPanel();
        pnlTitle.setLayout(null);
        pnlTitle.setPreferredSize(new Dimension(565, 35));
        pnlTitle.setBackground(titleBg);
        ClientForm.FrameDragListener frameDragListener = new ClientForm.FrameDragListener(this);
        pnlTitle.addMouseListener(frameDragListener);
        pnlTitle.addMouseMotionListener(frameDragListener);
        sp.add(pnlTitle);
        JLabel title = new JLabel("Chat with " + friend.getLastName() + " " + friend.getFirstName() + " - " + current.getUserName());
        title.setSize(new Dimension(400, 24));
        title.setLocation(10, 5);
        title.setForeground(Color.WHITE);
        pnlTitle.add(title);
        Color clrHover = new Color(31, 176, 165);
        btnClose = new JButton();
        btnClose.setSize(30, 35);
        btnClose.setText("X");
        btnClose.setLocation(535, 0);
        btnClose.setBackground(titleBg);
        btnClose.setBorder(empt);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closingChat();
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
        btnMinimize.setLocation(505, 0);
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
        sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        //<editor-fold defaultstate="collapsed" desc="Friend look">
        JPanel pnlFriend = new JPanel();
        pnlFriend.setBackground(new Color(238, 238, 238));
        pnlFriend.setPreferredSize(new Dimension(565, 70));
        pnlFriend.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 5));
        sp.add(pnlFriend);
        JPanel pnlIcon = new JPanel(new BorderLayout(5, 0));
        pnlIcon.add(new JLabel(), BorderLayout.WEST);
        ImageIcon imageIcon = friend.getAvatar();
        Image img = cf.makeRoundedCorner(cf.getScaledImage(imageIcon.getImage(), 50, 50), 200);
        imageIcon = new ImageIcon(img);
        JLabel lblIcon = new JLabel(imageIcon);
        pnlIcon.add(lblIcon, BorderLayout.CENTER);
        pnlIcon.setPreferredSize(new Dimension(60, 60));
        pnlFriend.add(pnlIcon, BorderLayout.WEST);
        JPanel pnlText = new JPanel(new BorderLayout(0, -17));
        pnlText.setPreferredSize(new Dimension(360, 53));
        JLabel lblName = new JLabel(friend.getLastName() + " " + friend.getFirstName());
        lblName.setVerticalAlignment(JLabel.CENTER);
        lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        pnlText.add(lblName, BorderLayout.NORTH);
        lblStatus = new JLabel();
        if (friend.getIsOnline()) {
            lblStatus.setText("Online");
            lblStatus.setForeground(new Color(0, 51, 0));
        } else {
            SimpleDateFormat sdfLastSeen;
            SimpleDateFormat sdfLastSeenHour = new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            Date lastSeen = friend.getLastSeen();
            Calendar lastCal = Calendar.getInstance();
            lastCal.setTime(lastSeen);
            int yearDiff = calendar.get(Calendar.YEAR) - lastCal.get(Calendar.YEAR);
            int dayDiff = calendar.get(Calendar.DATE) - lastCal.get(Calendar.DATE);
            if (yearDiff == 0) {
                if (dayDiff == 1) {
                    lblStatus.setText("Offline - Last Seen Yesterday at " + sdfLastSeenHour.format(lastSeen));
                } else if (dayDiff == 0) {
                    lblStatus.setText("Offline - Last Seen Today at " + sdfLastSeenHour.format(lastSeen));
                } else if (dayDiff > 1 && dayDiff < 7) {
                    sdfLastSeen = new SimpleDateFormat("EEE");
                    lblStatus.setText("Offline - Last Seen " + sdfLastSeen.format(lastSeen) + " at " + sdfLastSeenHour.format(lastSeen));
                } else {
                    sdfLastSeen = new SimpleDateFormat("MMM d");
                    lblStatus.setText("Offline - Last Seen " + sdfLastSeen.format(lastSeen) + " at " + sdfLastSeenHour.format(lastSeen));
                }
            }
            lblStatus.setForeground(Color.red);
        }
        lblStatus.setFont(new Font(Font.SANS_SERIF, Font.TRUETYPE_FONT, 12));
        pnlText.add(lblStatus, BorderLayout.CENTER);
        pnlFriend.add(pnlText);
        JPanel pnlMenu = new JPanel(new BorderLayout(0, 0));
        ImageIcon imgIconP = new ImageIcon(getClass().getResource("../images/plus.png"));
        Image imgP = cf.getScaledImage(imgIconP.getImage(), 30, 30);
        imgIconP = new ImageIcon(imgP);
        JButton btnAdd = new JButton(imgIconP);
        btnAdd.setBackground(pnlFriend.getBackground());
        btnAdd.setToolTipText("Add new friend");
        btnAdd.setBorder(empt);
        btnAdd.setFocusPainted(false);
        pnlMenu.add(btnAdd, BorderLayout.CENTER);
        btnAdd.addMouseListener(new MouseAdapter() {
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
        imgIconP = new ImageIcon(getClass().getResource("../images/dot.png"));
        imgP = cf.getScaledImage(imgIconP.getImage(), 38, 38);
        imgIconP = new ImageIcon(imgP);
        JButton btnMore = new JButton(imgIconP);
        btnMore.setBackground(pnlFriend.getBackground());
        btnMore.setVerticalAlignment(JButton.CENTER);
        btnMore.setBorder(empt);
        btnMore.setFocusPainted(false);
        btnMore.addMouseListener(new MouseAdapter() {
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
        pnlMenu.setPreferredSize(new Dimension(120, 60));
        pnlMenu.add(btnMore, BorderLayout.EAST);
        pnlFriend.add(pnlMenu);
        // </editor-fold>
        lstChat = new JList();
        spChat = new JScrollPane(lstChat);
        spChat.setPreferredSize(new Dimension(565, 445));
        spChat.setBackground(new Color(229, 221, 213));
        spChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        lstChat.setBackground(new Color(229, 221, 213));
        lstChatMsgs = new DefaultListModel<>();
        lstChat.setCellRenderer(new MessageCellRenderer());
        spChat.setBorder(empt);
        sp.add(spChat);
        lpMsg = new JLayeredPane();
        lpMsg.setBackground(new Color(245, 241, 238));
        lpMsg.setPreferredSize(new Dimension(565, 85));
        lpMsg.setOpaque(true);
        sp.add(lpMsg);
        txtMessage = new JTextArea();;;
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setBackground(Color.WHITE);
        txtMessage.setEnabled(true);
        txtMessage.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane spC = new JScrollPane(txtMessage);
        spC.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spC.setSize(450, 65);
        spC.setLocation(50, 13);
        spC.setBorder(empt);
        spC.setViewportView(txtMessage);

        txtMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtMessage.setText("");
                }
            }

        });
        lpMsg.add(spC, 0);
        imgIconP = new ImageIcon(getClass().getResource("../images/send.png"));
        imgP = cf.getScaledImage(imgIconP.getImage(), 30, 30);
        imgIconP = new ImageIcon(imgP);
        btnSend = new JButton(imgIconP);
        btnSend.setBackground(pnlFriend.getBackground());
        btnSend.setVerticalAlignment(JButton.CENTER);
        btnSend.setBorder(empt);
        btnSend.setFocusPainted(false);
        btnSend.addMouseListener(new MouseAdapter() {
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
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }

        });
        btnSend.setLocation(515, 30);
        btnSend.setSize(30, 30);
        btnSend.setBackground(lpMsg.getBackground());
        lpMsg.add(btnSend, 2);
        txtMessage.requestFocus();
    }//</editor-fold> 

    //Send message to a friend
    private void sendMessage() {
        //The friend a current user wants to send message may be online or offline
        boolean on = false;
        for (int i = 0; i < cf.onlines.size(); i++) {
            if (cf.onlines.get(i).getIdUser() == friend.getIdUser()) {
                on = true;
            }
        }
        if (!txtMessage.getText().equals("")) {
            //If the friend the current user sends message gets offline
            //They shouln't get the message notice.
            if (on) {
                Message newMessage = new Message();
                newMessage.setDestinationId(friend.getIdUser());
                newMessage.setSourceId(current.getIdUser());
                newMessage.setType(Message.MESSAGE);
                newMessage.setContent(txtMessage.getText());
                newMessage.setSendingTime(new java.sql.Date(new Date().getTime()));
                cf.client.sendMessage(newMessage);
                lstChatMsgs.addElement(newMessage);
                lstChat.setModel(lstChatMsgs);
                JScrollBar vertical = spChat.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
                txtMessage.setText("");
                txtMessage.requestFocus();
            } else {
                Message newMessage = new Message();
                newMessage.setType(Message.SYSTEM);
                newMessage.setContent(friend.getLastName() + " " + friend.getFirstName()
                        + " is currently offline. They will receive your message the next time they log in.");
                newMessage.setSendingTime(new java.sql.Date(new Date().getTime()));
                lstChatMsgs.addElement(newMessage);
                lstChat.setModel(lstChatMsgs);
                JScrollBar vertical = spChat.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
                friend.setIsOnline(false);
            }
        }
    }

    //When friend sends to a message
    //Current user gets it
    void getMessage(Message msg) {
        lstChatMsgs.addElement(msg);
        lstChat.setModel(lstChatMsgs);
        JScrollBar vertical = spChat.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
        requestFocus();
    }

    //If you close a chat screen, 
    //we need to know about it, if the friend you close chat screen with 
    private void closingChat() {
        cf.chats.remove(this);
        dispose();
    }

}
