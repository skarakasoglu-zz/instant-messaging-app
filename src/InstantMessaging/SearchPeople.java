/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantMessaging;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Süleyman
 */
public class SearchPeople extends JDialog {

    //To make a better GUI for lists.
    // <editor-fold defaultstate="collapsed" desc="CellRenderer">
    class UserCellRenderer extends JPanel implements ListCellRenderer {

        private JPanel iconPanel;
        private JPanel textPanel;
        private JLabel lblIcon;
        private JLabel lblText;
        private JLabel lblStatus;
        private ImageIcon imageIcon;

        public UserCellRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.LEFT, 7, 5));
            setBorder(new EmptyBorder(0, 0, 0, 0));
            setBackground(Color.WHITE);
            setSize(340, 60);

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
            textPanel.setPreferredSize(new Dimension(250, 60));

            lblStatus = new JLabel();
            lblStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
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
            lblStatus.setText("");
            if (u.getIdUser() == user.getIdUser()) {
                lblStatus.setText("You");
                textPanel.add(lblStatus);
            } else {
                DefaultListModel<User> friends = (DefaultListModel<User>) clientForm.lstFriends.getModel();
                for (int i = 0; i < friends.size(); i++) {
                    User f = friends.get(i);
                    if (f.getIdUser() == u.getIdUser()) {
                        lblStatus.setText("You are already friends");
                        textPanel.add(lblStatus);
                    }
                }
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

    private JButton btnClose;
    private JTextField txtSearch;
    private final User user;
    private final Client client;
    private final ClientForm clientForm;
    private JList lstPeople;
    DefaultListModel<User> users;

    public SearchPeople(User user, Client client, ClientForm clientForm) {
        this.user = user;
        this.client = client;
        this.clientForm = clientForm;
        createForm();
    }

    //<editor-fold defaultstate="collapsed" desc="Creating Form">
    private void createForm() {
        setContentPane(new InstantMessaging.ShadowPane());
        InstantMessaging.ShadowPane sp = (InstantMessaging.ShadowPane) getContentPane();
        sp.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        // <editor-fold defaultstate="collapsed" desc="Creating Title Bar">
        Color titleBg = new Color(44, 189, 165);
        Border empt = BorderFactory.createEmptyBorder();
        JPanel pnlTitle = new JPanel();

        pnlTitle.setLayout(
                null);
        pnlTitle.setPreferredSize(
                new Dimension(340, 35));
        pnlTitle.setBackground(titleBg);

        sp.add(pnlTitle);
        JLabel title = new JLabel("Instant Messaging App");

        title.setSize(
                new Dimension(150, 24));
        title.setLocation(
                10, 5);
        title.setForeground(Color.WHITE);

        pnlTitle.add(title);
        Color clrHover = new Color(31, 176, 165);
        btnClose = new JButton();

        btnClose.setSize(
                30, 35);
        btnClose.setText(
                "X");
        btnClose.setLocation(
                310, 0);
        btnClose.setBackground(titleBg);

        btnClose.setBorder(empt);

        btnClose.setForeground(Color.WHITE);

        btnClose.setFocusPainted(
                false);
        btnClose.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e
            ) {
                dispose();
            }
        }
        );
        btnClose.addMouseListener(
                new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e
            ) {
            }

            @Override
            public void mousePressed(MouseEvent e
            ) {
            }

            @Override
            public void mouseReleased(MouseEvent e
            ) {
            }

            @Override
            public void mouseEntered(MouseEvent e
            ) {
                btnClose.setBackground(clrHover);
            }

            @Override
            public void mouseExited(MouseEvent e
            ) {
                btnClose.setBackground(titleBg);
            }

        }
        );
        btnClose.setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 14));
        pnlTitle.add(btnClose);
        FrameDragListener frameDragListener = new FrameDragListener(this);
        pnlTitle.addMouseListener(frameDragListener);
        pnlTitle.addMouseMotionListener(frameDragListener);
        //</editor-fold>
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnlSearch.setPreferredSize(new Dimension(340, 70));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(340, 50));
        txtSearch.setBorder(new EmptyBorder(5, 5, 5, 5));
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchKey(e);
            }

        });
        pnlSearch.add(txtSearch);
        lstPeople = new JList();
        lstPeople.setPreferredSize(new Dimension(340, 210));
        lstPeople.setCellRenderer(new UserCellRenderer());
        lstPeople.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listClicking(e);
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
        JScrollPane spPeople = new JScrollPane(lstPeople);
        spPeople.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spPeople.setPreferredSize(new Dimension(340, 215));
        spPeople.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.add(pnlSearch);
        sp.add(spPeople);
    }//</editor-fold>

    private void listClicking(MouseEvent e) {
        if (e.getClickCount() == 2) {
            User u = (User) lstPeople.getSelectedValue();
            boolean isFriend = false;
            DefaultListModel<User> friends = (DefaultListModel<User>) clientForm.lstFriends.getModel();
            for (int i = 0; i < friends.size(); i++) {
                User f = friends.get(i);
                if (f.getIdUser() == u.getIdUser()) {
                    isFriend = true;
                    break;
                }
            }

            if (!isFriend && u.getIdUser() != user.getIdUser()) {
                client.sendFriendRequest(u.getIdUser());
                JOptionPane.showMessageDialog(this, "Request sent.", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else if (isFriend) {
                JOptionPane.showMessageDialog(this, "You are already friends. You can't add a friend more than once!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "You can't add yourself", "Error", JOptionPane.ERROR_MESSAGE);

            }

        }
    }

    private void searchKey(ActionEvent e) {
        if (!txtSearch.getText().isEmpty()) {
            client.searchFriend(txtSearch.getText());
        } else {
            users = new DefaultListModel<>();
            lstPeople.setModel(users);
        }
    }

    void bringUsers(DefaultListModel<User> users) {
        this.users = users;
        lstPeople.setModel(users);
    }

    //<editor-fold defaultstate="collapsed" desc="Move events">
    public static class FrameDragListener extends MouseAdapter {

        private final JDialog dialog;
        private Point mouseDownCompCoords = null;

        public FrameDragListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDownCompCoords = null;
        }

        public void mousePressed(MouseEvent e) {
            mouseDownCompCoords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            dialog.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        }
    }//</editor-fold>
}
