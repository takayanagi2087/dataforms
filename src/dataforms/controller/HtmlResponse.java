package dataforms.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import dataforms.servlet.DataFormsServlet;

/**
 * Htmlの応答情報クラス。
 *
 */
public class HtmlResponse extends Response {
	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 */
	public HtmlResponse(final Object result) {
		this.setResult(result);
		this.setContentType("text/html; charset=" + DataFormsServlet.getEncoding());
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * HTMLを送信します。
	 * </pre>
	 */
	@Override
	public void send(final HttpServletResponse resp) throws Exception {
		resp.setContentType(this.getContentType());
		resp.setCharacterEncoding(DataFormsServlet.getEncoding());
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		PrintWriter out = resp.getWriter();
		try {
			out.write(this.getResult().toString());
		} finally {
			out.close();
		}
	}
}
