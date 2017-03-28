package com.gaj2l.eventtus.lib;

import android.content.res.Resources;

import com.gaj2l.eventtus.busines.exceptions.ValidationException;

/**
 * Created by Jackson Majolo on 24/03/2017.
 */

public abstract class Validation<T> {
    public abstract void validate(T entity) throws ValidationException;

}
