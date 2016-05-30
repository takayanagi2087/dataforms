package dataforms.field.common;

import dataforms.field.sqltype.CharField;
import dataforms.util.StringUtil;


/**
 * フラグフィールドクラス。
 * <pre>
 * 0 or 1のフラグ情報を記録するフィールドです。
 * 対応するHTMLのタグは&lt;input type=&quot;checkbox&quot; ...&gt;になります。
 * </pre>
 */
public class FlagField extends CharField {
	/**
	 * コンストラクタ。
	 */
	public FlagField() {
		super(null, 1);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FlagField(final String id) {
		super(id, 1);
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * &lt;input type=&quot;checkbox&quot; ... &gt;はチェックされていない場合、nullが入力されるのでその場合"0"を設定します。
	 * </pre>
	 */
	@Override
	public void setClientValue(final Object v) {
		Object val = v;
		if (StringUtil.isBlank(v)) {
			val = "0";
		}
		super.setValue((String) val);
	}

	@Override
	public Object getDBValue() {
		Object ret = this.getValue();
		if (StringUtil.isBlank(ret)) {
			ret = "0";
		}
		return ret;
	}
}
