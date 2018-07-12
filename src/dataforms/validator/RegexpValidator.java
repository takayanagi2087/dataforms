package dataforms.validator;

import java.util.Map;
import java.util.regex.Pattern;

import dataforms.util.MessagesUtil;


/**
 * 正規表現パターンバリデータクラス。
 *
 */
public class RegexpValidator extends FieldValidator {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(RegexpValidator.class.getName());

	/**
	 * 正規表現パターン。
	 */
	private String pattern = null;

	/**
	 * コンストラクタ。
	 * @param pattern 正規表現パターン。
	 * <pre>
	 * クライアントのjavascriptでチェックする場合は"^...$"の形式で記述する。
	 * </pre>
	 */
	public RegexpValidator(final String pattern) {
		super("error.regexp");
		this.pattern = pattern;
	}

	/**
	 * コンストラクタ。
	 * @param msgkey メッセージキー。
	 * @param pattern 正規表現パターン。
	 */
	public RegexpValidator(final String msgkey, final String pattern) {
		super(msgkey);
		this.pattern = pattern;
	}


	/**
	 * 正規表現パターンを取得します。
	 * @return 正規表現パターン。
	 */
	public final String getPattern() {
		return pattern;
	}

	@Override
	public boolean validate(final Object value) throws Exception {
		if (this.isBlank(value)) {
			return true;
		}
		String str = (String) value;
		return Pattern.matches(this.pattern, str);
	}

	@Override
	public String getMessage() {
		return MessagesUtil.getMessage(this.getPage(), this.getMessageKey());
	}

	@Override
	public  Map<String, Object> getProperties() throws Exception {
		Map<String, Object> map = super.getProperties();
		map.put("pattern", this.pattern);
		return map;
	}
}
