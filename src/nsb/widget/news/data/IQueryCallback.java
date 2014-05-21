package nsb.widget.news.data;

import android.database.Cursor;

public interface IQueryCallback
{
    void doInTransaction(Cursor cursor);
}
