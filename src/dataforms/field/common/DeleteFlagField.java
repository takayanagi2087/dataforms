package dataforms.field.common;


/**
 * 削除フラグフィールドクラス。
 * <pre>
 * 処理対象外のレコードを示すフラグです。
 * DaoのexecuteRemoveメソッドはテーブル中のこのフィールドに"1"を設定します。
 * </pre>
 *
 */
public class DeleteFlagField extends FlagField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "削除フラグ";

	/**
	 * コンストラクタ。
	 */
	public DeleteFlagField() {
		this.setComment(COMMENT);
		this.setDefaultValue("0");
	}

	/**
	 * コンストラクタ。
	 * @param id ID。
	 */
	public DeleteFlagField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setDefaultValue("0");
	}
}
