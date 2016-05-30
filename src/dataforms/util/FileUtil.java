package dataforms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * ファイルI/Oユーティリティクラス。
 *
 */
public final class FileUtil {
	/**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(FileUtil.class.getName());


	/**
	 * コンストラクタ。
	 */
	private FileUtil() {

	}

	/**
	 * ファイ名の拡張子を取得します。
	 * @param filename ファイル名。
	 * @return 拡張子。
	 */
	public static String getExtention(final String filename) {
		String ext = "";
		int idx = filename.lastIndexOf('.');
		if (idx >= 0) {
			ext = filename.substring(idx + 1);
		}
		return ext;
	}


	/**
	 * パスからファイル名を取り出します。
	 * @param path パス。
	 * @return ファイル名。
	 */
	public static String getFileName(final String path) {
		String[] sp = path.split("[/\\\\]");
		int lastidx = sp.length - 1;
		return sp[lastidx];
	}

	/**
	 * テキストファイル全体を読み込みます。
	 *
	 * @param path ファイルパス。
	 * @param enc エンコーディング。
	 * @return 読み込んだ文字列。
	 * @throws Exception 例外。
	 */
	public static String readTextFile(final String path, final String enc) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			File f = new File(path);
			if (f.exists()) {
				FileInputStream is = new FileInputStream(path);
				try {
					copyStream(is, os);
				} finally {
					is.close();
				}
			} else {
				return null;
			}
		} finally {
			os.close();
		}
		return new String(os.toByteArray(), enc);
	}

	/**
	 * 入力ストリームを出力ストリームにコピーします。
	 * <pre>
	 * 出力ストリーム、入力ストリームともにクローズされません。
	 * </pre>
	 * @param is 入力ストリーム。
	 * @param os 出力ストリーム。
	 * @throws IOException IO例外。
	 */
	public static void copyStream(final InputStream is, final OutputStream os) throws IOException {
		byte[] buf = new byte[16 * 1024];
		while (true) {
			int len = is.read(buf);
			if (len <= 0) {
				break;
			}
			os.write(buf, 0, len);
		}
	}

	/**
	 * 入力ストリームを出力ストリームにコピーします。
	 * @param is 入力ストリーム。
	 * @param os 出力ストリーム。
	 * @param close trueの場合各ストリームをクローズします。
	 * @throws IOException IO例外。
	 */
	public static void copyStream(final InputStream is, final OutputStream os, final boolean close) throws IOException {
		try {
			try {
				copyStream(is, os);
			} finally {
				if (close) {
					os.close();
				}
			}
		} finally {
			if (close) {
				is.close();
			}
		}
	}


	/**
	 * テキストファイルを出力します。
	 * @param path 出力するファイル。
	 * @param text 出力する文字列。
	 * @param enc 文字コード。
	 * @throws Exception 例外。
	 */
	public static void writeTextFile(final String path, final String text, final String enc) throws Exception {
		PrintWriter out = new PrintWriter(path, enc);
		try {
			out.write(text);
		} finally {
			out.close();
		}
	}

	/**
	 * テキストファイルを出力します。既に存在する場合はバックアップファイルを作成します。
	 * @param path 出力するファイル。
	 * @param text 出力する文字列。
	 * @param enc 文字コード。
	 * @throws Exception 例外。
	 */
	public static void writeTextFileWithBackup(final String path, final String text, final String enc) throws Exception {
		backup(path);
		File srcfile = new File(path);
		File folder = srcfile.getParentFile();
		if (!folder.exists()) {
			folder.mkdirs();
		}
		FileUtil.writeTextFile(srcfile.getAbsolutePath(), text, enc);
	}

	/**
	 * ファイルが存在した場合、バックアップファイルを作成する。
	 * @param path ファイルのパス。
	 */
	public static void backup(final String path) {
		File srcfile = new File(path);
		if (srcfile.exists()) {
			File bakFile = new File(path + ".bak");
			if (bakFile.exists()) {
				bakFile.delete();
			}
			srcfile.renameTo(new File(path + ".bak"));
		}
	}


	/**
	 * InputStreamの内容を読み込み、バイト配列に記録します。
	 * @param is 読み込むInputStream。
	 * @return バイト配列。
	 * @throws Exception 例外。
	 */
	public static byte[] readInputStream(final InputStream is) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			try {
				copyStream(is, os);
			} finally {
				is.close();
			}
		} finally {
			os.close();
		}
		return os.toByteArray();
	}

	/**
	 * 指定されたバイト列をOutputStreamに出力します。
	 * @param data 出力するバイト列。
	 * @param os 出力先のOutputSteam。
	 * @throws Exception 例外。
	 */
	public static void writeOutputStream(final byte[] data, final OutputStream os) throws Exception {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			try {
				copyStream(is, os);
			} finally {
				is.close();
			}
		} finally {
			os.close();
		}
	}


}
