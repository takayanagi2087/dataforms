package dataforms.debug.dao.alltype;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataforms.dao.Table;
import dataforms.field.common.BlobStoreFileField;
import dataforms.field.common.CharSingleSelectField;
import dataforms.field.common.DeleteFlagField;
import dataforms.field.common.FolderStoreFileField;
import dataforms.field.common.MultiSelectField;
import dataforms.field.common.RecordIdField;
import dataforms.field.common.ZipCodeField;
import dataforms.field.sqltype.BigintField;
import dataforms.field.sqltype.CharField;
import dataforms.field.sqltype.ClobField;
import dataforms.field.sqltype.DateField;
import dataforms.field.sqltype.DoubleField;
import dataforms.field.sqltype.IntegerField;
import dataforms.field.sqltype.NumericField;
import dataforms.field.sqltype.SmallintField;
import dataforms.field.sqltype.TimeField;
import dataforms.field.sqltype.TimestampField;
import dataforms.field.sqltype.VarcharField;
import dataforms.validator.MaxLengthValidator;
import dataforms.validator.RequiredValidator;

/**
 * 全項目タイプテーブルクラス。
 *
 */
public class AllTypeTable extends Table {

	/**
	 * 選択肢。
	 */
	private static final String[][] OPTIONS = {
		{"0", "sel0"},
		{"1", "sel1"},
		{"2", "sel2"},
	};

	/**
	 * 選択肢リストを取得します。
	 * @return 選択肢リスト。
	 */
	public static List<Map<String, Object>> getOptionList() {
		List<Map<String, Object>> optlist = new ArrayList<Map<String, Object>>();
		for (String [] opt : OPTIONS) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("value", opt[0]);
			m.put("name", opt[1]);
			optlist.add(m);
		}
		return optlist;
	}


	/**
	 * コンストラクタ。
	 */
	public AllTypeTable() {
		this.setAutoIncrementId(true);
		this.setComment("全項目タイプテスト用テーブル");
		this.addPkField(new RecordIdField("recordIdField")).setNotNull(true).setComment("レコードID");
		this.addField(new CharField("charField", 10)).setComment("固定長文字列フィールド").addValidator(new MaxLengthValidator(10));
		this.addField(new VarcharField("varcharField", 64)).setNotNull(false).setComment("可変長文字列フィールド");

		this.addField((new SmallintField("smallintField")).setCommaFormat(true)).setComment("単精度整数フィールド");
		this.addField((new IntegerField("integerField")).setCommaFormat(true)).setComment("整数フィールド");
		this.addField((new BigintField("bigintField")).setCommaFormat(true)).setComment("倍精度整数フィールド");
		this.addField((new DoubleField("doubleField")).setCommaFormat(true)).setComment("実数フィールド");
		NumericField nf = new NumericField("numericField", 10, 2);
		nf.setCommaFormat(true);
		this.addField(nf).setNotNull(false).setComment("数値フィールド").setDefaultValue(BigDecimal.valueOf(0));
		this.addField(new DateField("dateField")).setComment("日付フィールド").setDefaultValue(new Date());
		this.addField(new TimeField("timeField")).setComment("時刻フィールド");
		this.addField(new TimestampField("timestampField")).setComment("日付時刻フィールド");
		ZipCodeField zipCode = new ZipCodeField();
		zipCode.setAddressFieldId("address");
		zipCode.setAddressFieldId2("address2");
//		zipCode.setAddressFieldId3("address3");
		this.addField(zipCode);
		this.addField(new VarcharField("address", 256)).setComment("住所");

		CharSingleSelectField dropdown = new CharSingleSelectField("dropdownField", 1);
		dropdown.setOptionList(AllTypeTable.getOptionList());
		dropdown.setHtmlFieldType(CharSingleSelectField.HtmlFieldType.SELECT);
		this.addField(dropdown).setComment("ドロップダウンフィールド");
		CharSingleSelectField radio = new CharSingleSelectField("radioField", 1);
		radio.setHtmlFieldType(CharSingleSelectField.HtmlFieldType.RADIO);
		radio.setComment("ラジオボタンフィールド");
		radio.setOptionList(AllTypeTable.getOptionList());
		this.addField(radio);

		MultiSelectField<String> multiSelectList = new MultiSelectField<String>("multiSelectListField", 2);
		multiSelectList.setOptionList(AllTypeTable.getOptionList());
		multiSelectList.setComment("マルチ選択リストフィールド");
		multiSelectList.setHtmlFieldType(MultiSelectField.HtmlFieldType.SELECT);
		this.addField(multiSelectList);
		MultiSelectField<String> checkbox = new MultiSelectField<String>("checkboxField", 2);
		checkbox.setOptionList(AllTypeTable.getOptionList());
		checkbox.setComment("チェックボックスフィールド");
		checkbox.setHtmlFieldType(MultiSelectField.HtmlFieldType.CHECKBOX);
		this.addField(checkbox);

		CharSingleSelectField text = new CharSingleSelectField("selectTextField", 2);
		text.setOptionList(AllTypeTable.getOptionList());
		text.setComment("選択テキストフィールド");
		this.addField(text);

		MultiSelectField<String> textList = new MultiSelectField<String>("selectTextListField", 2);
		textList.setOptionList(AllTypeTable.getOptionList());
		textList.setComment("選択テキストリストフィールド");
		this.addField(textList);
		//
		this.addField(new BlobStoreFileField("uploadBlobData")).setComment("添付ファイル").addValidator(new RequiredValidator());
		this.addField(new FolderStoreFileField("uploadFileData")).setComment("添付ファイル");

		this.addField(new ClobField("clobField")).setComment("CLOB");
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * AllTypeAttachFileTableとのリンク条件を作成します。
	 * </pre>
	 */
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		if (joinTable instanceof AllTypeAttachFileTable) {
			return this.getLinkFieldCondition("recordIdField", joinTable, alias, "recordIdField");
		}
		return super.getJoinCondition(joinTable, alias);
	}
}
