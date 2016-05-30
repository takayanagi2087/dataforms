package dataforms.controller;

import javax.servlet.http.HttpServletResponse;

/**
 * 応答情報クラスです。
 */
public abstract class Response {

	/**
	 * コンテントタイプ。
	 */
	private String contentType = null;



	/**
	 * 処理結果。
	 */
	private Object result = null;



	/**
	 * コンテントタイプを取得します。
	 * @return コンテントタイプ。
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * コンテントタイプを設定します。
	 * @param contentType コンテントタイプ。
	 */
	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	/**
	 * 処理の結果情報を取得します。
	 * @return 処理の結果情報。
	 */
	public final Object getResult() {
		return result;
	}

	/**
	 * 処理の結果情報を設定します。
	 * @param result 処理の結果情報。
	 */
	public final void setResult(final Object result) {
		this.result = result;
	}

    /**
     * 応答を送信します。
     * <pre>
     * 応答するオブジェクトに対応した送信処理を実装します。
     * </pre>
     * @param resp 応答情報。
     * @throws Exception 例外。
     */
    public abstract void send(final HttpServletResponse resp) throws Exception;


}
