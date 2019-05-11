/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InstantMessaging;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author Süleyman
 */
public class Message implements Serializable {

    private int idMessage;
    static final int ONLINES = 0, MESSAGE = 1,
            LOGOUT = 2, SYSTEM = 3, HISTORY = 4, SEARCH = 5,
            SENDREQUEST = 6, GETREQUEST = 7, ACCEPTREQUEST=8, REJECTREQUEST=9, AVATAR=10;
    private int type;
    private String content;
    private int sourceId;
    private int destinationId;
    private Date sendingTime;

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

}
