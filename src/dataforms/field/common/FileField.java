package dataforms.field.common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.ApplicationError;
import dataforms.controller.BinaryResponse;
import dataforms.controller.JsonResponse;
import dataforms.dao.file.BlobFileStore;
import dataforms.dao.file.FileObject;
import dataforms.dao.file.FileStore;
import dataforms.dao.file.FolderFileStore;
import dataforms.dao.file.WebResource;
import dataforms.dao.file.WebResourceFileStore;
import dataforms.dao.sqldatatype.SqlBlob;
import dataforms.dao.sqldatatype.SqlVarchar;
import dataforms.field.base.Field;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.StringUtil;


/**
 * ファイルフィールドベースクラス。
 * <pre>
 * 対応するHTMLのタグは&lt;input type=&quot;file&quot; ...&gt;になります。
 * </pre>
 *
 * @param <TYPE> サーバで処理するJavaのデータ型。
 */
public abstract class FileField<TYPE extends FileObject> extends Field<TYPE> {
	
	/**
	 * ダウンロードファイル一時ファイルを記録するセッションキー。
	 */
	public static final String DOWNLOADING_FILE = "downloadingFile_";
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(FileField.class);

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FileField(final String id) {
		super(id);
	}


	/**
	 * ファイルストアを作成します。
	 * @return ファイルストア。
	 */
	protected FileStore newFileStore() {
		if (this instanceof SqlBlob) {
			return new BlobFileStore(this);
		} else if (this instanceof SqlVarchar) {
			return new FolderFileStore(this);
		} else if (this instanceof WebResource){
			return new WebResourceFileStore(this);
		} else {
			return null;
		}
	}

	/**
	 * ファイルオブジェクトを作成します。
	 * @return ファイルオブジェクト。
	 */
	protected abstract FileObject newFileObject();


