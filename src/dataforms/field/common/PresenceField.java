package dataforms.field.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.dao.sqldatatype.SqlChar;
import dataforms.util.MessagesUtil;


/**
 * 有無フィールドクラス。
 */
public class PresenceField extends SingleSelectField<String> implements SqlChar {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public PresenceField(final String id) {
		super(id, 1);
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * ClientMessages.propertiesから、有無の選択肢を取得します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
		{
			String v0 = MessagesUtil.getMessage(this.getPage(), "presence.0");
			Map<String, Object> m0 = new HashMap<String, Object>();
			m0.put("name", v0);
			m0.put("value", "0");
			options.add(m0);
		}
		{
			String v1 = MessagesUtil.getMessage(this.getPage(), "presence.1");
			Map<String, Object> m1 = new HashMap<String, Object>();
			m1.put("name", v1);
			m1.put("value", "1");
			options.add(m1);
		}
		this.setOptionList(options);
	}
}
