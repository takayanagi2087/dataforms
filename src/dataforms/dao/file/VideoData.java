package dataforms.dao.file;

/**
 * 動画データクラス。
 *
 */
public class VideoData extends FileObject {

	/**
	 *  UID.
	 */
	private static final long serialVersionUID = -9139304972239002810L;
	/**
	 * Logger.
	 */
//	private static Logger log = Logger.getLogger(VideoData.class);

	/**
	 * コンストラクタ。
	 */
	public VideoData() {
	}

	/**
	 * コンストラクタ。
	 * @param filename ファイル名。
	 */
	public VideoData(final String filename) {
		this.setFileName(filename);
	}


}
