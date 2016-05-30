package dataforms.app.field.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.app.dao.user.UserDao;
import dataforms.app.page.login.LoginForm;
import dataforms.app.page.user.UserEditForm;
import dataforms.app.page.user.UserQueryForm;
import dataforms.app.page.user.UserSelfEditForm;
import dataforms.controller.EditForm;
import dataforms.controller.Form;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.field.sqltype.VarcharField;
import dataforms.validator.RegexpValidator;
import dataforms.validator.RequiredValidator;

/**
 * ログインIDフィールドクラス。
 *
 */
public class LoginIdField extends VarcharField {
	/**
	 * 項目長。
	 */
	private static final int LENGTH = 64;
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "ログインID.";

	/**
	 * コンストラクタ。
	 */
	public LoginIdField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public LoginIdField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		Form form = this.getParentForm();
		if (form instanceof UserQueryForm) {
			this.setMatchType(MatchType.BEGIN);
		} else 	if (form instanceof UserEditForm
				|| form instanceof LoginForm
				|| form instanceof UserSelfEditForm
				) {
			this.addValidator(new RequiredValidator());
		} else if (form instanceof EditForm) {
			this.addValidator(new RequiredValidator());
			this.setRelationDataAcquisition(true);
			this.setAutocomplete(true);
		}

//		this.addValidator(new RegexpValidator("^[0-9A-Za-z_\\.\\-_]+$"));
		this.addValidator(new RegexpValidator("^[\\x20-\\x7F]+$"));
	}


	/**
	 * ユーザの問い合わせを行綯います。
	 * @param id フィールドID。
	 * @param loginId ログインID。
	 * @param matchType マッチタイプ。
	 * @return 検索結果リスト。
	 * @throws Exception 例外。
	 */
	protected List<Map<String, Object>> queryUser(final String id, final String loginId, final MatchType matchType) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
    	String lang = this.getPage().getCurrentLanguage();
    	data.put("currentLangCode", lang);
    	Field<?> f = new LoginIdField();
    	f.setMatchType(matchType);
    	f.setClientValue(loginId);
    	data.put("loginId", f.getValue());
    	FieldList flist = new FieldList(f);
		UserDao dao = new UserDao(this);
		List<Map<String, Object>> result = dao.queryUserList(flist, data);
		// List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// autocomplete用に転記する.
		for (Map<String, Object> m: result) {
			m.put("labelText", m.get("loginId") + " " + m.get("userName"));
		}
		return this.convertToAutocompleteList(this.getHtmlTableRowId(id), result, "loginId", "labelText", "userId", "userName");
	}


	/**
	 * {@inheritDoc}
	 *
	 * ログインIDに関連した情報を取得します。
	 *
	 */
	@Override
	protected Map<String, Object> queryRelationData(final Map<String, Object> data)
			throws Exception {
		String id = (String) data.get("currentFieldId");
		String loginID = (String) data.get(id);
    	List<Map<String, Object>> list = this.queryUser(id, loginID, MatchType.FULL);
    	if (list.size() == 1) {
    		return list.get(0);
    	} else {
    		return new HashMap<String, Object>();
    	}
	}

	/**
	 * {@inheritDoc}
	 *
	 * ユーザのAutocompleteの候補リストの問い合わせを行います。
	 */
	@Override
	protected List<Map<String, Object>> queryAutocompleteSourceList(final Map<String, Object> data)
			throws Exception {
		String id = (String) data.get("currentFieldId");
		String loginId = (String) data.get(id);
    	return queryUser(id, loginId, MatchType.BEGIN);
	}

}