	/**
	 * {@inheritDoc}
	 * <pre>
	 * BLOB保存の場合、一時ファイルを返し、フォルダ保存の場合、そのパスを返します。
	 * </pre>
	 */
	@Override
	public Object getDBValue() {
		Object ret = null;
		try {
			if (this.getValue() != null) {
				FileStore store = this.newFileStore();
				ret = store.convertToDBValue(this.getValue());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * BLOB保存の場合、FileObjectのインスタンスが渡されるため、それをそのままセットします。
	 * フォルダ保存の場合、保存されたパスが渡ってくるので、それからFileObjectのインスタンスを
	 * 作成し、設定します。
	 * </pre>
	 */
	@Override
	public void setDBValue(final Object value) {
		FileObject fobj = newFileObject();
		try {
			FileStore store = this.newFileStore();
			FileObject o = store.convertFromDBValue(value);
			if (o != null) {
				fobj.copy(o);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
		this.setValueObject(fobj);
	}

	@Override
	public void setClientValue(final Object v) {
		try {
			FileObject value = this.newFileObject();
			if (v instanceof FileItem) {
				value.copy(this.newFileStore().convertToFileObject((FileItem) v));
			}
			this.setValueObject(value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * クライアントの処理で必要な項目のみを抜き出したマップを作成し、ブラウザに送信します。
	 * </pre>
	 */
	@Override
	public Object getClientValue() {
		Object obj = this.getValue();
		Map<String, Object> ret = null;
		if (obj != null) {
			if (obj instanceof FileObject) {
				FileObject v = (FileObject) obj;
				if (!StringUtil.isBlank(v.getFileName())) {
					ret = new HashMap<String, Object>();
					// ブラウザに渡す必要最小限の情報をマップに記録します。
					ret.put("fileName", v.getFileName());
					ret.put("size", v.getLength());
					ret.put("downloadParameter", v.getDownloadParameter());
				}
			}
		}
		return ret;
	}

	/**
	 * BLOB用ダウンロードパラメータを取得します。
	 * @param m データマップ。
	 * @return ダウンロードパラメータ。
	 * 
	 * 
	 */
	public String getBlobDownloadParameter(final Map<String, Object> m) {
		FileStore store = this.newFileStore();
		String ret = store.getDownloadParameter(this, m);
		// Videoやaudio再生中の一時ファイルがある場合削除する。
		String sessionKey;
		try {
			sessionKey = java.net.URLDecoder.decode(ret.replaceAll("^key=", DOWNLOADING_FILE), DataFormsServlet.getEncoding());
			String tf = (String) this.getPage().getRequest().getSession().getAttribute(sessionKey);
			if (tf != null) {
				File file = new File(tf);
				file.delete();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * ダウンロードパラメータからファイルストアを作成します。
	 * @param param ダウンロードパラメータ。
	 * @return ファイルストア。
	 * @throws Exception 例外。
	 */
	protected FileStore newFileStore(final Map<String, Object> param) throws Exception {
		String clsname = (String) param.get("store");
		Class<?> scls = Class.forName(clsname);
		FileStore store = (FileStore) scls.getDeclaredConstructors()[0].newInstance(this);
		store.initDownloadParameter(param);
		return store;
	}

	/**
	 * 一時ファイルを削除します。
	 * @param p パラメータ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = false)
	public JsonResponse deleteTempFile(final Map<String, Object> p) throws Exception {
		this.methodStartLog(log, p);
		String key = (String) p.get("key");
		log.debug("key=" + key);
		if (key != null) {
			String sessionKey = DOWNLOADING_FILE + key;
			String downloadingFile = (String) this.getPage().getRequest().getSession().getAttribute(sessionKey);
			log.debug("downloadingFile=" + downloadingFile);
			if (downloadingFile != null) {
				File tf = new File(downloadingFile);
				tf.delete();
			}
		}
		JsonResponse resp = new JsonResponse(JsonResponse.SUCCESS, "");
		this.methodFinishLog(log, resp);
		return resp;
	}
	
	/**
	 * ファイルをダウンロードします。
	 * @param p パラメータ。
	 * @return 画像応答。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = true)
	public BinaryResponse download(final Map<String, Object> p) throws Exception {
		this.methodStartLog(log, p);
		HttpServletRequest req = this.getPage().getRequest();
		Map<String, Object> param = p;
		String key = (String) p.get("key");
		log.debug("key=" + key);
		if (key != null) {
			param = FileStore.decryptDownloadParameter(key);
			// Rangeヘッダが指定されていた場合、送信中ファイルがあればそれをセットする。
			if (!StringUtil.isBlank(req.getHeader("Range"))) {
				String sessionKey = DOWNLOADING_FILE + key;
				log.debug("*sessionKey=" + sessionKey);
				String downloadingFile = (String) req.getSession().getAttribute(sessionKey);
				if (downloadingFile != null) {
					File tf = new File(downloadingFile);
					if (tf.exists()) {
						param.put("downloadingFile", downloadingFile);
					}
				}
			}
		}
		FileStore store = this.newFileStore(param);
		FileObject fobj = store.readFileObject(param);
		BinaryResponse resp = new BinaryResponse(fobj);
		resp.setRequest(req);
		resp.setTempFile(store.getTempFile(fobj));
		if (key != null) {
			if (!store.isSeekingSupported()) {
				// BLOBでRangeヘッダが指定されていた場合、一時ファイルのパスをセッションに記録する。
				if (!StringUtil.isBlank(req.getHeader("Range"))) {
					req.getSession().setAttribute(DOWNLOADING_FILE + key, fobj.getTempFile().getAbsolutePath());
					resp.setTempFile(null); // 転送終了時にファイルを削除しないようにする。
				}
			}
		}
		this.methodFinishLog(log, resp);
		return resp;
	}


	@Override
	public Object getValue(final Map<String, Object> param) {
		Object ret = super.getValue(param);
		if (StringUtil.isBlank(ret)) {
			ret = param.get(this.getId() + "_fn");
		}
		return ret;
	}

	@Override
	public dataforms.field.base.Field.MatchType getDefaultMatchType() {
		return MatchType.NONE;
	}

	/**
	 * インポートデータからファイルオブジェクトを取得します。
	 * @param map インポートフィールドマップ。
	 * @param filePath ファイルの保存パス。
	 * @return FileObject形式のデータ。
	 * @throws Exception 例外。
	 */
	public FileObject getFileObjectFromImportMap(final Map<String, Object> map, final String filePath) throws Exception {
		FileObject ret = this.newFileObject();
		File f = new File(filePath + "/" + (String) map.get("saveFile"));
		ret.setFileName((String) map.get("filename"));
		ret.setLength(f.length());
		ret.setDownloadParameter((String) map.get("downloadParameter"));
		FileStore fs = this.newFileStore();
		File tf = fs.makeTemp(ret.getFileName(), f);
		ret.setTempFile(tf);
		return ret;
	}
}
