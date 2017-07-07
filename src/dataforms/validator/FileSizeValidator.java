package dataforms.validator;

import java.util.Map;

/**
 * ファイルサイズバリデータ。
 *
 */
public class FileSizeValidator extends FieldValidator {

	/**
	 * 最大ファイルサイズ。
	 */
	private long maxFileSize = 10 * 1024 * 1024;

	/**
	 * コンストラクタ。
	 * @param maxSize 最大ファイルサイズ。
	 */
	public FileSizeValidator(final int maxSize) {
		super("error.fileSize");
		this.maxFileSize = maxSize;
	}



	/**
	 * 最大ファイルサイズを取得します。
	 * @return 最大ファイルサイズ。
	 */
	public long getMaxFileSize() {
		return maxFileSize;
	}


	/**
	 * 最大ファイルサイズを設定します。
	 * @param maxFileSize 最大ファイルサイズ。
	 */
	public void setMaxFileSize(final long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}


	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("maxFileSize", this.getMaxFileSize());
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * ファイルサイズの確認はjavascriptのみで実装する。
	 * </pre>
	 */
	@Override
	public boolean validate(final Object value) throws Exception {
		return true;
	}

}
