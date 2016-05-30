package dataforms.dialog.image;

import dataforms.controller.Dialog;

/**
 * 画像表示ダイアログ。
 *
 */
public class ImageDialog extends Dialog {
	/**
	 * ダイアログ。
	 */
	public ImageDialog() {
		super("imageDialog");
		this.addForm(new ImageForm());
	}
}
