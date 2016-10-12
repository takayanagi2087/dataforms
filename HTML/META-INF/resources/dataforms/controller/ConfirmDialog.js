/**
 * @fileOverview {@link ConfirmDialog}クラスを記述したファイルです。
 */

/**
 * @class ConfirmDialog
 *
 * @extends Dialog
 */
ConfirmDialog = createSubclass("ConfirmDialog", {title:null, message:null, okFunc: null, cancelFunc:null}, "Dialog");


/**
 * HTMLエレメントとの対応付けを行います。
 */
ConfirmDialog.prototype.attach = function() {
	Dialog.prototype.attach.call(this);
	var thisDialog = this;
	this.find("#confirmOkButton").click(function() {
		thisDialog.close();
		if (thisDialog.okFunc != null) {
			thisDialog.okFunc.call(this);
		}
		return false;
	});
	this.find("#confirmCancelButton").click(function() {
		thisDialog.close();
		if (thisDialog.cancelFunc != null) {
			thisDialog.cancelFunc.call(this);
		}
		return false;
	});
};

/**
 * ダイアログを表示します。
 * @param {Boolean} modal モーダル表示の場合true。
 * @param {Object} p 追加プロパティ。
 *
 */
ConfirmDialog.prototype.show = function(modal, p) {
	this.find("#confirmMessage").html(this.message);
	Dialog.prototype.show.call(this, modal, p);
};
