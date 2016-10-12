/**
 * @fileOverview {@link AlertDialog}クラスを記述したファイルです。
 */

/**
 * @class AlertDialog
 *
 * @extends Dialog
 */
AlertDialog = createSubclass("AlertDialog", {title:null, message:null, okFunc: null}, "Dialog");


/**
 * HTMLエレメントとの対応付けを行います。
 */
AlertDialog.prototype.attach = function() {
	Dialog.prototype.attach.call(this);
	var thisDialog = this;
	this.find("#alertOkButton").click(function() {
		thisDialog.close();
		if (thisDialog.okFunc != null) {
			thisDialog.okFunc.call(this);
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
AlertDialog.prototype.show = function(modal, p) {
	this.find("#alertMessage").html(this.message);
	Dialog.prototype.show.call(this, modal, p);
};