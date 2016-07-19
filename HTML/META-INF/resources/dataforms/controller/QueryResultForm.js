/**
 * @fileOverview {@link QueryResultForm}クラスを記述したファイルです。
 */

/**
 * @class QueryResultForm
 *
 * 問い合わせ結果フォームクラス。
 *  * <pre>
 * 問い合わせ結果を表示するフォームです。
 * </pre>
 * @extends Form
 */
QueryResultForm = createSubclass("QueryResultForm", {}, "Form");

/**
 * HTMLエレメントへの対応付けを行います。
 * <pre>
 * 以下のコンポーネントが存在した場合、イベント処理を登録します。
 * #linesPerPage ... 1ページの行数指定。
 * #pageNo ... ページ番号指定。
 * #topPageButton ... 先頭ページボタン。
 * #bottomPageButton ... 末尾ページボタン。
 * #prevPageButton ... 前ページボタン。
 * #nextPageButton ... 次ページボタン。
 * </pre>
 */
QueryResultForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	this.queryResult = null;
	var thisForm = this;
	this.find("#linesPerPage").change(function() {
		thisForm.find("#pageNo").val(0);
		thisForm.changePage();
	});
	this.find("#pageNo").change(function() {
		thisForm.changePage();
	});

	this.find("#topPageButton").click(function() {
		thisForm.find("#pageNo").val(0);
		thisForm.changePage();
		return false;
	});

	this.find("#bottomPageButton").click(function() {
		var v = thisForm.find("#pageNo>option:last").val();
		thisForm.find("#pageNo").val(v);
		thisForm.changePage();
		return false;
	});

	this.find("#prevPageButton").click(function() {
		var v = parseInt(thisForm.find("#pageNo").val(), 10);
		var idx = v - 1;
		if (idx < 0){
			idx = 0;
		}
		thisForm.find("#pageNo").val(idx);
		thisForm.changePage();
		return false;
	});


	this.find("#nextPageButton").click(function() {
		var max = parseInt(thisForm.find("#pageNo>option:last").val(), 10);
		var v = parseInt(thisForm.find("#pageNo").val(), 10);
		var idx = v + 1;
		if (idx > max){
			idx = max;
		}
		thisForm.find("#pageNo").val(idx);
		thisForm.changePage();
		return false;
	});
	this.controlPager();
};

/**
 * PKフィールドのdisabled属性を設定します。
 * @param {Boolean} disabled disbaled属性。
 */
QueryResultForm.prototype.setDisabledPkField = function(disabled) {
	for (var i = 0; i < this.pkFieldList.length; i++) {
		this.find("#" + this.selectorEscape(this.pkFieldList[i])).prop("disabled", disabled);
	}
};

/**
 * ページの更新を行います。
 */
QueryResultForm.prototype.changePage = function() {
	var queryResultForm = this;
	var lpp = this.find("#linesPerPage");
	var lines = "";
	if (lpp.prop("disabled")) {
		this.find("#linesPerPage").find("option").each(function() {
			if ($(this).attr("selected") == "selected") {
				lines = "&linesPerPage=" + $(this).val();
			}
		});
	}
	var rt = this.getComponent("queryResult");
	logger.log("sortOrder=" + rt.sortOrder);
	// queryFormにPKが設定されている場合、PKフィールドを送信しないようにする。
	this.setDisabledPkField(true);
	var param = this.condition + lines +  "&" + this.get().serialize() + "&sortOrder=" + rt.sortOrder;
	logger.log("param=" + param);
	this.setDisabledPkField(false);

	var method = this.getAsyncServerMethod("changePage");
	method.execute(param, function(result) {
		queryResultForm.parent.resetErrorStatus();
		if (result.status == ServerMethod.SUCCESS) {
			queryResultForm.setQueryResult(result.result);
		} else {
			queryResultForm.parent.setErrorInfo(queryResultForm.getValidationResult(result), queryResultForm);
		}
	});
};


/**
 * 選択データを更新します。
 */
QueryResultForm.prototype.updateData = function() {
	var queryResultForm = this;
	var editForm = this.parent.getComponent("editForm");
	if (editForm != null) {
		editForm.updateData();
	} else {
		if (queryResultForm.parent instanceof Dialog) {
			queryResultForm.parent.close();
		}
	}
};

/**
 * 選択データをコピーした新規データを登録します。
 */
QueryResultForm.prototype.referData = function() {
	var queryResultForm = this;
	var editForm = this.parent.getComponent("editForm");
	if (editForm != null) {
		editForm.referData();
	}
};



/**
 * 選択データの表示します。
 */
QueryResultForm.prototype.viewData = function() {
	var queryResultForm = this;
	var editForm = this.parent.getComponent("editForm");
	if (editForm != null) {
		editForm.viewData();
	}
};


/**
 * 選択データの削除を行います。
 */
QueryResultForm.prototype.deleteData = function() {
	var msg = MessagesUtil.getMessage("message.deleteconfirm");
	if (confirm(msg)) {
		var queryResultForm = this;
		this.submit("delete", function(result) {
			queryResultForm.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				queryResultForm.changePage();
			} else {
				queryResultForm.parent.setErrorInfo(queryResultForm.getValidationResult(result), queryResultForm);
			}
		});
	}
};


/**
 * ページ関連情報を設定します。
 * @param {Object} queryResult 問い合わせ結果。
 */
