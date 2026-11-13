package com.aurum_curve.api

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.math.BigDecimal

/**
 * Producto del catálogo.
 * Clases `open` + valores por defecto: Hibernate necesita constructor sin argumentos
 * y poder heredar (proxies) y el pom no tiene el plugin kotlin-jpa.
 */
@Entity
@Table(name = "products")
open class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false, unique = true, length = 80)
    open var slug: String = "",

    @Column(nullable = false, length = 160)
    open var name: String = "",

    @Column(nullable = false, length = 80)
    open var category: String = "",

    /** featured | new-season | accessories (filtros del catálogo). */
    @Column(name = "catalog_section", nullable = false, length = 40)
    open var section: String = "",

    @Column(nullable = false, length = 600)
    open var description: String = "",

    /** Precio unitario final en ARS, IVA incluido. */
    @Column(name = "price_with_vat", nullable = false, precision = 12, scale = 2)
    open var priceWithVat: BigDecimal = BigDecimal.ZERO,

    @Column(name = "min_quantity", nullable = false)
    open var minQuantity: Int = 1,

    /** Ruta pública servida por el frontend (carpeta public/). */
    @Column(name = "image_url", nullable = false, length = 255)
    open var imageUrl: String = "",

    @Column(nullable = false)
    open var active: Boolean = true,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id ASC")
    open var variants: MutableList<ProductVariant> = mutableListOf(),
) {
    fun addVariant(size: String, color: String?, stock: Int): ProductVariant {
        val variant = ProductVariant(product = this, size = size, color = color, stock = stock)
        variants.add(variant)
        return variant
    }

    fun totalStock(): Int = variants.sumOf { it.stock }
}
