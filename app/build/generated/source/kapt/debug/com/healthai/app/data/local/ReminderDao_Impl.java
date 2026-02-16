package com.healthai.app.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReminderDao_Impl implements ReminderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Reminder> __insertionAdapterOfReminder;

  private final SharedSQLiteStatement __preparedStmtOfDeleteReminder;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTakenStatus;

  public ReminderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReminder = new EntityInsertionAdapter<Reminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reminders` (`id`,`medicineName`,`dosage`,`time`,`schedule`,`startDate`,`endDate`,`isTaken`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Reminder entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getMedicineName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getMedicineName());
        }
        if (entity.getDosage() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDosage());
        }
        if (entity.getTime() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTime());
        }
        if (entity.getSchedule() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSchedule());
        }
        if (entity.getStartDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getStartDate());
        }
        if (entity.getEndDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getEndDate());
        }
        final int _tmp = entity.isTaken() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__preparedStmtOfDeleteReminder = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reminders WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTakenStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reminders SET isTaken = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertReminder(final Reminder reminder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReminder.insert(reminder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReminder(final int reminderId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteReminder.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, reminderId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteReminder.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTakenStatus(final int reminderId, final boolean isTaken,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTakenStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isTaken ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, reminderId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateTakenStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Reminder>> getAllReminders() {
    final String _sql = "SELECT * FROM reminders ORDER BY time ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reminders"}, new Callable<List<Reminder>>() {
      @Override
      @NonNull
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineName = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineName");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfSchedule = CursorUtil.getColumnIndexOrThrow(_cursor, "schedule");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsTaken = CursorUtil.getColumnIndexOrThrow(_cursor, "isTaken");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpMedicineName;
            if (_cursor.isNull(_cursorIndexOfMedicineName)) {
              _tmpMedicineName = null;
            } else {
              _tmpMedicineName = _cursor.getString(_cursorIndexOfMedicineName);
            }
            final String _tmpDosage;
            if (_cursor.isNull(_cursorIndexOfDosage)) {
              _tmpDosage = null;
            } else {
              _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            }
            final String _tmpTime;
            if (_cursor.isNull(_cursorIndexOfTime)) {
              _tmpTime = null;
            } else {
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
            }
            final String _tmpSchedule;
            if (_cursor.isNull(_cursorIndexOfSchedule)) {
              _tmpSchedule = null;
            } else {
              _tmpSchedule = _cursor.getString(_cursorIndexOfSchedule);
            }
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            final String _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate);
            }
            final boolean _tmpIsTaken;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTaken);
            _tmpIsTaken = _tmp != 0;
            _item = new Reminder(_tmpId,_tmpMedicineName,_tmpDosage,_tmpTime,_tmpSchedule,_tmpStartDate,_tmpEndDate,_tmpIsTaken);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
