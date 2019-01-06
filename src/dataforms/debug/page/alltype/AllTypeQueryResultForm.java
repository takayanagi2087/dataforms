package dataforms.debug.page.alltype;

import java.util.Map;

import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.dao.Table;
import dataforms.debug.dao.alltype.AllTypeDao;
import dataforms.debug.dao.alltype.AllTypeTable;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.PageScrollHtmlTable;

/**
 * 全データタイプの検索結果リストフォームクラス。
 *
 */
public class AllTypeQueryResultForm extends QueryResultForm {
	/**
	 * コンストラクタ。
	 */
	public AllTypeQueryResultForm() {
		Table tbl = new AllTypeTable();
		this.addPkFieldList(tbl.getPkFieldList());
		PageScrollHtmlTable htmltbl = new PageScrollHtmlTable(Page.ID_QUERY_RESULT,
				new RowNoField()
				, tbl.getField("recordIdField")
				, tbl.getField("charField").setSortable(true)
				, tbl.getField("varcharField").setSortable(true)
				, tbl.getField("numericField").setSortable(true)
				, tbl.getField("dateField").setSortable(true)
				, tbl.getField("timeField").setSortable(true)
				, tbl.getField("timestampField").setSortable(true)
				);
		htmltbl.setFixedColumns(2);
//		htmltbl.setFixedWidth(50.0);
		this.addHtmlTable(htmltbl);
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {

		AllTypeDao dao = new AllTypeDao(this);
		dao.subQueryTest();
		Map<String, Object> ret = dao.getQueryResult(data, flist);
		return ret;
	}


	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
		AllTypeDao dao = new AllTypeDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.deleteAllType(data);
	}
}
