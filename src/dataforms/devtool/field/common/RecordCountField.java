package dataforms.devtool.field.common;

import dataforms.field.sqltype.IntegerField;

/**
 * レコード件数フィールドクラス。
 *
 */
public class RecordCountField extends IntegerField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "レコード件数";

	/**
	 * コンストラクタ。
	 */
	public RecordCountField() {
		super(null);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public RecordCountField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}
}
