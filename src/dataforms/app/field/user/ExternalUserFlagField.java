package dataforms.app.field.user;

import dataforms.field.common.FlagField;

/**
 * 外部ユーザフラグフィールドクラス。
 *<pre>
 *管理ツールでなくユーザ登録画面から登録されたユーザを示すフラグです。
 *</pre>
 *
 */
public class ExternalUserFlagField extends FlagField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "外部ユーザフラグ";

	/**
	 * コンストラクタ。
	 */
	public ExternalUserFlagField() {
		super(null);
		this.setComment(COMMENT);
		this.setDefaultValue("0");
	}
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public ExternalUserFlagField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setDefaultValue("0");
	}
}
