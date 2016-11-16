package dataforms.dao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import dataforms.controller.Page;
import dataforms.field.common.FileField;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;
import dataforms.util.StringUtil;

/**
 * フォルダファイルストアクラス。
 * <pre>
 * アップロードされたファイルを、upload-data-folderで指定されたフォルダに保存し、
 * テーブルにはそのパスを記録するファイルストアです。
 * このファイルストアには、ユーザID,テーブル名,フィールドIDでフォルダ分けを行います。
 * </pre>
 */
public class FolderFileStore extends FileStore {

	/**
	 * Log.
	 */
	private Logger log = Logger.getLogger(FolderFileStore.class);

	/**
	 * ファイル名を一意にするためのタイムスタンプパターン。
	 */
	private static final String TIMESPAMP_PATTERN = "yyyyMMddHHmmssSSS";

	/**
	 * ユーザID。
	 */
	private String uploadDataForlder = null;

	/**
	 * ユーザID。
	 */
	private long userId = 0L;
	/**
	 * テーブル名。
	 */
	private String tableName = null;

	/**
	 * フィールドID。
	 */
	private String fieldId = null;


	/**
	 * ファイル名。
	 */
	private String fileName = null;


	/**
	 * ファイルのパス。
	 */
	private String filePath = null;
	
	/**
	 * ファイルのタイムスタンプ。
	 */
	private String timestamp = null;


	/**
	 * コンストラクタ。
	 * @param field フィールド。
	 */
	public FolderFileStore(final FileField<? extends FileObject> field) {
		this.uploadDataForlder = DataFormsServlet.getUploadDataFolder();
		Page p = field.getPage();
		this.userId = p.getUserId();
		if (field.getTable() != null) {
			this.tableName = field.getTable().getClass().getSimpleName();
		}
		this.fieldId = field.getId();
	}

	/**
	 * ダウンロードパラメータを指定してファイルストアを作成した場合の初期化処理です。
	 * <pre>
	 * パラメータに指定されたテーブル名設定します。
	 * </pre>
	 * @param param ダウンロードパラメータ。
	 */
	public void initDownloadParameter(final Map<String, Object> param) {
		String t = (String) param.get("t");
		this.tableName = t;
	}


	/**
	 * ファイルの保存フォルダを取得します。
	 * @return ファイルの保存フォルダ。
	 */
	public String getSaveFolder() {
		String ret = this.uploadDataForlder + "/" + this.tableName + "/" + this.fieldId  + "/" + this.userId;
		File f = new File(ret);
		if (!f.exists()) {
			f.mkdirs();
		}
		return ret;
	}

	/**
	 * 一意な保存ファイルを作成します。
	 * <pre>
	 * ファイル名が重複しないようなファイル名を作成します。
	 * </pre>
	 * @return 保存ファイル。
	 */
	public File makeUniqFile() {
		SimpleDateFormat fmt = new SimpleDateFormat(TIMESPAMP_PATTERN);
		Date now = new Date();
		String path = this.getSaveFolder() + "/" + fmt.format(now) + "_" + this.fileName;
		this.filePath = path;
		return new File(path);
	}

	/**
	 * ファイルパスを取得します。
	 * @return ファイルパス。
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * ファイルパスを設定します。
	 * @param filePath ファイルパス。
	 */
	public void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 一時ファイルの保存処理ですが、性能を考慮し本来のフォルダに保存します。
	 * </pre>
	 */
	@Override
	public File makeTempFromFileItem(final FileItem fileItem) throws Exception {
		this.fileName = FileUtil.getFileName(fileItem.getName());

		File file = this.makeUniqFile();
		FileOutputStream os = new FileOutputStream(file);
		try {
			InputStream is = fileItem.getInputStream();
			try {
				FileUtil.copyStream(is, os);
			} finally {
				is.close();
			}
		} finally {
			os.close();
		}
		return file;
	}
	
