package dataforms.debug.field;

import dataforms.field.sqltype.VarcharField;
import dataforms.validator.MaxLengthValidator;


/**
 * CommentFieldフィールドクラス。
 *
 */
public class CommentField extends VarcharField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 128;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "コメント";
	/**
	 * コンストラクタ。
	 */
	public CommentField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public CommentField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}
}
