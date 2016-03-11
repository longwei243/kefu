package com.moor.imkf.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 评价实体类
 */
@DatabaseTable(tableName = "investigate")
public class Investigate {

    /**
     * 主键
     */
    @DatabaseField(generatedId = true)
    public int _id;
    /**
     * 名称
     */
    @DatabaseField
    public String name;
    /**
     * 数值
     */
    @DatabaseField
    public String value;

}
