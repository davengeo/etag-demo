package com.davengeo.etag;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class ResourceController {

    Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    @RequestMapping(value="/check", method= GET)
    public Resource<String> check() {
        return new Resource<>("hello");
    }

    @RequestMapping(value="/channel/{channelId}", method = GET)
    public ResponseEntity<String> findOne(@PathVariable("channelId") String channelId) {
        Optional<String> present = Optional.ofNullable(cache.getIfPresent(channelId));
        if(present.isPresent()) {
            return new ResponseEntity<>(present.get(), OK);
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @RequestMapping(value="/channel/{channelId}", method = POST)
    public ResponseEntity<String> createOne(@PathVariable("channelId") String channelId,
                                            @RequestBody String content) {
        cache.put(channelId, content);
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(value = "/channel/", method = GET)
    public ResponseEntity<String> findAll() {
        return new ResponseEntity<>(cache.
                asMap().
                keySet().
                stream().
                reduce("", (s, key) -> s += cache.getIfPresent(key)), OK);
    }

    @RequestMapping(value = "/channel/", method = DELETE)
    public ResponseEntity<String> delete() {
        cache.invalidateAll();
        return new ResponseEntity<>(NO_CONTENT);
    }


}
