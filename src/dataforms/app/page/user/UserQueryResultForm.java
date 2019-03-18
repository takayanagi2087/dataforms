package dataforms.app.page.user;

import java.util.Map;

import dataforms.app.dao.user.UserDao;
import dataforms.app.dao.user.UserInfoTable;
import dataforms.app.field.enumeration.EnumOptionNameField;
import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.field.base.Field;
import dataforms.field.base.Field.SortOrder;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.PageScrollHtmlTable;
import dataforms.util.UserAdditionalInfoTableUtil;

/**
 * ユーザの検索結果フォームクラス。
 *
 */
public class UserQueryResultForm extends QueryResultForm {
    /**
     * Logger.
     */
//    private static Logger logger = Logger.getLogger(UserQueryResultForm.class.getName());

	/**
	 * 問合せ結果テーブル。
	 */
	private PageScrollHtmlTable queryResult = null;

	/**
	 * コンストラクタ。
	 */
	public UserQueryResultForm() {
		UserInfoTable tbl = new UserInfoTable();
		this.addPkField(tbl.getUserIdField());
		FieldList flist = new FieldList();
		flist.addField(new RowNoField());
		flist.addField(tbl.getUserIdField());
		flist.addField(tbl.getLoginIdField()).setSortable(true, SortOrder.DESC);
		flist.addField(tbl.getUserNameField()).setSortable(true);
		FieldList aflist = 	UserAdditionalInfoTableUtil.getFieldList();
		if (aflist != null) {
			for (Field<?> f: aflist) {
				flist.addField(f).setSortable(true);
			}
		}
		for (int i = 0; i < 10; i++) {
			flist.addField(new EnumOptionNameField("attribute" + i)).setSortable(true);
		}
		this.queryResult = new PageScrollHtmlTable(Page.ID_QUERY_RESULT, flist);
		this.addHtmlTable(this.queryResult);

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
