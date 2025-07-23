package com.phoebus.communitycentersapi.exception.utils;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getMessage(String key, String... args) throws NoSuchMessageException {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

    public String getMessage(String key) throws NoSuchMessageException {
        return getMessage(key, "");
    }

    public HttpHeaders createHttpHeaderErrorMessage(String message) {
        HttpHeaders header = new HttpHeaders();
        header.add("FAILURE", message);
        return header;
    }
}