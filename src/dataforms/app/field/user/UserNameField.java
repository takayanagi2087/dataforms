package dataforms.app.field.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.app.dao.user.UserDao;
import dataforms.app.page.user.UserEditForm;
import dataforms.app.page.user.UserQueryForm;
import dataforms.app.page.user.UserSelfEditForm;
import dataforms.controller.Form;
import dataforms.controller.QueryResultForm;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.sqltype.VarcharField;
import dataforms.validator.RequiredValidator;

/**
 * ユーザ名フィールドクラス。
 *
 */
public class UserNameField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 32;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "氏名";

	/**
	 * コンストラクタ。
	 */
	public UserNameField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UserNameField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}


	/**
	 * ユーザIDを設定するフィールドID。
	 */
	private String userIdField = "userId";



	/**
	 * ユーザIDフィールドを取得します。
	 * @return ユーザIDフィールド。
	 */
	public String getUserIdField() {
		return userIdField;
	}


	/**
	 * ユーザIDフィールドを設定します。
	 * @param userIdField ユーザIDフィールドのID。
	 */
	public void setUserIdField(final String userIdField) {
		this.userIdField = userIdField;
	}


	@Override
	protected void onBind() {
		Form form = this.getParentForm();
		if (form instanceof UserQueryForm) {
			this.setMatchType(Field.MatchType.PART);
		} else if (form instanceof QueryResultForm) {
			;
		} else {
			if (form instanceof UserEditForm || form instanceof UserSelfEditForm) {
				this.setAutocomplete(false);
				this.addValidator(new RequiredValidator());
			} else {
				this.setAutocomplete(true);
			}
		}
	}

	@Override
	protected List<Map<String, Object>> queryAutocompleteSourceList(final Map<String, Object> data) throws Exception {
		String id = (String) data.get("currentFieldId");
		String name = (String) data.get(id);
    	return queryUser(id, name, MatchType.BEGIN);
	}


	/**
	 * ユーザの問い合わせを行ないます。
	 * @param id フィールドID。
	 * @param name 氏名。
	 * @param matchType マッチタイプ。
	 * @return 検索結果リスト。
	 * @throws Exception 例外。
	 */
	protected List<Map<String, Object>> queryUser(final String id, final String name, final MatchType matchType) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
    	String lang = this.getPage().getCurrentLanguage();
    	data.put("currentLangCode", lang);
    	Field<?> f = new UserNameField();
    	f.setMatchType(matchType);
    	f.setClientValue(name);
    	data.put("userName", f.getValue());
    	FieldList flist = new FieldList(f);
		UserDao dao = new UserDao(this);
		List<Map<String, Object>> result = dao.queryUserList(flist, data);
		// autocomplete用に転記する.
		for (Map<String, Object> m: result) {
			m.put(this.userIdField, m.get("userId"));
		}
		return this.convertToAutocompleteList(this.getHtmlTableRowId(id), result, "userName", "userName", this.userIdField, "loginId");
	}


	@Override
	protected Map<String, Object> queryRelationData(final Map<String, Object> data) throws Exception {
    	//String name = (String) data.get(this.getId());
		String id = (String) data.get("currentFieldId");
		String name = (String) data.get(id);
    	List<Map<String, Object>> list = this.queryUser(id, name, MatchType.FULL);
    	if (list.size() == 1) {
    		return list.get(0);
    	} else {
    		return new HashMap<String, Object>();
    	}
	}

}
