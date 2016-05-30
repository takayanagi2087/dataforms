/**
 * @fileOverview {@link FieldListHtmlTable}クラスを記述したファイルです。
 */

/**
 * @class FieldListHtmlTable
 *
 * @extends EditableHtmlTable
 */
FieldListHtmlTable = createSubclass("FieldListHtmlTable", {}, "EditableHtmlTable");


/**
 * HTMLエレメントとの対応付けを行います。
 */
FieldListHtmlTable.prototype.attach = function() {
	EditableHtmlTable.prototype.attach.call(this);
};


/**
 * 行追加時に呼び出されるメソッドです。
 *
 * @param {String} rowid 設定する行のID('tableid[idx]'形式)。
 */
FieldListHtmlTable.prototype.onAddTr = function(rowid) {
	EditableHtmlTable.prototype.onAddTr.call(this, rowid);
	logger.log("rowid=" + rowid);
	var form = this.parent;
	var tpkgname = form.find("#packageName").val();
	var pkg = this.find("#" + this.selectorEscape(rowid + ".packageName"));
	var spkg = this.find("#" + this.selectorEscape(rowid + ".superPackageName"));
	if (pkg.val().length == 0) {
		pkg.val(tpkgname.replace(".dao", ".field"));
	}
	if (spkg.val().length == 0) {
		spkg.val("dataforms");
	}
};

