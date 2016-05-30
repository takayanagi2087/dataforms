/**
 * @fileOverview {@link ImageDialog}クラスを記述したファイルです。
 */

/**
 * @class ImageDialog 画像表示ダイアログクラス。
 *
 * @extends Dialog
 */
ImageDialog = createSubclass("ImageDialog", {}, "Dialog");


/**
 * HTMLエレメントとの対応付けを行います。
 */
ImageDialog.prototype.attach = function() {
	Dialog.prototype.attach.call(this);
}


/**
 * 閉じる際に画像をクリアします。
 */
ImageDialog.prototype.close = function() {
	var imgfld = this.getComponent("imageForm").getComponent("image");
	imgfld.setValue(null);
	Dialog.prototype.close.call(this);
};