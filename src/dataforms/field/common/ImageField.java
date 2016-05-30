package dataforms.field.common;

import java.io.File;
import java.util.Map;

import dataforms.annotation.WebMethod;
import dataforms.controller.ImageResponse;
import dataforms.dao.file.FileObject;
import dataforms.dao.file.FileStore;
import dataforms.dao.file.ImageData;

/**
 * 画像フィールドクラス。
 *
 */
public class ImageField extends FileField<ImageData> {
	/**
	 * サムネイル幅。
	 */
	private int thumbnailWidth = 64;

	/**
	 * サムネイル高さ。
	 */
	private int thumbnailHeight = 64;

	/**
	 * コンストラクタ。
	 *
	 * @param id フィールドID。
	 */
	public ImageField(final String id) {
		super(id);
	}

	/**
	 * サムネイル幅を取得します。
	 * @return サムネイル幅。
	 */
	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	/**
	 * サムネイル幅を設定します。
	 * @param thumbnailWidth サムネイル幅。
	 * @return 設定したフィールド。
	 */
	public ImageField setThumbnailWidth(final int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
		return this;
	}

	/**
	 * サムネイル高さを取得します。
	 * @return サムネイル高さ。
	 */
	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	/**
	 * サムネイル高さを設定します。
	 * @param thumbnailHeight サムネイル高さ。
	 * @return 設定したフィールド。
	 */
	public ImageField setThumbnailHeight(final int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
		return this;
	}

	@Override
	protected FileObject newFileObject() {
		return new ImageData();
	}


	/**
	 * 画像データを読み込みます。
	 * @param param 読み込みのパラメータ。
	 * @return 読み込み結果。
	 * @throws Exception 例外。
	 */
	protected ImageData readImageData(final Map<String, Object> param) throws Exception {
		FileStore store = this.newFileStore(param);
		FileObject fobj = store.readFileObject(param);
		ImageData ret = (ImageData) this.newFileObject();
		ret.copy(fobj);
		if (fobj.getTempFile() != null) {
			ret.readContents(fobj.getTempFile());
		} else {
			ret.setContents(fobj.getContents());
		}
		File temp = store.getTempFile(fobj);
		if (temp != null) {
			temp.delete();
		}
		return ret;
	}

	/**
	 * サムネイル画像をダウンロードします。
	 * @param param パラメータ。
	 * @return 画像応答。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = true)
	public ImageResponse downloadThumbnail(final Map<String, Object> param) throws Exception {
		ImageData image = this.readImageData(param);
		ImageResponse resp = new ImageResponse(image.getReducedImage(thumbnailWidth, thumbnailHeight));
		return resp;
	}

	/**
	 * 完全な画像をダウンロードします。
	 * @param param パラメータ。
	 * @return 画像応答。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = true)
	public ImageResponse downloadFullImage(final Map<String, Object> param) throws Exception {
		ImageData image = this.readImageData(param);
		ImageResponse resp = new ImageResponse(image);
		return resp;
	}

	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> ret = super.getClassInfo();
		ret.put("thumbnailWidth", this.getThumbnailWidth());
		ret.put("thumbnailHeight", this.getThumbnailHeight());
		return ret;
	}

}
