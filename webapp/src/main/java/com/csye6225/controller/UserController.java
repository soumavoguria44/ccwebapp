package com.csye6225.controller;

import com.csye6225.businessLogics.EmailAndPasswordLogics;
import com.csye6225.models.User;
import com.csye6225.repository.BookRepository;
import com.csye6225.repository.UserRepository;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bcrypt;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/", method= RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String home(HttpServletRequest request, HttpServletResponse response){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "you are logged in. current time is " + java.time.LocalTime.now().toString());
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        return jsonObject.toString();
    }

    // Post Method

    @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = "application/json")

    protected @ResponseBody
    String Register(HttpServletRequest request, HttpServletResponse response, @RequestBody User userReq){

        JsonObject jsonObject = new JsonObject();
        User user = new User();
        EmailAndPasswordLogics emailPass = new EmailAndPasswordLogics();

        String email_id = userReq.getEmailAddress();
        String password = userReq.getPassword();
        // Email address Validation
        if(emailPass.ValidEmail(email_id))
        {
            // Check for already registered user
            User regUser = userRepository.findByEmailAddress(email_id);
            if(regUser== null){
                // Encrypting the Password

                String hashedPassword = bcrypt.encode(password);
                user.setPassword(hashedPassword);
                user.setEmailAddress(email_id);
                userRepository.save(user);
                jsonObject.addProperty("message", "User Registered");
                response.setStatus(HttpServletResponse.SC_CREATED);
            }
            else
            {
                jsonObject.addProperty("message", "User account already exists!!!");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        else {
            jsonObject.addProperty("message", "Please enter a valid Email-Id");
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
        return jsonObject.toString();
    }
}
