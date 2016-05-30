package dataforms.field.common;

import dataforms.field.sqltype.TimestampField;

/**
 * レコード作成日時フィールドクラス。
 * <pre>
 * DBテーブル中のレコードの作成日時のフィールドです。
 * </pre>
 */
public class CreateTimestampField extends TimestampField implements DoNotUpdateField {
	/**
	 * コンストラクタ。
	 */
	public CreateTimestampField() {
		super(null);
		this.getValidatorList().clear();
		this.setDateFormat("format.updatetimestampfield");
		this.setComment("レコード作成日時");
		this.setHidden(true);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CreateTimestampField(final String id) {
		super(id);
		this.getValidatorList().clear();
		this.setDateFormat("format.updatetimestampfield");
		this.setComment("レコード作成日時");
		this.setHidden(true);
	}
}
