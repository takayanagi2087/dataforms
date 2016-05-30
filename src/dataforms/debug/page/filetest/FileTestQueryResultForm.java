package dataforms.debug.page.filetest;

import java.util.Map;

import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.dao.Query;
import dataforms.dao.Table;
import dataforms.debug.dao.filetest.FileFieldTestDao;
import dataforms.debug.dao.filetest.FileFieldTestTable;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.PageScrollHtmlTable;

/**
 * 問い合わせ結果フォームクラス。
 */
public class FileTestQueryResultForm extends QueryResultForm {
	/**
	 * コンストラクタ。
	 */
	public FileTestQueryResultForm() {
		Table tbl = new FileFieldTestTable();
		this.addPkFieldList(tbl.getPkFieldList());
		Query q = new FileFieldTestDao.MainQuery();
		FieldList flist = new FieldList();
		flist.add(new RowNoField());
		flist.addAll(q.getFieldList());
		PageScrollHtmlTable htmltbl = new PageScrollHtmlTable(Page.ID_QUERY_RESULT, flist);
		this.addHtmlTable(htmltbl);
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		FileFieldTestDao dao = new FileFieldTestDao(this);
		return dao.getQueryResult(data, queryFormFieldList);
	}
}
