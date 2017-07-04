package dataforms.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import dataforms.controller.ApplicationError;
import dataforms.controller.Page;


/**
 * メッセージユーティリティクラス。
 *
 */
public final class MessagesUtil {
    /**
     * Log.
     */
	private static Logger log = Logger.getLogger(MessagesUtil.class.getName());

	/**
	 * クライアント用メッセージプロパティ。
	 */
	private static String clientMessagesName = null;

	/**
	 * クライアント用メッセージプロパティ(アプリケーション独自)。
	 */
	private static String appClientMessagesName = null;

	/**
	 * メッセージプロパティ。
	 */
	private static String messagesName = null;

	/**
	 * メッセージプロパティ(アプリケーション独自)。
	 */
	private static String appMessagesName = null;
	/**
     * コンストラクタ。
     */
    private MessagesUtil() {

    }


    /**
     * クライアント用のメッセージリソースを設定します。
     * @param clientMessagesName クライアント用のメッセージリソース。
     */
    public static void setClientMessagesName(final String clientMessagesName) {
		MessagesUtil.clientMessagesName = clientMessagesName;
	}



    /**
     * サーバ用のメッセージリソースを設定します。
     * @param messagesName サーバ用のメッセージリソース。
     */
	public static void setMessagesName(final String messagesName) {
		MessagesUtil.messagesName = messagesName;
	}

	
	
    /**
     * クライアント用のメッセージリソース(アプリケーション独自)を設定します。
     * @param appClientMessagesName クライアント用のメッセージリソース(アプリケーション独自)。
     */
	public static void setAppClientMessagesName(final String appClientMessagesName) {
		MessagesUtil.appClientMessagesName = appClientMessagesName;
	}


    /**
     * サーバ用のメッセージリソース(アプリケーション独自)を設定します。
     * @param appMessagesName サーバ用のメッセージリソース(アプリケーション独自)。
     */
	public static void setAppMessagesName(final String appMessagesName) {
		MessagesUtil.appMessagesName = appMessagesName;
	}


	/**
	 * 順序付プロパティファイルを取得します。
	 * <pre>
	 * 指定されたパスのプロパティファイルを順序情報を含めて読み込みます。
	 * 基本的にFunction.properties用のメソッドです。
	 * </pre>
	 * @param page ページ情報。言語の判定などに使用します。
	 * @param path パス。
	 * @return プロパティ。
	 *
	 */
	public static SequentialProperties getProperties(final Page page, final String path) {
		SequentialProperties prop = new SequentialProperties();
		try {
			String proppath = page.getAppropriatePath(path + ".properties", page.getRequest());
			String proptext = page.getWebResource(proppath);
			prop.loadText(proptext);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
		return prop;
	}

	/**
	 * 指定されたプロパティファイルからメッセージを取得ます。
	 * 
	 * @param page ページ。
	 * @param messageKey メッセージキー。
	 * @param prop プロパティファイルのパス。
	 * @return メッセージ。
	 */
	public static String getMessage(final Page page, final String messageKey, final String prop) {
		String msg = null;
		Properties appClientMessages = getProperties(page, prop);
		if (appClientMessages.containsKey(messageKey)) {
			msg = appClientMessages.getProperty(messageKey);
		}
		return msg;
	}
	
	/**
	 * メッセージを取得します。
	 * 
	 * <pre>
	 * {@code
	 * 指定されたキーのメッセージを読み込みます。
	 * サーブレットの初期化パラメータ、"client-messages"に指定されたファイルからメッセージを取得します。
	 * 上記に無い場合、"messages"に指定されたファイルからメッセージを取得します。
	 * 上記に無い場合、"<page-path>.properties"に指定されたファイルからメッセージを取得します。
	 * }
	 * </pre>
	 * 
	 * @param page
	 *            ページ情報。言語の判定などに使用します。
	 * @param messageKey
	 *            メッセージキー。
	 * @return メッセージ。
	 */
	public static String getMessage(final Page page, final String messageKey) {
		String msg = getMessage(page, messageKey, appClientMessagesName);
		if (msg != null) {
			return msg;
		}
		msg = getMessage(page, messageKey, clientMessagesName);
		if (msg != null) {
			return msg;
		}
		msg = getMessage(page, messageKey, appMessagesName);
		if (msg != null) {
			return msg;
		}
		msg = getMessage(page, messageKey, messagesName);
		if (msg != null) {
			return msg;
		}
		String clsname = page.getClass().getName();
		String pageprop = "/" + clsname.replaceAll("\\.", "/");
		Map<String, String> pageMap = MessagesUtil.getMessageMap(page, pageprop);
		return pageMap.get(messageKey);
	}

    /**
     * メッセージを取得します。
     * <pre>
     * 指定されたキーのメッセージを読み込み、引数を展開します。
     * サーブレットの初期化パラメータ、"client-messages"に指定されたファイルからメッセージを取得します。
     * 上記に無い場合、"messages"に指定されたファイルからメッセージを取得します。
     * </pre>
	 * @param page ページ情報。言語の判定などに使用します。
     * @param messageKey メッセージキー。
     * @param args メッセージの引数。
     * @return メッセージ。
     */
    public static String getMessage(final Page page, final String messageKey, final String... args) {
        String msg = getMessage(page, messageKey);
        int idx = 0;
        for (String arg : args) {
            msg = msg.replaceAll("\\{" + idx + "\\}", arg);
            idx++;
        }
        return msg;
    }


    /**
     * メッセージマップを取得します。
     * <pre>
     * 指定されたパスのプロパティファイルのメッセージマップを読み込みます。
     * </pre>
	 * @param page ページ情報。言語の判定などに使用します。
     * @param prop プロパティファイルパス。
     * @return メッセージマップ。
     */
    public static Map<String, String> getMessageMap(final Page page, final String prop) {
        Properties clientMessages = getProperties(page, prop);
        Map<String, String> map = new HashMap<String, String>();
        for (Object key : clientMessages.keySet()) {
        	map.put((String) key, clientMessages.getProperty((String) key));
        }
        return map;
    }


    /**
     * クライアント用のメッセージマップを取得します。
     * <pre>
     * サーブレットの初期化パラメータ、"client-messages"に指定されたファイルから
     * メッセージマップを取得します。
     * このマップはページの初期化時にクライアントに送信され、javascriptから
     * 通信を行わずに利用できるものになります。
     * 基本的にクライアントバリデーション用のメッセージマップです。
     * </pre>
     *
	 * @param page ページ情報。言語の判定などに使用する.
     * @return クライアント用のメッセージマップ.
     */
    public static Map<String, String> getClientMessageMap(final Page page) {
    	Map<String, String> ret =  getMessageMap(page, clientMessagesName);
    	Map<String, String> amap =  getMessageMap(page, appClientMessagesName);
    	if (amap != null) {
    		ret.putAll(amap);
    	}
    	return ret;
    }

}
