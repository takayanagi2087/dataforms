package dataforms.validator;

/**
 * メールアドレスバリデータクラス。
 *
 */
public class MailAddressValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public MailAddressValidator() {
//		super("error.mailaddress", "^.+@.+\\..+$");
		super("error.mailaddress", "^([a-zA-Z0-9][a-zA-Z0-9_.+\\-]*)@(([a-zA-Z0-9][a-zA-Z0-9_\\-]*\\.)+[a-zA-Z]{2,6})$");
	}
}