/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantMessaging;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Süleyman
 */
public class Client {

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private SimpleDateFormat sdf;
    private Socket socket;
    private ClientForm cf;
    private ClientLoginScreen cls;
    private final String server;
    private User user;
    private final int port;

    Client(String server, int port, User user, ClientLoginScreen cls) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.cls = cls;
    }

    public boolean start() {

        try {
            socket = new Socket(server, port);
        } catch (IOException ex) {
            display(ex.toString());
            return false;
        }

        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            display(ex.toString());
            return false;
        }

        new ListenFromServer(this).start();
        try {
            sOutput.writeObject(user);
        } catch (IOException ex) {
            display(ex.toString());
            return false;
        }
        return true;
    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        System.out.println(time);
    }

    void sendMessage(Message msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException ex) {
            display(ex.toString());
        }
    }

    void getFriendRequests() {
        try {
            Message msg = new Message();
            msg.setType(Message.GETREQUEST);
            sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void sendFriendRequest(int id) {
        Message msg = new Message();
        msg.setType(Message.SENDREQUEST);
        msg.setDestinationId(id);
        try {
            sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void acceptFriendRequest(int selected) {
        Message m = new Message();
        m.setType(Message.ACCEPTREQUEST);
        m.setSourceId(selected);
        try {
            sOutput.writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void rejectRequest(int selected) {
        Message m = new Message();
        m.setType(Message.REJECTREQUEST);
        m.setSourceId(selected);
        try {
            sOutput.writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void searchFriend(String searchText) {
        Message s = new Message();
        s.setType(Message.SEARCH);
        s.setContent(searchText);

        try {
            sOutput.writeObject(s);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void changeAvatar(File file, String ext) {
        try {
            BufferedImage img = ImageIO.read(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(img, ext, baos);
            baos.flush();

            byte[] bytes = baos.toByteArray();
            baos.close();

            Message msg = new Message();
            msg.setType(Message.AVATAR);
            sOutput.writeObject(msg);

            sOutput.writeInt(bytes.length);
            sOutput.write(bytes, 0, bytes.length);
            sOutput.writeUTF(ext);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void disconnect() {
        try {
            if (sInput != null) {
                sInput.close();
            }
        } catch (IOException ex) {
        }
        try {
            if (sOutput != null) {
                sOutput.close();
            }
        } catch (IOException ex) {
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
        }
    }

    class ListenFromServer extends Thread {

        private Client c;

        ListenFromServer(Client c) {
            this.c = c;
        }

        public void run() {
            while (true) {
                try {
                    Object o = sInput.readObject();
                    if (o != null) {
                        String className = o.getClass().getSimpleName();
                        switch (className) {
                            //<editor-fold defaultstate="collapsed" desc="If another client loggins to the same account">
                            case "String":
                                Message mg = new Message();
                                mg.setType(Message.LOGOUT);
                                sOutput.writeObject(mg);
                                InstantMessaging.main(new String[]{(String) o});
                                c.cf.dispose();
                                break;//</editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="New message from a friend">
                            case "Message":
                                Message msg = (Message) o;
                                cf.newMessage(msg);
                                break;//</editor-fold>
                            //<editor-fold defaultstate="collapsed" desc="New people searching">
                            case "DefaultListModel":
                                if (cf.addScreen != null && cf.addScreen.isVisible()) {
                                    DefaultListModel<User> users = (DefaultListModel<User>) o;
                                    cf.addScreen.bringUsers(users);
                                } else if (cf.fr != null && cf.fr.isVisible()) {
                                    DefaultListModel<User> requests = (DefaultListModel<User>) o;
                                    cf.fr.loadRequests(requests);
                                }
                                break;//</editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="Obtaining friends">
                            case "ArrayList":
                                ArrayList<DefaultListModel> lof = (ArrayList<DefaultListModel>) o;
                                // Online friends
                                DefaultListModel<User> dlmOnlines = lof.get(0);
                                //Offline friends
                                DefaultListModel<User> dlmOfflines = lof.get(1);
                                cf.obtainFriends(dlmOnlines, dlmOfflines);
                                break;// </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="New login or friend on-off status">
                            case "User":
                                User in = (User) o;
                                //If username isn't empty, client should be logged in.
                                if (!in.getUserName().isEmpty()) {
                                    //If the username of the client is not the same with incoming user's
                                    //then it should be an update of a friend's status.
                                    if (!user.getUserName().equals(in.getUserName())) {
                                        User on = (User) o;
                                        DefaultListModel<User> ons = cf.onlines;
                                        DefaultListModel<User> offs = cf.offlines;
                                        //If incoming user got online, remove it from offlines, add it to onlines.
                                        if (on.getIsOnline()) {
                                            ons.addElement(on);
                                            for (int i = 0; i < offs.size(); i++) {
                                                User onl = offs.get(i);
                                                if (onl.getIdUser() == on.getIdUser()) {
                                                    offs.remove(i);
                                                }
                                            }//Then, list them with respect to their last names and first names.
                                            if (ons.size() != 1) {
                                                for (int i = ons.size() - 1; i >= 0; i--) {
                                                    User u = ons.get(i);
                                                    String name = u.getLastName() + u.getFirstName();
                                                    for (int j = i - 1; j >= 0; j--) {
                                                        User a = ons.get(j);
                                                        String name1 = a.getLastName() + a.getFirstName();
                                                        if (name.compareToIgnoreCase(name1) < 0) {
                                                            ons.set(j, u);
                                                            ons.set(i, a);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } //If incoming user got offline, remove it from onlines, add it to offlines.                                        
                                        else {
                                            offs.addElement(on);
                                            for (int i = 0; i < ons.size(); i++) {
                                                User onl = ons.get(i);
                                                if (onl.getIdUser() == on.getIdUser()) {
                                                    ons.remove(i);
                                                }
                                            }//Then, list them with respect to their last names and first names.
                                            if (offs.size() != 1) {
                                                for (int i = offs.size() - 1; i >= 0; i--) {
                                                    User u = offs.get(i);
                                                    for (int j = i - 1; j >= 0; j--) {
                                                        User a = offs.get(j);
                                                        if (u.getLastName().compareToIgnoreCase(a.getLastName()) < 0) {
                                                            offs.set(j, u);
                                                            offs.set(i, a);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        cf.obtainFriends(cf.onlines, cf.offlines);
                                    } /*If not then a client is logging in at the moment.*/ else {
                                        user = (User) o;
                                        cls.lblLogin.setText("Successfully logined.");
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                cf = new ClientForm(user, c);
                                                cf.setUndecorated(true);
                                                cf.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                                                cf.setBackground(new Color(0, 0, 0, 0));
                                                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                                                GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
                                                Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
                                                cf.setSize(400, 800);
                                                int x = (int) (rect.getMaxX() - cf.getWidth()) / 2;
                                                int y = (int) (rect.getMaxY() - cf.getHeight()) / 2;
                                                cf.setLocation(x, y - 30);
                                                cf.setVisible(true);
                                            }
                                        });
                                        cls.setVisible(false);
                                    }
                                } //If username is empty, then there is no valid user entered
                                else {
                                    JOptionPane.showMessageDialog(c.cls, "Login Failed");
                                }
                                break;//</editor-fold>
                            default:
                                break;
                        }
                    } else {
                        cls.lblLogin.setText("Login failed, try again.");
                    }
                } catch (IOException ex) {
                    display(ex.toString());
                } catch (ClassNotFoundException ex) {
                    display(ex.toString());
                } catch (Exception ex) {
                    display(ex.toString());
                }
            }
        }
    }
}
