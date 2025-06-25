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

    public static Map<String, List<String>> parse(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines()
                    .map(line -> URLDecoder.decode(line, StandardCharsets.UTF_8))
                    .map(line -> line.split("&"))
                    .flatMap(Arrays::stream)
                    .map(property -> property.split("="))
                    .collect(Collectors.toMap(key -> key[0],
                            value -> List.of(value[1]),
                            (a, b) -> Stream.concat(a.stream(), b.stream()).toList()));
        }
    }
}
