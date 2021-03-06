package com.jaybill.billblog.email;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件工具类
 * @author jaybill
 *
 */
public class EmailUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);
	
	public static String submitEmail(String receiveMailAccount) throws Exception{
		LOGGER.debug("读取我的邮箱的配置文件");
		//读取配置文件，获得我的邮箱的账号密码
//		Properties myEmailPro = new Properties();
//		myEmailPro.load(new FileInputStream("E:/eclipse_workplace/billblog-common/src/main/java/com/jaybill/billblog/email/my-email-info.properties"));
//		String myEmailAccount = myEmailPro.getProperty("myEmailAccount");
//		String myEmailPassword = myEmailPro.getProperty("myEmailPassword");
//		String myEmailSMTPHost = myEmailPro.getProperty("myEmailSMTPHost");
		LOGGER.debug("创建参数配置, 用于连接邮件服务器的参数配置");
		// 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.host", "smtp.163.com");        // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 请求认证，参数名称与具体实现有关
        LOGGER.debug("根据配置创建会话对象, 用于和邮件服务器交互");
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log
        // 3. 创建一封邮件
        //生成6位验证码
        LOGGER.debug("生成6位验证码");
        String arr = "2345678abcdefhjkmnpqrstuvwxyz"; 
        StringBuilder code = new StringBuilder();
        for(int i=0;i<6;i++){
        	code.append(arr.charAt(new Random().nextInt(arr.length()-1)));
        }
        LOGGER.debug("将6位验证码作为内容，创建一封邮箱");
        MimeMessage message = createMimeMessage(session, "jaybill951104@163.com", receiveMailAccount,code.toString());
        LOGGER.debug("根据 Session 获取邮件传输对象Transport，发送邮件");
        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        // 5. 使用 邮箱账号 和 密码 连接邮件服务器
        //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
        transport.connect("jaybill951104@163.com", "abc123456");
        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
        LOGGER.error("关闭Transport失败");
        //返回验证码
        return code.toString();
    }
	
	/**
     * 创建一封只包含文本的简单邮件
     *
     * @param session 和服务器交互的会话
     * @param sendMail 发件人邮箱
     * @param receiveMail 收件人邮箱
     * @return
     * @throws Exception
     */
	private static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail,String code) throws Exception{
		// 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "billblog微博", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "XX用户", "UTF-8"));

        // 4. Subject: 邮件主题
        message.setSubject("微博注册", "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        
        message.setContent(code.toString(), "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();
		return message;
	}
}
