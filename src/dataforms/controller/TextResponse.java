package dataforms.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import dataforms.servlet.DataFormsServlet;

/**
 * テキスト応答クラス。
 *
 */
public class TextResponse extends Response {

	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 */
	public TextResponse(final String result) {
		this.setResult(result);
		this.setContentType("text/plain; charset=" + DataFormsServlet.getEncoding());
	}

	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 * @param contentType content-typeヘッダの値。
	 */
	public TextResponse(final String result, final String contentType) {
		this.setResult(result);
		this.setContentType(contentType);
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * テキストを送信します。
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
