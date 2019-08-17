package dataforms.debug.page;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.controller.BatchProcess;

/**
 * バッチ処理テスト。
 *
 */
public class DebugBatch extends BatchProcess {
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(DebugBatch.class);

	@Override
	public int run(Map<String, Object> params) {
		logger.debug("params=" + params);
		return 0;
	}

}
