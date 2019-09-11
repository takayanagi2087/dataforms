package dataforms.dao.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.apache.log4j.Logger;

/**
 * 画像データクラス。
 *
 */
public class ImageData extends FileObject {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -5068822705733648844L;

	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(ImageData.class);

	/**
	 * コンストラクタ。
	 */
	public ImageData() {
	}

	/**
	 * コンストラクタ。
	 * @param filename ファイル名。
	 */
	public ImageData(final String filename) {
		this.setFileName(filename);
	}

	/**
	 * 画像データを読み込みます。
	 * @param buf 画像データ。
	 * @return イメージ。
	 * @throws Exception 例外。
	 */
	public BufferedImage readImage(final byte[] buf) throws Exception {
		BufferedImage image = null;
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		try {
			image = ImageIO.read(is);
		} finally {
			is.close();
		}
		return image;
	}


	/**
	 * 画像を取得します。
	 * @return 画像。
	 * @throws Exception 例外。
	 */
	public BufferedImage readImage() throws Exception {
		return this.readImage(this.readContents());
	}

	/**
	 * 画像データを書き出します。
	 * @param img イメージ。
	 * @return 出力されたバイト列。
	 * @throws Exception 例外。
	 */
	public byte[] writeImage(final BufferedImage img) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageWriter writer = ImageIO.getImageWritersByMIMEType(CONTENT_TYPE_PNG).next();
			try {
				writer.setOutput(ImageIO.createImageOutputStream(os));
				writer.write(img);
			} finally {
				writer.dispose();
			}
		} finally {
			os.close();
		}
		byte[] ret = os.toByteArray();
		this.setContents(ret);
		return ret;
	}


	/**
	 * 画像を縮小します。
	 * @param w 幅。
	 * @param h 高さ。
	 * @return 画像データ。
	 * @throws Exception 例外。
	 */
	public ImageData getReducedImage(final int w, final int h) throws Exception {
		ImageData ret = new ImageData();
		BufferedImage img = this.readImage(this.getContents());
		int width = w;
		int height = h;
		double iw = img.getWidth();
		double ih = img.getHeight();
		if (iw > ih) {
			width = w;
			height = (int) (ih * (w / iw));
		} else {
			width = (int) (iw * (h / ih));
			height = h;
		}
		log.debug("width,height=" + width + "," + height);
		BufferedImage thumb = new BufferedImage(width, height, img.getType());
		thumb.getGraphics().drawImage(img.getScaledInstance(width, height, java.awt.Image.SCALE_AREA_AVERAGING), 0, 0, width, height, null);
		ret.setContents(this.writeImage(thumb));
		ret.setFileName("thumb.png");
		return ret;
	}

}
