package dataforms.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import dataforms.dao.file.FileObject;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.HttpRangeInfo;

/**
 * バイナリデータの応答情報クラス。
 *
 */
public class BinaryResponse extends FileResponse {
	
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(BinaryResponse.class);

	/**
	 * 入力ストリーム。
	 */
	private InputStream inputStream = null;

	/**
	 * 削除すべき一時ファイル。
	 */
	private File tempFile = null;

	/**
	 * 送信データのソースがシークをサポートしているかどうか。
	 */
	private boolean seekingSupported = false;
	
	
	/**
	 * 送信データのソースがシークをサポートしているかどうかを取得します。
	 * @return 送信データのソースがシークをサポートしているかどうか。
	 */
	public boolean isSeekingSupported() {
		return seekingSupported;
	}

	/**
	 * 送信データのソースがシークをサポートしているかどうかを設定します。
	 * @param seekingSupported 送信データのソースがシークをサポートしているかどうか。 
	 */
	public void setSeekingSupported(final boolean seekingSupported) {
		this.seekingSupported = seekingSupported;
	}

	/**
	 * HTTP要求情報。
	 */
	private HttpServletRequest request = null;		
	
	
	/**
	 * HTTP要求情報を取得します。
	 * @return HTTPの要求情報。 
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * HTTP応答情報を設定します。
	 * @param request HTTP応答情報。
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 */
	public BinaryResponse(final byte[] result) {
		this.setResult(result);
		this.setContentType("application/octet-stream");
		this.inputStream = new ByteArrayInputStream(result);
	}

	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 * @param contentType コンテントタイプ。
	 */
	public BinaryResponse(final byte[] result, final String contentType) {
		this.setResult(result);
		this.setContentType(contentType);
		this.inputStream = new ByteArrayInputStream(result);
	}

	/**
	 * コンストラクタ。
	 * @param result 実行結果。
	 * @param contentType コンテントタイプ。
	 * @param fileName ファイル名。
	 */
	public BinaryResponse(final byte[] result, final String contentType, final String fileName) {
		this.setResult(result);
		this.setContentType(contentType);
		this.setFileName(fileName);
		this.inputStream = new ByteArrayInputStream(result);
	}

	/**
	 * コンストラクタ。
	 * @param is 結果の入力ストリーム。
	 */
	public BinaryResponse(final InputStream is) {
		this.setContentType("application/octet-stream");
		this.inputStream = is;
	}

	/**
	 * コンストラクタ。
	 * @param is 結果の入力ストリーム。
	 * @param contentType コンテントタイプ。
	 */
	public BinaryResponse(final InputStream is, final String contentType) {
		this.setContentType(contentType);
		this.inputStream = is;
	}

	/**
	 * コンストラクタ。
	 * @param is 結果の入力ストリーム。
	 * @param contentType コンテントタイプ。
	 * @param fileName ファイル名。
	 */
	public BinaryResponse(final InputStream is, final String contentType, final String fileName) {
		this.setContentType(contentType);
		this.setFileName(fileName);
		this.inputStream = is;
	}

	/**
	 * コンストラクタ。
	 * @param fobj ファイルオブジェクト。
	 * @throws Exception 例外。
	 */
	public BinaryResponse(final FileObject fobj) throws Exception {
		this.setContentType(fobj.getContentType());
		this.setFileName(fobj.getFileName());
		this.inputStream = fobj.openInputStream();
	}


	/**
	 * コンストラクタ。
	 * @param path 送信するファイル。
	 * @param contentType コンテントタイプ。
	 * @param fileName ファイル名。
	 * @throws Exception 例外。
	 */
	public BinaryResponse(final String path, final String contentType, final String fileName) throws Exception {
		this.setContentType(contentType);
		this.setFileName(fileName);
		this.inputStream = new FileInputStream(path);
	}

	
	
	/**
	 * 一時ファイルを取得します。
	 * @return 一時ファイル。
	 */
	public File getTempFile() {
		return tempFile;
	}

	/**
	 * 一時ファイルを設定します。
	 * @param tempFile 一時ファイル。
	 */
	public void setTempFile(final File tempFile) {
		this.tempFile = tempFile;
	}

	/**
	 * {@inheritDoc}
	 * バイナリデータを送信する。
	 */
	@Override
	public void send(final HttpServletResponse resp) throws Exception {
		resp.setContentType(this.getContentType());
		log.debug("content-type:" + resp.getContentType());
		if (this.getFileName() != null) {
			resp.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(this.getFileName(), DataFormsServlet.getEncoding()));
		}
		String rangeHeader = null;
		if (this.isSeekingSupported()) {
			if (this.getRequest() != null) {
				rangeHeader = this.getRequest().getHeader("Range");
				log.debug("rangeHeader=" + rangeHeader);
			}
		}
		HttpRangeInfo p = new HttpRangeInfo(rangeHeader);
		p.parse(this.inputStream);
		log.debug("status=" + p.getStatus());
		log.debug("contentLength=" + p.getContentLength());
		log.debug("contentRange=" + p.getContentRange());
		resp.setHeader("Content-Length", "" +  p.getContentLength());
		resp.setStatus(p.getStatus());
		if (p.getContentRange() != null) {
			resp.setHeader("Accept-Ranges", "bytes");
			resp.setHeader("Content-Range", p.getContentRange());
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(resp.getOutputStream());
			try {
				BufferedInputStream bis = new BufferedInputStream(this.inputStream);
				try {
					bis.skip(p.getStart());
					for (long idx = p.getStart(); idx <= p.getFinish(); idx++) {
						int c = bis.read();
						if (c < 0) {
							break;
						}
						bos.write(c);
					}
				} finally {
					bis.close();
				}
			} finally {
				bos.close();
				if (this.getTempFile() != null) {
					this.getTempFile().delete();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * ファイルへの保存。
	 * @param path 保存先。
	 * @throws Exception 例外。
	 */
	public void saveFile(final String path) throws Exception {
		FileOutputStream os = new FileOutputStream(path);
		try {
			InputStream is = this.inputStream;
			try {
				byte[] buf = new byte[16 * 1024];
				while (true) {
					int len = is.read(buf);
					if (len <= 0) {
						break;
					}
					os.write(buf, 0, len);
				}
			} finally {
				is.close();
			}
		} finally {
			os.flush();
			os.close();
		}
		if (this.getTempFile() != null) {
			this.getTempFile().delete();
		}
	}

}
