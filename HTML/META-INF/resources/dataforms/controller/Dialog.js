/**
 * @fileOverview {@link Dialog}クラスを記述したファイルです。
 */


/**
 * @class Dialog
 *
 * ダイアログクラス。
 * <pre>
 * DataFormsのサブクラスのDialogクラスです。
 * ダイアログはjquery-uiのdialogで実装しています。
 * </pre>
 * @extends DataForms
 */
Dialog = createSubclass("Dialog", {width: "auto", height: "auto", resizable : true}, "DataForms");

/**
 * 初期化処理を行います。
 * <pre>
 * </pre>
 */
Dialog.prototype.init = function() {
	DataForms.prototype.init.call(this);
	// Dialog用HTMLの読み込み.
	var getParts = new SyncServerMethod("getParts");
	var dlgdiv = $('body').find('#' + this.selectorEscape(this.id));
	if (dlgdiv.length == 0) {
		var htmlstr = getParts.execute("parts=" + escape(this.htmlPath));
		if (htmlstr.status == 0) {
			dlgdiv = $('body').append("<div id='" + this.id + "' style='display:none;'>" + htmlstr.result + "</div>");
		}
	}
	// ダイアログ中のFormの初期化.
	this.initForm(this.formMap);
};

/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * #closeButtonのイベント処理を登録します。
 * </pre>
 */
Dialog.prototype.attach = function() {
	DataForms.prototype.attach.call(this);
	var thisDialog = this;
	this.find("#closeButton").click(function() {
		thisDialog.close();
	});
}

/**
 * ダイアログを表示します。
 * @param {Boolean} modal モーダル表示の場合true。
 * @param {Object} p 追加プロパティ。
 *
 */
Dialog.prototype.show = function(modal, p) {
	this.toQueryMode();
	var dlgdiv = $('body').find('#' + this.selectorEscape(this.id));
	var m = {
		modal: modal
		,title: this.title
		,height: this.width
		,width: this.height
		,resizable: this.resizable
	};
	if (p != null) {
		for (var k in p) {
			m[k] = p[k];
		}
	}
	dlgdiv.dialog(m);
};



/**
 * モーダルダイアログ表示を行います。
 * @param {Object} p 追加プロパティ。
 */
Dialog.prototype.showModal = function(p) {
	this.show(true, p);
};

/**
 * モードレスダイアログ表示を行います。
 * @param {Object} p 追加プロパティ。
 */
Dialog.prototype.showModeless = function(p) {
	this.show(false, p);
};


/**
 * ダイアログを閉じます。
 */
Dialog.prototype.close = function() {
	this.resetErrorStatus();
	var dlgdiv = $('body').find('#' + this.selectorEscape(this.id));
	dlgdiv.dialog('close');
};
