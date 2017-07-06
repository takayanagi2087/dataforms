package dataforms.field.common;

import dataforms.controller.Page;
import dataforms.dao.file.AudioData;
import dataforms.dao.file.FileObject;

/**
 * 音声フィールドクラス。
 *
 */
public class AudioField extends StreamingField<AudioData> {

	/**
	 * コンストラクタ。
	 *
	 */
	public AudioField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 *
	 * @param id フィールドID。
	 */
	public AudioField(final String id) {
		super(id);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.setAdditionalHtml(Page.getFramePath() + "/AudioField.html");
	}


	@Override
	protected FileObject newFileObject() {
		return new AudioData();
	}



}
