package dataforms.app.page.enumeration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dataforms.app.dao.enumeration.EnumManagementDao;
import dataforms.app.dao.enumeration.EnumOptionNameTable;
import dataforms.app.dao.enumeration.EnumOptionTable;
import dataforms.app.dao.enumeration.EnumTypeNameTable;
import dataforms.controller.ApplicationException;
import dataforms.controller.EditForm;
import dataforms.controller.QueryForm;
import dataforms.dao.Table;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;
import net.arnx.jsonic.JSON;

/**
 * 列挙型管理編集フォームクラス。
 */
public class EnumManagementEditForm extends EditForm {
	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(EnumManagementEditForm.class);
	/**
	 * コンストラクタ。
	 */
	public EnumManagementEditForm() {
		EnumOptionTable table = new EnumOptionTable();
//		this.addTableFields(table);
		this.addField(table.getEnumTypeCodeField());
		Table typeNameTable = new EnumTypeNameTable();
		EditableHtmlTable typeNameList = new EditableHtmlTable("typeNameList", typeNameTable.getFieldList());
		typeNameList.getFieldList().get(EnumTypeNameTable.Entity.ID_ENUM_TYPE_CODE).removeRequiredValidator();
		typeNameList.getFieldList().get(EnumTypeNameTable.Entity.ID_LANG_CODE).addValidator(new RequiredValidator());
		this.addHtmlTable(typeNameList);
		Table optionNameTable = new EnumOptionNameTable();
		EditableHtmlTable optionNameList = new EditableHtmlTable("optionNameList", optionNameTable.getFieldList());
		optionNameList.getFieldList().get(EnumOptionNameTable.Entity.ID_ENUM_TYPE_CODE).removeRequiredValidator();
		optionNameList.getFieldList().get(EnumOptionNameTable.Entity.ID_LANG_CODE).addValidator(new RequiredValidator());
		optionNameList.getFieldList().get(EnumOptionNameTable.Entity.ID_ENUM_OPTION_NAME).addValidator(new RequiredValidator());
		this.addHtmlTable(optionNameList);
	}

	@Override
	public void init() throws Exception {
		super.init();
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		EnumManagementDao dao = new EnumManagementDao(this);
		return dao.query(data);
	}

	@Override
	protected Map<String, Object> queryDataByQueryFormCondition(final Map<String, Object> data) throws Exception {
		EnumManagementDao dao = new EnumManagementDao(this);
		QueryForm qf = (QueryForm) this.getPage().getComponent("queryForm");
		List<Map<String, Object>> list = dao.query(data, qf.getFieldList());
		if (list.size() == 0) {
			throw new ApplicationException(this.getPage(), "error.notfounddata");
		}
		if (list.size() > 1) {
			throw new ApplicationException(this.getPage(), "error.cannotidentifydata");
		}
		return list.get(0);
	}

	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret = super.validate(param);
		if (ret.size() == 0) {
			Map<String, Object> data = this.convertToServerData(param);
			ret.addAll(this.validateTypeNameLangCode(data));
			ret.addAll(this.validateOptionNameLangCode(data));
		}
		return ret;
	}

	/**
	 * 列挙型名称の言語コードをチェックします。
	 * @param data データ。
	 * @return チェック結果。
	 * @throws Exception 例外。
	 */
	private List<ValidationError> validateTypeNameLangCode(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> typeNameList = (List<Map<String, Object>>) data.get("typeNameList");
		HashSet<String> set = new HashSet<String>();
		boolean dflg = false;
		for (int i = 0; i < typeNameList.size(); i++) {
			Map<String, Object> m = typeNameList.get(i);
			EnumTypeNameTable.Entity e = new EnumTypeNameTable.Entity(m);
			String lang = e.getLangCode(); //(String) m.get("langCode");
			if (set.contains(lang)) {
				ValidationError err = new ValidationError("typeNameList[" + i + "].langCode", this.getPage().getMessage("error.duplicate"));
				ret.add(err);
			}
			if ("default".equals(lang)) {
				dflg = true;
			}
			set.add(lang);
		}
		if (!dflg) {
			ValidationError err = new ValidationError("typeNameList", this.getPage().getMessage("error.nodefaultlangcode"));
			ret.add(err);
		}
		return ret;
	}

	/**
	 * 列挙型選択肢名称の言語コードをチェックします。
	 * @param data データ。
	 * @return チェック結果。
	 * @throws Exception 例外。
	 */
	private List<ValidationError> validateOptionNameLangCode(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> optionNameList = (List<Map<String, Object>>) data.get("optionNameList");
		HashSet<String> set = new HashSet<String>();
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < optionNameList.size(); i++) {
			Map<String, Object> m = optionNameList.get(i);
			EnumOptionNameTable.Entity e = new EnumOptionNameTable.Entity(m);
			String enumOptionCode = e.getEnumOptionCode();  //(String) m.get("enumOptionCode");
			String langCode = e.getLangCode();  //(String) m.get("langCode");
			String key = enumOptionCode + "_" + langCode;
			if (set.contains(key)) {
				ValidationError err = new ValidationError("optionNameList[" + i + "].langCode", this.getPage().getMessage("error.duplicate"));
				ret.add(err);
			}
			set.add(key);
			if (map.containsKey(enumOptionCode)) {
				map.put(enumOptionCode, map.get(enumOptionCode) + "," + langCode);
			} else {
				map.put(enumOptionCode, langCode);
			}
		}
		log.debug("optionNameList=" + JSON.encode(optionNameList, true));
		log.debug("langMap=" + JSON.encode(map, true));
		for (int i = 0; i < optionNameList.size(); i++) {
			Map<String, Object> m = optionNameList.get(i);
			String enumOptionCode = (String) m.get("enumOptionCode");
			String langList = map.get(enumOptionCode);
			if (langList.indexOf("default") < 0) {
				ValidationError err = new ValidationError("optionNameList[" + i + "].langCode", this.getPage().getMessage("error.nodefaultlangcode"));
				ret.add(err);
			}
		}
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
/*		Table table = new EnumOptionTable();
		boolean ret = this.isUpdate(table, data);
		return ret;*/
		// 常にupdateを使用する。
		return true;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
/*		EnumManagementDao dao = new EnumManagementDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.insert(data);*/
		// 何もしない。
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		String enumTypeCode = (String) data.get("enumTypeCode");
		List<Map<String, Object>> typeNameList = (List<Map<String, Object>>) data.get("typeNameList");
		for (Map<String, Object> m: typeNameList) {
			EnumTypeNameTable.Entity e = new EnumTypeNameTable.Entity(m);
//			m.put("enumTypeCode", enumTypeCode);
			e.setEnumTypeCode(enumTypeCode);
		}
		List<Map<String, Object>> optionNameList = (List<Map<String, Object>>) data.get("optionNameList");
		for (Map<String, Object> m: optionNameList) {
			EnumOptionNameTable.Entity e = new EnumOptionNameTable.Entity(m);
			//m.put("enumTypeCode", enumTypeCode);
			e.setEnumTypeCode(enumTypeCode);
		}
		this.setUserInfo(data); // 更新を行うユーザID等を設定する.
		EnumManagementDao dao = new EnumManagementDao(this);
		dao.update(data);
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		EnumManagementDao dao = new EnumManagementDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.delete(data);
	}
}
