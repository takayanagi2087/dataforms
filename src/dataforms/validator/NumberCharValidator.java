package dataforms.validator;

/**
 * 数字バリデータクラス。
 *
 */
public class NumberCharValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public NumberCharValidator() {
		super("error.numberchar", "^[0-9]+$");
	}
}
