package dataforms.mail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;

/**
 * テンプレートクラス。
 *
 */
public class MailTemplate {
	/**
	 * Logger。
	 */
	private static Logger log = Logger.getLogger(MailTemplate.class);

	/**
	 * 引数マップ。
	 */
	private Map<String, String> argMap = new HashMap<String, String>();

	/**
	 * リンク情報。
	 *
	 */
	private class LinkInfo {
		/**
		 * URL。
		 */
		private String url;
		/**
		 * リンクテキスト。
		 */
		private String text;

		/**
		 * コンストラクタ。
		 * @param url URL。
		 * @param text リンクテキスト。
		 */
		public LinkInfo(final String url, final String text) {
			this.url = url;
			this.text = text;
		}

		/**
		 * URLを取得する。
		 * @return URL。
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * URLを設定する。
		 * @return URL。
		 */
		public String getText() {
			return text;
		}
	}

	/**
	 * リンク情報マップ。
	 */
	private Map<String, LinkInfo> linkMap = new HashMap<String, LinkInfo>();

	/**
	 * メールのタイトル。
	 */
	private String mailSubject;
	/**
	 * メール本文。
	 */
	private String mailBody;
	/**
	 * HTMLフレーム。
	 */
	private String htmlFrame;

	/**
	 * 宛先リスト。
	 */
	private List<String> toList = new ArrayList<String>();
	/**
	 * CCリスト。
	 */
	private List<String> ccList = new ArrayList<String>();
	/**
	 * BCCリスト。
	 */
	private List<String> bccList = new ArrayList<String>();
	/**
	 * From。
	 */
	private String from = null;
	/**
	 * 返信先。
	 */
	private String replyTo = null;
	/**
	 * Fromの表示名。
	 */
	private String fromPersonal = null;

	/**
	 * 添付ファイル情報。
	 */
	public class AttachFileInfo {
		/**
		 * 添付ファイルパス。
		 */
		private String path = null;
		/**
		 * ファイル名。
		 */
		private String filename = null;
		/**
		 * コンストラクタ。
		 * @param filename ファイル名。
		 * @param path パス。
		 */
		public AttachFileInfo(final String filename, final String path) {
			this.path = path;
			this.filename = filename;
		}

		/**
		 * 添付ファイルのパスを取得します。
		 * @return 添付ファイルのパス。
		 */
		public String getPath() {
			return path;
		}

		/**
		 * ファイル名を取得します。
		 * @return  ファイル名。
		 */
		public String getFilename() {
			return filename;
		}
	}

	/**
	 * 添付ファイルリスト。
	 */
	private List<AttachFileInfo> attachFileList = new ArrayList<AttachFileInfo>();

	
	
	/**
	 * コンストラクタ。
	 * 
	 * @param templete テキストテンプレート。
	 * @param htmlframe HTMLメールのテンプルレート。
	 * @throws Exception 例外。
	 */
	public MailTemplate(final String templete, final String htmlframe) throws Exception {
		this.setTemplate(templete);
		this.setHtmlFrame(htmlframe);
	}

	/**
	 * メールテンプレートの読み込み。
	 * @param templete メールテンプレートのテキスト。
	 * @throws Exception 例外。
	 */
	private void setTemplate(final String templete) throws Exception {
		log.debug("rpath=" + templete);
		Pattern p = Pattern.compile(".*");
		Matcher m = p.matcher(templete);
		m.find();
		this.mailSubject = m.group();
		this.mailBody = templete.substring(m.end());
	}

	/**
	 * 指定された文字列リソースを取得します。
	 * @param path リソースパス。
	 * @return 文字列。
	 * @throws Exception 例外。
	 */
	private String getStringResourse(final String path) throws Exception {
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream(path);
		String text = new String(FileUtil.readInputStream(is), DataFormsServlet.getEncoding());
		return text;
	}

	
	/**
	 * メールテンプレートの読み込み。
	 * @param htmlframe HTMLメールテンプレート。
	 * @throws Exception 例外。
	 */
	private void setHtmlFrame(final String htmlframe) throws Exception {
		if (htmlframe != null) {
			this.htmlFrame = htmlframe;
		} else {
			this.htmlFrame = this.getStringResourse("htmlframe/htmlFrame.html");
		}
		log.debug("htmltext=" + this.htmlFrame);
	}

	
	/**
	 * 変身先アドレスを取得します。
	 * @return 変身先アドレス。
	 */
	public String getReplyTo() {
		return replyTo;
	}

