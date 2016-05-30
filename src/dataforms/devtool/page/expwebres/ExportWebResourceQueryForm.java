package dataforms.devtool.page.expwebres;

import dataforms.controller.QueryForm;
import dataforms.field.common.FlagField;
import dataforms.field.sqltype.VarcharField;

/**
 * Webリソースの問い合わせフォームクラス。
 */
public class ExportWebResourceQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public ExportWebResourceQueryForm() {
		super();
		this.addField(new VarcharField("fileName", 1024)).setComment("ファイル名");
		this.addField(new FlagField("regexpFlag")).setComment("正規表現を使用する。");
	}
}
