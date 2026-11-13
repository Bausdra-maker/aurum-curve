package com.aurum_curve.api

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/** Variante vendible: talle + color con su propio stock. */
@Entity
@Table(name = "product_variants")
open class ProductVariant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    open var product: Product? = null,

    @Column(name = "size_label", nullable = false, length = 20)
    open var size: String = "",

    @Column(name = "color_name", length = 40)
    open var color: String? = null,

    @Column(nullable = false)
    open var stock: Int = 0,
)
