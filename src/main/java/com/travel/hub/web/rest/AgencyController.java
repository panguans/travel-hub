package com.travel.hub.web.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The AgencyController class handles REST requests related to agency operations.
 * It is secured with the ROLE_AGENCY authority.
 * To be removed
 * @author hamza
 */
@RestController
@RequestMapping("/api/agency")
@PreAuthorize("hasAuthority('ROLE_AGENCY')")
public class AgencyController {

    @GetMapping("")
    public String helloAuthority() {
        return "Hello Authority";
    }
}
