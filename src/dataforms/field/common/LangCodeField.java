package dataforms.field.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.dao.sqldatatype.SqlVarchar;
import dataforms.servlet.DataFormsServlet;



/**
 * 言語コードフィールドクラス。
 *
 */
public class LangCodeField extends SingleSelectField<String> implements SqlVarchar {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "言語コード";

	/**
	 * コンストラクタ。
	 */
	public LangCodeField() {
		super(null, 8);
		this.setComment(COMMENT);
		this.setHtmlFieldType(HtmlFieldType.SELECT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public LangCodeField(final String id) {
		super(id, 8);
		this.setComment(COMMENT);
		this.setHtmlFieldType(HtmlFieldType.SELECT);
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * web.xmlから、言語コードの選択肢を取得します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
		String[] langlist = DataFormsServlet.getSupportLanguage().split(",");
		Map<String, Object> dm = new HashMap<String, Object>();
		dm.put("name", "default");
		dm.put("value", "default");
		options.add(dm);
		for (String lang: langlist) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("name", lang.trim());
			m.put("value", lang.trim());
			options.add(m);
		}
		this.setOptionList(options);
	}

}
