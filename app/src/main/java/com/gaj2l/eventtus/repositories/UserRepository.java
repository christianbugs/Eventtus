package com.gaj2l.eventtus.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gaj2l.eventtus.lib.Repository;
import com.gaj2l.eventtus.models.User;

/**
 * Created by Jackson Majolo on 25/03/2017.
 */

public class UserRepository extends Repository<User> {
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MAIL = "mail";
    public static final String COLUMN_IMAGE= "image";
    public static final String COLUMN_METHOD_AUTENTICATION = "method_autentication";
    private int columnIndexName;
    private int columnIndexMail;
    private int columnIndexImage;
    private int columnIndexMethodAutentication;

    public UserRepository(SQLiteDatabase database) {
        super(User.class, database, "user");
    }

    @Override
    protected ContentValues entityToValues(User entity, boolean insert) {
        ContentValues contentValues = super.entityToValues(entity, insert);

        contentValues.put(COLUMN_NAME,  entity.getName());
        contentValues.put(COLUMN_MAIL,  entity.getMail());
        contentValues.put(COLUMN_IMAGE, entity.getImage());
        contentValues.put(COLUMN_METHOD_AUTENTICATION, entity.getMethodAutentication());

        return contentValues;
    }

    @Override
    protected User cursorToEntity(Cursor cursor) {
        User entity = super.cursorToEntity(cursor);

        entity.setName(cursor.getString(this.columnIndexName));
        entity.setMail(cursor.getString(this.columnIndexMail));
        entity.setImage(cursor.getString(this.columnIndexImage));
        entity.setMethodAutentication(cursor.getString(this.columnIndexMethodAutentication));

        return entity;
    }

    @Override
    protected void mapColumns(Cursor cursor) {
        super.mapColumns(cursor);

        this.columnIndexName = cursor.getColumnIndex(COLUMN_NAME);
        this.columnIndexMail = cursor.getColumnIndex(COLUMN_MAIL);
        this.columnIndexImage= cursor.getColumnIndex(COLUMN_IMAGE);
        this.columnIndexMethodAutentication = cursor.getColumnIndex(COLUMN_METHOD_AUTENTICATION);
    }
}