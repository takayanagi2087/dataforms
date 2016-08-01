package dataforms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * ファイルI/Oユーティリティクラス。
 *
 */
public final class FileUtil {
	/**
     * Logger.
     */
    private static Logger log = Logger.getLogger(FileUtil.class.getName());


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

	
	/**
	 * 一時フォルダを作成します。
	 * @param path 作成するパス。
	 * @param prefix パスの先頭文字列。
	 * @return 作成されたパス。
	 * @throws Exception 例外。
	 */
	public static Path createTempDirectory(final String path, final String prefix) throws Exception {
		FileSystem fs = FileSystems.getDefault();
		Path temp = Files.createTempDirectory(fs.getPath(path), prefix);
		return temp;
	}

	
	
	/**
	 * ディレクトリ削除Visitorクラス。 
	 *
	 */
	public static class DeleteVisitor extends SimpleFileVisitor<Path> {

		@Override
		public FileVisitResult visitFile(final Path path, final BasicFileAttributes attributes) throws IOException {
			log.debug("delete file : " + path.getFileName());
			Files.delete(path);
			return checkNotExist(path);
		}

		@Override
		public FileVisitResult postVisitDirectory(final Path path, final IOException exception) throws IOException {
			if (exception == null) {
				log.debug("delete directory : " + path.getFileName());
				Files.delete(path);
				return checkNotExist(path);
			} else {
				throw exception;
			}

		}

		
		/**
		 * 削除結果の確認を行います。
		 * @param path パス。
		 * @return ファイルが削除された場合FileVisitResult.CONTINUEを返します。
		 * @throws IOException 例外。
		 */
		private static FileVisitResult checkNotExist(final Path path) throws IOException {
			if (!Files.exists(path)) {
				return FileVisitResult.CONTINUE;
			} else {
				throw new IOException();
			}
		}
	}	

	/**
	 * ディレクトリ階層の削除を行います。
	 * @param path パス。
	 * @throws Exception 例外。
	 */
	public static void deleteDirectory(final String path) throws Exception {
		FileSystem fs = FileSystems.getDefault();
		Path backup = fs.getPath(path);
		Files.walkFileTree(backup, new DeleteVisitor());
	}
	
	/**
	 * Zipファイルにファイルを追加します。
	 * @param basePath 規定パス。
	 * @param file ファイル。
	 * @param zos Zipファイル出力ストリーム。
	 * @throws Exception 例外。
	 */
	private static void addFileToZip(final String basePath, final File file, final ZipOutputStream zos) throws Exception {
		if (!file.isDirectory()) {
			String p = file.getAbsolutePath().substring(basePath.length() + 1).replaceAll("\\\\", "/");
			log.debug("path=" + p);
			ZipEntry zent = new ZipEntry(p);
			zos.putNextEntry(zent);
			FileInputStream is = new FileInputStream(file);
			try {
				copyStream(is, zos);
			} finally {
				is.close();
			}
		} else {
			File[] flist = file.listFiles();
			for (File f: flist) {
				addFileToZip(basePath, f, zos);
			}
		}
	}	
	
	/**
	 * 指定ディレクトリを圧縮しZipファイルを作成します。
	 * @param zipfile Zipファイル。
	 * @param path 圧縮するパス。
	 * @throws Exception 例外。
	 */
	public static void createZipFile(final String zipfile, final String path) throws Exception {
		String basePath = path;
		FileOutputStream os = new FileOutputStream(zipfile);
		try {
			ZipOutputStream zos = new ZipOutputStream(os);
			try {
				File file = new File(path);
				if (!file.isDirectory()) {
					basePath = file.getParentFile().getAbsolutePath();
				}
				addFileToZip(basePath, file, zos);
			} finally {
				zos.close();
			}
		} finally {
			os.close();
		}
	}
}
