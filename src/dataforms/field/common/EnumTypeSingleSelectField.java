package dataforms.field.common;

import dataforms.app.dao.enumeration.EnumDao;
import dataforms.app.field.enumeration.EnumTypeCodeField;
import dataforms.dao.sqldatatype.SqlVarchar;

/**
 * 列挙型単一選択フィールドクラス。
 * <pre>
 * EnumGroupTableに登録された列挙型グループ中の列挙型を選択するためのフィールドです。
 * 対応するHTMLのタグは単一選択の&lt;select&gt;や&lt;input type=&quot;radio&quot; ...&gt;になります。
 * </pre>
 *
 */
public class EnumTypeSingleSelectField extends SingleSelectField<String> implements SqlVarchar {
	/**
	 * 列挙型グループコード。
	 */
	private  String enumGroupCode = null;
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param enumGroupCode 列挙型グループコード。
	 */
	public EnumTypeSingleSelectField(final String id, final String enumGroupCode) {
		super(id, EnumTypeCodeField.LENGTH);
		this.enumGroupCode = enumGroupCode;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 設定されたenumGroupCodeに対応した選択肢を EnumGroupTable,EnumTypeNameTableから取得します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		EnumDao dao = new EnumDao(this);
//		String lang = this.getPage().getRequest().getLocale().getLanguage();
		String lang = this.getPage().getCurrentLanguage();
		this.setOptionList(dao.getTypeList(this.enumGroupCode, lang));
	}

	/**
	 * 列挙型グループコードを取得します。
	 * @return 列挙型グループコード。
	 */
	public String getEnumGroupCode() {
		return enumGroupCode;
	}

	/**
	 * 列挙型グループコードを設定します。
	 * @param enumGroupCode 列挙型グループコード。
	 * @return 設定したフィールド。
	 */
	public EnumTypeSingleSelectField setEnumGroupCode(final String enumGroupCode) {
		this.enumGroupCode = enumGroupCode;
		return this;
	}


}