QueryResultForm.prototype.setPagerInfo = function(queryResult) {
	var hitCount = queryResult.hitCount;
	var linesPerPage = queryResult.linesPerPage;
	var pageSelector = this.find("select[id='pageNo']");
	if (pageSelector.size()) {
		var max = Math.floor(hitCount / linesPerPage);
		if (hitCount % linesPerPage != 0) {
			max ++;
		}
		pageSelector.empty();
		for (var i = 0; i < max; i++) {
			pageSelector.append('<option value="' + i + '">' + (i + 1) + '</option>');
		}
	}
}


/**
 * ページ関連情報を制御します。
 */
QueryResultForm.prototype.controlPager = function() {
	if (this.find("#queryResult>tbody>tr").size() == 0) {
		this.find("#linesPerPage").prop("disabled", true);
		this.find("#topPageButton").prop("disabled", true);
		this.find("#prevPageButton").prop("disabled", true);
		this.find("#pageNo").prop("disabled", true);
		this.find("#nextPageButton").prop("disabled", true);
		this.find("#bottomPageButton").prop("disabled", true);
	} else {
		this.find("#linesPerPage").prop("disabled", false);
		this.find("#topPageButton").prop("disabled", false);
		this.find("#prevPageButton").prop("disabled", false);
		this.find("#pageNo").prop("disabled", false);
		this.find("#nextPageButton").prop("disabled", false);
		this.find("#bottomPageButton").prop("disabled", false);
		var minPage = 0;
		var maxPage = parseInt(this.find("#pageNo>option:last").val(), 10);
		var pageNo = parseInt(this.find("#pageNo").val(), 10);
		if (pageNo == minPage) {
			this.find("#topPageButton").prop("disabled", true);
			this.find("#prevPageButton").prop("disabled", true);
		}
		if (pageNo == maxPage) {
			this.find("#nextPageButton").prop("disabled", true);
			this.find("#bottomPageButton").prop("disabled", true);
		}
	}
}

/**
 * 各種操作をするためのキーを設定します。
 * <pre>
 * 更新等のイベント処理時に更新対象のキー情報を適切に設定する処理です。
 * </pre>
 * @param {jQuery} comp イベントの発生したコンポーネント。
 * @return {Boolean} 基本的にtrueを返す。
 */
QueryResultForm.prototype.setSelectedKey = function(comp) {
	// クリックされたボタンと同一行にあるキー項目の値を取得する.
	var tbl = this.getComponent("queryResult");
	var ridx = tbl.getRowIndex(comp);
	for (var i = 0; i < this.pkFieldList.length; i++) {
		var id = this.pkFieldList[i];
		var v = this.queryResult.queryResult[ridx][id];
		// 処理対象を指定するキーフィールドに値を設定する.
		this.find("#" + id).val(v);
		var editForm = this.parent.getComponent("editForm");
		if (editForm != null) {
			editForm.setFieldValue(id, v);
		}
	}
	return true;
};

/**
 * 選択データを設定します。
 * @param {jQuery} comp イベントの圧制したコンポーネント。
 */
QueryResultForm.prototype.setSelectedData = function(comp) {
	var table = this.getComponent("queryResult");
	var idx = table.getRowIndex(comp);
	var seldata = this.queryResult.queryResult[idx];
	var dlg = this.getParentDataForms();
	dlg.data = seldata;
};

/**
 * 問合せ結果にデフォルトイベント処理を設定します。
 */
QueryResultForm.prototype.setQueryResultEventHandler = function() {
	var thisForm = this;
	this.find("[id$='\.viewButton']").click(function() {
		if (thisForm.setSelectedKey($(this))) {
			thisForm.viewData();
		}
	});
	//
	this.find("[id$='\.updateButton']").click(function() {
		if (thisForm.setSelectedKey($(this))) {
			// データ検索ダイアログように、選択されたデータを設定する。
			thisForm.setSelectedData($(this));
			thisForm.updateData();
		}
	});
	this.find("[id$='\.referButton']").click(function() {
		if (thisForm.setSelectedKey($(this))) {
			thisForm.referData();
		}
	});
	this.find("[id$='\.deleteButton']").click(function() {
		if (thisForm.setSelectedKey($(this))) {
			thisForm.deleteData();
		}
	});

	var editForm = this.parent.getComponent("editForm");
	if (editForm == null) {
		this.find(".deleteColumn").hide();
	}
};


/**
 * 問い合わせ結果を表示します。
 * <pre>
 * 各結果行に以下のボタンが存在した場合、それぞれのイベント処理を登録します。
 * [id$='\.viewButton'] ... 表示ボタン。
 * [id$='\.updateButton'] ... 更新ボタン。
 * [id$='\.referButton'] ... 参照登録ボタン。
 * [id$='\.deleteButton'] ... 削除ボタン。
 *
 * </pre>
 * @param {Object} queryResult 問い合わせ結果。
 */
QueryResultForm.prototype.setQueryResult = function(queryResult) {
	this.queryResult = queryResult;
	this.setPagerInfo(queryResult);
	this.setFormData(queryResult);
	// 各リンクのイベント処理を登録.
	var thisForm = this;
	this.controlPager();
	// テーブルのイベント処理を追加する。
	this.setQueryResultEventHandler();
};

