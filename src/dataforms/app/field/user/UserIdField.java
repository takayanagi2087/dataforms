package dataforms.app.field.user;

import dataforms.field.common.RecordIdField;

/**
 * 各テーブルのIDフィールドクラス。
 */
public class UserIdField extends RecordIdField {
	/**
	 * テーブルコメント。
	 */
	private static final String COMMENT = "ユーザを示すID。";

	/**
	 * コンストラクタ。
	 */
	public UserIdField() {
		super(null);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID.。
	 */
	public UserIdField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

}
