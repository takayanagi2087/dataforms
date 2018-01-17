package dataforms.app.page.enumeration;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import dataforms.app.dao.enumeration.EnumGroupDao;
import dataforms.app.dao.enumeration.EnumGroupTable;
import dataforms.app.dao.enumeration.EnumTypeNameTable;
import dataforms.app.field.enumeration.EnumGroupCodeField;
import dataforms.app.field.enumeration.EnumTypeNameField;
import dataforms.controller.EditForm;
import dataforms.field.base.FieldList;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.util.MessagesUtil;
import dataforms.validator.RequiredValidator;
import dataforms.validator.ValidationError;

/**
 * 編集フォームクラス。
 */
public class EnumGroupEditForm extends EditForm {
	/**
	 * コンストラクタ。
	 */
	public EnumGroupEditForm() {
		EnumGroupTable table = new EnumGroupTable();
		this.addField(new EnumGroupCodeField()).addValidator(new RequiredValidator());
		FieldList flist = new FieldList();
		flist.addAll(table.getFieldList());
		flist.addField(new EnumTypeNameField()).setReadonly(true);
		flist.get(EnumGroupTable.Entity.ID_ENUM_TYPE_CODE).addValidator(new RequiredValidator()).setAutocomplete(true).setRelationDataAcquisition(true);
		EditableHtmlTable htmltable = new EditableHtmlTable("codeList", flist);
		this.addHtmlTable(htmltable);
		this.setPkFieldIdList(table.getPkFieldList());
	}

	/**
	 * フォームの初期化を行います。
	 * <pre>
	 * DBを使用した初期化処理はここに記述します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
	}

	/**
	 * 編集対象のデータを取得します。
	 * <pre>
	 * 問い合わせ結果フォームに表示されたデータを選択した際に呼び出されます。
	 * dataには最低編集対象レコードのPKのマップが入ってきます。
	 * </pre>
	 * @param data 取得するデータのPKの値が入ってきます。
	 * @return 編集対象データ。
	 */
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		EnumGroupDao dao = new EnumGroupDao(this);
		EnumTypeNameTable.Entity e = new EnumTypeNameTable.Entity(data);
		e.setLangCode(this.getPage().getCurrentLanguage());
		Map<String, Object> ret = dao.query(data);
		return ret;
	}

	/**
	 * 参照登録対象対象のデータを取得します。
	 * <pre>
	 * queryDataから取得したデータから、PK項目を削除します。
	 * </pre>
	 * @param data 取得するデータのPKの値が入ってきます。
	 * @return 編集対象データ。
	 */
	@Override
	protected Map<String, Object> queryReferData(final Map<String, Object> data) throws Exception {
		// 使用しない。
		return null;
	}


	/**
	 * 編集対象のデータを取得します。
	 * <pre>
	 * 問い合わせフォームと編集フォームのみが配置されたページ(問い合わせ結果フォームが存在しないページ)の場合、
	 * 問い合わせフォームの入力データが渡され、それを元に編集対象のデータを取得します。
	 * </pre>
	 * @param data 問い合わせフォームの入力データ。
	 * @return 編集対象データ。
	 */
	@Override
	protected Map<String, Object> queryDataByQueryFormCondition(final Map<String, Object> data) throws Exception {
		// 今回は呼ばれない。
		return null;
	}

	/**
	 * ポストされたデータが更新するのか新規追加するのかを判定します。
	 * <pre>
	 * 編集対象データにPKの入力があった場合、更新すべきと判断します。
	 * </pre>
	 * @param data 入力データ。
	 * @return 更新対象データの場合true。
	 */
	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
/*		Table table = new EnumGroupTable();
		boolean ret = this.isUpdate(table, data);
		return ret;*/
		// 常に更新。
		return true;
	}

	/**
	 * データを新規追加します。
	 * @param data ポストされたデータ。
	 */
	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
/*		EnumGroupDao dao = new EnumGroupDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.insert(data);*/
		// 呼ばれることはない
	}
	
	@Override
	protected List<ValidationError> validateForm(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = super.validateForm(data);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("codeList");
		if (list != null && list.size() > 0) {
			// 同じ列挙型グルーブコードが指定されている。
			String saveMode = (String) data.get("saveMode");
			if ("new".equals(saveMode))	{
				EnumGroupTable table = new EnumGroupTable();
				FieldList flist = new FieldList(table.getEnumGroupCodeField());
				EnumGroupDao dao = new EnumGroupDao(this);
				List<Map<String, Object>> result = dao.query(data, flist);
				if (result.size() > 0) {
					ret.add(new ValidationError(EnumGroupTable.Entity.ID_ENUM_GROUP_CODE, MessagesUtil.getMessage(this.getPage(), "error.duplicate")));
				}
			}
			// 同じ列挙型コードが指定されていなかどうかをチェック。
			HashSet<String> keyset = new HashSet<String>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				EnumGroupTable.Entity e = new EnumGroupTable.Entity(m);
				String code = e.getEnumTypeCode();
				if (keyset.contains(code)) {
					String fid = "codeList[" + i + "]." + EnumGroupTable.Entity.ID_ENUM_TYPE_CODE;
					ret.add(new ValidationError(fid, MessagesUtil.getMessage(this.getPage(), "error.duplicateenumcode")));
				}
				keyset.add(code);
			}
		} else {
			ret.add(new ValidationError("codeList", MessagesUtil.getMessage(this.getPage(), "error.noenumcode")));
		}
		return ret;
	}

	/**
	 * データを更新します。
	 * @param data ポストされたデータ。
	 */
	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		EnumGroupTable.Entity e = new EnumGroupTable.Entity(data);
		String gc = e.getEnumGroupCode();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("codeList");
		for (Map<String, Object> m: list) {
			EnumGroupTable.Entity ee = new EnumGroupTable.Entity(m);
			ee.setEnumGroupCode(gc);
			this.setUserInfo(m); // 更新を行うユーザIDを設定する.
		}
		EnumGroupDao dao = new EnumGroupDao(this);
		dao.update(list);
	}

	/**
	 * データを削除します。
	 * @param data ポストされたデータ。
	 */
	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		EnumGroupDao dao = new EnumGroupDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.delete(data);
	}
}
