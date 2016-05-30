package dataforms.field.common;

import dataforms.app.dao.enumeration.EnumDao;
import dataforms.dao.sqldatatype.SqlVarchar;

/**
 * 列挙型オプション複数選択フィールドクラス。
 * <pre>
 * EnumTypeNameTable,EnumOptionTable,EnumOptionNameTableから取得した
 * 選択肢を複数選択するためのフィールドです。
 * 対応するHTMLのタグは複数選択の&lt;select&gt;や&lt;input type=&quot;checkbox&quot; ...&gt;になります。
 * </pre>
 */
public class EnumOptionMultiSelectField extends MultiSelectField<String> implements SqlVarchar {
	/**
	 * 列挙型コード。
	 */
	private  String enumTypeCode = null;

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * <pre>
	 * 対応するDBのフィールドはvarchar(1024)になります。
	 * </pre>
	 * @param enumTypeCode 列挙型コード。
	 */
	public EnumOptionMultiSelectField(final String id, final String enumTypeCode) {
		super(id, 1024);
		this.enumTypeCode = enumTypeCode;
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param length フィールド長。
	 * <pre>
	 * 配列はjson形式の文字列に変換してvarchar形式のフィールドに記録するので、
	 * 十分な長さを指定してください。
	 * </pre>
	 * @param enumTypeCode 列挙型コード。
	 */
	public EnumOptionMultiSelectField(final String id, final String enumTypeCode, final int length) {
		super(id, length);
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
	public EnumOptionMultiSelectField setEnumTypeCode(final String enumTypeCode) {
		this.enumTypeCode = enumTypeCode;
		return this;
	}


}
