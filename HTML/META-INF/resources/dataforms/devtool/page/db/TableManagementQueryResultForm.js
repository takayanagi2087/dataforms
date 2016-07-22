/**
 *
 * @fileOverview {@link TableManagementQueryResultForm}クラスを記述したファイルです。
 */

/**
 * @class TableManagementQueryResultForm
 * テーブル問い合わせ結果フォーム。
 * <pre>
 * テーブル管理の問い合わせ結果を表示するフォームです。
 * </pre>
 * @extends QueryResultForm
 */
TableManagementQueryResultForm = createSubclass("TableManagementQueryResultForm", {}, "QueryResultForm");

/**
 * HTMLエレメントとの対応付けを行います。
 */
TableManagementQueryResultForm.prototype.attach = function() {
	QueryResultForm.prototype.attach.call(this);
	var systemName = MessagesUtil.getMessage("message.systemname");
	var thisForm = this;
	this.find("#selectAllButton").click(function() {
		$("[name='checkedClass']").each(function() {
			$(this).prop("checked", true);
		});
		thisForm.controlButton();
	});
	this.find("#selectNothingButton").click(function() {
		for (var i = 0;;i++) {
			var id = "queryResult[" + i + "].statusVal";
			var st = thisForm.find("#" + thisForm.selectorEscape(id));
			if (st.size() == 0) {
				break;
			}
			if (st.val() == "0") {
				var cbid = "queryResult[" + i + "].checkedClass";
				thisForm.find("#" + thisForm.selectorEscape(cbid)).prop("checked", true);
			}
		}
		thisForm.controlButton();
	});
	this.find("#selectDiffButton").click(function() {
		for (var i = 0;;i++) {
			var id = "queryResult[" + i + "].differenceVal";
			var st = thisForm.find("#" + thisForm.selectorEscape(id));
			if (st.size() == 0) {
				break;
			}
			if (st.val() == "1") {
				var cbid = "queryResult[" + i + "].checkedClass";
				thisForm.find("#" + thisForm.selectorEscape(cbid)).prop("checked", true);
			}
		}
		thisForm.controlButton();
	});
	this.find("#unselectAllButton").click(function() {
		$("[name='checkedClass']").each(function() {
			$(this).prop("checked", false);
		});
		thisForm.controlButton();
	});
	this.find("#initTableButton").click(function() {
		currentPage.confirm(systemName, MessagesUtil.getMessage("message.initTableConfirm"), function() {
			thisForm.submit("initTable", function(result) {
				thisForm.updateTableInfoList(result);
			});
		})
	});

	this.find("#updateTableButton").click(function() {
		currentPage.confirm(systemName, MessagesUtil.getMessage("message.updateTableConfirm"), function() {
			thisForm.submit("updateTable", function(result) {
				thisForm.updateTableInfoList(result);
			});
		});
	});

	this.find("#dropTableButton").click(function() {
		currentPage.confirm(systemName, MessagesUtil.getMessage("message.dropTableConfirm"), function() {
			thisForm.submit("dropTable", function(result) {
				thisForm.updateTableInfoList(result);
			});
		});
	});

	this.find("#exportAsInitialDataButton").click(function() {
		currentPage.confirm(systemName, MessagesUtil.getMessage("message.dexportAsInitialDataConfirm"), function() {
			thisForm.submit("exportTableAsInitialData", function(result) {
				if (result.status == ServerMethod.SUCCESS) {
					var path = result.result;
					currentPage.alert(systemName, MessagesUtil.getMessage("message.exportInitialDataResult", path));
				}
			});
		});
	});
	
	this.find("#exportTableButton").click(function() {
		currentPage.confirm(systemName, MessagesUtil.getMessage("message.dexportTableConfirm"), function() {
			thisForm.submit("exportTable", function(result) {
				if (result.status == ServerMethod.SUCCESS) {
					var path = result.result;
					currentPage.alert(systemName, MessagesUtil.getMessage("message.exportInitialDataResult", path));
				}
			});
		});
	});
	this.find("#importTableButton").click(function() {
		var dlg = thisForm.parent.getComponent("importDataDialog");
		dlg.showModal();
	});
	var tbl = this.getComponent("queryResult");
	// ソート結果の行番号を修正。
	tbl.getSortedList = function() {
		var list = HtmlTable.prototype.getSortedList.call(this);
		for (var i = 0; i < list.length; i++) {
			list[i].rowNo = (i + 1);
		}
		return list;
	};
	// ソート時のイベントハンドラ設定。
	tbl.sortTable = function(col) {
		logger.log("sort");
		var list = HtmlTable.prototype.sortTable.call(this, col);
		this.parent.setTableEventHandler(list);
	};

	this.controlButton();
};


/**
 * ボタンのenable/disable制御を行います。
 */
