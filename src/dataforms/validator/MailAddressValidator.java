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
		super("error.mailaddress", "^.+@.+\\..+$");
	}
}
