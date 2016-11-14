package com.hjc.scriptutil.mail;

import android.util.Log;

import com.hjc.util.Constants;

/**
 * Created by hujiachun684 on 16/5/11.
 */
public class Email {

    private String[] receiver;
    private String[] cc;
    private String subject;
    private String content;
    private String[] attachment;
    private String[] attachmentName;

    public String[] getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String[] attachmentName) {
        this.attachmentName = attachmentName;
    }


    public String[] getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {

        if(receiver.contains(";")){
            this.receiver = receiver.split(";");
        }
        else this.receiver = new String[]{receiver};
        for(int i = 0; i < this.receiver.length; i++){
            Log.e(Constants.TAG,"接收：" + this.receiver[i]);
        }


    }

    public void setCc(String cc){
        if(cc.contains(";")){
            this.cc = cc.split(";");
        }
        else this.cc = new String[]{cc};
        for(int i = 0; i < this.cc.length; i++){
            Log.e(Constants.TAG,"抄送：" + this.cc[i]);
        }

    }

    public String[] getCc() {
        return cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getAttachment() {
        return attachment;
    }

    public void setAttachment(String[] attachment) {
        this.attachment = attachment;
    }



}
