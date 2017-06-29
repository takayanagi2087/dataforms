package dataforms.app.page.enumeration;

import java.util.Map;

import dataforms.app.dao.enumeration.EnumGroupDao;
import dataforms.app.dao.enumeration.EnumGroupTable;
import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.field.base.FieldList;
import dataforms.htmltable.PageScrollHtmlTable;


/**
 * 問い合わせ結果フォームクラス。
 */
public class EnumGroupQueryResultForm extends QueryResultForm {
	/**
	 * コンストラクタ。
	 */
	public EnumGroupQueryResultForm() {
		EnumGroupTable table = new EnumGroupTable();
		table.getEnumGroupCodeField().setHidden(true);
//		table.getEnumTypeCodeField().setHidden(true);
//		this.addPkFieldList(table.getPkFieldList());
		this.addPkFieldList(new FieldList(table.getEnumGroupCodeField()));
		PageScrollHtmlTable htmltable = new PageScrollHtmlTable(Page.ID_QUERY_RESULT, EnumGroupDao.getQueryResultFieldList());
		htmltable.getFieldList().get(EnumGroupTable.Entity.ID_ENUM_GROUP_CODE).setSortable(true);

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
		EnumGroupDao dao = new EnumGroupDao(this);
		return dao.queryPage(data, queryFormFieldList);
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
		EnumGroupDao dao = new EnumGroupDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.delete(data);
	}
}
