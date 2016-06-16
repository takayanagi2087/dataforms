package dataforms.devtool.page.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.QueryClassNameField;
import dataforms.field.base.FieldList;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.HtmlTable;


/**
 * 問い合わせ結果フォームクラス。
 */
public class QueryGeneratorQueryResultForm extends QueryResultForm {
	/**
	 * コンストラクタ。
	 */
	public QueryGeneratorQueryResultForm() {
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
				, (new RowNoField()).setSpanField(true)
				, (new PackageNameField()).setHidden(true)
				, (new QueryClassNameField()).setHidden(true)
				, (new ClassNameField("fullClassName")).setSpanField(true).setComment("問合せクラス名")
			);
			this.addHtmlTable(htmltbl);
			this.addPkField(htmltbl.getFieldList().get("packageName"));
			this.addPkField(htmltbl.getFieldList().get("queryClassName"));
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("queryResult", new ArrayList<Map<String, Object>>()); // とりあえず空のリストを返信。 
		return ret;
	}

	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
		// TODO:レコードの削除を実装してください。
	}
}
