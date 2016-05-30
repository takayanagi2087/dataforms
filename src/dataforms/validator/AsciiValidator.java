package dataforms.validator;

/**
 * ASCII文字バリデータクラス。
 *
 */
public class AsciiValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public AsciiValidator() {
		super("error.ascii", "^[\\u0020-\\u007E]+$");
	}
}
