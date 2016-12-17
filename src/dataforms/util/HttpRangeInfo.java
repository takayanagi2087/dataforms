package dataforms.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * HTTPのGET時にRangeリクエストヘッダが付いた場合の計算ロジックをまとメタクラスです。
 *
 */
public class HttpRangeInfo {
	
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(HttpRangeInfo.class);
	
	/**
	 * ブロックサイズリスト。
	 */
	private static List<LinkedHashMap<String, Object>> blockSizeList = null;
	
	/**
	 * 範囲の終了が指定されなかった場合、転送するサイズ。
	 */
	private static final long DEFAULT_BLOCK_SIZE = 16 * 1024 * 1024;
	
	/**
	 * HttpのRangeヘッダ。
	 */
	private String rangeHeader = null;
	
	/**
	 * HttpのUser-Agentヘッダ。
	 */
	private String userAgent = null;
	
	/**
	 * ダウンロード開始位置。
	 * 
	 */
	private long start = 0;
	/**
	 * ダウンロード終了位置。
	 */
	private long finish = -1;
	
	/**
	 * 送信バイト数。
	 */
	private long contentLength = 0;

	/**
	 * HTTP status。
	 */
	private int status = HttpURLConnection.HTTP_OK; // OK
	
	/**
	 * contentRangeヘッダ。
	 */
	private String contentRange = null;
	
	/**
	 * ブロックサイズリストを取得します。
	 * @return ブロックサイズリスト。
	 */
	public static List<LinkedHashMap<String, Object>> getBlockSizeList() {
		return blockSizeList;
	}

	/**
	 * ブロックサイズリストを設定します。
	 * @param blockSizeList ブロックサイズリスト。
	 */
	public static void setBlockSizeList(final List<LinkedHashMap<String, Object>> blockSizeList) {
		HttpRangeInfo.blockSizeList = blockSizeList;
	}


	/**
	 * コンストラクタ。
	 * @param req HTTP要求情報。
	 */
	public HttpRangeInfo(final HttpServletRequest req) {
		if (req != null) {
			this.rangeHeader = req.getHeader("Range");
			this.userAgent = req.getHeader("User-Agent");
		}
		log.debug("this.rangeHeader=" + this.rangeHeader);
		log.debug("this.userAgent=" + this.userAgent);
	}


	/**
	 * 転送範囲の開始位置を取得します。
	 * @return 転送範囲の開始位置。
	 */
	public long getStart() {
		return start;
	}


	/**
	 * 転送範囲の開始位置を設定します。
	 * @param start 転送範囲の開始位置。
	 */
	public void setStart(final long start) {
		this.start = start;
	}


	/**
	 * 転送範囲の終了位置を所得します。
	 * @return 転送範囲の終了位置。
	 */
	public long getFinish() {
		return finish;
	}


	/**
	 * 転送範囲の終了位置を設定します。
	 * @param finish 転送範囲の終了位置。
	 */
	public void setFinish(final long finish) {
		this.finish = finish;
	}

	/**
	 * Content-Lengthヘッダの値を返します。
	 * @return Content-Lengthヘッダの値。
	 */
	public long getContentLength() {
		return contentLength;
	}

	/**
	 * 返すべきHTTP_STATUSを取得します。
	 * @return HTTP status。
	 */
	public int getStatus() {
		return status;
	}


	/**
	 * 返すべきContent-Rangeヘッダを返します。
	 * @return 返すべきContent-Rangeヘッダ。
	 */
	public String getContentRange() {
		return contentRange;
	}

	/**
	 * 指定されたRangeヘッダを解析し、ファイルの部分転送範囲の決定と、各種応答情報を作成します。
	 * @param is 入力ストリーム。
	 * @throws Exception 例外。
	 */
	public void parse(final InputStream is) throws Exception {
		long size = is.available();
		this.parse(size);
	}

	/**
	 * ストリーミング転送のブロックサイズを取得します。
	 * @return ストリーミング転送のブロックサイズ。 
	 */
	private long getBlockSize() {
		if (HttpRangeInfo.blockSizeList == null) {
			long ret = HttpRangeInfo.DEFAULT_BLOCK_SIZE;
			if (Pattern.matches(".*Firefox.*", this.userAgent)) {
				ret = -1;
			}
			return ret;
		} else {
			long ret = HttpRangeInfo.DEFAULT_BLOCK_SIZE;
			for (Map<String, Object> m: HttpRangeInfo.blockSizeList) {
				String uaPattern = (String) m.get("uaPattern");
				BigDecimal size = (BigDecimal) m.get("blockSize");
				if (Pattern.matches(uaPattern, this.userAgent)) {
					ret = size.longValue();
					break;
				}
			}
			return ret;

		}
	}
	
	/**
	 * 指定されたRangeヘッダを解析し、ファイルの部分転送範囲の決定と、各種応答情報を作成します。
	 * @param size ファイルサイズ。
	 */
	public void parse(final long size) {
		log.debug("size=" + size);
		this.start = 0;
		this.finish = size - 1;
		if (!StringUtil.isBlank(this.rangeHeader)) {
			if (Pattern.matches("bytes=[0-9]+\\-[0-9]*", this.rangeHeader)) {
				log.debug("range=" + this.rangeHeader);
				String [] sp = this.rangeHeader.split("[=-]");
				if (sp.length == 3) {
					this.start = Long.parseLong(sp[1]);
					this.finish = Long.parseLong(sp[2]);
				} else if (sp.length == 2) {
					this.start = Long.parseLong(sp[1]);
					long blockSize = this.getBlockSize();
					if (blockSize < 0) {
						this.finish = size - 1;
					} else {
						this.finish = this.start + blockSize - 1;
						if (this.finish > size - 1) {
							this.finish = size - 1;
						}
					}
				}
				log.debug("start=" + this.start);
				log.debug("finish=" + this.finish);
				this.status = HttpURLConnection.HTTP_PARTIAL;
				this.contentRange = "bytes " + this.start + "-" + this.finish + "/" + size;
			}
		}
		this.contentLength = this.finish - this.start + 1;
		log.debug("status=" + this.status);
		log.debug("contentRange=" + this.contentRange);
		log.debug("contentLength=" + this.contentLength);
	}
	
	
}
