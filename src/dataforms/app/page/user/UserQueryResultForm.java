package dataforms.app.page.user;

import java.util.Map;

import dataforms.app.dao.user.UserDao;
import dataforms.app.dao.user.UserInfoTable;
import dataforms.app.field.enumeration.EnumOptionNameField;
import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.dao.Table;
import dataforms.field.base.Field.SortOrder;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.HtmlTable;
import dataforms.htmltable.PageScrollHtmlTable;

/**
 * ユーザの検索結果フォームクラス。
 *
 */
public class UserQueryResultForm extends QueryResultForm {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(UserQueryResultForm.class.getName());
	/**
	 * コンストラクタ。
	 */
	public UserQueryResultForm() {
		Table tbl = new UserInfoTable();
		//Table atbl = new UserAttributeTable();

		this.addPkField(tbl.getField("userId"));
		HtmlTable htmltbl = new PageScrollHtmlTable(Page.ID_QUERY_RESULT
			, new RowNoField()
			, tbl.getField("userId")
			, tbl.getField("loginId").setSortable(true, SortOrder.DESC)
			, tbl.getField("userName").setSortable(true)
			, new EnumOptionNameField("userLevelName").setSortable(true)
		);
		this.addHtmlTable(htmltbl);
	}


	/**
	 * {@inheritDoc}
	 * 検索結果の指定ページを取得します。
	 */
	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
		this.setUserInfo(data);
		UserDao dao = new UserDao(this);
		Map<String, Object> ret = dao.queryUser(flist, data);
    	return ret;
	}


	/**
	 * [{@inheritDoc}
	 * 選択データの削除を行います。
	 */
	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
		UserDao dao = new UserDao(this);
		dao.deleteUser(data);
	}
}