	/**
	 * 変身先アドレスを設定します。
	 * @param replyTo 変身先アドレス。
	 */
	public void setReplyTo(final String replyTo) {
		this.replyTo = replyTo;
	}

	
	
	
	/**
	 * TOアドレスを追加します。
	 * @param toAddress TOアドレス。
	 */
	public void addToAddress(final String toAddress) {
		this.toList.add(toAddress);
	}

	/**
	 * CCアドレスを追加します。
	 * @param ccAddress CCアドレス。
	 */
	public void addCcAddress(final String ccAddress) {
		this.ccList.add(ccAddress);
	}

	/**
	 * 添付ファイルを追加します。
	 * @param filename ファイル名。
	 * @param path パス。
	 */
	public void addAttachFile(final String filename, final String path) {
		this.attachFileList.add(new AttachFileInfo(filename, path));
	}

	/**
	 * BCCアドレスを追加します。
	 * @param bccAddress BCCアドレス。
	 */
	public void addBccAddress(final String bccAddress) {
		this.bccList.add(bccAddress);
	}


	/**
	 * TOアドレスのリストを取得します。
	 * @return TOアドレスのリスト。
	 */
	public List<String> getToList() {
		return toList;
	}

	/**
	 * CCアドレスのリストを取得します。
	 * @return CCアドレスのリスト。
	 */
	public List<String> getCcList() {
		return ccList;
	}

	/**
	 * BCCアドレスのリストを取得します。
	 * @return BCCアドレスのリスト。
	 */
	public List<String> getBccList() {
		return bccList;
	}

	/**
	 * 添付ファイルリストを取得します。
	 * @return 添付ファイルリスト。
	 */
	public List<AttachFileInfo> getAttachFileList() {
		return attachFileList;
	}

	/**
	 * Fromのアドレスを取得します。
	 * @return Fromのアドレス。
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * 送信元アドレスを設定します。
	 * @param from 送信元アドレス。 
	 */
	public void setFrom(final String from) {
		this.from = from;
	}

	/**
	 * Fromの名称を取得します。
	 * @return Fromの名称。
	 */
	public String getFromPersonal() {
		return fromPersonal;
	}

	/**
	 * Fromの名称を取得します。
	 * @param fromPersonal  Fromの名称。
	 */
	public void setFromPersonal(final String fromPersonal) {
		this.fromPersonal = fromPersonal;
	}

	/**
	 * メールの引数を指定します。
	 * @param arg 引数名。
	 * @param value 値。
	 */
	public void setArg(final String arg, final String value) {
		this.argMap.put(arg, value);
	}

	/**
	 * メールのリンク情報を指定します。
	 * @param arg 引数名。
	 * @param url URL。
	 * @param text テキスト。
	 */
	public void setLink(final String arg, final String url, final String text) {
		this.linkMap.put(arg, new LinkInfo(url, text));
	}

	/**
	 * メールの題名を取得します。
	 * @return メール題名。
	 */
	public String getMailSubject() {
		String text = this.mailSubject;
		for (String key: this.argMap.keySet()) {
			String value = this.argMap.get(key);
			text = text.replaceAll("\\$\\{" + key + "\\}", value);
		}
		return text;
	}

	/**
	 * メール本文を取得します。
	 * @return メール本文。
	 */
	public String getMailTextBody() {
		String text = this.mailBody;
		for (String key: this.argMap.keySet()) {
			log.debug("key=" + key);
			String value = this.argMap.get(key);
			text = text.replaceAll("\\$\\{" + key + "\\}", value);
		}
		for (String key: this.linkMap.keySet()) {
			LinkInfo link = this.linkMap.get(key);
			text = text.replaceAll("\\$\\{" + key + "\\}", link.getUrl());
		}
		return text;
	}

	/**
	 * メール本文を取得します。
	 * @return メール本文。
	 */
	public String getMailHtmlBody() {
		String text = this.mailBody;
		for (String key: this.argMap.keySet()) {
			String value = this.argMap.get(key);
			text = text.replaceAll("\\$\\{" + key + "\\}", "<b>" + value + "</b>");
		}
		for (String key: this.linkMap.keySet()) {
			LinkInfo link = this.linkMap.get(key);
			String a = "<a href='" + link.getUrl() + "' target='_blank'>" + link.getText() + "</a>";
			text = text.replaceAll("\\$\\{" + key + "\\}", a);
		}
		text = text.replaceAll("\r", "");
		text = text.replaceAll("\n", "<br/>");
		log.debug("text=" + text);
		text = this.htmlFrame.replaceAll("\\$\\{mailBody\\}", text);
		return text;
	}
}

