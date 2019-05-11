/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantMessaging;

import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Süleyman
 */
public class Server {

    private static int uniqueId;
    private int port;
    private ArrayList<Handler> all;
    private boolean keepGoing;
    private SimpleDateFormat sdf;

    public Server(int port) {
        this.port = port;
        sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        all = new ArrayList<>();
    }

    public void start() {
        keepGoing = true;

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (keepGoing) {

                display("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();

                if (!keepGoing) {
                    break;
                }
                Handler h = new Handler(socket);
                all.add(h);
                h.start();

            }

            try {
                serverSocket.close();
                for (int i = 0; i < all.size(); ++i) {
                    Handler hnd = all.get(i);
                    try {
                        hnd.sInput.close();
                        hnd.sOutput.close();
                        hnd.socket.close();
                        remove(i);
                    } catch (IOException e) {
                        break;
                    }
                }
            } catch (Exception ex) {
            }
        } catch (IOException e) {
            display("Exception " + e);
        }
    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        System.out.println(time);
    }

    private synchronized void broadcast(Message message, String sender) throws IOException {

        for (int i = 0; i < all.size(); i++) {
            Handler hnd = all.get(i);
            if (message.getDestinationId() == hnd.user.getIdUser()) {
                if (!hnd.writeMsg(message, sender)) {
                    all.remove(i);
                    display("Disconnected client " + hnd.user.getUserName() + " removed from list.");;
                }
            }
        }
    }

    private synchronized void broadcastStatus(DefaultListModel<User> friends, User user) throws IOException {

        for (int i = 0; i < all.size(); i++) {
            Handler hnd = all.get(i);
            for (int j = 0; j < friends.size(); j++) {
                User fr = friends.get(j);
                if (fr.getIdUser() == hnd.user.getIdUser()) {
                    User u = new User();
                    u.setIdUser(user.getIdUser());
                    u.setUserName(user.getUserName());
                    u.setLastName(user.getLastName());
                    u.setFirstName(user.getFirstName());
                    u.setIsOnline(user.getIsOnline());
                    u.setAvatar(user.getAvatar());
                    u.setLastSeen(user.getLastSeen());
                    if (!hnd.onlineMsg(u)) {
                        all.remove(i);
                        display("Disconnected client " + hnd.user.getUserName() + " removed from list.");;
                    }
                }
            }
        }
    }

    private synchronized void broadcastStatus(Handler hnd, User user) throws IOException {
        hnd.onlineMsg(user);
    }

    synchronized void remove(int id) {
        for (int i = 0; i < all.size(); i++) {
            Handler h = all.get(i);
            if (h.id == id) {
                all.remove(i);
                return;
            }
        }
    }

