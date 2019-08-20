package dataforms.field.common;

import dataforms.dao.sqldatatype.SqlInteger;
import dataforms.util.NumberUtil;
import dataforms.util.StringUtil;

/**
 * INTEGER型の単一選択フィールドクラス。
 * <pre>
 * optionList中の選択肢を単一選択の&lt;select&gt;や&lt;input type=&quot;radio&quot; ...&gt;に
 * 表示し、選択するためのフィールドです。
 * </pre>
 *
 */
public class IntegerSingleSelectField extends SingleSelectField<Integer> implements SqlInteger {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public IntegerSingleSelectField(final String id) {
		super(id);
	}

	@Override
	public void setClientValue(final Object v) {
		if (!StringUtil.isBlank(v)) {
			this.setValue(Integer.parseInt(((String) v).replaceAll(",", "")));
		} else {
			this.setValue(null);
		}
	}

	@Override
	public void setDBValue(final Object value) {
		this.setValue(NumberUtil.integerValueObject(value));
	}

}
