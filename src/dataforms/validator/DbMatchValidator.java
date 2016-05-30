package dataforms.validator;


/**
 * DBマッチングパリデータ基本クラス。
 */
public abstract class DbMatchValidator extends FieldValidator {
	/**
	 * コンストラクタ。
	 */
	public DbMatchValidator() {
		super("error.codenotexist");
	}

	/**
	 * コンストラクタ。
	 * @param msgkey メッセージキー。
	 */
	public DbMatchValidator(final String msgkey) {
		super(msgkey);
	}

}

