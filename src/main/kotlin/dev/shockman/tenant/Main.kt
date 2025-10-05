package dev.shockman.tenant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class TenantServiceApplication

fun main(args: Array<String>) {
    runApplication<TenantServiceApplication>(*args)
}