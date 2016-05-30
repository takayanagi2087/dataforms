package dataforms.validator;

import java.util.Map;

import dataforms.controller.WebComponent;
import dataforms.util.MessagesUtil;
import dataforms.util.StringUtil;


/**
 * フィールドバリデータの基本クラス。
 *
 */
public abstract class FieldValidator extends WebComponent {
	/**
	 * フィールドID。
	 */
	private String fieldId = null;



	/**
	 * フィールドIDを取得します。
	 * @return フィールドID。
	 */
	public String getFieldId() {
		return fieldId;
	}


	/**
	 * フィールドIDを設定します。
	 * @param fieldId フィールドID。
	 */
	public void setFieldId(final String fieldId) {
		this.fieldId = fieldId;
	}


	/**
	 * メッセージキー。
	 */
	private String messageKey = null;

	/**
	 * メッセージキーを取得します。
	 * @return メッセージキー。
	 */
	public final String getMessageKey() {
		return messageKey;
	}


	/**
	 * コンストラクタ。
	 * @param msgkey エラー時に表示するメッセージキー。
	 */
	public FieldValidator(final String msgkey) {
		this.messageKey = msgkey;
	}

	/**
	 * フィールドの内容をチェックします。
	 * @param value フィールドの内容。
	 * @return チェック結果。
	 * @throws Exception 例外。
	 */
	public abstract boolean validate(final Object value) throws Exception;

	/**
	 * 指定された値がブランクかどうかを確認します。
	 * @param value フィールドの値。
	 * @return ブランクの場合true。
	 */
	protected final boolean isBlank(final Object value) {
		return StringUtil.isBlank(value);
	}

	/**
	 * メッセージを取得します。
	 * @param args メッセージの引数。
	 * @return メッセージ。
	 */
	protected final String getMessageResource(final String... args) {
		return MessagesUtil.getMessage(this.getPage(), this.messageKey, args);
	}

	/**
	 * 対応するエラーメッセージを取得します。
	 * @return メッセージ。
	 */
	public String getMessage() {
		return this.getMessageResource("{0}");
	}


	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> map = super.getClassInfo();
		map.put("fieldId", this.fieldId);
		map.put("messageKey", this.messageKey);
		return map;
	}
}

