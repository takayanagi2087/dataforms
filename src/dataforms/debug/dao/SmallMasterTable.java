package dataforms.debug.dao;

import java.util.Map;
import dataforms.dao.Table;
import dataforms.debug.field.SmallMasterIdField;
import dataforms.debug.field.Key1Field;
import dataforms.debug.field.Key2Field;
import dataforms.field.common.SortOrderField;
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
		this.addField(new Key1Field()); //キー1
		this.addField(new Key2Field()); //キー2
		this.addField(new SortOrderField()); //ソート順
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
		/** キー1のフィールドID。 */
		public static final String ID_KEY1 = "key1";
		/** キー2のフィールドID。 */
		public static final String ID_KEY2 = "key2";
		/** ソート順のフィールドID。 */
		public static final String ID_SORT_ORDER = "sortOrder";
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
		 * キー1を取得します。
		 * @return キー1。
		 */
		public java.lang.String getKey1() {
			return (java.lang.String) this.getMap().get(Entity.ID_KEY1);
		}

		/**
		 * キー1を設定します。
		 * @param key1 キー1。
		 */
		public void setKey1(final java.lang.String key1) {
			this.getMap().put(Entity.ID_KEY1, key1);
		}

		/**
		 * キー2を取得します。
		 * @return キー2。
		 */
		public java.lang.String getKey2() {
			return (java.lang.String) this.getMap().get(Entity.ID_KEY2);
		}

		/**
		 * キー2を設定します。
		 * @param key2 キー2。
		 */
		public void setKey2(final java.lang.String key2) {
			this.getMap().put(Entity.ID_KEY2, key2);
		}

		/**
		 * ソート順を取得します。
		 * @return ソート順。
		 */
		public java.lang.Short getSortOrder() {
			return (java.lang.Short) this.getMap().get(Entity.ID_SORT_ORDER);
		}

		/**
		 * ソート順を設定します。
		 * @param sortOrder ソート順。
		 */
		public void setSortOrder(final java.lang.Short sortOrder) {
			this.getMap().put(Entity.ID_SORT_ORDER, sortOrder);
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
	 * キー1フィールドを取得します。
	 * @return キー1フィールド。
	 */
	public Key1Field getKey1Field() {
		return (Key1Field) this.getField(Entity.ID_KEY1);
	}

	/**
	 * キー2フィールドを取得します。
	 * @return キー2フィールド。
	 */
	public Key2Field getKey2Field() {
		return (Key2Field) this.getField(Entity.ID_KEY2);
	}

	/**
	 * ソート順フィールドを取得します。
	 * @return ソート順フィールド。
	 */
	public SortOrderField getSortOrderField() {
		return (SortOrderField) this.getField(Entity.ID_SORT_ORDER);
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
