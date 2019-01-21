package io.givedirect.givedirectpos.model.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import io.givedirect.givedirectpos.model.appflag.AppFlag;
import io.givedirect.givedirectpos.model.appflag.AppFlagDao;

@Database(entities = AppFlag.class, version = AppFlagDB.VERSION)
public abstract class AppFlagDB extends RoomDatabase {
    public static final String DATABASE_NAME = "AppFlagDB";
    public static final int VERSION = 1;

    public enum AppFlags {
        INITIALIZED_ADDRESS,
        INITIALIZED_SEED
    }

    public abstract AppFlagDao appFlagDao();
}