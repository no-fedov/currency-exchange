package com.nefedov.currency_exchange.web.filter;

import com.nefedov.currency_exchange.domain.dao.exception.DataBaseException;
import com.nefedov.currency_exchange.domain.dao.exception.EntityAlreadyExistsException;
import com.nefedov.currency_exchange.domain.dao.exception.EntityNotFoundException;
import com.nefedov.currency_exchange.web.exception.ValidationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.nefedov.currency_exchange.web.servlet.JsonResponseWriter.writeJsonToResponse;

public class ErrorHandlerFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (DataBaseException e) {
            if (e.getClass().equals(EntityAlreadyExistsException.class)) {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            if (e.getClass().equals(EntityNotFoundException.class)) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            if (e.getClass().equals(DataBaseException.class)) {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            writeJsonToResponse(res, new ErrorResponse(e.getMessage()));
        } catch (ValidationException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJsonToResponse(res, new ErrorResponse(e.getMessage()));
        }
        res.setContentType("application/json; charset=UTF-8");
    }
}
// поиск валюты
// 400 код валюты осутствует в адресе (поиск валюты)
// 400 - отсутствует нужно поле формы (публикация валюты)
//
// курсы
// Коды валют пары отсутствуют в адресе - 400
//Отсутствует нужное поле формы - 400
//            Отсутствует нужное поле формы - 400
