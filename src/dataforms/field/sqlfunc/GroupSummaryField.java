package dataforms.field.sqlfunc;

import dataforms.field.base.Field;

/**
 * 集計関数フィールド基本クラス。
 * <pre>
 * Sum,Max,Min.Avg,Count等の集計関数フィールドの基本クラスです。
 * </pre>
 * @param <TYPE> データ型。
 */
public abstract class GroupSummaryField<TYPE> extends Field<TYPE> {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(GroupSummaryField.class.getName());

	/**
	 * 対象フィールド。
	 */
	private Field<?> targetField = null;

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param field フィールド。
	 */
	public GroupSummaryField(final String id, final Field<?> field) {
		super(id);
		this.targetField = field;
//		log.debug("GroupSummaryField.table=" + field.getTable());
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
	public GroupSummaryField<TYPE> setTargetField(final Field<?> targetField) {
		this.targetField = targetField;
		return this;
	}

	@Override
	public void setClientValue(final Object v) {
		// 何もしない.
	}

	/**
	 * 集計関数名を取得します。
	 * @return 集計関数名。
	 */
	public abstract String getFunctionName();


	@Override
	public Field<?> cloneForSubQuery() {
		Field<?> sf = this.getTargetField().clone();
		sf.setId(this.getId());
		return sf;
	}
}
