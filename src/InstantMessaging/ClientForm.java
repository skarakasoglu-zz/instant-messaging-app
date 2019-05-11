/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantMessaging;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import InstantMessaging.InstantMessaging.ShadowPane;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Süleyman
 */
public class ClientForm extends JFrame {

    //To make a better GUI for lists.
    // <editor-fold defaultstate="collapsed" desc="CellRenderer">
    class UserCellRenderer extends JPanel implements ListCellRenderer {

        private JPanel iconPanel;
        private JPanel textPanel;
        private JLabel lblIcon;
        private JLabel lblText;
        private JLabel lblStatus;
        private JLabel lblP;
        private ImageIcon imageIcon;

        public UserCellRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            setBorder(new EmptyBorder(0, 0, 0, 0));
            setBackground(Color.WHITE);
            setSize(390, 60);

            // icon
            iconPanel = new JPanel();
            iconPanel.setLayout(new BorderLayout(10, 0));
            iconPanel.setBackground(Color.WHITE);
            iconPanel.setPreferredSize(new Dimension(55, 60));
            lblIcon = new JLabel(); // <-- this will be an icon instead of a
            // text
            iconPanel.add(lblIcon, BorderLayout.CENTER);
            add(iconPanel);

            // text
            textPanel = new JPanel();
            Border brd = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(244, 245, 245));
            textPanel.setBorder(brd);
            textPanel.setBackground(Color.WHITE);
            textPanel.setLayout(new BorderLayout(0, -20));
            lblText = new JLabel();
            lblText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            lblText.setVerticalAlignment(JLabel.CENTER);
            textPanel.add(lblText, BorderLayout.NORTH);
            lblStatus = new JLabel();
            lblStatus.setFont(new Font(Font.SANS_SERIF, Font.TRUETYPE_FONT, 12));
            textPanel.add(lblStatus, BorderLayout.CENTER);
            textPanel.setPreferredSize(new Dimension(325, 60));
            add(textPanel);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            User u = (User) value;
            lblText.setText(u.getLastName() + " " + u.getFirstName());
            imageIcon = u.getAvatar();
            Image img = makeRoundedCorner(getScaledImage(imageIcon.getImage(), 50, 50), 200);
            imageIcon = new ImageIcon(img);
            lblIcon.setIcon(imageIcon);
            if (u.getIsOnline()) {
                lblStatus.setText("Online");
                lblStatus.setForeground(new Color(0, 51, 0));
            } else {
                SimpleDateFormat sdfLastSeen;
                SimpleDateFormat sdfLastSeenHour = new SimpleDateFormat("HH:mm");
                Calendar calendar = Calendar.getInstance();
                Date lastSeen = u.getLastSeen();
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
                        lblStatus.setText("Offline - Last Seen "
                                + sdfLastSeen.format(lastSeen) + " at " + sdfLastSeenHour.format(lastSeen));
                    } else {
                        sdfLastSeen = new SimpleDateFormat("MMM d");
                        lblStatus.setText("Offline - Last Seen "
                                + sdfLastSeen.format(lastSeen) + " at " + sdfLastSeenHour.format(lastSeen));
                    }
                }
                lblStatus.setForeground(Color.red);
            }

            if (isSelected) {
                setBackground(new Color(244, 245, 245));
                iconPanel.setBackground(new Color(244, 245, 245));
                textPanel.setBackground(new Color(244, 245, 245));
            } else {
                setBackground(Color.WHITE);
                iconPanel.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
            }
            return this;
        }

    }// </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Image processing">
    BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)
        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    BufferedImage getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(srcImg, 0, 0, w, h, null);
        g2d.dispose();

        return resizedImage;
    }//</editor-fold>

    //Local variables--start
    private JButton btnMinimize, btnClose;
    public User user;
    public ArrayList<ChatScreen> chats;
    Client client;
    private JPanel pnlFriends;
    JList lstFriends;
    DefaultListModel<User> onlines, offlines;
    private JFrame fNotice;
    private User selectedFriend;
    private JPanel pnlUser;
    private DefaultListModel<User> friends;
    SearchPeople addScreen;
    FriendRequests fr;
    //Local variables--end

    public ClientForm(User user, Client client) {
        super(user.getUserName());
        this.user = user;
        this.client = client;
        createForm();
        Message msg = new Message();
        msg.setType(Message.ONLINES);
        msg.setSourceId(user.getIdUser());
        client.sendMessage(msg);
        chats = new ArrayList<>();
    }

    // <editor-fold defaultstate="collapsed" desc="Creating Form">
    private void createForm() {
        setContentPane(new ShadowPane());
        ShadowPane sp = (ShadowPane) getContentPane();
        sp.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        // <editor-fold defaultstate="collapsed" desc="Creating Title Bar">
        Color titleBg = new Color(44, 189, 165);
        Border empt = BorderFactory.createEmptyBorder();
        JPanel pnlTitle = new JPanel();
        pnlTitle.setLayout(null);
        pnlTitle.setPreferredSize(new Dimension(390, 35));
        pnlTitle.setBackground(titleBg);
        FrameDragListener frameDragListener = new FrameDragListener(this);
        pnlTitle.addMouseListener(frameDragListener);
        pnlTitle.addMouseMotionListener(frameDragListener);
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
        btnClose.setLocation(360, 0);
        btnClose.setBackground(titleBg);
        btnClose.setBorder(empt);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logOutActionPerformed();
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
        btnMinimize.setLocation(330, 0);
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
        //User's own section start
        pnlUser = new JPanel(new BorderLayout(10, 15));
        pnlUser.setBackground(new Color(238, 238, 238));
        pnlUser.setPreferredSize(new Dimension(390, 80));
        sp.add(pnlUser);
        JPanel pnlIcon = new JPanel(new BorderLayout(10, 0));
        ImageIcon imageIcon = user.getAvatar();
        Image img = makeRoundedCorner(getScaledImage(imageIcon.getImage(), 50, 50), 200);
        imageIcon = new ImageIcon(img);
        JLabel lblIcon = new JLabel(imageIcon);
        lblIcon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeAvatarDialog();
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
        pnlIcon.add(new JLabel(), BorderLayout.WEST);
        pnlIcon.add(lblIcon, BorderLayout.CENTER);
        pnlUser.add(pnlIcon, BorderLayout.WEST);
        JPanel pnlText = new JPanel(new BorderLayout(0, -15));
        JLabel lblName = new JLabel(user.getLastName() + " " + user.getFirstName());
        pnlUser.add(new JLabel(), BorderLayout.NORTH);
        lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        pnlText.add(lblName, BorderLayout.NORTH);
        JLabel lblStatus = new JLabel("Online");
        lblStatus.setForeground(new Color(0, 51, 0));
        lblStatus.setFont(new Font(Font.SANS_SERIF, Font.TRUETYPE_FONT, 12));
        pnlText.add(lblStatus, BorderLayout.CENTER);
        pnlUser.add(pnlText, BorderLayout.CENTER);
        pnlUser.add(new JLabel(), BorderLayout.SOUTH);
        JPanel pnlMenu = new JPanel(new BorderLayout(0, 40));
        ImageIcon imgIconP = new ImageIcon(getClass().getResource("../images/plus.png"));
        Image imgP = getScaledImage(imgIconP.getImage(), 30, 30);
        imgIconP = new ImageIcon(imgP);
        JButton btnAdd = new JButton(imgIconP);
        btnAdd.setBackground(pnlUser.getBackground());
        btnAdd.setToolTipText("Add new friend");
        btnAdd.setBorder(empt);
        btnAdd.setSize(38, 38);
        btnAdd.setFocusPainted(false);
        pnlMenu.add(btnAdd, BorderLayout.WEST);
        btnAdd.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                searchPeopleScreen();
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
        imgP = getScaledImage(imgIconP.getImage(), 38, 38);
        imgIconP = new ImageIcon(imgP);
        JButton btnRequests = new JButton(imgIconP);
        btnRequests.setBackground(pnlUser.getBackground());
        btnRequests.setVerticalAlignment(JButton.CENTER);
        btnRequests.setBorder(empt);
        btnRequests.setSize(38, 38);
        btnRequests.setFocusPainted(false);
        btnRequests.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                friendRequestsScreen();
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
        pnlMenu.add(btnRequests, BorderLayout.CENTER);
        pnlMenu.add(new JLabel(), BorderLayout.EAST);
        pnlUser.add(pnlMenu, BorderLayout.EAST);
        // User's own section end
        //Friendlist start
        pnlFriends = new JPanel();
        pnlFriends.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        pnlFriends.setPreferredSize(new Dimension(390, 675));
        pnlFriends.setBackground(Color.WHITE);
        sp.add(pnlFriends);
        lstFriends = new JList();
        JScrollPane spFriends = new JScrollPane(lstFriends);
        spFriends.setPreferredSize(new Dimension(390, 655));
        spFriends.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spFriends.setBorder(empt);
        pnlFriends.add(spFriends);
        lstFriends.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                friendSelectionChanged(e);
            }
        });
        lstFriends.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                onlinesMouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        lstFriends.setCellRenderer(
                new UserCellRenderer());
        //Friend list end

    }//</editor-fold>   

    private void changeAvatarDialog() {
        ChangeAvatar ca = new ChangeAvatar(user, client, this);
        ca.setUndecorated(true);
        ca.setBackground(new Color(0, 0, 0, 0));
        ca.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        ca.setSize(450, 200);
        int x = (int) (rect.getMaxX() - ca.getWidth()) / 2;
        int y = (int) (rect.getMaxY() - ca.getHeight()) / 2;
        ca.setLocation(x, y - 30);
        ca.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        ca.setVisible(true);

    }

    //Create new FriendRequests for looking at friend requests
    private void friendRequestsScreen() {
        fr = new FriendRequests(user, client, this);
        client.getFriendRequests();
        fr.setUndecorated(true);
        fr.setBackground(new Color(0, 0, 0, 0));
        fr.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        fr.setSize(420, 380);
        int x = (int) (rect.getMaxX() - fr.getWidth()) / 2;
        int y = (int) (rect.getMaxY() - fr.getHeight()) / 2;
        fr.setLocation(x, y - 30);
        fr.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        fr.setVisible(true);
    }

    //Create new AddFriend
    private void searchPeopleScreen() {
        addScreen = new SearchPeople(this.user, this.client, this);
        addScreen.setUndecorated(true);
        addScreen.setBackground(new Color(0, 0, 0, 0));
        addScreen.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        addScreen.setSize(350, 330);
        int x = (int) (rect.getMaxX() - addScreen.getWidth()) / 2;
        int y = (int) (rect.getMaxY() - addScreen.getHeight()) / 2;
        addScreen.setLocation(x, y - 30);
        addScreen.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        addScreen.setVisible(true);
    }

    //List friends with respect to their status
    void obtainFriends(DefaultListModel<User> onlines, DefaultListModel<User> offlines
    ) {
        this.onlines = onlines;
        this.offlines = offlines;
        friends = new DefaultListModel<>();
        for (int i = 0; i < onlines.size(); i++) {
            User u = onlines.get(i);
            u.setIsOnline(true);

            for (ChatScreen chat : chats) {
                if (chat.friend.getIdUser() == u.getIdUser()) {
                    chat.lblStatus.setText("Online");
                    chat.lblStatus.setForeground(new Color(0, 51, 0));
                }
            }
            friends.addElement(u);
        }
        for (int i = 0; i < offlines.size(); i++) {
            User u = offlines.get(i);
            u.setIsOnline(false);

            for (ChatScreen chat : chats) {
                if (chat.friend.getIdUser() == u.getIdUser()) {
                    // Show last seen 
                    SimpleDateFormat sdfLastSeen;
                    SimpleDateFormat sdfLastSeenHour = new SimpleDateFormat("HH:mm");
                    Calendar calendar = Calendar.getInstance();
                    Date lastSeen = u.getLastSeen();
                    Calendar lastCal = Calendar.getInstance();
                    lastCal.setTime(lastSeen);
                    int yearDiff = calendar.get(Calendar.YEAR) - lastCal.get(Calendar.YEAR);
                    int dayDiff = calendar.get(Calendar.DATE) - lastCal.get(Calendar.DATE);
                    if (yearDiff == 0) {
                        if (dayDiff == 1) {
                            chat.lblStatus.setText("Offline - Last Seen Yesterday at " + sdfLastSeenHour.format(lastSeen));
                        } else if (dayDiff == 0) {
                            chat.lblStatus.setText("Offline - Last Seen Today at " + sdfLastSeenHour.format(lastSeen));
                        } else if (dayDiff > 1 && dayDiff < 7) {
                            sdfLastSeen = new SimpleDateFormat("EEE");
                            chat.lblStatus.setText("Offline - Last Seen "
                                    + sdfLastSeen.format(lastSeen) + " at " + sdfLastSeenHour.format(lastSeen));
                        } else {
                            sdfLastSeen = new SimpleDateFormat("MMM d");
                            chat.lblStatus.setText("Offline - Last Seen "
                                    + sdfLastSeen.format(lastSeen) + " at " + sdfLastSeenHour.format(lastSeen));
                        }
                    }
                    chat.lblStatus.setForeground(Color.red);
                }
            }
            friends.addElement(u);
        }
        lstFriends.setModel(friends);
    }

    //Logout and close
    private void logOutActionPerformed() {
        int result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to exit the application?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Message msg = new Message();
            msg.setType(Message.LOGOUT);
            client.sendMessage(msg);
            System.exit(0);
        }
    }

    private void friendSelectionChanged(ListSelectionEvent e) {
        selectedFriend = (User) ((JList) (e.getSource())).getSelectedValue();
    }

    //Create new chat screen with a friend current user chooses
    private void onlinesMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            User choosen = (User) lstFriends.getSelectedValue();
            ChatScreen cs = null;
            for (int i = 0; i < chats.size(); i++) {
                ChatScreen chat = chats.get(i);
                if (chat.friend.getIdUser() == choosen.getIdUser()) {
                    cs = chat;
                    break;
                }
            }
            if (cs == null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        newChatScreen();
                    }
                });
            } else {
                cs.setState(Frame.NORMAL);
                cs.toFront();
            }
        }
    }

    //Creating new ChatScreen due to selection
    private void newChatScreen() {
        ChatScreen cs = new ChatScreen(user, selectedFriend, this);
        cs.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cs.setUndecorated(true);
        cs.setBackground(new Color(0, 0, 0, 0));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        cs.setSize(575, 650);
        int x = (int) (rect.getMaxX() - cs.getWidth()) / 2;
        int y = (int) (rect.getMaxY() - cs.getHeight()) / 2;
        cs.setLocation(x, y - 30);
        cs.setVisible(true);
        chats.add(cs);
    }

    //Creating new ChatScreen due to a new message
    private void newMessageChatScreen(User source, Message msg) {
        ChatScreen cs = new ChatScreen(user, source, this, msg);
        cs.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cs.setUndecorated(true);
        cs.setBackground(new Color(0, 0, 0, 0));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        cs.setSize(575, 650);
        int x = (int) (rect.getMaxX() - cs.getWidth()) / 2;
        int y = (int) (rect.getMaxY() - cs.getHeight()) / 2;
        cs.setLocation(x, y - 30);
        cs.setVisible(true);
        chats.add(cs);

    }

    //New messages from friends
    void newMessage(final Message msg) {
        ChatScreen cs = null;
        for (int i = 0; i < chats.size(); i++) {
            ChatScreen chat = chats.get(i);
            if (chat.friend.getIdUser() == msg.getSourceId()) {
                cs = chat;
            }
        }
        //If client doesn't have a chat screen with the friend who sends the message
        //create new chat screen.
        if (cs == null) {
            final User source = user.findUser(msg.getSourceId());
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    newMessageChatScreen(source, msg);
                }
            });
            //While creating new chat screen, 
            //pop-up a notification frame for 5 seconds..
            fNotice = new JFrame();
            fNotice.setUndecorated(true);
            fNotice.pack();
            fNotice.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            //x = (int) rect.getMaxX() - fNotice.getWidth();
            //y = (int) (rect.getMaxY() - fNotice.getHeight()) - 35;
            Timer not = new Timer();
            TimerTask gorev = new TimerTask() {
                int say = 0;

                public void run() {
                    say++;
                    if (say == 5) {
                        fNotice.setVisible(false);
                        not.cancel();
                    }
                }
            };
            //fNotice.setLocation(x, y);
            fNotice.setTitle(source.getLastName() + " " + source.getFirstName());
            fNotice.setVisible(true);
            not.schedule(gorev, 0, 1000);
            fNotice.toFront();
        } else {
            cs.getMessage(msg);

        }
        //System.out.println(userChat.toString());
        //System.out.println(msg.getContent());
    }

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
