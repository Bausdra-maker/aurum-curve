package com.aurum_curve.api

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.math.BigDecimal

/**
 * Carga el catálogo inicial solo con el perfil `local` (H2 en memoria).
 * Las imágenes viven en el frontend: aurum_curve_pagina/public/images/products/<slug>.png
 * Precios en ARS con IVA incluido. El stock total de cada producto se reparte
 * en partes iguales entre sus variantes (talle x color).
 */
@Configuration
@Profile("local")
class DataInitializer {

    @Bean
    fun seedCatalog(products: ProductRepository): CommandLineRunner = CommandLineRunner {
        if (products.count() > 0L) return@CommandLineRunner
        products.saveAll(catalog())
        println(">>> DataInitializer: ${products.count()} productos cargados en el catálogo local")
    }

    private fun catalog(): List<Product> {
        val tops = listOf("S", "M", "L", "XL", "XXL")
        return listOf(
            product(
                slug = "pantalon-jean", name = "Pantalón Jean", category = "Pantalones", section = "featured",
                description = "Denim rígido de 14 oz, corte recto, remaches reforzados y avíos dorados.",
                price = 18500, minQuantity = 6, totalStock = 240,
                sizes = listOf("36", "38", "40", "42", "44", "46"),
                colors = listOf("Azul oscuro"),
            ),
            product(
                slug = "buzo-rompeviento", name = "Buzo Rompeviento", category = "Abrigos", section = "new-season",
                description = "Rompeviento hidrorrepelente con forro de polar frisado, de la colección Otoño · Invierno.",
                price = 24900, minQuantity = 6, totalStock = 96,
                sizes = tops, colors = listOf("Grafito y café"),
            ),
            product(
                slug = "remera-deportiva", name = "Remera Deportiva", category = "Deportivo", section = "featured",
                description = "Remera dry-fit respirable con costuras planas, ideal para gimnasios y locales deportivos.",
                price = 8900, minQuantity = 6, totalStock = 320,
                sizes = listOf("XS", "S", "M", "L", "XL"), colors = listOf("Arena"),
            ),
            product(
                slug = "prendas-unisex", name = "Prendas Unisex", category = "Básicos", section = "featured",
                description = "Básicos oversize de algodón pesado, pensados para todos los cuerpos. Combiná colores por tanda.",
                price = 11500, minQuantity = 6, totalStock = 18,
                sizes = tops, colors = listOf("Crema", "Topo", "Grafito"),
            ),
            product(
                slug = "variedad-disenos", name = "Variedad De Diseños", category = "Básicos", section = "new-season",
                description = "Pack surtido de prendas estampadas. Cada tanda llega con diseños distintos.",
                price = 13900, minQuantity = 6, totalStock = 0,
                sizes = listOf("XS", "S", "M", "L"),
                colors = listOf("Crema", "Terracota", "Verde oliva", "Grafito"),
            ),
            product(
                slug = "gorras", name = "Gorras", category = "Accesorios", section = "accessories",
                description = "Gorras de gabardina de seis paneles con frente bordado y hebilla metálica regulable.",
                price = 6500, minQuantity = 12, totalStock = 150,
                sizes = listOf("Única"), colors = listOf("Negro", "Beige", "Camel"),
            ),
            product(
                slug = "cinturones", name = "Cinturones", category = "Accesorios", section = "accessories",
                description = "Cinturones de cuero genuino con hebilla dorada satinada. Se venden en largos surtidos.",
                price = 7200, minQuantity = 6, totalStock = 42,
                sizes = listOf("90", "100", "110"), colors = listOf("Cuero"),
            ),
            product(
                slug = "medias", name = "Medias", category = "Accesorios", section = "accessories",
                description = "Medias de algodón peinado con talón y puntera reforzados, por docena.",
                price = 2900, minQuantity = 12, totalStock = 600,
                sizes = listOf("35-38", "39-42", "43-46"),
                colors = listOf("Crema", "Arena", "Verde oliva", "Grafito"),
            ),
            product(
                slug = "rinoneras", name = "Riñoneras", category = "Accesorios", section = "new-season",
                description = "Riñoneras de nylon con tres bolsillos con cierre y correa regulable.",
                price = 9800, minQuantity = 6, totalStock = 75,
                sizes = listOf("Única"), colors = listOf("Grafito"),
            ),
        )
    }

    private fun product(
        slug: String,
        name: String,
        category: String,
        section: String,
        description: String,
        price: Long,
        minQuantity: Int,
        totalStock: Int,
        sizes: List<String>,
        colors: List<String>,
    ): Product {
        val product = Product(
            slug = slug,
            name = name,
            category = category,
            section = section,
            description = description,
            priceWithVat = BigDecimal.valueOf(price),
            minQuantity = minQuantity,
            imageUrl = "/images/products/$slug.png",
        )
        val combos = sizes.flatMap { size -> colors.map { color -> size to color } }
        val base = totalStock / combos.size
        val remainder = totalStock % combos.size
        combos.forEachIndexed { index, (size, color) ->
            product.addVariant(size, color, base + if (index < remainder) 1 else 0)
        }
        return product
    }
}
