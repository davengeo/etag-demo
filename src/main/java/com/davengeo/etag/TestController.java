package com.davengeo.etag;

import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Iterator;

@RestController
public class TestController {

    @RequestMapping(value="/check", method= RequestMethod.GET)
    public Resource<String> get(WebRequest request) {
        Resource<String> res = getResource();
        request.getHeaderNames().forEachRemaining(s -> {
            System.out.println(s + ":" + request.getHeader(s));
        });
        return res;
    }

    private Resource<String> getResource() {
        return new Resource<>("hello");
    }

}
