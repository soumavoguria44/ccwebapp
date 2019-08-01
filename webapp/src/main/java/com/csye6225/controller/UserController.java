package com.csye6225.controller;

import com.csye6225.businessLogics.EmailAndPasswordLogics;
import com.csye6225.models.User;
import com.csye6225.repository.BookRepository;
import com.csye6225.repository.UserRepository;
import com.csye6225.services.MyUserDetailsService;
import com.google.gson.JsonObject;
import com.timgroup.statsd.StatsDClient;
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
    @Autowired
    private EmailAndPasswordLogics emailAndPasswordLogics;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private StatsDClient statsDClient;

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Get method to get the Logged in User
     *
     * @param request
     * @param response
     * @return Json
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String GetUser(HttpServletRequest request, HttpServletResponse response) {
        statsDClient.incrementCounter("endpoint.api.get");

        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("message", "you are logged in. current time is " + java.time.LocalTime.now().toString());
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return jsonObject.toString();
        } catch (Exception ex) {
            logger.info("user register");
            logger.error(ex.getMessage(), ex.getStackTrace());
            jsonObject.addProperty("error", "Exception occured! Check log");
            return jsonObject.toString();
        }
    }

    /**
     * Post method to save the user in the database
     *
     * @param request
     * @param response
     * @param user
     * @return Json message
     */

    @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = "application/json")

    @ResponseBody
    public String Register(HttpServletRequest request, HttpServletResponse response, @RequestBody User user) {
        statsDClient.incrementCounter("endpoint.user.register.api.post");
        JsonObject jsonObject = new JsonObject();
        //   try {
        // User user = new User();
        logger.info("user register");
        EmailAndPasswordLogics emailPass = new EmailAndPasswordLogics();

        String email_id = user.getEmailAddress();
        String password = user.getPassword();
        // Password Validation
        if (emailPass.ValidPassword(password)) {
            // Email address Validation
            if (emailPass.ValidEmail(email_id)) {
                // Check for already registered user
                User regUser = userRepository.findByEmailAddress(email_id);
                if (regUser == null) {
                    myUserDetailsService.register(user);
                    jsonObject.addProperty("message", "User Registered");
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    jsonObject.addProperty("message", "User account already exists!!!");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            } else {
                jsonObject.addProperty("message", "Please enter a valid Email-Id");
                response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            }
        } else {
            jsonObject.addProperty("message", "Please enter a acceptable password");
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
        return jsonObject.toString();
    }

    @PostMapping(value = "/reset", produces = "application/json")
    public String generateResetToken(@RequestBody User user, HttpServletRequest request,
                                     HttpServletResponse response) {

        statsDClient.incrementCounter("endpoint.reset.api.post");
        logger.info("generateResetToken - Start ");
        logger.info("email" + " " + (user.getEmailAddress()));
        JsonObject j = new JsonObject();

        try {
            User user1 = userRepository.findByEmailAddress(user.getEmailAddress());
            if (user1 != null) {
                emailAndPasswordLogics.sendMessage(user1.getEmailAddress());
                j.addProperty("message", "Password reset email sent");
                response.setStatus(HttpServletResponse.SC_CREATED);

            } else {
                logger.info("user not present");
                j.addProperty("Error", "Email does not exist!");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error("Exception in generating reset token : " + e.getMessage());
            j.addProperty("message", "Reset email failed");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        logger.info("generateResetToken - End ");

        return j.toString();

    }
}