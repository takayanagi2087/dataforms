/**
 * @fileOverview {@link ImportDataForm}クラスを記述したファイルです。
 */

/**
 * @class ImportDataForm
 * テーブル情報フォームクラス。
 * <pre>
 * テーブル情報を表示するためのフォームです。
 * </pre>
 * @extends Form
 */
ImportDataForm = createSubclass("ImportDataForm", {}, "Form");

/**
 * HTMLエレメントフォームとの対応付けを行います。
 *
 */
ImportDataForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var thisForm = this;
	this.find("#importButton").click(function() {
		var rform = currentPage.getComponent("queryResultForm");
		var path = thisForm.find("#pathName").val();
		rform.import(path);
		thisForm.parent.close();
	});
};

