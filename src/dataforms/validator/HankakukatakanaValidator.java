package dataforms.validator;

/**
 * 半角カタカナバリデータクラス。
 *
 */
public class HankakukatakanaValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public HankakukatakanaValidator() {
		super("error.hankakukatakana", "^[\\uFF65-\\uFF9F]+$");
	}
}
