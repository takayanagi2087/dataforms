package dataforms.validator;

import java.util.Map;

/**
 * 最大長バリデータクラス。
 *
 */
public class MaxLengthValidator extends FieldValidator {
	/**
	 * 最大長。
	 */
	private int length = 8;

	/**
	 * コンストラクタ。
	 * @param length 最大長。
	 */
	public MaxLengthValidator(final int length) {
		super("error.maxlength");
		this.length = length;
	}

	/**
	 * コンストラクタ。
	 * @param messageKey メッセージキー。
	 * @param length 最大長。
	 */
	public MaxLengthValidator(final String messageKey, final int length) {
		super(messageKey);
		this.length = length;
	}

	/**
	 * 最大長を取得します。
	 * @return 最大長。
	 */
	public final int getLength() {
		return length;
	}


	@Override
	public final boolean validate(final Object value) throws Exception {
		if (this.isBlank(value)) {
			return true;
		}
		String str = (String) value;
		if (str.length() <= this.length) {
			return true;
		}
		return false;
	}


	@Override
	public final String getMessage() {
		return this.getMessageResource("{0}", Integer.toString(this.getLength()));
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("length", this.length);
		return ret;
	}
}
