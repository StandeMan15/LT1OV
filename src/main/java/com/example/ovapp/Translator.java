package com.example.ovapp;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Translator {
    private static ResourceBundle messages;

    public static void setLanguage(String language) {
        try {
            Locale locale = new Locale(language);
            messages = ResourceBundle.getBundle("com.example.ovapp.MessagesBundle", locale);
        } catch (MissingResourceException e) {
            System.err.println("Error loading language resources: " + e.getMessage());
        }
    }

    public static String translate(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            System.err.println("Key not found: " + key);
            return "Translation not found";
        }
    }

}
