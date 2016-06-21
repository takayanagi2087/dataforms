package dataforms.devtool.page.query;

import java.util.Map;

import dataforms.controller.EditForm;
import dataforms.devtool.field.common.FieldClassNameField;
import dataforms.devtool.field.common.FieldIdField;
import dataforms.devtool.field.common.FunctionSelectField;
import dataforms.devtool.field.common.PackageNameField;
import dataforms.devtool.field.common.QueryClassNameField;
import dataforms.devtool.field.common.TableClassNameField;
import dataforms.field.base.FieldList;
import dataforms.field.common.FlagField;
import dataforms.htmltable.EditableHtmlTable;
import dataforms.htmltable.HtmlTable;
import dataforms.validator.RequiredValidator;

/**
 * 問合せJavaクラス作成編集フォーム。
 *
 */
public class QueryGeneratorEditForm extends EditForm {

	/**
	 * Joinテーブルクラス。
	 *
	 */
	private static class JoinHtmlTable extends EditableHtmlTable {
		/**
		 * コンストラクタ。
		 * @param id デーブルID。
		 */
		public JoinHtmlTable(final String id) {
			super(id);
			FieldList flist = new FieldList(new FunctionSelectField(), new PackageNameField(), new TableClassNameField());
			flist.get("tableClassName").setAutocomplete(true);
			this.setFieldList(flist);
		}
	};

	/**
	 * 選択フィールドクラス。
	 *
	 */
	private static class SelectFieldHtmlTable extends HtmlTable {
		/**
		 * コンストラクタ。
		 * @param id デーブルID。
		 */
		public SelectFieldHtmlTable(final String id) {
			super("id");
			FieldList flist = new FieldList(new FlagField("selectFlag"), new FieldIdField(), new FieldClassNameField(), new TableClassNameField());
			this.setFieldList(flist);
		}
	}

	/**
	 * コンストラクタ。
	 */
	public QueryGeneratorEditForm() {
		this.addField(new FunctionSelectField());
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new QueryClassNameField()).setAutocomplete(true).addValidator(new RequiredValidator());
		this.addField((new FunctionSelectField("mainTableFunctionSelect")).setPackageFieldId("mainTablePackageName")).setComment("主テーブルの機能");
		this.addField(new PackageNameField("mainTablePackageName")).setComment("主テーブルのパッケージ").addValidator(new RequiredValidator());
		this.addField((new TableClassNameField("mainTableClassName")).setPackageNameFieldId("mainTablePackageName"))
			.setAutocomplete(true).setComment("主テーブルクラス名").addValidator(new RequiredValidator());
		
		EditableHtmlTable joinTableList = new JoinHtmlTable("joinTableList");
		joinTableList.setCaption("JOINするテーブルリスト");
		this.addHtmlTable(joinTableList);
		EditableHtmlTable leftJoinTableList = new JoinHtmlTable("leftJoinTableList");
		leftJoinTableList.setCaption("LEFT JOINするテーブルリスト");
		this.addHtmlTable(leftJoinTableList);
		EditableHtmlTable rightJoinTableList = new JoinHtmlTable("rightJoinTableList");
		rightJoinTableList.setCaption("RIGHT JOINするテーブルリスト");
		this.addHtmlTable(rightJoinTableList);
		SelectFieldHtmlTable slectFieldList = new SelectFieldHtmlTable("selectFieldList");
		slectFieldList.setCaption("選択フィールドリスト");
		this.addHtmlTable(slectFieldList);
		
	}
	
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

}
