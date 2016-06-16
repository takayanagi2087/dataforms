package dataforms.devtool.field.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.app.dao.func.FuncInfoDao;
import dataforms.field.base.Field;
import dataforms.field.common.SingleSelectField;

/**
 * 機能選択フィールドクラス。
 *
 */
public class FunctionSelectField extends SingleSelectField<Long> {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "機能";
	/**
	 * パッケージオプション。
	 */
	private String packageOption = "";

	/**
	 * パッケージ名を展開するフィールドID。
	 */
	private String packageFieldId = "packageName";

	/**
	 * コンストラクタ。
	 */
	public FunctionSelectField() {
		super(null);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FunctionSelectField(final String id) {
		super(id);
		this.setComment(COMMENT);
	}


	@Override
	public void init() throws Exception {
		super.init();
		FuncInfoDao dao = new FuncInfoDao(this);
		List<Map<String, Object>> list = dao.queryFuncList(true);
		List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			Map<String, Object> opt = new HashMap<String, Object>();
			opt.put("value", m.get("funcPath"));
			opt.put("name", m.get("funcPath"));
			options.add(opt);
		}
		this.setOptionList(options, true);
	}

	@Override
	protected void onBind() {
		super.onBind();
//		this.addValidator(new RequiredValidator());
	}

	/**
	 * パッケージオプションを取得します。
	 * @return パッケージオプション。
	 */
	public String getPackageOption() {
		return packageOption;
	}

	/**
	 * パッケージオプションを設定します。
	 * @param packageOption パッケージオプション。
	 * @return 設定したフィールド。
	 */
	public Field<?> setPackageOption(final String packageOption) {
		this.packageOption = packageOption;
		return this;
	}


	/**
	 * パッケージ名を展開するフィールドIDを取得します。
	 * @return パッケージ名を展開するフィールドID。
	 */
	public String getPackageFieldId() {
		return packageFieldId;
	}

	/**
	 * パッケージ名を展開するフィールドを設定します。
	 * @param packageFieldId パッケージ名を展開するフィールド。
	 * @return 設定したフィールド。
	 */
	public FunctionSelectField setPackageFieldId(final String packageFieldId) {
		this.packageFieldId = packageFieldId;
		return this;
	}

	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> ret = super.getClassInfo();
		ret.put("packageFieldId", this.getPackageFieldId());
		ret.put("packageOption", this.getPackageOption());
		return ret;
	}
}
