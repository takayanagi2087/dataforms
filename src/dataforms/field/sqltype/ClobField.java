package dataforms.field.sqltype;

import dataforms.dao.sqldatatype.SqlClob;
import dataforms.field.base.TextField;


/**
 * CLOBフィールドクラス。
 *
 */
public class ClobField extends TextField implements SqlClob {
	/**
	 * コンストラクタ。
	 * @param fieldId フィールドID。
	 */
	public ClobField(final String fieldId) {
		super(fieldId);
	}

	@Override
	public void setClientValue(final Object v) {
		this.setValue((String) v);
	}

}
