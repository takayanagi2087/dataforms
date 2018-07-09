package dataforms.debug.dao;

import java.util.Map;
import dataforms.dao.Table;
import dataforms.debug.field.SmallMasterIdField;
import dataforms.debug.field.CommentField;
import dataforms.debug.field.AttachFileField;


/**
 * 小さいマスタクラス。
 *
 */
public class SmallMasterTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public SmallMasterTable() {
		this.setAutoIncrementId(true);
		this.setComment("小さいマスタ");
		this.addPkField(new SmallMasterIdField()); //レコードID
		this.addField(new CommentField()); //コメント
		this.addField(new AttachFileField()); //添付ファイル

		this.addUpdateInfoFields();
	}
	
	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		SmallMasterTableRelation r = new SmallMasterTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** レコードIDのフィールドID。 */
		public static final String ID_SMALL_MASTER_ID = "smallMasterId";
		/** コメントのフィールドID。 */
		public static final String ID_COMMENT = "comment";
		/** 添付ファイルのフィールドID。 */
		public static final String ID_ATTACH_FILE = "attachFile";

		/**
		 * コンストラクタ。
		 */
		public Entity() {
			
		}
		/**
		 * コンストラクタ。
		 * @param map 操作対象マップ。
		 */
		public Entity(final Map<String, Object> map) {
			super(map);
		}
		/**
		 * レコードIDを取得します。
		 * @return レコードID。
		 */
		public java.lang.Long getSmallMasterId() {
			return (java.lang.Long) this.getMap().get(Entity.ID_SMALL_MASTER_ID);
		}

		/**
		 * レコードIDを設定します。
		 * @param smallMasterId レコードID。
		 */
		public void setSmallMasterId(final java.lang.Long smallMasterId) {
			this.getMap().put(Entity.ID_SMALL_MASTER_ID, smallMasterId);
		}

		/**
		 * コメントを取得します。
		 * @return コメント。
		 */
		public java.lang.String getComment() {
			return (java.lang.String) this.getMap().get(Entity.ID_COMMENT);
		}

		/**
		 * コメントを設定します。
		 * @param comment コメント。
		 */
		public void setComment(final java.lang.String comment) {
			this.getMap().put(Entity.ID_COMMENT, comment);
		}

		/**
		 * 添付ファイルを取得します。
		 * @return 添付ファイル。
		 */
		public dataforms.dao.file.FileObject getAttachFile() {
			return (dataforms.dao.file.FileObject) this.getMap().get(Entity.ID_ATTACH_FILE);
		}

		/**
		 * 添付ファイルを設定します。
		 * @param attachFile 添付ファイル。
		 */
		public void setAttachFile(final dataforms.dao.file.FileObject attachFile) {
			this.getMap().put(Entity.ID_ATTACH_FILE, attachFile);
		}


	}
	/**
	 * レコードIDフィールドを取得します。
	 * @return レコードIDフィールド。
	 */
	public SmallMasterIdField getSmallMasterIdField() {
		return (SmallMasterIdField) this.getField(Entity.ID_SMALL_MASTER_ID);
	}

	/**
	 * コメントフィールドを取得します。
	 * @return コメントフィールド。
	 */
	public CommentField getCommentField() {
		return (CommentField) this.getField(Entity.ID_COMMENT);
	}

	/**
	 * 添付ファイルフィールドを取得します。
	 * @return 添付ファイルフィールド。
	 */
	public AttachFileField getAttachFileField() {
		return (AttachFileField) this.getField(Entity.ID_ATTACH_FILE);
	}


}
