package com.example.springbootrest;

import org.springframework.web.bind.annotation.*;

/**
 * Created by sbiliaiev on 1/22/2018.
 */
@RestController
public class Controller {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello(@RequestParam(value = "name", defaultValue = "worldsdsd") String name) {
        return "Hello " + name;
    }

    @RequestMapping(value = "/hello-post", method = RequestMethod.POST)
    public String helloPost(@RequestBody String name) {
        return "Post hello " + name;
    }
}
