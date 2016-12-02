package dataforms.dao.file;

/**
 * 音声データクラス。
 *
 */
public class AudioData extends FileObject {

	/**
	 * UID。
	 */
	private static final long serialVersionUID = 3145476715977931565L;

	/**
	 * Logger.
	 */
//	private static Logger log = Logger.getLogger(VideoData.class);

	/**
	 * コンストラクタ。
	 */
	public AudioData() {
	}

	/**
	 * コンストラクタ。
	 * @param filename ファイル名。
	 */
	public AudioData(final String filename) {
		this.setFileName(filename);
	}


}
