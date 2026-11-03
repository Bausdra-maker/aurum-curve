package com.aurum_curve.api

import org.springframework.web.bind.annotation.*

data class ProductDTO(
    val id: Long,
    val name: String,
    val wholesalePrice: Double,
    val stock: Int
)

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = ["http://localhost:3000"])
class ProductController {

    @GetMapping
    fun getAllProducts(): List<ProductDTO> {
        return listOf(
            ProductDTO(1L, "Remera Oversize Aurum", 8500.0, 45),
            ProductDTO(2L, "Buzo Hooded Heavyweight", 18000.0, 20),
            ProductDTO(3L, "Pantalón Cargo Wide", 15500.0, 30)
        )
    }
}
