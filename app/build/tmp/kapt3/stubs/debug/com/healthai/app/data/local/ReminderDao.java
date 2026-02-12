package com.healthai.app.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bH\'J\u0016\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u001e\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011\u00a8\u0006\u0012"}, d2 = {"Lcom/healthai/app/data/local/ReminderDao;", "", "deleteReminder", "", "reminderId", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllReminders", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/healthai/app/data/local/Reminder;", "insertReminder", "reminder", "(Lcom/healthai/app/data/local/Reminder;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateTakenStatus", "isTaken", "", "(IZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface ReminderDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertReminder(@org.jetbrains.annotations.NotNull()
    com.healthai.app.data.local.Reminder reminder, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM reminders ORDER BY time ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.healthai.app.data.local.Reminder>> getAllReminders();
    
    @androidx.room.Query(value = "DELETE FROM reminders WHERE id = :reminderId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteReminder(int reminderId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE reminders SET isTaken = :isTaken WHERE id = :reminderId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTakenStatus(int reminderId, boolean isTaken, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}