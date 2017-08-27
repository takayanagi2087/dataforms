package dataforms.mail;

import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;


/**
 * メール送信クラス。
 *
 */
public class MailSender {
	/**
	 * Logger.
	 */
	private Logger log = Logger.getLogger(MailSender.class);

	/**
	 * jndi-prefix。
	 */
	private static String jndiPrefix = null;
	
	/**
	 * メールセッション名称。
	 */
	private static String mailSessionName = null;

	/**
	 * 送信元アドレス。
	 */
	private static String mailFrom = null;

	
	/**
	 * コンストラクタ。
	 * @throws Exception 例外。
	 */
	public MailSender() throws Exception {
	}

	/**
	 * JndiPrefixを取得します。
	 * <pre>
	 * web.xmlのjndi-prefixの内容を取得します。
	 * </pre>
	 * @return JndiPrefix。
	 */
	public static String getJndiPrefix() {
		return jndiPrefix;
	}

	/**
	 * JndiPrefixを設定します。
	 * <pre>
	 * 基本的にweb.xmlのjndi-prefixの内容を設定します。
	 * </pre>
	 * @param jndiPrefix JndiPrefix。
	 */
	public static void setJndiPrefix(final String jndiPrefix) {
		MailSender.jndiPrefix = jndiPrefix;
	}

	/**
	 * メールセッション名を取得します。
	 * <pre>
	 * web.xmlのmail-sessionの内容を取得します。
	 * </pre>
	 * @return メールセッション名。
	 */
	public static String getMailSessionName() {
		return mailSessionName;
	}

	/**
	 * メールセッション名を設定します。
	 * <pre>
	 * web.xmlのmail-sessionの内容を設定します。
	 * </pre>
	 * @param mailSessionName メールセッション名。
	 */
	public static void setMailSessionName(final String mailSessionName) {
		MailSender.mailSessionName = mailSessionName;
	}

	/**
	 * メール送信元アドレスを取得します。
	 * <pre>
	 * web.xmlのmail-fromの内容を取得します。
	 * </pre>
	 * @return メール送信元アドレス。
	 */
	public static String getMailFrom() {
		return mailFrom;
	}

	/**
	 * メール送信元アドレスを設定します。
	 * <pre>
	 * web.xmlのmail-fromの内容を設定します。
	 * </pre>
	 * @param mailFrom メール送信元アドレス。
	 */
	public static void setMailFrom(final String mailFrom) {
		MailSender.mailFrom = mailFrom;
	}

	/**
	 * メールセッションを取得します。
	 * @return メールセッション。
	 * @throws Exception 例外。
	 */
	public static Session getMailSession() throws Exception {
		Context initCtx = new InitialContext();
		Session session = (Session) initCtx.lookup(MailSender.jndiPrefix + MailSender.mailSessionName);
		return session;
	}
	
	/**
	 * アドレスリストを変換します。
	 * @param list アドレスリスト。
	 * @return 変換されたアドレスリスト。
	 * @throws Exception 例外。
	 */
	private InternetAddress[] getAddressList(final List<String> list) throws Exception {
		if (list.size() > 0) {
			InternetAddress[] ret = new InternetAddress[list.size()];
			for (int i = 0; i < list.size(); i++) {
				ret[i] =  new InternetAddress(list.get(i));
			}
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * Mailテンプレートの内容を送信する。
	 * @param templ メール送信。
	 * @param session メールセッション。
	 * @throws Exception 例外。
	 */
	public void send(final MailTemplate templ, final Session session) throws Exception {
		String subject = templ.getMailSubject();
		log.debug("mail subject=" + subject);
		InternetAddress[] tolist = this.getAddressList(templ.getToList());
		InternetAddress[] cclist = this.getAddressList(templ.getCcList());
		InternetAddress[] bcclist = this.getAddressList(templ.getBccList());
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(templ.getFrom(), templ.getFromPersonal(), "UTF-8"));
		InternetAddress[] replyTo = new InternetAddress[1];
		replyTo[0] = new InternetAddress(templ.getReplyTo());
		msg.setReplyTo(replyTo);
		msg.setRecipients(Message.RecipientType.TO, tolist);
		if (cclist != null) {
			msg.setRecipients(Message.RecipientType.CC, cclist);
		}
		if (bcclist != null) {
			msg.setRecipients(Message.RecipientType.BCC, bcclist);
		}
//		msg.setSubject(subject, "ISO-2022-JP");
		msg.setSubject(subject, "UTF-8");

		// mixed
		Multipart mixedPart = new MimeMultipart("mixed");

		// alternative
		MimeBodyPart alternativeBodyPart = new MimeBodyPart();
		MimeMultipart alternativePart = new MimeMultipart("alternative");
		alternativeBodyPart.setContent(alternativePart);
		mixedPart.addBodyPart(alternativeBodyPart);

		// text mail
		MimeBodyPart textBodyPart = new MimeBodyPart();
//		textBodyPart.setText(templ.getMailTextBody(), "ISO-2022-JP", "plain");
		textBodyPart.setText(templ.getMailTextBody(), "UTF-8", "plain");
		textBodyPart.setHeader("Content-Transfer-Encoding", "base64");
		alternativePart.addBodyPart(textBodyPart);	// alter

		// related
		MimeBodyPart relatedBodyPart = new MimeBodyPart();
		Multipart relatedPart = new MimeMultipart("related");
		relatedBodyPart.setContent(relatedPart);
		alternativePart.addBodyPart(relatedBodyPart);

		// html mail
		MimeBodyPart htmlBodyPart = new MimeBodyPart();
//		htmlBodyPart.setText(templ.getMailHtmlBody(), "ISO-2022-JP", "html");
		htmlBodyPart.setText(templ.getMailHtmlBody(), "UTF-8", "html");
		htmlBodyPart.setHeader("Content-Transfer-Encoding", "base64");
		relatedPart.addBodyPart(htmlBodyPart);

		// attach file
		for (MailTemplate.AttachFileInfo finfo: templ.getAttachFileList()) {
			MimeBodyPart attachBodyPart = new MimeBodyPart();
			DataSource dataSource2 = new FileDataSource(finfo.getPath());
			DataHandler dataHandler2 = new DataHandler(dataSource2);
			attachBodyPart.setDataHandler(dataHandler2);
//			attachBodyPart.setFileName(MimeUtility.encodeWord(finfo.getFilename(), "iso-2022-jp", "B"));
//			attachBodyPart.setFileName(MimeUtility.encodeWord(finfo.getFilename(), "UTF-8", null));
			attachBodyPart.setFileName(MimeUtility.encodeWord(finfo.getFilename(), "UTF-8", "B"));
			attachBodyPart.setDisposition("attachment");  // attachment 指定しておく
			mixedPart.addBodyPart(attachBodyPart);
		}
		msg.setContent(mixedPart);
		Transport.send(msg);
	}

}
