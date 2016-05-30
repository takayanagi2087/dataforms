package dataforms.app.page.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.UserAttributeTypeField;
import dataforms.app.field.user.UserAttributeValueField;
import dataforms.app.field.user.UserNameField;
import dataforms.controller.QueryForm;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.htmltable.EditableHtmlTable;

/**
 * ユーザ検索フォームクラス。
 */
public class UserQueryForm extends QueryForm {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(UserQueryForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public UserQueryForm() {
		this.addField(new LoginIdField()).setMatchType(Field.MatchType.BEGIN);
		this.addField(new UserNameField());
		EditableHtmlTable at = new EditableHtmlTable("attTable",
			new FieldList(
				new UserAttributeTypeField(),
				new UserAttributeValueField()
			)
		);
		this.addHtmlTable(at);
	}

	@Override
	public void init() throws Exception {
		super.init();
		// 初期データ設定.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		this.setFormData("attTable", list);
	}

}
