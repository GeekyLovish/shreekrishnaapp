package com.raman.kumar.customClasses;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;

import com.raman.kumar.shrikrishan.util.PrefHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extensions {
    public static String getAuthToken() {
        PrefHelper prefHelper = new PrefHelper(getApplicationContext());
        return prefHelper.getAuthToken();
    }
    public static String getBearerToken() {
        String authToken = getAuthToken();
        return "Bearer " + authToken;
    }


    public static String formatDate(String dateString) {
        // Input format (matches your original date string format)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
        // Output format (e.g., "13, Aug 2024")
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault());

        try {
            // Parse the input date string
            Date date = inputFormat.parse(dateString);
            // Format the parsed date into the desired format
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Return the original string if parsing fails
        }
    }




    public static String removeUTFCharacters(String data) {
        // Regular expression to match \\u followed by four hexadecimal digits
        String pattern = "\\\\u([0-9A-Fa-f]{4})";
        Pattern regex = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher matcher = regex.matcher(data);
        StringBuffer result = new StringBuffer();

        // Process matches
        while (matcher.find()) {
            // Convert the hex value (captured group) to a character
            int charCode = Integer.parseInt(matcher.group(1), 16);
            String replacement = Character.toString((char) charCode);

            // Append the replacement to the result
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

}



