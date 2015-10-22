package com.moor.im.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * SDK用到的登录信息等
 *
 */
@DatabaseTable(tableName = "info")
public class Info {

    public Info() {}

    @DatabaseField(generatedId = true)
    public int _id;

    @DatabaseField
    public String loginName;

    @DatabaseField
    public String userId;

    @DatabaseField
    public String imServiceNo;

    @DatabaseField
    public String accessId;

    @DatabaseField
    public String connectionId;


}
