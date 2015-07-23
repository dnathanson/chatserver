package com.ddnconsulting.chatserver.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Generic class for handling any exceptions thrown from other controllers.
 *
 * This is a quick hack to make up for the fact that there is not robust exception handling in the other Controllers
 * or the DAO layer.  By globally handling all exception here, we can at least ensure that the server returns
 * valid JSON and doesn't expose nasty stack traces or HTTP 500 errors to the client.
 *
 * @author Dan Nathanson
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    /**
     * Handles all exceptions generically.  Errors will look like:
     * {
     *     "error" : {
     *         "url" : "<the requested url>"
     *         "message" : "<message from exception>"
     *     }
     * }
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody Map globalExceptionHandler(HttpServletRequest req, Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", new ErrorMetaData(req.getRequestURL().toString(), ex.getMessage()));
        return response;
    }

    public static final class ErrorMetaData {
        private String url;
        private String errorMessage;

        public ErrorMetaData() {
        }

        public ErrorMetaData(String url, String errorMessage) {
            this.url = url;
            this.errorMessage = errorMessage;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
