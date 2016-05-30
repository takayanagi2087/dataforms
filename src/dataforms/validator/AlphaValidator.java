package dataforms.validator;

/**
 * 半角英字バリデータクラス。
 *
 */
public class AlphaValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public AlphaValidator() {
		super("error.alpha", "^[a-zA-Z]+$");
	}
}
