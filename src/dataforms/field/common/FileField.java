package dataforms.field.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.ApplicationError;
import dataforms.controller.BinaryResponse;
import dataforms.dao.Table;
import dataforms.dao.file.BlobFileStore;
import dataforms.dao.file.FileObject;
import dataforms.dao.file.FileStore;
import dataforms.dao.file.FolderFileStore;
import dataforms.dao.file.WebResource;
import dataforms.dao.file.WebResourceFileStore;
import dataforms.dao.sqldatatype.SqlBlob;
import dataforms.dao.sqldatatype.SqlVarchar;
import dataforms.field.base.Field;
import dataforms.util.StringUtil;


/**
 * ファイルフィールドベースクラス。
 * <pre>
 * 対応するHTMLのタグは&lt;input type=&quot;file&quot; ...&gt;になります。
 * </pre>
 *
 * @param <TYPE> データ型。
 */
public abstract class FileField<TYPE extends FileObject> extends Field<TYPE> {

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
			fobj = store.convertFromDBValue(value);
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
					ret.put("downloadParam", v.getDownloadParameter());
				}
			}
		}
		return ret;
	}

	/**
	 * BLOB用ダウンロードパラメータを取得します。
	 * @param m データマップ。
	 * @return ダウンロードパラメータ。
	 */
	public String getBlobDownloadParameter(final Map<String, Object> m) {
		StringBuilder sb = new StringBuilder();
		Table table = this.getTable();
		if (table != null) {
			FileStore store = this.newFileStore();
			sb.append("store=");
			sb.append(store.getClass().getName());
			sb.append("&table=");
			sb.append(table.getClass().getName());
			sb.append("&fieldId=");
			sb.append(this.getId());
			for (Field<?> f : table.getPkFieldList()) {
				sb.append("&");
				sb.append(f.getId());
				sb.append("=");
				sb.append(m.get(f.getId()).toString());
			}
		} else {
			log.warn("Table not found. field ID=" + this.getId());
		}
		return sb.toString();
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
	 * ファイルをダウンロードします。
	 * @param param パラメータ。
	 * @return 画像応答。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = true)
	public BinaryResponse download(final Map<String, Object> param) throws Exception {
		FileStore store = this.newFileStore(param);
		FileObject fobj = store.readFileObject(param);
		BinaryResponse resp = new BinaryResponse(fobj);
		resp.setTempFile(store.getTempFile(fobj));
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

}
