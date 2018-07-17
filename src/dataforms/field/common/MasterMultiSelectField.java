package dataforms.field.common;

import java.util.List;
import java.util.Map;

import dataforms.dao.sqldatatype.SqlBigint;


/**
 * マスタレコードを複数選択するフィールドクラスです。
 * <pre>
 * サブクラスでqueryOptionListを実装し、マスタから選択肢を取得するようにしてください。
 * </pre>
 */
public abstract class MasterMultiSelectField extends MultiSelectField<Long> implements SqlBigint{

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MasterMultiSelectField(final String id) {
		super(id);
		this.setMatchType(MatchType.IN);
	}


	@Override
	public void init() throws Exception {
		super.init();
		this.setOptionList(this.queryOptionList());
	}

	/**
	 * 選択肢の一覧を取得します。
	 * @return 選択肢リスト。
	 * @throws Exception 例外。
	 */
	protected abstract List<Map<String, Object>> queryOptionList() throws Exception;
}
