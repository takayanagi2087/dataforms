package dataforms.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import dataforms.servlet.DataFormsServlet;
import net.arnx.jsonic.JSON;

/**
 * Jsonの応答情報クラスです。
 *
 */
public class JsonResponse extends Response {
	/**
     * Logger.
     */
    private static Logger log = Logger.getLogger(JsonResponse.class.getName());

	/**
	 * 処理が正常終了したことを返す。
	 */
	public static final int SUCCESS = 0;

	/**
	 * バリデーション等のエラーが発生したことを返す。
	 */
	public static final int INVALID = 1;

	/**
	 * アプリケーション例外。
	 */
	public static final int APPLICATION_EXCEPTION = 2;

	/**
	 * 処理の状態。
	 */
	private int status = SUCCESS;


	/**
	 * コンストラクタ。
	 * <pre>
	 * content-typeがapplication/jsonだとiframeで受けた場合、IEはダウンロードの動作になってしまうので
	 * text/plainを設定しておきます。
	 * </pre>
	 * @param status 処理の状態。
	 * @param result 実行結果。
	 */
	public JsonResponse(final int status, final Object result) {
		this.setStatus(status);
		this.setResult(result);
//		this.setContentType("application/json; charset=" + DataFormsServlet.getEncoding());
		/*
		 * content-typeがapplication/jsonだとiframeで受けた場合、IEはダウンロードの動作になってしまうので
		 * text/plainを設定しておく。
		 */
		this.setContentType("text/plain; charset=" + DataFormsServlet.getEncoding());
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * jsonを送信します。
	 * </pre>
	 */
	@Override
	public void send(final HttpServletResponse resp) throws Exception {
		resp.setContentType(this.getContentType());
		Object obj = this;
		PrintWriter out = resp.getWriter();
		try {
			if (obj != null) {
				String json = JSON.encode(obj, DataFormsServlet.isJsonDebug());
				if (log.isDebugEnabled()) {
					log.debug("json=" + json);
				}
				out.print(json);
			}
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 処理の状態を取得します。
	 * @return 処理の状態。
	 */
	public final int getStatus() {
		return status;
	}

	/**
	 * 処理の状態を設定します。
	 * @param status 処理の状態。
	 */
	public final void setStatus(final int status) {
		this.status = status;
	}


}