    public static void main(String args[]) {
        int portNumber = 6427;
        switch (args.length) {
            case 1:
                try {
                    portNumber = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Invalid port number");
                    System.out.println("Usage is : > java Server [portNumber]");
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is : > java Server [portNumber]");
                return;

        }

        Server server = new Server(portNumber);
        server.start();

    }

    class Handler extends Thread {

        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        Message msg;
        int id;
        User user;
        DefaultListModel<User> onlineFriends;
        DefaultListModel<User> friends;
        DefaultListModel<User> offlineFriends;

        Handler(Socket socket) {
            this.socket = socket;
            display("Thread trying to create Object Input/Output Streams");
            id = ++uniqueId;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());

                user = (User) sInput.readObject();
                user = userLogin();
                for (int i = 0; i < all.size(); i++) {
                    if (user.getIdUser() == all.get(i).user.getIdUser() && this.id != all.get(i).id) {
                        all.get(i).close();
                        all.remove(i);
                    }
                }
                sOutput.writeObject(user);
                if (!user.getUserName().isEmpty()) {
                    display(user.getUserName() + " just connected.");
                    findFriends();
                    broadcastStatus(friends, this.user);
                } else {
                    display("A user Failed to login.");
                    close();
                }

            } catch (IOException e) {
                display("Exception creating new Input/Output streams." + e);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void run() {
            boolean keepGoing = true;
            while (keepGoing) {

                try {
                    msg = (Message) sInput.readObject();
                } catch (IOException e) {
                    display(" exception reading streams: " + e);
                    break;
                } catch (ClassNotFoundException ex) {
                    break;
                }

                switch (msg.getType()) {
                    case Message.MESSAGE: {
                        try {
                            broadcast(msg, user.getUserName());
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                    case Message.AVATAR:
                        int len;
                        try {
                            len = sInput.readInt();
                            byte[] data = new byte[len];
                            sInput.readFully(data);
                            String ext = sInput.readUTF();
                            InputStream ian = new ByteArrayInputStream(data);
                            BufferedImage bImage = ImageIO.read(ian);
                            File f = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getParentFile();
                            File fileToSave = new File(f.getPath() + "/src/images/" + user.getIdUser() + "." + ext);
                            fileToSave.delete();                            
                            ImageIO.write(bImage, ext, fileToSave);

                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case Message.SENDREQUEST:
                        sendRequest(msg.getDestinationId());
                        break;
                    case Message.GETREQUEST:
                        try {
                            sOutput.writeObject(getRequests());
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case Message.ACCEPTREQUEST:
                        try {
                            sOutput.writeObject(acceptRequest(msg.getSourceId()));
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case Message.REJECTREQUEST:
                        rejectRequest(msg.getSourceId());
                        break;
                    case Message.SEARCH: {
                        try {
                            sOutput.writeObject(searchPeople(msg.getContent()));
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                    case Message.LOGOUT:
                        display(user.getUserName() + " disconnected with a LOGOUT message.");
                        user.setIsOnline(false);
                        logOut();
                         {
                            try {
                                broadcastStatus(friends, this.user);
                            } catch (IOException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        all.remove(this);
                        keepGoing = false;
                        break;
                    case Message.ONLINES:
                        try {
                            sOutput.writeObject(createFriendsGUI());
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        display(user.getUserName() + " sent a request to see online friends.");
                        break;
                }
            }
        }

        // try to close everything
        private void close() throws IOException {
            // try to close the connection
            sOutput.writeObject("Your account has been logged in elsewhere now.");
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch (Exception e) {
            }

            try {
                if (sInput != null) {
                    sInput.close();
                }
            } catch (Exception e) {
            };
            try {
                if (socket != null) {
                    socket.close();
                    all.remove(id);
                }
            } catch (Exception e) {
            }

        }

        private void sendRequest(int id) {
            try {
                Connection con;
                Statement statement;
                Class.forName(DatabaseOperations.connectionDriver);
                con = DriverManager.getConnection(DatabaseOperations.connectionString, DatabaseOperations.dbId,
                        DatabaseOperations.dbPass);
                statement = con.createStatement();
                String sql = "insert into friendships (oneFriendId, twoFriendId) values (" + user.getIdUser() + ", " + id + ")";
                int row = statement.executeUpdate(sql);
                display(row + " rows affected.");
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void rejectRequest(int id) {
            try {
                Connection con;
                Statement statement;
                Class.forName(DatabaseOperations.connectionDriver);
                con = DriverManager.getConnection(DatabaseOperations.connectionString, DatabaseOperations.dbId,
                        DatabaseOperations.dbPass);
                statement = con.createStatement();
                String sql = "delete from friendships where oneFriendId=" + id + " AND twoFriendId=" + user.getIdUser();
                int row = statement.executeUpdate(sql);
                display(row + " rows affected.");
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private User acceptRequest(int id) {
            User u = null;
            try {
                Connection con;
                Statement statement;
                Class.forName(DatabaseOperations.connectionDriver);
                con = DriverManager.getConnection(DatabaseOperations.connectionString, DatabaseOperations.dbId,
                        DatabaseOperations.dbPass);
                statement = con.createStatement();
                String sql = "update friendships set status=1 where oneFriendId=" + id + " AND twoFriendId=" + user.getIdUser();
                int row = statement.executeUpdate(sql);
                display(row + " rows affected.");
                sql = "SELECT users.idUser, userName, lastName, firstName, avatarFolder, "
                        + "MAX(user_logs.time) FROM users, "
                        + "user_logs "
                        + "where (user_logs.idUser=users.idUser AND idType=2) AND users.idUser=" + id;
                ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                    u = new User();
                    u.setIdUser(rs.getInt(1));
                    u.setUserName(rs.getString(2));
                    u.setLastName(rs.getString(3));
                    u.setFirstName(rs.getString(4));
                    u.setAvatar(new ImageIcon(getClass().getResource(rs.getString(5))));
                    u.setLastSeen(rs.getTimestamp(6));
                    u.setIsOnline(false);
                }
                for (int i = 0; i < all.size(); i++) {
                    Handler hnd = all.get(i);
                    if (hnd.user.getIdUser() == u.getIdUser()) {
                        u.setIsOnline(true);
                        broadcastStatus(hnd, this.user);
                    }
                }
                friends.addElement(u);
                con.close();
                return u;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return u;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return u;
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return u;
            }
        }

        private DefaultListModel<User> getRequests() {
            DefaultListModel<User> requests = null;
            try {
                Connection con;
                Statement statement;
                Class.forName(DatabaseOperations.connectionDriver);
                con = DriverManager.getConnection(DatabaseOperations.connectionString, DatabaseOperations.dbId,
                        DatabaseOperations.dbPass);
                statement = con.createStatement();
                String sqlQuery = "SELECT users.idUser, lastName, firstName, avatarFolder"
                        + " FROM users, "
                        + "friendships "
                        + "where (oneFriendId=users.idUser OR twoFriendId=users.idUser) "
                        + "AND (users.idUser!=" + user.getIdUser() + " AND "
                        + "twoFriendId=" + user.getIdUser() + ") AND friendships.status=0 "
                        + "ORDER BY lastName, firstName ASC";
                ResultSet rs = statement.executeQuery(sqlQuery);
                requests = new DefaultListModel<>();
                while (rs.next()) {
                    User request = new User();
                    request.setIdUser(rs.getInt(1));
                    request.setLastName(rs.getString(2));
                    request.setFirstName(rs.getString(3));
                    request.setAvatar(new ImageIcon(getClass().getResource(rs.getString(4))));
                    requests.addElement(request);
                }
                con.close();
                return requests;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return requests;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return requests;
            }
        }

        private DefaultListModel<User> searchPeople(String search) {
            DefaultListModel<User> u = null;
            try {
                Connection con;
                Statement statement;
                Class.forName(DatabaseOperations.connectionDriver);
                con = DriverManager.getConnection(DatabaseOperations.connectionString, DatabaseOperations.dbId,
                        DatabaseOperations.dbPass);
                statement = con.createStatement();
                ResultSet rs
                        = statement.executeQuery(
                                "SELECT idUser, lastName, firstName, avatarFolder "
                                + "FROM whatsapp.users where concat(lastName,\' \', firstName) LIKE \'" + search + "%\'");
                u = new DefaultListModel<>();
                while (rs.next()) {
                    User found = new User();
                    found.setIdUser(rs.getInt(1));
                    found.setLastName(rs.getString(2));
                    found.setFirstName(rs.getString(3));
                    found.setAvatar(new ImageIcon(getClass().getResource(rs.getString(4))));
                    u.addElement(found);
                }
                con.close();
                return u;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return u;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return u;
            }
        }

        private User userLogin() {
            User u = null;
            try {
                Connection con;
                Statement statement;
                Class.forName(DatabaseOperations.connectionDriver);
                con = DriverManager.getConnection(DatabaseOperations.connectionString, DatabaseOperations.dbId,
                        DatabaseOperations.dbPass);
                statement = con.createStatement();
                ResultSet rs
                        = statement.executeQuery(
                                "select * from users where userName=\'" + user.getUserName()
                                + "\' AND userPass=\'" + user.getUserPass() + "\'");
                u = new User();
                u.setUserName("");
                while (rs.next()) {
                    u = new User(rs.getInt(1), rs.getString(2),
                            rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6),
                            new ImageIcon(getClass().getResource(rs.getString(7))));
                }
                if (!u.getUserName().isEmpty()) {
                    Date dt = new Date();
                    SimpleDateFormat sdfR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    int row = statement.executeUpdate("insert into user_logs (idUser, time, ipAddress, idType) values("
                            + u.getIdUser() + ", \'"
                            + sdfR.format(dt) + "\', \'" + socket.getRemoteSocketAddress().toString() + "\', 1)");
                    display(row + " rows affected.");
                }
                con.close();
                return u;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return u;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return u;
            }
        }

        private boolean logOut() {
            try {
                Connection con;
                Statement statement;
                Class.forName(DatabaseOperations.connectionDriver);
                con = DriverManager.getConnection(DatabaseOperations.connectionString, DatabaseOperations.dbId,
                        DatabaseOperations.dbPass);
                statement = con.createStatement();
                Date dt = new Date();
                user.setLastSeen(dt);
                SimpleDateFormat sdfR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                int row = statement.executeUpdate("insert into user_logs (idUser, time, ipAddress, idType) values("
                        + user.getIdUser() + ", \'"
                        + sdfR.format(dt) + "\', \'" + socket.getRemoteSocketAddress().toString() + "\', 2)");
                display(row + " rows affected.");
                con.close();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        private void findFriends() {
            friends = new DefaultListModel<>();
            try {
                Connection con;
                Statement statement;
                Class.forName(DatabaseOperations.connectionDriver);
                con = DriverManager.getConnection(DatabaseOperations.connectionString,
                        DatabaseOperations.dbId, DatabaseOperations.dbPass);
                statement = con.createStatement();
                String sqlQuery = "SELECT users.idUser, userName, lastName, firstName, avatarFolder, "
                        + "MAX(user_logs.time) FROM users, "
                        + "friendships, user_logs "
                        + "where ((user_logs.idUser=users.idUser AND idType=2) "
                        + "AND (oneFriendId=users.idUser "
                        + "OR twoFriendId=users.idUser)) AND (users.idUser!=" + user.getIdUser() + " AND "
                        + "(oneFriendId=" + user.getIdUser()
                        + " OR twoFriendId=" + user.getIdUser() + ")) AND friendships.status=1 "
                        + "GROUP BY users.idUser ORDER BY lastName, firstName ASC";
                ResultSet rs = statement.executeQuery(sqlQuery);
                while (rs.next()) {
                    User u = new User();
                    u.setIdUser(rs.getInt(1));
                    u.setUserName(rs.getString(2));
                    u.setLastName(rs.getString(3));
                    u.setFirstName(rs.getString(4));
                    u.setAvatar(new ImageIcon(getClass().getResource(rs.getString(5))));
                    u.setLastSeen(rs.getTimestamp(6));
                    friends.addElement(u);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private DefaultListModel<User> onlineFriends() {
            onlineFriends = new DefaultListModel<>();
            findFriends();
            for (int i = 0; i < all.size(); i++) {
                Handler hnd = all.get(i);
                User online = new User();
                online.setIdUser(hnd.user.getIdUser());
                online.setLastName(hnd.user.getLastName());
                online.setFirstName(hnd.user.getFirstName());
                online.setUserName(hnd.user.getUserName());
                online.setAvatar(hnd.user.getAvatar());
                for (int j = 0; j < friends.size(); j++) {
                    User friend = friends.get(j);
                    if (online.getIdUser() == friend.getIdUser() && !onlineFriends.contains(online)) {
                        onlineFriends.addElement(online);
                    }
                }
            }
            return onlineFriends;
        }

        private ArrayList<DefaultListModel> createFriendsGUI() {
            ArrayList<DefaultListModel> listOfFriends = new ArrayList<>();
            onlineFriends = onlineFriends();
            offlineFriends = new DefaultListModel<>();
            for (int i = 0; i < friends.size(); i++) {
                User friend = friends.get(i);
                boolean exist = false;
                for (int j = 0; j < onlineFriends.size(); j++) {
                    User on = onlineFriends.get(j);
                    if (on.getIdUser() == friend.getIdUser()) {
                        exist = true;
                    }
                }
                if (!exist) {
                    offlineFriends.addElement(friend);
                }
            }
            listOfFriends.add(onlineFriends);
            listOfFriends.add(offlineFriends);
            return listOfFriends;
        }

        private boolean writeMsg(Message msg, String sender) throws IOException {
            if (!socket.isConnected()) {
                close();
                return false;
            }

            try {
                sOutput.writeObject(msg);
                String messageWithTime = sdf.format(msg.getSendingTime())
                        + " FROM " + sender + " TO " + user.getUserName() + ": " + msg.getContent() + "\n";
                System.out.print(messageWithTime);
            } catch (IOException e) {
                display("Error sending message to " + user.getUserName());
                display(e.toString());
            }
            return true;
        }

        private boolean onlineMsg(User u) throws IOException {
            if (!socket.isConnected()) {
                close();
                return false;
            }

            try {
                boolean isCurrent = false;
                for (int i = 0; i < friends.size(); i++) {
                    if (friends.get(i).getIdUser() == u.getIdUser()) {
                        isCurrent = true;
                    }
                }
                if (!isCurrent) {
                    friends.addElement(u);
                }
                sOutput.writeObject(u);
            } catch (IOException e) {
                display("Error sending message to " + user.getUserName());
                display(e.toString());
            }
            return true;
        }

    }
}