	@Override
	public File makeTemp(final String filename, final File orgfile) throws Exception {
		this.fileName = filename; //FileUtil.getFileName(orgfile.getAbsolutePath());

		File file = this.makeUniqFile();
		FileOutputStream os = new FileOutputStream(file);
		try {
			InputStream is = new FileInputStream(orgfile);
			try {
				FileUtil.copyStream(is, os);
			} finally {
				is.close();
			}
		} finally {
			os.close();
		}
		return file;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 保存したファイルのパスを返します。
	 * 対応するDBのカラムはVARCHAR型で、この値をそのまま記録します。
	 * </pre>
	 */
	@Override
	public Object convertToDBValue(final Object obj) throws Exception {
		if (obj != null) {
			if (obj instanceof FileObject) {
				FileObject fobj = (FileObject) obj;
				if (fobj.getTempFile() != null) {
					File folder = new File(this.uploadDataForlder);
					String ret = fobj.getTempFile().getAbsolutePath().substring(folder.getAbsolutePath().length());
					ret = ret.replaceAll("\\\\", "/");
					log.debug("FileFolderStore:path=" +  ret);
					return ret;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * colValueは保存されたファイルパスが渡ってくるので、その内容を解析し、fobjに設定します。
	 * </pre>
	 *
	 */
	@Override
	public FileObject convertFromDBValue(final Object colValue) throws Exception {
		FileObject fobj = new FileObject();
		String path = (String) colValue;
		if (!StringUtil.isBlank(path)) {
			String[] sp = path.split("/");
			this.tableName = sp[1];
			this.fieldId = sp[2];
			this.userId = Long.parseLong(sp[3]);
			this.fileName = sp[4].substring(TIMESPAMP_PATTERN.length() + 1);
			this.timestamp = sp[4].substring(0, TIMESPAMP_PATTERN.length());
			File f = new File(this.uploadDataForlder + path);
			fobj.setFileName(this.fileName);
			fobj.setLength(f.length());
			String dlparam = this.getDownloadParameter(null, null); //"store=" + this.getClass().getName() + "&u=" + this.userId + "&t=" + this.tableName + "&f=" + this.fieldId + "&n=" + this.fileName + "&ts=" + ts;
			fobj.setDownloadParameter(dlparam);
			fobj.setTempFile(f);
		} else {
			fobj.setFileName(null);
			fobj.setLength(0);
		}
		return fobj;
	}


	@Override
	public FileObject readFileObject(final Map<String, Object> param) throws Exception {
		String folder = DataFormsServlet.getUploadDataFolder();
		Long u = ((BigDecimal) param.get("u")).longValue();
		String t = (String) param.get("t");
		String f = (String) param.get("f");
		String n = (String) param.get("n");
		String ts = (String) param.get("ts");
		String path = folder + "/" + t + "/" + f + "/" + u + "/" + ts + "_" + n;
		File file = new File(path);
		FileObject fobj = new FileObject();
		fobj.setFileName(n);
		fobj.setLength(file.length());
		fobj.setTempFile(file);
		return fobj;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 削除すべき一時ファイルは無いので、nullを返します。
	 * </pre>
	 */
	@Override
	public File getTempFile(final FileObject fobj) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * DBに記録されたパスからダウンロードパラメータを取得します。
	 * FolderFileStoreの場合、引数のfield,dは使用しないで作成可能です。
	 * </pre>
	 */
	@Override
	public String getDownloadParameter(final FileField<?> field, final Map<String, Object> d) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("store", this.getClass().getName());
		m.put("u", this.userId);
		m.put("t", this.tableName);
		m.put("f", this.fieldId);
		m.put("n", this.fileName);
		m.put("ts", this.timestamp);
		return "key=" + this.encryptDownloadParameter(m);
/*	String dlparam = "store=" + this.getClass().getName() + "&u=" + this.userId + "&t=" + this.tableName + "&f=" + this.fieldId + "&n=" + this.fileName + "&ts=" + this.timestamp;
		return dlparam;*/
	}

}
