package com.nefedov.currency_exchange.web.util;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FormURLEncoderParser {

    private FormURLEncoderParser() {
    }

    public static Map<String, String[]> parse(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines()
                    .map(line -> URLDecoder.decode(line, StandardCharsets.UTF_8))
                    .flatMap(line -> Arrays.stream(line.split("&")))
                    .map(pair -> pair.split("=", 2))
                    .collect(Collectors.groupingBy(
                            p -> p[0],
                            Collectors.mapping(
                                    p -> p.length > 1 ? p[1] : "",
                                    Collectors.toList()
                            )
                    ))
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().toArray(String[]::new)
                    ));
        }
    }
}
