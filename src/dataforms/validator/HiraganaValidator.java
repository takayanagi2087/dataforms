package dataforms.validator;

/**
 * ひらがなバリデータクラス。
 *
 */
public class HiraganaValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public HiraganaValidator() {
		super("error.hiragana",  "^[\\u3040-\\u309F]+$");
	}
}
