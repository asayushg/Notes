package ml.ayush.notes.models;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note {
    @ColumnInfo(name = "noteId")
    @PrimaryKey
    @NonNull
    private String noteId;

    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "date")
    private String date;

    public Note(String title, String content, String date,@NonNull String noteId) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
