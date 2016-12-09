package dataforms.field.common;

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
	protected FileObject newFileObject() {
		return new AudioData();
	}



}
