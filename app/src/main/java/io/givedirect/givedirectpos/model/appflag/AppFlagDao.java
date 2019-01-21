package io.givedirect.givedirectpos.model.appflag;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import io.reactivex.Single;

@Dao
public interface AppFlagDao {
    @Query("SELECT * FROM appflag WHERE flagKey = :flagKey")
    Single<AppFlag> getFlagByKey(@NonNull String flagKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppFlag appFlag);
}
