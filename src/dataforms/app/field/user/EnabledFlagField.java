package dataforms.app.field.user;

import dataforms.field.common.FlagField;

/**
 * 外部ユーザ有効フラグフィールドクラス。
 *<pre>
 *正式にメールを経由し登録された外部ユーザであることを示すフラグです。
 *</pre>
 *
 */
public class EnabledFlagField extends FlagField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "ユーザ有効フラグ";

	/**
	 * コンストラクタ。
	 */
	public EnabledFlagField() {
		super(null);
		this.setComment(COMMENT);
		this.setDefaultValue("1");
	}
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public EnabledFlagField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setDefaultValue("1");
	}
}
