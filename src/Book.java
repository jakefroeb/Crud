/**
 * Created by jakefroeb on 9/26/16.
 */
public class Book {
    int year;
    String name;
    String author;
    int edition;
    int index;
    String bookCreator;

    public Book(int year, String name, String author, int edition, int index, String bookCreator) {
        this.year = year;
        this.name = name;
        this.author = author;
        this.edition = edition;
        this.index = index;
        this.bookCreator = bookCreator;
    }
}
