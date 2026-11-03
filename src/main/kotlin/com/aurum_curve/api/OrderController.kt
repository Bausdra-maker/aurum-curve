package com.aurum_curve.api

import org.springframework.web.bind.annotation.*

// Estructura de los datos que viajan desde el carrito del frontend
data class CheckoutItem(
    val productId: Long,
    val size: String,
    val quantity: Int
)

data class OrderRequest(
    val userId: String,
    val lines: List<CheckoutItem>,
    val fulfillment: String, // "pickup" (retiro) o "delivery" (envío)
    val paymentMethod: String, // "transfer", "cash", "card"
    val address: String?, // Dirección de casa para el envío
    val notes: String?
)

@RestController
@RequestMapping("/api/orders")
class OrderController {

    @PostMapping
    fun crearPedido(@RequestBody pedido: OrderRequest): Map<String, Any> {
        // Aquí es donde el backend procesa lo que mandó el frontend
        println("Recibido pedido del usuario ID: ${pedido.userId}")
        println("Productos en el carrito: ${pedido.lines.size}")
        println("Dirección de entrega: ${pedido.address}")

        // Devuelve una respuesta confirmando que se guardó
        return mapOf(
            "ok" to true,
            "orderId" to 1001,
            "message" to "Pedido registrado con éxito en el backend"
        )
    }
}
