package dataforms.controller;

import dataforms.util.MessagesUtil;

/**
 * アプリケーションレベルで発生する例外クラス。
 *
 */
public class ApplicationException extends Exception {
	/**
	 * UID。
	 */
	private static final long serialVersionUID = 7025730004513064767L;

	/**
	 * メッセージキー。
	 */
	private String messageKey = null;

	/**
	 * コンストラクタ。
	 * @param page エラーが発生したページ。
	 * @param msgkey メッセージのキー。
	 * @param args メッセージ引数。
	 */
	public ApplicationException(final Page page, final String msgkey, final String... args) {
		super(MessagesUtil.getMessage(page, msgkey, args));
		this.messageKey = msgkey;
	}


	/**
	 * コンストラクタ。
	 *
	 * @param msgkey メッセージのキー。
	 * @param message メッセージのテキスト。
	 * @deprecated
	 */
	public ApplicationException(final String msgkey, final String message) {
		super(message);
		this.messageKey = msgkey;
	}

	/**
	 * メッセージキーを取得します。
	 * @return メッセージキー。
	 */
	public String getMessageKey() {
		return messageKey;
	}

}
