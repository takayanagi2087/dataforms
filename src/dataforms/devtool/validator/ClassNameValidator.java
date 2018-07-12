package dataforms.devtool.validator;

import java.util.Map;

import dataforms.util.MessagesUtil;
import dataforms.validator.RegexpValidator;

/**
 * クラス名バリデータ。
 *
 */
public class ClassNameValidator extends RegexpValidator {
	
	/**
	 * クラスタイプ。
	 */
	private String classType = null;
	
	/**
	 * コンストラクタ。
	 * @param classType クラスタイプ。
	 */
	public ClassNameValidator(final String classType) {
		super("error.classname", "^[A-Z].*" + classType + "$");
		this.classType = classType;
	}
	
	@Override
	public String getMessage() {
		return MessagesUtil.getMessage(this.getPage(), this.getMessageKey(), "{0}", this.classType);
	}
	
	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		prop.put("classType", this.classType);
		return prop;
	}
}
