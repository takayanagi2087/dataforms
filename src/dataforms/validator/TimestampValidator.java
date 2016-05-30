package dataforms.validator;

import dataforms.util.MessagesUtil;


/**
 * 日付バリデータクラス。
 *
 */
public class  TimestampValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	public TimestampValidator() {
		super("error.date");
	}

	@Override
	public boolean validate(final Object value) {
		String format = MessagesUtil.getMessage(this.getPage(), "format.timestampfield");
		this.setDateFormat(format);
		return super.validate(value);
	}
}

