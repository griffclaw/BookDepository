package ru.rsue.dmitrenko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.rsue.dmitrenko.database.BookBaseHelper;
import ru.rsue.dmitrenko.database.BookCursorWrapper;
import ru.rsue.dmitrenko.database.BookDbSche;

public class BookLab {
    private static BookLab sBookLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static BookLab get(Context context) {
        if (sBookLab == null) {
        sBookLab = new BookLab(context);
    }
        return sBookLab;
    }
    private BookLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new BookBaseHelper(mContext)
                .getWritableDatabase();
    }
    public void addBook(Book b) {
        ContentValues values = getContentValues(b);
        mDatabase.insert(BookDbSche.BookTable.NAME, null, values);
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        BookCursorWrapper cursor = queryBooks(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;

    }
    public Book getBook(UUID id) {
        BookCursorWrapper cursor = queryBooks( BookDbSche.BookTable.Cols.UUID + " = ?", new String[] {
                id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBook();
        } finally {
            cursor.close();
        }

    }
    public File getPhotoFile(Book book) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, book.getPhotoFilename());
    }

        public void updateBook(Book book) {
        String uuidString = book.getId().toString();
        ContentValues values = getContentValues(book);
        mDatabase.update(BookDbSche.BookTable.NAME, values, BookDbSche.BookTable.Cols.UUID + " = ?", new String[] {
                uuidString
        });
    }

    private static ContentValues getContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookDbSche.BookTable.Cols.UUID, book.getId().toString());
        values.put(BookDbSche.BookTable.Cols.TITLE, book.getTitle());
        values.put(BookDbSche.BookTable.Cols.DATE, book.getDate().getTime());
        values.put(BookDbSche.BookTable.Cols.READED, book.isReaded() ? 1 : 0);
        return values;
    }
    private BookCursorWrapper queryBooks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
            BookDbSche.BookTable.NAME,
            null, // Columns - null выбирает все столбцы
            whereClause, whereArgs,
            null, // groupBy
             null, // having
             null // orderBy

    );
        return new BookCursorWrapper(cursor);
    }


}
