package com.hourfun.ensemble.app.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TestController {
    @GetMapping("/security")
    public String testSecurity() throws Exception {
        log.info("test security");
        return "i got it";
    }

}
