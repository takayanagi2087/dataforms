package dataforms.validator;

import dataforms.util.MessagesUtil;


/**
 * 時刻バリデータクラス。
 *
 */
public class  TimeValidator extends DateTimeValidator {
	/**
	 * コンストラクタ。
	 */
	public TimeValidator() {
		super("error.date");
	}

	@Override
	public boolean validate(final Object value) {
		String format = MessagesUtil.getMessage(this.getPage(), "format.timefield");
		this.setDateFormat(format);
		return super.validate(value);
	}
}

