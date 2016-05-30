package dataforms.controller;

import javax.servlet.http.HttpServletResponse;

/**
 * リダイレクト応答クラスです。
 *
 */
public class RedirectResponse extends Response {
	/**
	 * リダイレクション先URL。
	 */
	private String url = null;


	/**
	 * コンストラクタ。
	 * @param url リダイレクション先URL。
	 */
	public RedirectResponse(final String url) {
		this.url = url;
	}

	/**
	 * {@inheritDoc}
	 * リダイレクションを送信します。
	 *
	 */
	@Override
	public void send(final HttpServletResponse resp) throws Exception {
		resp.sendRedirect(this.url);
	}
}
