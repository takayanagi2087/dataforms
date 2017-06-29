package dataforms.app.field.enumeration;

import java.util.List;
import java.util.Map;

import dataforms.app.dao.enumeration.EnumDao;
import dataforms.app.dao.enumeration.EnumTypeNameTable;
import dataforms.field.sqltype.VarcharField;

/**
 * 列挙型コードクラス。
 *
 */
public class EnumTypeCodeField extends VarcharField {

	/**
	 * フィールド長。
	 */
	public static final int LENGTH = 16;

	/**
	 * コメント。
	 */
	private static final String COMMENT = "列挙型コード.";

	/**
	 * コンストラクタ。
	 */
	public EnumTypeCodeField() {
		super(null, LENGTH);
		this.setComment(COMMENT);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドのID。
	 */
	public EnumTypeCodeField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}
	
	@Override
	protected void onBind() {
		super.onBind();
	}
	
	@Override
	protected List<Map<String, Object>> queryAutocompleteSourceList(final Map<String, Object> data) throws Exception {
		String id = (String) data.get("currentFieldId");
		String val = (String) data.get(id);
		EnumDao dao = new EnumDao(this);
		String langCode = this.getPage().getRequest().getLocale().getLanguage();
		List<Map<String, Object>> list = dao.queryEnumTypeAutocomplateList(val, langCode);
		for (Map<String, Object> m: list) {
			EnumTypeNameTable.Entity e = new EnumTypeNameTable.Entity(m);
			m.put("listLabel", e.getEnumTypeCode() + " " + e.getEnumTypeName());
		}
		return this.convertToAutocompleteList(this.getHtmlTableRowId(id), list, "enumTypeCode", "listLabel", "enumTypeName");
	}
}
