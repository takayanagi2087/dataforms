package dataforms.field.base;

/**
 * 日付、時刻フィールドの基本クラス。
 *
 * @param <TYPE> データ型。
 */
public abstract class DateTimeField<TYPE> extends Field<TYPE> {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(TextField.class.getName());

	/**
	 * 日付フォーマット。
	 */
	private String dateFormat = null;

	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public DateTimeField(final String fieldId) {
		super(fieldId);
	}

	/**
	 * 日付フォーマットを取得します。
	 * @return 日付フォーマット。
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * 日付フォーマットを設定します。
	 * @param dateFormat 日付フォーマット。
	 * @return 設定したフィールド。
	 *
	 */
	public DateTimeField<TYPE> setDateFormat(final String dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 検索条件フィールドを生成する際は範囲条件とします。
	 * </pre>
	 */
	@Override
	public dataforms.field.base.Field.MatchType getDefaultMatchType() {
		return MatchType.RANGE_FROM;
	}
}
