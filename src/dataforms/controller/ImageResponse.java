package dataforms.controller;

import dataforms.dao.file.ImageData;

/**
 * 画像応答クラス。
 *
 */
public class ImageResponse extends BinaryResponse {
	/**
	 * BLOBデータ。
	 */
	private ImageData image = null;

	/**
	 * コンストラクタ。
	 * @param image 画像データ。
	 * @throws Exception 例外。
	 */
	public ImageResponse(final ImageData image) throws Exception {
		super(image.openInputStream());
		this.image = image;
		this.setContentType(this.image.getContentType());
	}
}
