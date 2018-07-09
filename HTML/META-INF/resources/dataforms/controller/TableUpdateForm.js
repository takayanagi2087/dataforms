/**
 * @fileOverview  {@link TableUpdateForm}クラスを記述したファイルです。
 */


/**
 * @class TableUpdateForm
 *
 * データベーステーブル更新フォーム。
 * <pre>
 * データベーステーブルを更新するためのフォームのベースクラスです。
 * </pre>
 * @extends Form
 *
 *
 */
TableUpdateForm = createSubclass("TableUpdateForm", {}, "Form");

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 以下のボタンが存在した場合、イベント処理を登録します。
 * #confirmButton ... 「確認」ボタンの処理.
 * #saveButton ... 「保存」ボタンの処理.
 * #resetButton ... 「リセット」ボタンの処理.
 * #backButton ... 「戻る」ボタンの処理.
 * </pre>
 */
TableUpdateForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var form = this;
	form.find('#confirmButton').click(function () {
		form.confirm();
		return false;
	});
	form.find('#saveButton').click(function () {
		form.save();
		return false;
	});
	form.find('#resetButton').click(function() {
		form.reset();
		return false;
	});
	form.find('#backButton').click(function() {
		if (form.parent.isBrowserBackEnabled()) {
			history.back();
		} else {
			form.back();
		}
		return false;
	});
};


/**
 * 戻るボタンのイベント処理を行います。
 */
TableUpdateForm.prototype.back = function() {
	if (this.mode == "edit") {
		this.clearData();
		if (!this.parent.toQueryMode()) {
			currentPage.toTopPage();
		}
	} else if (this.mode == "confirm") {
		this.toEditMode();
	}
};

/**
 * 編集モードにします。
 * <pre>
 * 各フィールドを編集可能状態にします。
 * </pre>
 */
TableUpdateForm.prototype.toEditMode = function() {
	this.mode = "edit";
	this.lockFields(false);
	var cb = this.find("#confirmButton");
	if (cb.length > 0) {
		// 確認画面があるパターン.
		cb.show();
		this.find("#resetButton").show();
		this.find("#saveButton").hide();
	} else {
		// いきなり保存するパターン.
		this.find("#saveButton").show();
	}
};

/**
 * 確認モードにします。
 * <pre>
 * 各フィールドを編集不可状態にします。
 * </pre>
 */
TableUpdateForm.prototype.toConfirmMode = function() {
	this.mode = "confirm";
	this.lockFields(true);
	var cb = this.find('#confirmButton');
	if (cb.length > 0) {
		// 確認画面があるパターン.
		cb.hide();
		this.find('#resetButton').hide();
		this.find('#saveButton').show();
	} else {
		// いきなり保存するパターン.
		this.find('#saveButton').show();
	}
};

/**
 * 確認ボタンのイベント処理を行います。
 * <pre>
 * 対応するFormのconfirmメソッドを呼び出し、問題なければ確認モードに遷移します。
 * ファイルアップロードフィールドはサーバーに送信されません。
 * </pre>
 */
TableUpdateForm.prototype.confirm = function() {
	var form = this;
	if (form.validate()) {
		this.find("#saveMode").val(this.saveMode);
		form.submitWithoutFile("confirm", function(result) {
			form.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				form.toConfirmMode();
				form.parent.pushConfirmModeStatus();
			} else {
				form.parent.setErrorInfo(form.getValidationResult(result), form);
			}
		});
	}
};

/**
 * 保存や削除後の画面状態遷移を行います。
 */
TableUpdateForm.prototype.changeStateForAfterUpdate = function() {
	var form = this;
	var queryForm = form.parent.getComponent("queryForm");
	var resultForm = form.parent.getComponent("queryResultForm");
	if (queryForm == null && resultForm == null) {
		form.clearData();
		currentPage.toTopPage();
	} else {
		form.clearData();
		form.toEditMode();
		form.parent.toQueryMode();
		var queryResultForm = form.parent.getComponent("queryResultForm");
		if (queryResultForm != null) {
			queryResultForm.changePage();
		}
	}
};


/**
 * 保存ボタンのイベント処理を行います。
 * <pre>
 * 対応するFormのsaveメソッドを呼び出し、保存処理を行います。
 * ファイルアップロードフィールドもサーバーに送信されます。
 * </pre>
 */
TableUpdateForm.prototype.save = function() {
	var form = this;
	if (form.validate()) {
		this.find("#saveMode").val(this.saveMode);
		form.submit("save", function(result) {
			form.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				if (result.result != null && result.result.length > 0) {
					currentPage.alert(null, result.result, function() {
						form.changeStateForAfterUpdate();
					});
				} else {
					form.changeStateForAfterUpdate();
				}
			} else {
				form.parent.setErrorInfo(form.getValidationResult(result), form);
			}
		});
	}
};

