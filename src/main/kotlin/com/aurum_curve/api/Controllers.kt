package com.aurum_curve.api

import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDate

data class VariantDTO(val id: Long, val size: String, val color: String?, val stock: Int)
data class ProductDTO(
    val id: Long, val slug: String, val name: String, val category: String, val section: String,
    val description: String, val wholesalePrice: BigDecimal, val vatIncluded: Boolean,
    val minQuantity: Int, val image: String, val stock: Int, val sizes: List<String>,
    val colors: List<String>, val variants: List<VariantDTO>
)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val userId: Long, val name: String, val email: String, val businessName: String?, val role: String)
data class CheckoutItem(val productId: Long, val variantId: Long? = null, val size: String, val color: String? = null, val quantity: Int)
data class OrderRequest(val userId: String, val lines: List<CheckoutItem>, val fulfillment: String, val paymentMethod: String, val address: String? = null, val notes: String? = null)
data class InquiryRequest(val name: String, val email: String, val message: String)

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = ["http://localhost:3000", "http://127.0.0.1:3000"])
class ProductController(private val productoRepo: ProductoRepository) {
    @GetMapping
    fun getAll(): List<ProductDTO> = productoRepo.findAllByActivoTrueOrderByIdAsc().map { it.toDto() }

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ProductDTO =
        productoRepo.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado") }.toDto()

    private fun Producto.toDto() = ProductDTO(
        id = id!!, slug = slug ?: "producto-$id", name = nombre, category = categoria?.nombre ?: "General",
        section = seccion, description = descripcion, wholesalePrice = precioMayorista, vatIncluded = true,
        minQuantity = cantidadMinima, image = imagenUrl, stock = stock, sizes = listOf(talle), colors = listOf(color),
        variants = listOf(VariantDTO(id!!, talle, color, stock))
    )
}

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = ["http://localhost:3000", "http://127.0.0.1:3000"])
class CategoryController(private val categoriaRepo: CategoriaRepository) {
    @GetMapping
    fun getAll(): List<Categoria> = categoriaRepo.findAll()
}

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:3000", "http://127.0.0.1:3000"])
class AuthController(private val clienteRepo: ClienteRepository) {
    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): AuthResponse {
        val cliente = clienteRepo.findByEmailIgnoreCase(req.email.trim())
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos")
        if (cliente.password != req.password) throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos")
        return AuthResponse(cliente.id!!, cliente.razonSocial, cliente.email, cliente.razonSocial, cliente.rol)
    }
}

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = ["http://localhost:3000", "http://127.0.0.1:3000"])
class OrderController(private val pedidoRepo: PedidoRepository, private val productoRepo: ProductoRepository) {
    @PostMapping
    @Transactional
    fun crearPedido(@RequestBody req: OrderRequest): Map<String, Any> {
        val clienteId = req.userId.toLongOrNull() ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "ID de usuario inválido")
        var totalPedido = BigDecimal.ZERO
        val pedido = Pedido(clienteId = clienteId, fecha = LocalDate.now(), estado = "Pendiente", metodoEntrega = req.fulfillment, metodoPago = req.paymentMethod, direccion = req.address, notas = req.notes)

        for (line in req.lines) {
            val producto = productoRepo.findById(line.productId).orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no existe") }
            if (producto.stock < line.quantity) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para ${producto.nombre}")
            producto.stock -= line.quantity
            productoRepo.save(producto)
            totalPedido = totalPedido.add(producto.precioMayorista.multiply(BigDecimal.valueOf(line.quantity.toLong())))
            pedido.detalles.add(DetallePedido(pedido = pedido, productoId = producto.id!!, talle = line.size, color = line.color ?: producto.color, cantidad = line.quantity, precioUnitario = producto.precioMayorista))
        }
        pedido.total = totalPedido
        val guardado = pedidoRepo.save(pedido)
        return mapOf("ok" to true, "orderId" to (guardado.id ?: 1001), "message" to "Pedido registrado con éxito")
    }

    @GetMapping("/user/{userId}")
    fun pedidosPorUsuario(@PathVariable userId: Long): List<Pedido> = pedidoRepo.findAllByClienteIdOrderByIdDesc(userId)
}

@RestController
@RequestMapping("/api/inquiries")
@CrossOrigin(origins = ["http://localhost:3000", "http://127.0.0.1:3000"])
class InquiryController(private val consultaRepo: ConsultaRepository) {
    @PostMapping
    fun crearConsulta(@RequestBody req: InquiryRequest): Map<String, Any> {
        consultaRepo.save(Consulta(nombre = req.name.trim(), email = req.email.trim(), mensaje = req.message.trim()))
        return mapOf("ok" to true)
    }
}
