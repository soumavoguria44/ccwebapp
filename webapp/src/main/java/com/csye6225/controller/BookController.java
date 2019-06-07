package com.csye6225.controller;

import com.csye6225.models.Book;
import com.csye6225.repository.BookRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController("BookController")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Get method to get all the books from the database
     * @param request
     * @param response
     * @return Json - All Books
     */
    @RequestMapping(value = "/book", method= RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String GetBooks(HttpServletRequest request, HttpServletResponse response){

        try {
            List<Book> books = (List) bookRepository.findAll();
            String json = new Gson().toJson(books);
            return json;
        }
        catch (Exception ex){
            logger.error(ex.getMessage(), ex.getStackTrace());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", "Exception occured! Check log");
            return jsonObject.toString();
        }

    }

    /**
     * Post method  to save a book in the database
     * @param request
     * @param response
     * @param book
     * @return Json of the created book
     */

    @RequestMapping(value = "/book", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String SaveBook(HttpServletRequest request, HttpServletResponse response, @RequestBody Book book){

        JsonObject jsonObject = new JsonObject();
        try {
//            Book book = new Book();

            UUID id = UUID.randomUUID(); // Generating UUID for Book Id
            book.setId(id.toString());
            bookRepository.save(book);
            String json = new Gson().toJson(book);
            response.setStatus(HttpServletResponse.SC_CREATED);
            return json;
        }
        catch (Exception ex){
            logger.error(ex.getMessage(), ex.getStackTrace());
            jsonObject.addProperty("error", "Exception occured! Check log");
            return jsonObject.toString();
        }

    }

    /**
     * Put method to update a book
     * @param request
     * @param response
     * @param bookReq
     * @return response status only
     */

    @RequestMapping(value = "/book", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public String UpdateBook(HttpServletRequest request, HttpServletResponse response, @RequestBody Book bookReq){

        JsonObject jsonObject = new JsonObject();
        try {
            Book book = bookRepository.findById(bookReq.getId());
            if (book != null) {
                book.setTitle(bookReq.getTitle());
                book.setAuthor(bookReq.getAuthor());
                book.setIsbn(bookReq.getIsbn());
                book.setQuantity(bookReq.getQuantity());
                bookRepository.save(book);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        catch (Exception ex){
            logger.error(ex.getMessage(), ex.getStackTrace());
            jsonObject.addProperty("error", "Exception occured! Check log");
        }
        return jsonObject.toString();
    }

    /**
     * Get method to get book by Id
     * @param id
     * @param response
     * @return Json
     * @throws IOException
     */

    @RequestMapping(value = "/book/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String GetBookById(@PathVariable("id") String id, HttpServletResponse response) throws IOException{

        JsonObject jsonObject = new JsonObject();
        try {
            Book book = bookRepository.findById(id);
            String json = new Gson().toJson(book);
            if (book != null) {

                response.setStatus(HttpServletResponse.SC_OK);
                return json;
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return jsonObject.toString();
            }
        }
        catch (Exception ex){
            logger.error(ex.getMessage(), ex.getStackTrace());
            jsonObject.addProperty("error", "Exception occured! Check log");
            return jsonObject.toString();
        }

    }

    /**
     * Delete method to delete a book by Id
     * @param id
     * @param response
     * @return response status only
     * @throws IOException
     */

    @RequestMapping(value = "/book/{id}", method = RequestMethod.DELETE, produces="application/json")
    @ResponseBody
    public String DeleteBookById(@PathVariable("id") String id, HttpServletResponse response) throws IOException{

        JsonObject jsonObject = new JsonObject();
        try {
            Book book = bookRepository.findById(id);
            if (book != null) {
                bookRepository.delete(book);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);

            } else {

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        catch (Exception ex){
            logger.error(ex.getMessage(), ex.getStackTrace());
            jsonObject.addProperty("error", "Exception occured! Check log");
        }

        return jsonObject.toString();

    }

}