package com.aurum_curve.api

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

data class VariantDTO(
    val id: Long,
    val size: String,
    val color: String?,
    val stock: Int,
)

data class ProductDTO(
    val id: Long,
    val slug: String,
    val name: String,
    val category: String,
    val section: String,
    val description: String,
    /** Precio unitario final en ARS (IVA incluido). Se llama wholesalePrice por compatibilidad con el frontend. */
    val wholesalePrice: BigDecimal,
    val vatIncluded: Boolean,
    val minQuantity: Int,
    val image: String,
    /** Suma del stock de todas las variantes. */
    val stock: Int,
    val sizes: List<String>,
    val colors: List<String>,
    val variants: List<VariantDTO>,
)

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = ["http://localhost:3000", "http://127.0.0.1:3000"])
class ProductController(private val products: ProductRepository) {

    @GetMapping
    fun getAllProducts(): List<ProductDTO> =
        products.findAllActiveWithVariants().map { it.toDto() }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable("id") id: Long): ProductDTO =
        products.findActiveWithVariantsById(id)?.toDto()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")
}

private fun Product.toDto() = ProductDTO(
    id = id!!,
    slug = slug,
    name = name,
    category = category,
    section = section,
    description = description,
    wholesalePrice = priceWithVat,
    vatIncluded = true,
    minQuantity = minQuantity,
    image = imageUrl,
    stock = totalStock(),
    sizes = variants.map { it.size }.distinct(),
    colors = variants.mapNotNull { it.color }.distinct(),
    variants = variants.map { VariantDTO(it.id!!, it.size, it.color, it.stock) },
)
