package com.davengeo.etag;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class ResourceController {

    Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    @RequestMapping(value="/check", method= RequestMethod.GET)
    public Resource<String> check() {
        return new Resource<>("hello");
    }

    @RequestMapping(value="/channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<String> findOne(@PathVariable("channelId") String channelId) {
        Optional<String> present = Optional.ofNullable(cache.getIfPresent(channelId));
        if(present.isPresent()) {
            return new ResponseEntity<>(present.get(), OK);
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @RequestMapping(value="/channel/{channelId}", method = RequestMethod.POST)
    public ResponseEntity<String> createOne(@PathVariable("channelId") String channelId,
                                            @RequestBody String content) {
        cache.put(channelId, content);
        return new ResponseEntity<>(OK);
    }



}
