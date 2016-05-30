package dataforms.controller;

/**
 * アプリケーションエラークラス。
 *
 */
public class ApplicationError extends Error {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5873669599634950645L;

	/**
	 * コンストラクタ。
	 * @param cause エラーの原因。
	 */
	public ApplicationError(final Throwable cause) {
		super(cause);
	}
}
