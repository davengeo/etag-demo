package com.davengeo.etag;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Optional;

@RestController
public class TestController {

    Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    @RequestMapping(value="/check", method= RequestMethod.GET)
    public Resource<String> check(WebRequest request) {
        return new Resource<>("hello");
    }

    @RequestMapping(value="/channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<String> findOne(@PathVariable("channelId") String channelId) {
        Optional<String> present = Optional.ofNullable(cache.getIfPresent(channelId));
        if(present.isPresent()) {
            return new ResponseEntity<>(present.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value="/channel/{channelId}", method = RequestMethod.POST)
    public ResponseEntity<String> createOne(@PathVariable("channelId") String channelId,
                                            @RequestBody String content) {
        cache.put(channelId, content);
        return ResponseEntity.ok("OK");
    }



}
