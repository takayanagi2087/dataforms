/**
 * @fileOverview {@link SelectFieldHtmlTable}クラスを記述したファイルです。
 */

/**
 * @class SelectFieldHtmlTable
 *
 * @extends HtmlTable
 */
SelectFieldHtmlTable = createSubclass("SelectFieldHtmlTable", {}, "HtmlTable");


/**
 * HTMLエレメントとの対応付けを行います。
 */
SelectFieldHtmlTable.prototype.attach = function() {
	HtmlTable.prototype.attach.call(this);
};

SelectFieldHtmlTable.prototype.setTableData = function(list) {
	HtmlTable.prototype.setTableData.call(this, list);
	this.setRowSpan("tableClassName");
};

