package dataforms.field.common;

import java.util.List;
import java.util.Map;

import dataforms.dao.sqldatatype.SqlBigint;
import dataforms.util.NumberUtil;
import dataforms.util.StringUtil;


/**
 * マスタレコードを選択するフィールドクラスです。
 * <pre>
 * サブクラスでqueryOptionListを実装し、マスタから選択肢を取得するようにしてください。
 * </pre>
 */
public abstract class MasterSingleSelectField extends SingleSelectField<Long> implements SqlBigint{

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MasterSingleSelectField(final String id) {
		super(id);
	}


	@Override
	public void init() throws Exception {
		super.init();
		this.setOptionList(this.queryOptionList());
	}

	@Override
	public void setClientValue(final Object v) {
		if (!StringUtil.isBlank(v)) {
			this.setValue(Long.parseLong(((String) v).replaceAll(",", "")));
		} else {
			this.setValue(null);
		}
	}

	@Override
	public void setDBValue(final Object value) {
		this.setValue(NumberUtil.longValueObject(value));
	}


	/**
	 * 選択肢の一覧を取得します。
	 * @return 選択肢リスト。
	 * @throws Exception 例外。
	 */
	protected abstract List<Map<String, Object>> queryOptionList() throws Exception;
}
