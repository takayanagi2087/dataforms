package dataforms.validator;

/**
 * 郵便番号バリデータ。
 *
 */
public class ZipCodeValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public ZipCodeValidator() {
		super("error.zipcode", "[0-9]{3}\\-[0-9]{4}");
	}
}
