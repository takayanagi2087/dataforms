package dataforms.debug.field;

import dataforms.field.common.SmallintSingleSelectField;


/**
 * SmallintItemFieldフィールドクラス。
 *
 */
public class SmallintItemField extends SmallintSingleSelectField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "Smallintフィールド";
	/**
	 * コンストラクタ。
	 */
	public SmallintItemField() {
		super(null);
		this.setComment(COMMENT);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SmallintItemField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}
}
