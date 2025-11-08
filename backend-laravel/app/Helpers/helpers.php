<?php

/**
 * Remove all html tags from string 
 *
 * @return response()
 */
if (!function_exists('cleanHTMLtags')) {
    function cleanHTMLtags(string $string)
    {
        $string = '' . $string . '';
        $string = str_replace('&nbsp;', ' ', $string);
        return strip_tags($string);
    }
}

/***
 * Function to convert Unicode escape sequences (\uXXXX) to actual characters
 */
if (!function_exists('removeUTFCharacters')) {
    function removeUTFCharacters($data)
    {
        // Regular expression to match \u followed by four hexadecimal digits
        $pattern = '/\\\\u([0-9A-Fa-f]{4})/';

        // Callback function to convert the match into the corresponding character
        $result = preg_replace_callback($pattern, function ($matches) {
            // Convert the hex value (captured group) to a character
            return mb_chr(hexdec($matches[1]), 'UTF-8');
        }, $data);

        return $result;
    }
}


if (!function_exists('generatePassword')) {
    function generatePassword($length)
    {
        $str_result = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        return substr(str_shuffle($str_result), 0, $length);
    }
}

if (!function_exists('generateOTP')) {
    function generateOTP($length)
    {
        $otp = '';
        for ($i = 0; $i < $length; $i++) {
            $otp .= rand(0, 9);
        }
        return $otp;
    }
}

if (!function_exists('extractYouTubeVideoId')) {
    function extractYouTubeVideoId($url)
    {
        $pattern = '/(?:v=|\/shorts\/|youtu\.be\/|\/embed\/)([a-zA-Z0-9_-]{11})/';
        if (preg_match($pattern, $url, $matches)) {
            return $matches[1];
        }
        return null;
    }
}
if (!function_exists('getYouTubeLink')) {
    function getYouTubeLink($url)
    {
        $video_id = extractYouTubeVideoId($url);
        return 'https://www.youtube.com/watch?v=' . $video_id;
    }
}

// Standard	https://www.youtube.com/watch?v=abc123XYZ78	abc123XYZ78
// Short URL	https://youtu.be/abc123XYZ78	abc123XYZ78
// Shorts	https://www.youtube.com/shorts/abc123XYZ78	abc123XYZ78
// Embed	https://www.youtube.com/embed/abc123XYZ78	abc123XYZ78
// Playlist	https://www.youtube.com/watch?v=abc123XYZ78&list=PLabcxyz...	abc123XYZ78
