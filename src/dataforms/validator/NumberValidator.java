package dataforms.validator;

import org.apache.commons.validator.GenericValidator;

import dataforms.util.MessagesUtil;

/**
 * 数値バリデータクラス。
 *
 */
public class  NumberValidator extends FieldValidator {

	/**
	 * コンストラクタ。
	 */
	public NumberValidator() {
		super("error.number");
	}

	@Override
	public boolean validate(final Object value) {
		if (this.isBlank(value)) {
			return true;
		}
		String str = ((String) value).replaceAll(",", "");
		if (GenericValidator.isDouble(str)) {
			return true;
		}
		return false;
	}

	@Override
	public String getMessage() {
		return MessagesUtil.getMessage(this.getPage(), this.getMessageKey(), "{0}");
	}
}

