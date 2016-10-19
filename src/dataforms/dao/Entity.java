package dataforms.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * テーブルや問合せ結果などのMapデータを操作するためのクラスです。
 * 
 */
public class Entity {
	
	/** 削除フラグ。*/
	public static final String ID_DELETE_FLAG = "deleteFlag";

	/** レコード作成者。*/
	public static final String ID_CREATE_USER_ID = "createUserId";

	/** レコード作成日時。*/
	public static final String ID_CREATE_TIMESTAMP = "createTimestamp";

	/** レコード更新者。*/
	public static final String ID_UPDATE_USER_ID = "updateUserId";

	/** レコード更新日時。*/
	public static final String ID_UPDATE_TIMESTAMP = "updateTimestamp";

	
	/**
	 * 操作対象マップ。
	 */
	private Map<String, Object> map = null;
	

	/**
	 * コンストラクタ。
	 */
	public Entity() {
		this.map = new HashMap<String, Object>();
	}

	/**
	 * コンストラクタ。
	 * @param map 操作対象マップ。
	 */
	public Entity(final Map<String, Object> map) {
		this.map = map;
	}

	/**
	 * 操作対象マップを取得します。
	 * @return 操作対象マップ。
	 */
	public Map<String, Object> getMap() {
		return map;
	}

	/**
	 * 操作対象マップを設定します。
	 * @param map 操作対象マップ。
	 */
	public void setMap(final Map<String, Object> map) {
		this.map = map;
	}

	/**
	 * 削除フラグを取得します。
	 * @return 削除フラグ。
	 */
	public String getDeleteFlag() {
		return (String) this.getMap().get(ID_DELETE_FLAG);
	}
	
	/**
	 * 削除フラグを設定します。
	 * @param flag 削除フラグ。
	 */
	public void setDeleteFlag(final String flag) {
		this.getMap().put(ID_DELETE_FLAG, flag);
	}

	/**
	 * 作成者のユーザIDを取得します。
	 * @return 作成者のユーザID。
	 */
	public Long getCreateUserId() {
		return (Long) this.getMap().get(ID_CREATE_USER_ID);
	}
	
	/**
	 * 作成者のユーザIDを設定します。
	 * @param userId 作成者のユーザID。
	 */
	public void setCreateUserId(final Long userId) {
		this.getMap().put(ID_CREATE_USER_ID, userId);
	}
	
	/**
	 * 作成時刻を取得します。
	 * @return 作成時刻。
	 */
	public Timestamp getCreateTimestamp() {
		return (Timestamp) this.getMap().get(ID_CREATE_TIMESTAMP);
	}
	
	/**
	 * 作成時刻を設定します。
	 * @param ts 作成時刻。
	 */
	public void setCreateTimestamp(final Timestamp ts) {
		this.getMap().put(ID_CREATE_TIMESTAMP, ts);
	}
	
	/**
	 * 更新者のユーザIDを取得します。
	 * @return 更新者のユーザID。
	 */
	public Long getUpdateUserId() {
		return (Long) this.getMap().get(ID_UPDATE_USER_ID);
	}
	
	/**
	 * 更新者のユーザIDを設定します。
	 * @param userId 更新者のユーザID。
	 */
	public void setUpdateUserId(final Long userId) {
		this.getMap().put(ID_UPDATE_USER_ID, userId);
	}
	
	/**
	 * 更新時刻を取得します。
	 * @return 更新時刻。
	 */
	public Timestamp getUpdateTimestamp() {
		return (Timestamp) this.getMap().get(ID_UPDATE_TIMESTAMP);
	}
	
	/**
	 * 更新時刻を設定します。
	 * @param ts 更新時刻。
	 */
	public void setUpdateTimestamp(final Timestamp ts) {
		this.getMap().put(ID_UPDATE_TIMESTAMP, ts);
	}

}
