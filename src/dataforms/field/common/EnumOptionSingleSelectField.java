package dataforms.field.common;

import dataforms.app.dao.enumeration.EnumDao;
import dataforms.app.field.enumeration.EnumOptionCodeField;
import dataforms.dao.sqldatatype.SqlVarchar;

/**
 * 列挙型オプション単一選択フィールドクラス。
 * <pre>
 * EnumTypeNameTable,EnumOptionTable,EnumOptionNameTableから取得した
 * 選択肢を単一選択するためのフィールドです。
 * 対応するHTMLのタグは単一選択の&lt;select&gt;や&lt;input type=&quot;radio&quot; ...&gt;になります。
 * </pre>
 */
public class EnumOptionSingleSelectField extends SingleSelectField<String> implements SqlVarchar {
	/**
	 * 列挙型コード。
	 */
	private  String enumTypeCode = null;
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param enumTypeCode 列挙型コード。
	 */
	public EnumOptionSingleSelectField(final String id, final String enumTypeCode) {
		super(id, EnumOptionCodeField.LENGTH);
		this.enumTypeCode = enumTypeCode;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 設定されたenumTypeCodeに対応した選択肢を EnumTypeNameTable,EnumOptionTable,EnumOptionNameTableから取得します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		if (this.enumTypeCode != null) {
			EnumDao dao = new EnumDao(this);
			String lang = this.getPage().getCurrentLanguage();
			this.setOptionList(dao.getOptionList(this.enumTypeCode, lang));
		}
	}

	/**
	 * 列挙型タイプコードを取得します。
	 * @return 列挙型タイプコード。
	 */
	public String getEnumTypeCode() {
		return enumTypeCode;
	}

	/**
	 * 列挙型タイプコードを設定します。
	 * @param enumTypeCode 列挙型タイプコード。
	 * @return 設定したフィールド。
	 */
	public EnumOptionSingleSelectField setEnumTypeCode(final String enumTypeCode) {
		this.enumTypeCode = enumTypeCode;
		return this;
	}


}
