package dataforms.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import dataforms.dao.file.FileObject;
import dataforms.servlet.DataFormsServlet;

/**
 * バイナリデータの応答情報クラス。
 *
 */
public class BinaryResponse extends FileResponse {

	/**
	 * 入力ストリーム。
	 */
	private InputStream inputStream = null;

	/**
	 * 削除すべき一時ファイル。
	 */
	private File tempFile = null;


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
		if (this.getFileName() != null) {
			resp.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(this.getFileName(), DataFormsServlet.getEncoding()));
		}
		ServletOutputStream os = resp.getOutputStream();
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
