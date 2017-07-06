package dataforms.field.common;

import java.util.Map;

import dataforms.dao.file.FileObject;
import dataforms.dao.file.VideoData;

/**
 * 動画フィールドクラス。
 *
 */
public class VideoField extends StreamingField<VideoData> {

	/**
	 * ビデオプレーヤー幅。
	 */
	private int playerWidth = 420;
	/**
	 * ビデオプレーヤー高さ。
	 */
	private int playerHeight = 340;
	
	/**
	 * コンストラクタ。
	 *
	 */
	public VideoField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 *
	 * @param id フィールドID。
	 */
	public VideoField(final String id) {
		super(id);
	}
	
	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setAdditionalHtml(this.getPage().getPageFramePath() + "/VideoField.html");
	}
	
	/**
	 * ビデオプレーヤー幅を取得します。
	 * @return ビデオプレーヤー幅。
	 */
	public int getPlayerWidth() {
		return playerWidth;
	}

	/**
	 * ビデオプレーヤー幅を設定します。
	 * @param playerWidth ビデオプレーヤー幅。
	 */
	public void setPlayerWidth(final int playerWidth) {
		this.playerWidth = playerWidth;
	}


	/**
	 * ビデオプレーヤー幅を取得します。
	 * @return ビデオプレーヤー幅。
	 */
	public int getPlayerHeight() {
		return playerHeight;
	}

	/**
	 * ビデオプレーヤー高さを設定します。
	 * @param playerHeight ビデオプレーヤー高さ。
	 */
	public void setPlayerHeight(final int playerHeight) {
		this.playerHeight = playerHeight;
	}

	@Override
	protected FileObject newFileObject() {
		return new VideoData();
	}


	@Override
	public Map<String, Object> getClassInfo() throws Exception {
		Map<String, Object> ret = super.getClassInfo();
		ret.put("playerWidth", this.getPlayerWidth());
		ret.put("playerHeight", this.getPlayerHeight());
		return ret;
	}

}
