package dataforms.devtool.page.query;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.JsonResponse;
import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.dao.Dao;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;
import dataforms.htmltable.PageScrollHtmlTable;
import net.arnx.jsonic.JSON;


/**
 * 問い合わせ結果フォームクラス。
 */
public class QueryExecutorQueryResultForm extends QueryResultForm {
	
	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(QueryExecutorQueryResultForm.class);
	
	/**
	 * HTMLテーブル。
	 */
	private PageScrollHtmlTable htmlTable = null;
	
	/**
	 * コンストラクタ。
	 */
	public QueryExecutorQueryResultForm() {
		this.htmlTable = new PageScrollHtmlTable(Page.ID_QUERY_RESULT);
		this.addHtmlTable(this.htmlTable);
	}

	
	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		String sql = (String) data.get("sql");
		Dao dao = new Dao(this);
		Map<String, Object> ret = dao.executePageQuery(sql, data);
		FieldList flist = dao.getResultSetFieldList();
		this.htmlTable.getFieldList().clear();
		this.htmlTable.getFieldList().addAll(flist);
/*		for (Field<?> f: this.htmlTable.getFieldList()) {
			f.setSortable(true);
		}*/
		ret.put("htmlTable", htmlTable.getClassInfo());
		log.debug("result=" + JSON.encode(ret, true));
		return ret;
	}

	/**
	 * テーブルのヘッダを作成します。
	 * @return テーブルのヘッダ。
	 */
	private String getHeaderHtml() {
		StringBuilder sb = new StringBuilder();
		for (Field<?> f: this.htmlTable.getFieldList()) {
			sb.append("<th>");
			sb.append(f.getId());
			sb.append("</th>");
		}
		return sb.toString();
	}

	/**
	 * テーブルのデータ行を作成します。
	 * @return テーブルのデータ行。
	 */
	private String getDataHtml() {
		StringBuilder sb = new StringBuilder();
		for (Field<?> f: this.htmlTable.getFieldList()) {
			sb.append("<td>");
			sb.append("<span id=\"queryResult[0].");
			sb.append(f.getId());
			sb.append("\"></span>");
			sb.append("</td>");
		}
		return sb.toString();
	}

	@WebMethod
	@Override
	public JsonResponse changePage(final Map<String, Object> param) throws Exception {
		JsonResponse ret = super.changePage(param);
		@SuppressWarnings("unchecked")
		Map<String, Object> r = (Map<String, Object>) ret.getResult();
		r.put("headerHtml", this.getHeaderHtml());
		r.put("dataHtml", this.getDataHtml());
		r.put("htmlTable", this.htmlTable.getClassInfo());
		return ret;
	}
	
	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
		// TODO:レコードの削除を実装してください。
	}
}
