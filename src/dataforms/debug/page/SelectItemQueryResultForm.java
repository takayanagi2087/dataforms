package dataforms.debug.page;

import java.util.List;
import java.util.Map;

import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.debug.dao.SingleSelectDao;
import dataforms.debug.dao.SingleSelectTable;
import dataforms.field.base.FieldList;
import dataforms.htmltable.PageScrollHtmlTable;


/**
 * 問い合わせ結果フォームクラス。
 */
public class SelectItemQueryResultForm extends QueryResultForm {
	/**
	 * コンストラクタ。
	 */
	public SelectItemQueryResultForm() {
		SingleSelectTable table = new SingleSelectTable();
		this.addPkFieldList(table.getPkFieldList());
		PageScrollHtmlTable htmltable = new PageScrollHtmlTable(Page.ID_QUERY_RESULT, SingleSelectDao.getQueryResultFieldList());
		htmltable.getFieldList().get(SingleSelectTable.Entity.ID_CHAR_ITEM).setSortable(true);
		htmltable.getFieldList().get(SingleSelectTable.Entity.ID_VARCHAR_ITEM).setSortable(true);
		htmltable.getFieldList().get(SingleSelectTable.Entity.ID_SMALLINT_ITEM).setSortable(true);
		htmltable.getFieldList().get(SingleSelectTable.Entity.ID_INTEGER_IETM).setSortable(true);
		htmltable.getFieldList().get(SingleSelectTable.Entity.ID_BIGINT_ITEM).setSortable(true);

		this.addHtmlTable(htmltable);
	}

	/**
	 * 問い合わせを行い、1ページ分の問い合わせ結果を返します。
	 *
	 * @param data 問い合わせフォームの入力データ。
	 * @param queryFormFieldList 問い合わせフォームのフィールドリスト。
	 * @return 問い合わせ結果。
	 *
	 */
	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		SingleSelectDao dao = new SingleSelectDao(this);
		Map<String, Object> ret = dao.queryPage(data, queryFormFieldList);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> queryResult = (List<Map<String, Object>>) ret.get("queryResult");
		for (Map<String, Object> m: queryResult) {
			SingleSelectTable.Entity e = new SingleSelectTable.Entity(m);
			e.setCharItem(e.getCharItem().trim());
		}
		return ret;
	}

	/**
	 * 問い合わせ結果リストのデータを削除します。
	 * <pre>
	 * 問い合わせ結果リストからの削除が不要な場合、HTMLから削除ボタンを削除し、
	 * このメソッドを何もしないメソッドにしてください。
	 * </pre>
	 * @param data 選択したデータのPKの値を記録したマップ。
	 */
	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
		SingleSelectDao dao = new SingleSelectDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.delete(data);
	}
}
