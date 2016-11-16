package dataforms.dao.file;

import java.io.File;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import dataforms.field.common.FileField;

/**
 * Webリソースファイルストアクラス。
 * <pre>
 * 画像等http経由でアクセス可能なリソースをアクセスするためのファイルストアです。
 * リソースは読み取り専用になります。
 * </pre>
 */
public class WebResourceFileStore extends FileStore {
	/**
	 * フィールド。
	 */
	private FileField<? extends FileObject> field = null;

	/**
	 * コンストラクタ。
	 * @param field フィールド。
	 */
	public WebResourceFileStore(final FileField<? extends FileObject> field) {
		this.field = field;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 読み取り専用なので、何もせずnullを返します。
	 * </pre>
	 */
	@Override
	protected File makeTempFromFileItem(final FileItem fileItem) throws Exception {
		return null;
	}

	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * 読み取り専用なので、何もせずnullを返します。
	 * </pre>
	 */
	@Override
	public File makeTemp(final String filename, final File orgfile) throws Exception {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * 読み取り専用なので、何もせずnullを返します。
	 * </pre>
	 */
	@Override
	public Object convertToDBValue(final Object obj) throws Exception {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 読み取り専用なので、何もせずnullを返します。
	 * </pre>
	 */
	@Override
	public FileObject convertFromDBValue(final Object colValue) throws Exception {
		return null;
	}

	/**
	 * 指定されたWebリソースを読み込みます。
	 * @param url URL。
	 * @return FileObject。
	 * @throws Exception 例外。
	 */
	public FileObject readWebResource(final String url) throws Exception {
		FileObject val = new FileObject();
//		String v = this.field.getWebResourceUrl(url);
		byte[] image = this.field.getBinaryWebResource(url);
		val.setWebResourceUrl(url);
		val.setContents(image);
		val.setLength(image.length);
		return val;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 指定されたURLの内容を読み込みます。
	 * </pre>
	 */
	@Override
	public FileObject readFileObject(final Map<String, Object> param) throws Exception {
		String url = (String) param.get("url");
		return this.readWebResource(url);
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 読み取り専用なので、何もせずnullを返します。
	 * </pre>
	 */
	@Override
	public File getTempFile(final FileObject fobj) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 何も処理せずnullを返します。
	 * </pre>
	 */
	@Override
	public String getDownloadParameter(final FileField<?> field, final Map<String, Object> d) {
		return null;
	}

}
