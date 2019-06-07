package com.csye6225.businessLogics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAndPasswordLogics {

    /**
     * Method to Validate is the email id is a Valid email Id
     * @param email
     * @return True/False
     */
    public boolean ValidEmail(String email){
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";


        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Method to check if the entered Password is acceptable Password or not
     * @param password
     * @return True/False
     */
    public boolean ValidPassword(String password){
        String regex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}