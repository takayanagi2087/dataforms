/**
 * @fileOverview  {@link MultiRecordEditForm}クラスを記述したファイルです。
 */


/**
 * @class MultiRecordEditForm
 *
 * データ編集フォーム。
 * <pre>
 * データ編集を行うフォームです。
 * </pre>
 * @extends TableUpdateForm
 *
 * @prop {String} mode "edit"(フォームが編集可能な状態)または"confirm"(フォーム全体が編集不可の状態)の値を取ります。
 * @prop {Boolean} multiRecord 複数レコード編集モードの場合はtrue。このクラスではtrueに設定されます。
 * @prop {Object} keyMap QueryFormで編集対象を限定した場合、その指定内容を保存します。
 *
 */
MultiRecordEditForm = createSubclass("MultiRecordEditForm", {mode:"edit", multiRecord: true, keyMap:{}}, "TableUpdateForm");

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
MultiRecordEditForm.prototype.attach = function() {
	TableUpdateForm.prototype.attach.call(this);
	var thisForm = this;
	var list = this.getComponent("list");
	list.onAddTr = function(rowid) {
		EditableHtmlTable.prototype.onAddTr.call(list, rowid);
		logger.log("custom onAddTr=" + rowid);
		thisForm.setKeyValue(rowid);
	}
	this.toEditMode();
};

/**
 * QueryFormで指定されたキー情報を設定します。
 */
MultiRecordEditForm.prototype.setKeyValue = function(rowid) {
	for (var k in this.keyMap) {
		var id = rowid + "." + k;
		logger.log("id=" + id);
		this.find("#" + this.selectorEscape(id)).val(this.keyMap[k]);
	}
};

/**
 * QueryFormで指定された条件でレコードを抽出し、そのレコードを編集対象にします。
 */
MultiRecordEditForm.prototype.updateData = function(qs) {
	this.keyMap = QueryStringUtil.parse(qs);
	var title = MessagesUtil.getMessage("message.editformtitle.update");
	this.find("#editFormTitle").text(title);
	var form = this;
	form.find("#dfMethod").remove();
	var data = qs;
	logger.log("qs=" + data);
	var method = new AsyncServerMethod("editForm.getDataByQueryFormCondition");
	method.execute(data, function(result) {
		form.parent.resetErrorStatus();
		if (result.status == ServerMethod.SUCCESS) {
			form.parent.toEditMode();
			form.setFormData(result.result);
			form.toEditMode();
			form.parent.pushEditModeStatus();
		} else {
			form.parent.setErrorInfo(form.getValidationResult(result), form);
		}
	});
};