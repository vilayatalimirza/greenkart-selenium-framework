package com.vilayat.utils;

public class TestData {

    // ── Application ──────────────────────────────────────────────────────────
    
    // ── Search ───────────────────────────────────────────────────────────────
    public static final String SEARCH_VALID_FULL = "Brocolli";
    public static final String SEARCH_VALID_TOMATO = "Tomato";
    public static final String SEARCH_VALID_BEETROOT = "Beetroot";
    public static final String SEARCH_PARTIAL = "Bro";
    public static final String SEARCH_LOWERCASE = "brocolli";
    public static final String SEARCH_UPPERCASE = "BROCOLLI";
    public static final String SEARCH_NON_EXISTING = "Brother";
    public static final String SEARCH_SPECIAL_CHARS = "#@!QJS*";
    public static final String SEARCH_NUMERIC = "1231123";
    public static final String SEARCH_SINGLE_CHAR = "B";
    public static final String SEARCH_LONG_STRING = "hdcbudbdvcgfbvyfbsuhnhbvfomasomxsmxdcuhysbhncjam";
    public static final String SEARCH_SPACES_ONLY = "   ";

    // ── Products ─────────────────────────────────────────────────────────────
    public static final String[] PRODUCTS_E2E = {"Cucumber", "Brocolli", "Beetroot"};
    public static final String[] PRODUCTS_MULTI = {"Brocolli", "Tomato", "Beetroot"};
    public static final String PRODUCT_SINGLE = "Brocolli";

    // ── Quantity ─────────────────────────────────────────────────────────────
    public static final int QTY_DEFAULT = 1;
    public static final int QTY_MULTIPLE = 5;
    public static final int QTY_MIN = 1;

    // ── Promo Codes ──────────────────────────────────────────────────────────
    public static final String PROMO_TRAILING_SPACES = "rahulshettyacademy ";
    public static final String PROMO_INVALID_ALPHANUMERIC = "TEST123";
    public static final String PROMO_LONG_STRING = "rahulshettyacademythisisaveryextrastringtomakeitoverahundredcharacterslongforboundarytesting123456";
    public static final String PROMO_VALID = "rahulshettyacademy";
    public static final String PROMO_INVALID = "INVALID123";
    public static final String PROMO_EMPTY = "";
    public static final String PROMO_SPECIAL_CHARS = "@#$%^&*";
    public static final String PROMO_NUMERIC = "123456";
    public static final String PROMO_MIXED_CASE = "RahulShettyAcademy";
    public static final String PROMO_LEADING_SPACES = " rahulshettyacademy";
    public static final String PROMO_EMPTY_MSG = "Empty code ..!";

    // ── Place Order ──────────────────────────────────────────────────────────
    public static final String COUNTRY_INDIA = "India";
    public static final String COUNTRY_AFGHANISTAN = "Afghanistan";

    // ── Expected Results ─────────────────────────────────────────────────────
    public static final String PROMO_SUCCESS_MSG = "Code applied ..!";
    public static final String PROMO_ERROR_MSG = "Invalid code ..!";

    // ── Browsers ─────────────────────────────────────────────────────────────
    public static final String BROWSER_CHROME = "Chrome";
    public static final String BROWSER_FIREFOX = "Firefox";
    public static final String BROWSER_EDGE = "Edge";
}