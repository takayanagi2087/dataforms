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

/**
 * テーブルにデータを設定します。
 * @param {Array} list テーブルデータ。
 * 
 * <pre>
 * テーブルデータを設定し、レコードフィールドのチェックボックスにイベント処理を登録します。
 * </pre>
 */
SelectFieldHtmlTable.prototype.setTableData = function(list) {
	HtmlTable.prototype.setTableData.call(this, list);
	this.setRowSpan("tableClassName");
	var thisTable = this;
	this.find("[id$='selectTableClass']").click(function() {
		thisTable.checkTableField($(this));
		thisTable.disableDuplicate();
	});
	this.find("[id$='selectFieldId']").click(function() {
		thisTable.disableDuplicate();
	});
	thisTable.disableDuplicate();
};

/**
 * テーブル選択チェックボックスのイベント処理を行います。
 * @param {jQuery} jq クリックされたチェックボックス。
 */
SelectFieldHtmlTable.prototype.checkTableField = function(jq) {
	logger.log("checkTableField");
	if (this.tableData != null) {
		for (var i = 0; i < this.tableData.length; i++) {
			var tc = jq.val();
			var d = this.tableData[i];
			if (tc == d.selectTableClass) {
				var ckid = "selectFieldList[" + i + "].selectFieldId";
				var fck = this.find("#" + this.selectorEscape(ckid));
				if (!fck.prop("disabled")) {
					fck.prop("checked", jq.prop("checked"));
				}
			}
		}
	}
};

/**
 * フィールド選択チェックボックスのイベント処理を行います。
 */
SelectFieldHtmlTable.prototype.disableDuplicate = function() {
	if (this.tableData != null) {
		var map = {};
		for (var i = 0; i < this.tableData.length; i++) {
			var ckid = "selectFieldList[" + i + "].selectFieldId";
			var ck = this.find("#" + this.selectorEscape(ckid));
			if (map[ck.val()] == true) {
				ck.prop("checked", false);
				ck.prop("disabled", true);
			} else {
				ck.prop("disabled", false);
			}
			if (ck.prop("checked") == true) {
				map[ck.val()] = true;
			}
		}
	}
};
