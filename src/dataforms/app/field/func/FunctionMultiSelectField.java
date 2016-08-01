package dataforms.app.field.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.app.dao.func.FuncInfoDao;
import dataforms.field.common.MultiSelectField;
import dataforms.util.MessagesUtil;
import dataforms.util.SequentialProperties;

/**
 * 機能複数選択フィールド。
 *
 */
public class FunctionMultiSelectField extends MultiSelectField<String> {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "機能複数選択";


	/**
	 * コンストラクタ。
	 */
	public FunctionMultiSelectField() {
		super(null);
		this.setComment(COMMENT);
		this.setHtmlFieldType(HtmlFieldType.CHECKBOX);
	}

	/**
	 * コンストラクタフィールドID。
	 * @param id フィールドID。
	 */
	public FunctionMultiSelectField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setHtmlFieldType(HtmlFieldType.CHECKBOX);
	}
	
	
	@Override
	public void init() throws Exception {
		super.init();
		FuncInfoDao dao = new FuncInfoDao(this);
		List<Map<String, Object>> list = dao.queryFuncList(true);
		List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			Map<String, Object> opt = new HashMap<String, Object>();
			String funcPath = (String) m.get("funcPath");
			opt.put("value", funcPath);
			String propFile = funcPath + "/Function";
			SequentialProperties prop = MessagesUtil.getProperties(this.getPage(), propFile);
			if (prop.getKeyList().size() > 0) {
				String key = prop.getKeyList().get(0);
				String name = (String) prop.get(key);
				opt.put("name", name);
			} else {
				opt.put("name", m.get("funcPath"));
			}
			options.add(opt);
		}
		this.setOptionList(options, false);
	}
}
