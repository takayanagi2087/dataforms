package dataforms.debug.dao.alltype;

import java.util.ArrayList;
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
import dataforms.field.common.SortOrderField;
import dataforms.field.common.ZipCodeField;
import dataforms.field.sqltype.DateField;
import dataforms.field.sqltype.SmallintField;
import dataforms.field.sqltype.VarcharField;

/**
 *
 * 全項目タイプ添付ファイルテーブルクラス。
 *
 */
public class AllTypeAttachFileTable extends Table {

	/**
	 * 選択肢。
	 */
	private static final String[][] OPTIONS = {
		{"0", "sel0"},
		{"1", "sel1"},
		{"2", "sel2"},
	};

	/**
	 * 選択肢リストを取得する。
	 * @return 選択肢リスト。
	 */
	private List<Map<String, Object>> getOptionList() {
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
	public AllTypeAttachFileTable() {
		this.setComment("全項目タイプ明細テスト用テーブル");
		this.addPkField(new RecordIdField("recordIdField")).setNotNull(true).setComment("レコードID");
		this.addPkField(new SmallintField("fileKey")).setComment("ファイルキー").setHidden(true);
		//this.addField(new SmallintField("sortOrder")).setComment("表示順");
		this.addField(new SortOrderField()).setComment("表示順");
		this.addField(new VarcharField("fileComment", 64)).setComment("コメント");
		this.addField(new DateField("dateField")).setComment("日付");

		this.addField(new BlobStoreFileField("blobData")).setNotNull(false).setComment("blobフィールド");
		this.addField(new FolderStoreFileField("fileData")).setNotNull(false).setComment("ファイルフィールド");


		CharSingleSelectField dropdown = new CharSingleSelectField("dropdownField", 1);
		dropdown.setOptionList(this.getOptionList());
		dropdown.setComment("ドロップダウンフィールド");
		this.addField(dropdown).setComment("ドロップダウンフィールド");
		CharSingleSelectField radio = new CharSingleSelectField("radioField", 1);
		radio.setComment("ラジオボタンフィールド");
		radio.setOptionList(this.getOptionList());
		this.addField(radio);

		MultiSelectField<String> multiSelectList = new MultiSelectField<String>("multiSelectListField", 2);
		multiSelectList.setOptionList(this.getOptionList());
		multiSelectList.setComment("マルチ選択リストフィールド");
		this.addField(multiSelectList);
		MultiSelectField<String> checkbox = new MultiSelectField<String>("checkboxField", 2);
		checkbox.setOptionList(this.getOptionList());
		checkbox.setComment("チェックボックスフィールド");
		this.addField(checkbox);
		ZipCodeField zipCode = new ZipCodeField();
		zipCode.setAddressFieldId("address");
		this.addField(zipCode);
		this.addField(new VarcharField("address", 256));


		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
	}
}
