package dataforms.validator;

/**
 * Validationで発生したエラー情報クラス。
 *
 */
public class ValidationError {
	/**
	 * field id。
	 */
	private String fieldId = null;

	/**
	 * メッセージ。
	 */
	private String message = null;


	/**
	 * コンストラクタ。
	 */
	public ValidationError() {

	}

	/**
	 * コンストラクタ。
	 * @param fieldid フィールドID。
	 * @param message メッセージ。
	 */
	public ValidationError(final String fieldid, final String message) {
		this.fieldId = fieldid;
		this.message = message;
	}

	/**
	 * フィールドIDを取得します。
	 * @return フィールドID。
	 */
	public final String getFieldId() {
		return fieldId;
	}

	/**
	 * フィールドIDを設定します。
	 * @param fieldId フィールドID。
	 */
	public final void setFieldId(final String fieldId) {
		this.fieldId = fieldId;
	}

	/**
	 * エラーメッセージを取得します。
	 * @return エラーメッセージ。
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * エラーメッセージを設定します。
	 * @param message エラーメッセージ。
	 */
	public final void setMessage(final String message) {
		this.message = message;
	}
}
