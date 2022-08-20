package com.khwish.backend.controllers;

import com.khwish.backend.constants.Constants;
import com.khwish.backend.constants.ResponseStrings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasePathController {

    @GetMapping(path = "/")
    private String basePing() {
        return ResponseStrings.BASE_PATH_RESPONSE;
    }
}