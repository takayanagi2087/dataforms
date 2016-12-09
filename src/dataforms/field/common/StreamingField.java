package dataforms.field.common;

import dataforms.dao.file.FileObject;

/**
 * ストリーミングフィールドクラス。
 * <pre>
 * VideoField、AudioFieldの基底クラスです。
 * </pre>
 *
 * @param <TYPE> サーバで処理するJavaのデータ型。
 */
public abstract class StreamingField<TYPE extends FileObject> extends FileField<TYPE> {

	
	/**
	 * コンストラクタ。
	 *
	 */
	public StreamingField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 *
	 * @param id フィールドID。
	 */
	public StreamingField(final String id) {
		super(id);
	}
}
