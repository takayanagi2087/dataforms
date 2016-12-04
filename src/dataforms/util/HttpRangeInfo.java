package dataforms.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.regex.Pattern;

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
	 * 範囲の終了が指定されなかった場合、転送するサイズ。
	 */
	private long blockSize = 16 * 1024 * 1024;
	
	/**
	 * HttpのRangeヘッダ。
	 */
	private String rangeHeader = null;
	
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
	 * コンストラクタ。
	 * @param range HttpのRangeヘッダ。
	 */
	public HttpRangeInfo(final String range) {
		if (StringUtil.isBlank(range)) {
			this.rangeHeader = null;
		} else {
			this.rangeHeader = range;
		}
	}


	/**
	 * 範囲の終了が指定されなかった場合、転送するサイズを取得します。
	 * @return 範囲の終了が指定されなかった場合、転送するサイズを取得。
	 */
	public long getBlockSize() {
		return blockSize;
	}


	/**
	 * 範囲の終了が指定されなかった場合、転送するサイズを設定します。
	 * @param blockSize 範囲の終了が指定されなかった場合、転送するサイズ。
	 */
	public void setBlockSize(final long blockSize) {
		this.blockSize = blockSize;
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
//					this.finish = size - 1;
					this.finish = this.start + this.blockSize;
					if (this.finish > size - 1) {
						this.finish = size - 1;
					}
				}
				log.debug("start=" + this.start);
				log.debug("finish=" + this.finish);
				this.status = HttpURLConnection.HTTP_PARTIAL;
				this.contentRange = "bytes " + this.start + "-" + this.finish + "/" + size;
				if (this.start == 0 && this.finish == size - 1) {
					this.status = HttpURLConnection.HTTP_OK;
					this.contentRange = null;
				} else {
					this.status = HttpURLConnection.HTTP_PARTIAL;
					this.contentRange = "bytes " + this.start + "-" + this.finish + "/" + size;
				}
			}
		}
		this.contentLength = this.finish - this.start + 1;
		log.debug("status=" + this.status);
		log.debug("contentRange=" + this.contentRange);
		log.debug("contentLength=" + this.contentLength);
	}
	
	
}
