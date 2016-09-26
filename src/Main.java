import com.sun.tools.internal.ws.processor.model.Model;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jakefroeb on 9/26/16.
 */
public class Main {


    static String warning;
    static HashMap<String, User> users = new HashMap<>();
    static ArrayList<Book> books = new ArrayList<>();
    static int bookIndex;


    public static void main(String[] args) {

        Spark.init();

        Spark.get("/",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    Session session = request.session();
                    String userName = session.attribute("userName");

                    m.put("userName", userName);
                    m.put("books", books);
                    m.put("warning", warning);
                    return new ModelAndView(m, "home.html");
                }), new MustacheTemplateEngine());
        Spark.get("/editBook.html",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    Session session = request.session();
                    int index = session.attribute("index");
                    Book book = books.get(index);
                    m.put("book", book);
                    return new ModelAndView(m, "editBook.html");
                }), new MustacheTemplateEngine());


        Spark.post("/login",
                ((request, response) -> {
                    String userName = request.queryParams("loginName");
                    if (userName == null) {
                        throw new Exception("Login Name not found");
                    }
                    User user = users.get(userName);
                    if (user == null) {
                        user = new User(userName);
                        users.put(userName, user);
                    }
                    Session session = request.session();
                    session.attribute("userName", userName);
                    response.redirect("/");
                    return "";
                }));

        Spark.post("/create-book",
                ((request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    String bookYear = request.queryParams("year");
                    String bookName = request.queryParams("bookName");
                    String bookAuthor = request.queryParams("author");
                    String bookEdition = request.queryParams("edition");

                    if (userName == null) {
                        warning = "not logged in";
                    }
                    if (bookYear == null || bookName == null || bookAuthor == null || bookEdition == null) {
                        warning = "please enter in all fields";
                    }
                    if (bookYear.equals("") || bookName.equals("") || bookAuthor.equals("") || bookEdition.equals("")) {
                        warning = "empty field";
                    }

                    int bookYearNum = Integer.parseInt(bookYear);
                    int bookEditionNum = Integer.parseInt(bookEdition);
                    Book book = new Book(bookYearNum, bookName, bookAuthor, bookEditionNum, bookIndex++, userName);
                    books.add(book);
                    response.redirect("/");
                    return "";
                }));
        Spark.post("/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }));
        Spark.post("/edit-book",
                ((request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    String index = request.queryParams("index");
                    if (index == null) {
                        throw new Exception("no such entry");
                    }
                    int indexNum = Integer.parseInt(index);
                    Book book = books.get(indexNum);
                    if (userName.equalsIgnoreCase(book.bookCreator)) {
                        session.attribute("index", indexNum);
                        response.redirect("/editBook.html");
                        return "";
                    } else {
                        warning = "can only edit books you have added yourself";
                        response.redirect("/");
                        return "";
                    }
                }));
        Spark.post("/delete-book",
                ((request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    String index = request.queryParams("index");
                    if (index == null) {
                        warning = "no such entry";
                    }
                    int indexNum = Integer.parseInt(index);
                    if (userName.equalsIgnoreCase(books.get(indexNum).bookCreator)) {
                        books.remove(indexNum);
                        bookIndex = bookIndex - 1;
                        warning = "";
                    } else {
                        warning = "can only delete books you have added";
                    }


                    response.redirect("/");
                    return "";
                }));
        Spark.post("/editBook",
                ((request, response) -> {
                    Session session = request.session();
                    int index = session.attribute("index");
                    String userName = session.attribute("userName");
                    String bookYear = request.queryParams("year");
                    String bookName = request.queryParams("bookName");
                    String bookAuthor = request.queryParams("author");
                    String bookEdition = request.queryParams("edition");
                    Book book = books.get(index);
                    books.remove(index);
                    if (bookYear != null && bookYear.length() > 0) {
                        book.year = Integer.parseInt(bookYear);
                    }
                    if (bookName != null && bookName.length() > 0) {
                        book.name = bookName;
                    }
                    if (bookAuthor != null && bookAuthor.length() > 0) {
                        book.author = bookAuthor;
                    }
                    if (bookEdition != null && bookEdition.length() > 0) {
                        book.edition = Integer.parseInt(bookEdition);
                    }
                    warning = "";

                    books.add(index, book);
                    response.redirect("/");
                    return "";
                }));


    }
}
