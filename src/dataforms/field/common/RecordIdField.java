package dataforms.field.common;

import dataforms.field.sqltype.BigintField;

/**
 * レコードIDフィールドクラス。
 * <pre>
 * テーブルのレコードIDとなるフィールドです。
 * このクラスから派生したフィールドをテーブルの先頭の主キーとし
 * autoIncrementIdがtrueの場合、Dao.executeInsertでレコードIDを
 * 自動生成します。
 * </pre>
 */
public class RecordIdField extends BigintField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "レコードID";

	/**
	 * コンストラクタ。
	 */
	public RecordIdField() {
		super(null);
		this.setHidden(true);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public RecordIdField(final String id) {
		super(id);
		this.setHidden(true);
		this.setComment(COMMENT);
	}

	@Override
	public dataforms.field.base.Field.MatchType getDefaultMatchType() {
		return MatchType.NONE;
	}
}
