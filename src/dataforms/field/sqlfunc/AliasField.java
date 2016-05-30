package dataforms.field.sqlfunc;

import dataforms.field.base.Field;

/**
 * 別名フィールドクラス。
 * <pre>
 * QueryのfieldListにnew AliasField("yyy", xxxField)を追加すると、以下のようなsqlを作成します。
 * select m.xxx as yyy ...
 * </pre>
 */
public class AliasField extends Field<Object> {
	/**
	 * 対象フィールド。
	 */
	private Field<?> targetField = null;

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param field 別名を付けるフィールド。
	 */
	public AliasField(final String id, final Field<?> field) {
		super(id);
		this.targetField = field;
		this.setTable(field.getTable());
	}

	/**
	 * 対象フィールドを取得します。
	 * @return 対象フィールド。
	 */
	public Field<?> getTargetField() {
		return targetField;
	}

	/**
	 * 対象フィールドを設定します。
	 * @param targetField 対象フィールド。
	 * @return 設定したフィールド。
	 */
	public AliasField setTargetField(final Field<?> targetField) {
		this.targetField = targetField;
		return this;
	}

	@Override
	public void setClientValue(final Object v) {
		// 何もしない.
	}

	@Override
	public Field<?> cloneForSubQuery() {
		Field<?> ret =  this.targetField.clone();
		ret.setId(this.getId());
		return ret;
	}
}
