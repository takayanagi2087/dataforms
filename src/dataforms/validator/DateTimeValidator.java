package dataforms.validator;

import java.util.logging.Logger;



/**
 * 日付時刻関連バリデータの基本クラス。
 *
 */
public class  DateTimeValidator extends FieldValidator {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(DateTimeValidator.class.getName());

	/**
	 * 日付　時刻のフォーマット。
	 */
	private String dateFormat = null;

	/**
	 * コンストラクタ。
	 * @param msgkey メッセージキー。
	 */
	public DateTimeValidator(final String msgkey) {
		super(msgkey);

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
	 */
	public void setDateFormat(final String dateFormat) {
		this.dateFormat = dateFormat;
	}



	@Override
	public boolean validate(final Object value) {
		if (this.isBlank(value)) {
			return true;
		}
		String str = (String) value;
		log.info("DateTimeValudator.format=" + this.dateFormat);
		org.apache.commons.validator.DateValidator v = org.apache.commons.validator.DateValidator.getInstance();
		return v.isValid(str, this.dateFormat, true);
	}

	@Override
	public String getMessage() {
		return this.getMessageResource("{0}", this.dateFormat);
	}
}

