package frame.util;


import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * 邮件发送工具
 * Created by sumei on 17/9/11.
 */
public class MailUtil {
    private static Session session;
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(MailUtil.class.getClassLoader().getResourceAsStream("mail.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件
     * @param toAddr 收件人地址
     * @param title     邮件标题
     * @param context   邮件内容
     * @throws Exception
     * @throws Exception
     */
    public static boolean sendMail(String toAddr,String uname , String title ,String context){
        try {
            // 1.创建会话

            // 会话
            session = Session.getDefaultInstance(properties);

            // 2.创建邮件
            MimeMessage mail = createMail(properties.getProperty("sender"),properties.getProperty("senderName"),
                    toAddr,uname,session,title,context);

            Transport transport = session.getTransport();

            transport.connect(properties.getProperty("sender"),properties.getProperty("senderPwd"));

            transport.sendMessage(mail, mail.getAllRecipients());

            transport.close();
            return  true;
        } catch (Exception e) {
            return false;
        }


    }

    // 创建mail对象
    private static MimeMessage createMail( String sender, String senderName , String toAddr ,String uname, Session session
            , String title ,String content)
            throws UnsupportedEncodingException, MessagingException {

        // 邮件对象
        MimeMessage mail = new MimeMessage(session);

        mail.setFrom(new InternetAddress(sender,senderName,"utf-8"));

        // 设置接收方
        mail.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(toAddr,uname,"utf-8"));


        // 设置主题
        mail.setSubject(title);
        mail.setContent(content,"text/html;charset=utf-8");

        mail.setSentDate(new Date());

        mail.saveChanges();

        return mail;
    }
}
