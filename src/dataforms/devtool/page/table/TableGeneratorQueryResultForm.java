package dataforms.devtool.page.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.controller.Page;
import dataforms.controller.QueryResultForm;
import dataforms.devtool.dao.db.TableManagerDao;
import dataforms.devtool.field.common.ClassNameField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.RecordCountField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.devtool.field.common.TableNameField;
import dataforms.field.base.FieldList;
import dataforms.field.common.PresenceField;
import dataforms.field.common.RowNoField;
import dataforms.htmltable.HtmlTable;
import dataforms.util.ClassNameUtil;

/**
 * 問い合わせ結果フォームクラス。
 */
public class TableGeneratorQueryResultForm extends QueryResultForm {
	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(TableGeneratorQueryResultForm.class.getName());

	/**
	 * コンストラクタ。
	 */
	public TableGeneratorQueryResultForm() {
		HtmlTable htmltbl = new HtmlTable(Page.ID_QUERY_RESULT
			, (new RowNoField()).setSpanField(true)
			, (new PackageNameField()).setHidden(true)
			, (new TableClassNameField()).setHidden(true)
			, (new ClassNameField("fullClassName")).setSpanField(true)
			, (new TableNameField())
			, (new PresenceField("status")).setSpanField(true).setComment("テーブル有無")
			, new PresenceField("statusVal")
			, (new PresenceField("sequenceGeneration")).setSpanField(true).setComment("シーケンス有無")
			, (new PresenceField("difference")).setSpanField(true).setComment("構造の差分")
			, new PresenceField("differenceVal")
			, (new RecordCountField()).setSpanField(true)
		);
		this.addHtmlTable(htmltbl);
		this.addPkField(htmltbl.getFieldList().get("packageName"));
		this.addPkField(htmltbl.getFieldList().get("tableClassName"));
	}

	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList flist) throws Exception {
    	this.methodStartLog(log, data);
    	TableManagerDao dao = new TableManagerDao(this);
    	List<Map<String, Object>> queryResult = dao.queryTableClass(data);
    	List<String> clslist = new ArrayList<String>();
    	for (Map<String, Object> r: queryResult) {
    		String className = (String) r.get("className");
/*    		int idx = className.lastIndexOf(".");
    		r.put("packageName", className.substring(0, idx));
    		r.put("tableClassName", className.substring(idx + 1));*/
    		r.put("packageName", ClassNameUtil.getPackageName(className));
    		r.put("tableClassName", ClassNameUtil.getSimpleClassName(className));
    		r.put("fullClassName", className);
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("checkedClass", clslist);
    	result.put("queryResult", queryResult);
    	this.methodFinishLog(log, result);
		return result;
	}

}
