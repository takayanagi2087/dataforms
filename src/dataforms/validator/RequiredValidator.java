package dataforms.validator;


/**
 * 必須バリデータ。
 *
 */
public class RequiredValidator extends FieldValidator {

	/**
	 * コンストラクタ。
	 */
	public RequiredValidator() {
		super("error.required");
	}

	@Override
	public boolean validate(final Object value) {
		return !this.isBlank(value);
	}
}

