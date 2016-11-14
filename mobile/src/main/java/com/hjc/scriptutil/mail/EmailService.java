package com.hjc.scriptutil.mail;

import android.util.Log;
import android.widget.Toast;

import com.hjc.util.Constants;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by hujiachun684 on 16/5/11.
 */
public class EmailService {
    private String SIMP = "smtp.163.com";
    private int PORT = 25;
    private String username = "scripttool@163.com";
    private String passwprd = "qweqwe123";

    private String subject;
    private String content;
    private String[] attachment;
    private String[] receiver;
    private String[] cc;
    private String[] attachmentName;


    public EmailService(Email email) {
        subject = email.getSubject();
        content = email.getContent();
        attachment = email.getAttachment();
        receiver = email.getReceiver();
        cc = email.getCc();
        attachmentName = email.getAttachmentName();

    }

//    public void sendCommonEMail(){
//        Properties props = new Properties();
//        props.setProperty("mail.smtp.auth", "true");//必须 普通客户端
//        props.setProperty("mail.transport.protocol", "smtp");//必须选择协议
//        Session session = Session.getDefaultInstance(props);
//        session.setDebug(true);//设置debug模式   在控制台看到交互信息
//        Message msg = new MimeMessage(session);  //建立一个要发送的信息
//
//        try {
//            msg.setSubject(subject);
//            msg.setText(content);//设置简单的发送内容
//            msg.setFrom(new InternetAddress(username));//发件人邮箱号
//            Transport transport = session.getTransport();//发送信息的工具
//
//            transport.connect(SIMP, PORT, username, passwprd);//发件人邮箱号 和密码
//
//            InternetAddress[] sendTo = new InternetAddress[receiver.length];
//            for (int i = 0; i < receiver.length; i++)
//            {
//                sendTo[i] = new InternetAddress(receiver[i]);
//            }
//
//            transport.sendMessage(msg, new Address[] { new InternetAddress(
//                    receiver) });//对方的地址
//            transport.close();
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }


    @SuppressWarnings("deprecation")
    public void sendAttachmentEmail() throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");//必须 普通客户端
        props.setProperty("mail.transport.protocol", "smtp");//必须选择协议
        props.setProperty("mail.host", "smtp.163.com");
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(username, passwprd);
            }
        });



        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        session.setDebug(true);
        Message msg = new MimeMessage(session);
        MimeMultipart mimeMultipart = new MimeMultipart();
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setText(content);

        //添加附件
        for(int i = 0; i < attachment.length; i++){
            MimeBodyPart attach = new MimeBodyPart();
            DataSource ds = new FileDataSource(attachment[i]);
            DataHandler dh = new DataHandler(ds);
            attach.setDataHandler(dh);
            attach.setFileName(attachmentName[i]);

            mimeMultipart.addBodyPart(attach);
        }

//        MimeBodyPart attach1 = new MimeBodyPart();
//        mimeMultipart.addBodyPart(mimeBodyPart);
//        DataSource ds1 = new FileDataSource(attachment);
//        DataHandler dh1 = new DataHandler(ds1);
//        attach1.setDataHandler(dh1);
//        attach1.setFileName(attachmentName);
//        mimeMultipart.addBodyPart(attach1);


        mimeMultipart.addBodyPart(mimeBodyPart);
        msg.setContent(mimeMultipart);
        msg.saveChanges();
        msg.setFrom(new InternetAddress(username));
        msg.setSubject(subject);
        InternetAddress[] sendTo = new InternetAddress[receiver.length];
            for (int i = 0; i < receiver.length; i++)
            {
                sendTo[i] = new InternetAddress(receiver[i]);
            }


        if(cc != null){
            Log.e(Constants.TAG, "抄送中");
            InternetAddress[] sendCc = new InternetAddress[cc.length];
            for (int i = 0; i < cc.length; i++)
            {
                sendCc[i] = new InternetAddress(cc[i]);
            }
            msg.setRecipients(RecipientType.CC, sendCc);
        }


        msg.setRecipients(RecipientType.TO, sendTo);

        Transport.send(msg);
        Log.e(Constants.TAG, "send Email success");

    }
}
