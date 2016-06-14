package dataforms.dao.file;

import java.io.File;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import dataforms.util.FileUtil;

/**
 * ファイル保存領域クラス。
 * <pre>
 * アップロードされたファイルを管理するためのクラスです。
 *
 * </pre>
 *
 */
public abstract class FileStore {
	/**
	 * コンストラクタ。
	 */
	public FileStore() {
	}

	/**
	 * アップロード直後にファイルを一時ファイルに保存します。
	 * @param fileItem アップロードファイル情報。
	 * @return 作成された一時ファイル。
	 * @throws Exception 例外。
	 */
	protected abstract File makeTempFromFileItem(final FileItem fileItem) throws Exception;

	/**
	 * テーブル保存用一時ファイルを作成します。
	 * @param filename ファイル名。
	 * @param orgfile 元ファイル。
	 * @return 作成された一時ファイル。
	 * @throws Exception 例外。
	 */
	public abstract File makeTemp(final String filename, final File orgfile) throws Exception;

	/**
	 * ダウンロードパラメータを指定してファイルストアを作成した場合の初期化処理です。
	 * @param param ダウンロードパラメータ。
	 */
	public void initDownloadParameter(final Map<String, Object> param) {

	}

	/**
	 * アップロードファイル情報をFileObjectに変換します。
	 * @param fileItem アップロードファイル情報。
	 * @return FileObject。
	 * @throws Exception 例外。
	 */
	public FileObject convertToFileObject(final FileItem fileItem) throws Exception {
		FileObject ret = new FileObject();
		File tempFile = this.makeTempFromFileItem(fileItem);
		ret.setFileName(FileUtil.getFileName(fileItem.getName()));
		ret.setLength(fileItem.getSize());
		ret.setTempFile(tempFile);
		return ret;
	}



	/**
	 * DBに保存する値に変換します。
	 * @param obj FileObject。
	 * @return DBに保存する値。
	 * @throws Exception 例外。
	 */
	public abstract Object convertToDBValue(final Object obj) throws Exception;


	/**
	 * DBから読み込んだ値をFileObjectに変換します。
	 * @param colValue DBから読み込んだ値。
	 * @return FileObject。
	 * @throws Exception 例外。
	 */
	public abstract FileObject convertFromDBValue(final Object colValue) throws Exception;


	/**
	 * ファイルダウンロード用にファイルオブジェクトを読み込みます。
	 * @param param ダウンロードパラメータ。
	 * @return ファイルオブジェクト。
	 * @throws Exception 例外。
	 */
	public abstract FileObject readFileObject(final Map<String, Object> param) throws Exception;

	/**
	 * 削除すべき一時ファイルを取得します。
	 * @param fobj ファイルオブジェクト。
	 * @return 削除すべき一時ファイル。
	 */
	public abstract File getTempFile(final FileObject fobj);
}

