package dataforms.devtool.page.query;

import java.util.HashMap;
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
import dataforms.util.StringUtil;
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

	/**
	 * Order byに指定するカラムリストを取得します。
	 * @param sortOrder ソート順指定。
	 * @return order by指定のカラムリスト。
	 */
	private String getOrderByString(final String sortOrder) {
		StringBuilder sb = new StringBuilder();
		if (!StringUtil.isBlank(sortOrder)) {
			String[] sp = sortOrder.split("\\,");
			for (String f: sp) {
				log.debug("f=" + f);
				String[] fsp = f.split("\\:");
				log.debug("fsp[0]=" + fsp[0]);
				String id = fsp[0];
				String order = fsp[1];
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(StringUtil.camelToSnake(id));
				sb.append(" ");
				sb.append(order);
			}
		}
		return sb.toString();
	}
	
	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		String sql = (String) data.get("sql");
		String sortOrder = (String) data.get("sortOrder");
		String orderby = this.getOrderByString(sortOrder);
		log.debug("orderby=" + orderby);
		if (sortOrder.length() > 0) {
			sql = "select * from (" + sql + ") m order by " + orderby;
		}
		Dao dao = new Dao(this);
		Map<String, Object> ret = dao.executePageQuery(sql, data);
		FieldList flist = dao.getResultSetFieldList();
		this.htmlTable.getFieldList().clear();
		this.htmlTable.getFieldList().addAll(flist);
		for (Field<?> f: this.htmlTable.getFieldList()) {
			if (!"rowNo".equals(f.getId())) {
				f.setSortable(true);
			}
		}
		this.htmlTable.getFieldList().getOrderByFieldList(sortOrder);
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
		try {
			JsonResponse ret = super.changePage(param);
			@SuppressWarnings("unchecked")
			Map<String, Object> r = (Map<String, Object>) ret.getResult();
			r.put("headerHtml", this.getHeaderHtml());
			r.put("dataHtml", this.getDataHtml());
			r.put("htmlTable", this.htmlTable.getClassInfo());
			return ret;
		} catch (Exception e) {
			Map<String, Object> einfo = new HashMap<String, Object>();
			einfo.put("message", e.getMessage());
			JsonResponse ret = new JsonResponse(JsonResponse.APPLICATION_EXCEPTION, einfo);
			return ret;
		}
	}
	
	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
	}
}
