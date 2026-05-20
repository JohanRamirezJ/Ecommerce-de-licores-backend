package com.unilibre.vinos_licores.app.Ecommerce.controller;


import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;
import com.unilibre.vinos_licores.app.Ecommerce.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    // ==============================
    // 1️⃣ CREAR PRODUCTO CON IMAGEN
    // ==============================
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> crearProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "precio", required = false) Double precio,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            Producto nuevo = productoService.crearProducto(nombre, descripcion, precio, imagen);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al crear producto: " + e.getMessage());
        }
    }

    // ==============================
    // 2️⃣ LISTAR PRODUCTOS
    // ==============================
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    // ==============================
    // 3️⃣ BUSCAR PRODUCTO POR ID
    // ==============================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            Producto producto = productoService.buscarProductoPorId(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("❌ Producto no encontrado");
        }
    }

    // ==============================
    // 4️⃣ ACTUALIZAR PRODUCTO CON IMAGEN
    // ==============================
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "precio", required = false) Double precio,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            Producto actualizado = productoService.actualizarProductoConImagen(id, nombre, descripcion, precio, imagen);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al actualizar: " + e.getMessage());
        }
    }

    // ==============================
    // 5️⃣ ELIMINAR PRODUCTO
    // ==============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        try {
            Producto producto = productoService.buscarProductoPorId(id);
            productoService.eliminarProducto(producto);
            return ResponseEntity.ok("✅ Producto eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("❌ Error al eliminar producto: " + e.getMessage());
        }
    }
}