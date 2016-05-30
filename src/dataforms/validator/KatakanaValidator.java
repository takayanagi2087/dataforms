package dataforms.validator;

/**
 * カタカナバリデータクラス。
 *
 */
public class KatakanaValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public KatakanaValidator() {
		super("error.katakana", "^[\\u30A0-\\u30FF]+$");
	}
}
