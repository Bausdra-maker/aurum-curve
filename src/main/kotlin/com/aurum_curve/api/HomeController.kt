package com.aurum_curve.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @GetMapping("/")
    fun inicio(): String {
        return "¡Bienvenido a la API de Aurum Curve conectada con éxito a la base de datos!"
    }
}
