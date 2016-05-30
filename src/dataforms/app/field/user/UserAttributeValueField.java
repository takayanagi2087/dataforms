package dataforms.app.field.user;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.app.dao.enumeration.EnumDao;
import dataforms.app.page.user.UserEditForm;
import dataforms.controller.Form;
import dataforms.controller.JsonResponse;
import dataforms.field.common.EnumOptionSingleSelectField;
import dataforms.validator.RequiredValidator;

/**
 * ユーザ属性値フィールドクラス。
 *
 */
public class UserAttributeValueField extends EnumOptionSingleSelectField {

	/**
	 * フィールドコメント。
	 */
    private static final String COMMENT = "ユーザ属性値";
	/**
     * Logger.
     */
    private static Logger log = Logger.getLogger(UserAttributeValueField.class.getName());

	/**
	 * コンストラクタ。
	 */
	public UserAttributeValueField() {
		super(null, null);
		this.setComment(COMMENT);
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UserAttributeValueField(final String id) {
		super(id, null);
		this.setComment(COMMENT);
	}


	@Override
	protected void onBind() {
		Form form = this.getParentForm();
		if (form instanceof UserEditForm) {
			this.addValidator(new RequiredValidator());
		}
	}


	/**
	 * typeに対応した選択肢を取得します。
	 * @param param パラメータ。
	 * @return 選択肢リスト。
	 * @throws Exception 例外。
	 */
    @WebMethod
	public JsonResponse getTypeOption(final Map<String, Object> param) throws Exception {
    	this.methodStartLog(log, param);
    	EnumDao dao = new EnumDao(this);
    	String type = (String) param.get("type");
    	String lang = this.getPage().getCurrentLanguage();
    	List<Map<String, Object>> list = dao.getOptionList(type, lang);
    	this.setOptionList(list, true);
    	JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, this.getOptionList());
    	this.methodFinishLog(log, result);
    	return result;
    }


}
