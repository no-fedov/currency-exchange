package com.nefedov.currency_exchange.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class JsonResponseWriter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonResponseWriter() {
    }

    public static void writeJsonToResponse(HttpServletResponse resp, Object object) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter writer = resp.getWriter()) {
            objectMapper.writeValue(writer, object);
        }
    }
}
