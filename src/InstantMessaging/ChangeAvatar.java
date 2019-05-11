/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantMessaging;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Süleyman
 */
public class ChangeAvatar extends JDialog {

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

    private final User user;
    private final ClientForm clientForm;
    private final Client client;
    private JTextField txtFile;
    private JButton btnClose;
    private JFileChooser chooseFile = new JFileChooser();
    private File selectedFile;

    public ChangeAvatar(User user, Client client, ClientForm clientForm) {
        this.user = user;
        this.clientForm = clientForm;
        this.client = client;
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
                new Dimension(440, 35));
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
                410, 0);
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
        JPanel pnlChange = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlChange.setPreferredSize(new Dimension(440, 155));
        pnlChange.setBackground(new Color(238, 238, 238));
        sp.add(pnlChange);
        JPanel pnlAvatar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        ImageIcon imageIcon = user.getAvatar();
        Image img = makeRoundedCorner(getScaledImage(imageIcon.getImage(), 100, 100), 200);
        imageIcon = new ImageIcon(img);
        JLabel lblIcon = new JLabel(imageIcon);
        pnlAvatar.setPreferredSize(new Dimension(110, 120));
        pnlAvatar.add(lblIcon);
        pnlChange.add(pnlAvatar);
        JPanel pnlFile = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        txtFile = new JTextField();
        txtFile.setEditable(false);
        txtFile.setBackground(Color.WHITE);
        txtFile.setPreferredSize(new Dimension(300, 24));
        pnlFile.add(txtFile);
        JButton btnFile = new JButton("Browse");
        btnFile.setPreferredSize(new Dimension(100, 30));
        btnFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDialog();
            }

        });
        JButton btnUpload = new JButton("Upload");
        btnUpload.setPreferredSize(new Dimension(100, 30));
        btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadFile();
            }

        });
        pnlFile.add(btnFile);
        pnlFile.add(btnUpload);
        pnlFile.setPreferredSize(new Dimension(320, 100));
        pnlChange.add(pnlFile);
    }// </editor-fold>

    private void openDialog() {
        int returnVal = chooseFile.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooseFile.getSelectedFile();
            txtFile.setText(selectedFile.getPath());
        }
    }

    private void uploadFile() {
        String ext = FilenameUtils.getExtension(selectedFile.getPath());
        if (ext.equals("png") || ext.equals("jpg")) {
            client.changeAvatar(selectedFile, ext);
        }
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
