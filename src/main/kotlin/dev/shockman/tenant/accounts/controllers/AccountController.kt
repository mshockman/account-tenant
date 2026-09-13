package dev.shockman.tenant.accounts.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AccountController {
    @GetMapping("/select-account")
    fun selectAccount(
        @RequestParam("realm") realm: String,
    ) {

    }
}