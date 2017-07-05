/**
 * @fileOverview {@link PageScrollHtmlTable}クラスを記述したファイルです。
 */

/**
 * @class PageScrollHtmlTable
 * ページスクロールHTMLテーブルクラス。
 * <pre>
 * 行の追加、削除、ドラックによる順序変更をサポートします。
 * </pre>
 * @extends HtmlTable
 */
PageScrollHtmlTable = createSubclass("PageScrollHtmlTable", {}, "HtmlTable");

/**
 * エレメントとの対応付け.
 */
PageScrollHtmlTable.prototype.attach = function() {
	this.sortOrder = "";
	var thisTable = this;
	HtmlTable.prototype.attach.call(this);
	var m = this.getAsyncServerMethod("getPageController");
	m.execute("", function (ret) {
		if (ret.status == ServerMethod.SUCCESS) {
			thisTable.get().before(ret.result);
			thisTable.parent.find(":input").each(function() {
				if ($(this).attr("name") == null) {
					$(this).attr("name", $(this).attr("id"));
				}
			});
		}
		
	});
	this.sortOrder = this.getSortOrder();
};

PageScrollHtmlTable.prototype.getSortOrder = function() {
	var flist = this.getSortFieldList();
	var sortOrder = "";
	for (var i = 0; i < flist.length; i++) {
		if (sortOrder.length > 0) {
			sortOrder += ",";
		}
		var f = flist[i];
		sortOrder += (f.id + ":" + f.currentSortOrder);
	}
	return sortOrder;
};

/**
 * ソートを行います。
 * @param co {jQuery} ラベルのエレメント.
 * @return {Array} ソート結果リスト。
 *
 */
PageScrollHtmlTable.prototype.sortTable = function(col) {
	var thisTable = this;
	this.changeSortMark(col);
/*	var flist = this.getSortFieldList();
	this.sortOrder = "";
	for (var i = 0; i < flist.length; i++) {
		if (this.sortOrder.length > 0) {
			this.sortOrder += ",";
		}
		var f = flist[i];
		this.sortOrder += (f.id + ":" + f.currentSortOrder);
	}*/
	this.sortOrder = this.getSortOrder();
	for (var i = 0; i < this.fields.length; i++) {
		var f = this.fields[i];
		f.sortOrder = f.currentSortOrder;
	}

//	logger.log("sortOrder=" + this.sortOrder);
	this.parent.find("#pageNo").val("0");
	this.parent.changePage();
};
