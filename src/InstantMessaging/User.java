/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantMessaging;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author Süleyman
 */
public class User implements Serializable {

    private int idUser;
    private String userName;
    private String userPass;
    private String lastName;
    private String firstName;
    private String eMail;
    private ImageIcon avatar;
    private boolean isOnline = true;
    private Date lastSeen;

    public User() {

    }

    public User(String userName, String userPass) {
        this.userName = userName;
        this.userPass = userPass;
    }

    public User(int idUser, String userName, String userPass, String lastName, String firstName,
            String eMail, ImageIcon avatar) {
        this.idUser = idUser;
        this.userName = userName;
        this.userPass = userPass;
        this.lastName = lastName;
        this.firstName = firstName;
        this.eMail = eMail;
        this.avatar=avatar;
    }

    public User findUser(int idUser) {
        User u = null;
        try {
            Connection con;
            Statement statement;
            Class.forName(DatabaseOperations.connectionDriver);
            con = DriverManager.getConnection(DatabaseOperations.connectionString,
                    DatabaseOperations.dbId, DatabaseOperations.dbPass);
            statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select idUser, userName, lastName, firstName, avatarFolder"
                    + " from users where idUser=" + idUser);
            while (rs.next()) {
                u = new User();
                u.setIdUser(rs.getInt(1));
                u.setUserName(rs.getString(2));
                u.setLastName(rs.getString(3));
                u.setFirstName(rs.getString(4));
                u.setAvatar(new ImageIcon(getClass().getResource(rs.getString(5))));
            }
            return u;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    public int getIdUser() {
        return this.idUser;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.eMail;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String eMail) {
        this.eMail = eMail;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public ImageIcon getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageIcon avatar) {
        this.avatar = avatar;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }
}