TableManagementQueryResultForm.prototype.controlButton = function() {
	var tr = this.find("#queryResult>tbody>tr");
	if (tr.size() > 0) {
		this.find("#selectAllButton").prop("disabled", false);
		this.find("#selectNothingButton").prop("disabled", false);
		this.find("#selectDiffButton").prop("disabled", false);
		this.find("#unselectAllButton").prop("disabled", false);
	} else {
		this.find("#selectAllButton").prop("disabled", true);
		this.find("#selectNothingButton").prop("disabled", true);
		this.find("#selectDiffButton").prop("disabled", true);
		this.find("#unselectAllButton").prop("disabled", true);
	}
	var ckcb = this.find("[name='checkedClass']:checked");
	if (ckcb.size() > 0) {
		this.find("#updateTableButton").prop("disabled", false);
		this.find("#initTableButton").prop("disabled", false);
		this.find("#dropTableButton").prop("disabled", false);
		this.find("#exportAsInitialDataButton").prop("disabled", false);
		this.find("#exportTableButton").prop("disabled", false);
		this.find("#importTableButton").prop("disabled", false);
	} else {
		this.find("#updateTableButton").prop("disabled", true);
		this.find("#initTableButton").prop("disabled", true);
		this.find("#dropTableButton").prop("disabled", true);
		this.find("#exportAsInitialDataButton").prop("disabled", true);
		this.find("#exportTableButton").prop("disabled", true);
		this.find("#importTableButton").prop("disabled", true);
	}
};

/**
 * 結果テーブルへイベントハンドラを設定します。
 * @param queryResult {Array} 検索結果。
 */
TableManagementQueryResultForm.prototype.setTableEventHandler = function(queryResult) {
	var thisForm = this;
	if (queryResult != null) {
		for (var i = 0; i < queryResult.length; i++) {
			var id = "queryResult[" + i + "].className";
			this.find("#" + this.selectorEscape(id)).click(function() {
				var clsname = $(this).html();
				var qs="className=" + clsname;
				var method = thisForm.getSyncServerMethod("getTableInfo");
				var sqllist = method.execute(qs);
				if (sqllist.status == ServerMethod.SUCCESS) {
					thisForm.showTableInfo(sqllist.result);
				}
			});
			var sid = "queryResult[" + i + "].statusVal";
		}
		this.find("[name='checkedClass']").each(function() {
			$(this).click(function() {
				thisForm.controlButton();
			});
		});
		for (var i = 0; i < queryResult.length; i++) {
			logger.log("statusVal=" + queryResult[i].statusVal + ",differenceVal=" + queryResult[0].differenceVal);
			if (queryResult[i].differenceVal == "1") {
				this.find("#queryResult tbody tr:eq(" + i + ")").addClass("warnTr");
			} else {
				this.find("#queryResult tbody tr:eq(" + i + ")").removeClass("warnTr");
			}
			if (queryResult[i].statusVal == "0") {
				this.find("#queryResult tbody tr:eq(" + i + ")").addClass("errorTr");
			} else {
				this.find("#queryResult tbody tr:eq(" + i + ")").removeClass("errorTr");

			}
		}
	}
	this.controlButton();
};

/**
 * 問い合わせの結果を設定します。
 * <pre>
 * 問い合わせ結果をHTMLテーブルに設定されたソート順に従ってソートしてから設定します。
 * </pre>
 * @param result 問い合わせ結果。
 */
TableManagementQueryResultForm.prototype.setQueryResult = function(result) {
	var tbl = this.getComponent("queryResult");
	result.queryResult = tbl.sort(result.queryResult);
	QueryResultForm.prototype.setQueryResult.call(this, result);
}


/**
 * 問い合わせ結果を表示します。
 * @param {Object} result 問い合わせ結果。
 */
TableManagementQueryResultForm.prototype.setFormData = function(result) {
	QueryResultForm.prototype.setFormData.call(this, result);
	var queryResult = result.queryResult;
	this.setTableEventHandler(queryResult);
};

/**
 * テーブル情報ダイアログを表示します。
 * @param {Object} result テーブル情報。
 *
 */
TableManagementQueryResultForm.prototype.showTableInfo = function(result) {
	var dlg = this.parent.getComponent("tableInfoDialog");
	dlg.getComponent("tableInfoForm").setFormData(result);
	dlg.showModal();
};

/**
 * 検索結果リストのテーブル情報を更新します。
 * @param {Object} result テーブル情報。
 */
TableManagementQueryResultForm.prototype.updateTableInfo = function(result) {
	for (var i = 0;;i++) {
		var id = "queryResult[" + i + "].className";
		var clsname = this.find("#" + this.selectorEscape(id));
		if (clsname.length > 0) {
			if (clsname.html() == result.className) {
				result.rowNo = (i + 1);
				var rt = this.getComponent("queryResult");
				rt.updateRowData(i, result);

				if (result.differenceVal == "1") {
					this.find("#queryResult tbody tr:eq(" + i + ")").addClass("warnTr");
				} else {
					this.find("#queryResult tbody tr:eq(" + i + ")").removeClass("warnTr");
				}
				if (result.statusVal == "0") {
					this.find("#queryResult tbody tr:eq(" + i + ")").addClass("errorTr");
				} else {
					this.find("#queryResult tbody tr:eq(" + i + ")").removeClass("errorTr");
				}

			}
		} else {
			break;
		}
	}
};

/**
 * 検索結果リストのテーブル情報を更新します。
 * @param {Array} result テーブル情報の配列。
 */
TableManagementQueryResultForm.prototype.updateTableInfoList = function(result) {
	if (result.status == ServerMethod.SUCCESS) {
		var tlist = result.result;
		for (var i = 0; i < tlist.length; i++) {
			this.updateTableInfo(tlist[i]);
		}
	}
};

/**
 * Importを実行します。
 * @param {String} path インポートデータのパス。
 */
TableManagementQueryResultForm.prototype.import = function(path) {
	var thisForm = this;
	$("#datapath").val(path);
	thisForm.submit("importTable", function(result) {
		if (result.status == ServerMethod.SUCCESS) {
			thisForm.updateTableInfoList(result);
		}
	});

};