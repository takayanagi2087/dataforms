package dataforms.controller;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;

/**
 * バッチ処理。
 *
 */
public abstract class BatchProcess extends Page {
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(BatchProcess.class);

	/**
	 * コンストラクタ。
	 */
	public BatchProcess() {
		super();
		this.setMenuItem(false);
	}

	/**
	 * localhostからのアクセスみ許可。
	 */
	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		String rhost = this.getRequest().getRemoteAddr();
		logger.debug("rhost=" + rhost);
		if ("0:0:0:0:0:0:0:1".equals(rhost) || "127.0.0.1".equals(rhost)) {
			return true;
		} else {
			return false;
		}
	}

	@WebMethod
	@Override
	public Response getHtml(final Map<String, Object> params) throws Exception {
		int ret = this.run(params);
		return new TextResponse(Integer.toString(ret));
	}

	/**
	 * バッチ処理。
	 * @param params パラメータ。
	 * @return 終了コード。
	 */
	public abstract int run(final Map<String, Object> params);
}
