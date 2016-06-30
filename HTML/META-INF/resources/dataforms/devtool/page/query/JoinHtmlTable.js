/**
 * @fileOverview {@link JoinHtmlTable}クラスを記述したファイルです。
 */

/**
 * @class JoinHtmlTable
 *
 * @extends EditableHtmlTable
 */
JoinHtmlTable = createSubclass("JoinHtmlTable", {}, "EditableHtmlTable");


/**
 * HTMLエレメントとの対応付けを行います。
 */
JoinHtmlTable.prototype.attach = function() {
	EditableHtmlTable.prototype.attach.call(this);
};

