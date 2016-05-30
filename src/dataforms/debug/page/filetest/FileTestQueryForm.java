package dataforms.debug.page.filetest;

import dataforms.controller.QueryForm;
import dataforms.debug.field.filetest.FileCommentField;
import dataforms.field.base.Field.MatchType;

/**
 * 問い合わせフォームクラス。
 */
public class FileTestQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public FileTestQueryForm() {
		this.addField(new FileCommentField()).setMatchType(MatchType.PART);
	}
}
