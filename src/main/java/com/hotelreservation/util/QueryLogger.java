package com.hotelreservation.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


public class QueryLogger {
    private static final QueryLogger INSTANCE = new QueryLogger();
    private static final int MAX_ENTRIES = 500;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final ConcurrentLinkedDeque<QueryEntry> entries = new ConcurrentLinkedDeque<>();
    private volatile boolean enabled = true;

    private QueryLogger() {}

    public static QueryLogger getInstance() {
        return INSTANCE;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Log a query execution.
     *
     * @param sql          the SQL string
     * @param params       the bound parameter values (human-readable)
     * @param resultCount  number of rows returned (or affected)
     * @param durationMs   execution time in milliseconds
     * @param error        error message (null if success)
     * @param callerClass  short name of the DAO class that executed this
     */
    public void log(String sql, String params, int resultCount, long durationMs, String error, String callerClass) {
        if (!enabled) return;

        QueryEntry entry = new QueryEntry();
        entry.timestamp = LocalDateTime.now().format(FMT);
        entry.sql = sql;
        entry.params = params;
        entry.resultCount = resultCount;
        entry.durationMs = durationMs;
        entry.error = error;
        entry.callerClass = callerClass;
        entry.threadName = Thread.currentThread().getName();

        entries.addFirst(entry);

        // Trim old entries
        while (entries.size() > MAX_ENTRIES) {
            entries.removeLast();
        }
    }

    /**
     * Convenience: log a successful query.
     */
    public void logSuccess(String sql, String params, int resultCount, long durationMs, String callerClass) {
        log(sql, params, resultCount, durationMs, null, callerClass);
    }

    /**
     * Convenience: log a failed query.
     */
    public void logError(String sql, String params, long durationMs, String error, String callerClass) {
        log(sql, params, 0, durationMs, error, callerClass);
    }

    public List<QueryEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public void clear() {
        entries.clear();
    }

    public int size() {
        return entries.size();
    }

    /**
     * Represents a single captured query execution.
     */
    public static class QueryEntry {
        public String timestamp;
        public String sql;
        public String params;
        public int resultCount;
        public long durationMs;
        public String error;       // null = success
        public String callerClass;
        public String threadName;

        public boolean isSuccess() {
            return error == null;
        }
    }
}

