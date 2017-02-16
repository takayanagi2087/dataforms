package dataforms.field.base;

/**
 * テキストフィールドの基本クラス。
 *
 */
public class TextField extends Field<String> {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(TextField.class.getName());


	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public TextField(final String fieldId) {
		super(fieldId);
	}


	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 * @param length フィールド長。
	 */
	public TextField(final String fieldId, final int length) {
		super(fieldId);
		this.setLength(length);
	}


	@Override
	public void setClientValue(final Object v) {
		this.setValue((String) v);
	}

	@Override
	protected String getLengthParameterSample() throws Exception {
		if (this.getLength() == 0) {
			return "64";
		} else {
			return Integer.toString(this.getLength());
		}
	}

	@Override
	public String getLengthParameterPattern() throws Exception {
		return "^[0-9]+$";
	}

}
