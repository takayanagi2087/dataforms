package dataforms.htmltable;

import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.annotation.WebMethod;
import dataforms.controller.DataForms;
import dataforms.controller.JsonResponse;
import dataforms.field.base.Field;
import dataforms.field.base.FieldList;

/**
 * ページスクロールするHTMLテーブル。
 *
 */
public class PageScrollHtmlTable extends HtmlTable {
    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(DataForms.class.getName());

    /**
     * Spanフラグをtrueにします。
     */
    private void setSpanFlagToField() {
    	for (Field<?> f: this.getFieldList()) {
    		f.setSpanField(true);
    	}
    }

	/**
	 * コンストラクタ。
	 * @param id テーブルID。
	 * @param fieldList フィールドリスト。
	 */
	public PageScrollHtmlTable(final String id, final FieldList fieldList) {
		super(id, fieldList);
	}

	/**
	 * コンストラクタ。
	 * @param id テーブルID。
	 * @param fieldList フィールドリスト。
	 */
	public PageScrollHtmlTable(final String id, final Field<?>... fieldList) {
		super(id, fieldList);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.setSpanFlagToField();
	}

	/**
	 * ページコントローラーを取得します。
	 * @param params パラメータ。
	 * @return ページコントローラーHTML。
	 * @throws Exception 例外。
	 */
    @WebMethod(useDB = false)
	public JsonResponse getPageController(final Map<String, Object> params) throws Exception {
		this.methodStartLog(log, params);
		String path = this.getPage().getAppropriatePath(this.getPage().getPageFramePath() + "/PageController.html", this.getPage().getRequest());
    	String htmltext = this.getWebResource(path);
    	if (htmltext != null) {
        	htmltext = this.getHtmlBody(htmltext);
    	}
		JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, htmltext);
		this.methodFinishLog(log, ret);
		return ret;
	}

}
