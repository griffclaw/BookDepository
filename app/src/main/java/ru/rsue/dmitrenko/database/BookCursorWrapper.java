package ru.rsue.dmitrenko.database;

import android.database.Cursor;
import android.database.CursorWrapper;

//import java.sql.Date;
import java.util.Date;
import java.util.UUID;

import ru.rsue.dmitrenko.Book;

public class BookCursorWrapper extends CursorWrapper {
    public BookCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Book getBook() {
        String uuidString = getString(getColumnIndex(BookDbSche.BookTable.Cols.UUID));
        String title = getString(getColumnIndex(BookDbSche.BookTable.Cols.TITLE));
        long date = getLong(getColumnIndex(BookDbSche.BookTable.Cols.DATE));
        int isReaded = getInt(getColumnIndex(BookDbSche.BookTable.Cols.READED));
        Book book = new Book(UUID.fromString(uuidString));
        book.setTitle(title);
        book.setDate(new Date(date));
        book.setReaded(isReaded != 0);
        return book;

    }

}
