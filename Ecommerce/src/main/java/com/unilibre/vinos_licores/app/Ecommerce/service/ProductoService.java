package com.unilibre.vinos_licores.app.Ecommerce.service;

import com.unilibre.vinos_licores.app.Ecommerce.exception.ProductoNotFoundException;
import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;
import com.unilibre.vinos_licores.app.Ecommerce.repository.ProductoRepository;
import com.unilibre.vinos_licores.app.Ecommerce.functor.ProductoFunctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    // ===========================
    //   GUARDAR IMAGEN
    // ===========================
    private String guardarImagen(MultipartFile imagen) throws IOException {

        if (imagen == null || imagen.isEmpty()) return null;

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
        Path destino = uploadPath.resolve(nombreArchivo);

        Files.write(destino, imagen.getBytes());

        return "/uploads/" + nombreArchivo;
    }


    // ===========================
    //   CREAR PRODUCTO CON IMAGEN
    // ===========================
    @Transactional
    public Producto crearProducto(String nombre, String descripcion, Double precio, MultipartFile imagen) {

        try {
            String urlImagen = guardarImagen(imagen);

            Producto nuevo = new Producto();
            nuevo.setNombre(nombre);
            nuevo.setDescripcion(descripcion);
            nuevo.setPrecio(precio);
            nuevo.setImagen(urlImagen);

            return productoRepository.save(nuevo);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear producto: " + e.getMessage());
        }
    }

    // ===========================
    //   ACTUALIZAR PRODUCTO CON IMAGEN
    // ===========================
    @Transactional
    public Producto actualizarProductoConImagen(Integer id, String nombre, String descripcion, Double precio, MultipartFile imagen) {

        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto con ID " + id + " no encontrado."));

        existente.setNombre(nombre);
        existente.setDescripcion(descripcion);
        existente.setPrecio(precio);

        try {
            if (imagen != null && !imagen.isEmpty()) {
                existente.setImagen(guardarImagen(imagen));
            }

            return productoRepository.save(existente);

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Otro usuario ya modificó este producto. Refresca la página.");
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el producto.");
        }
    }


    // ===============
    //   TUS MÉTODOS (NO TOCADOS)
    // ===============

    // Guardado asincrónico con functor
    public Producto guardarProductoAsync(Producto producto, ProductoFunctor functor) {
        ProductoTask tarea = new ProductoTask(producto, functor);
        tarea.start();
        return productoRepository.save(producto);
    }

    // Guardado normal con functor
    public Producto guardarProducto(Producto producto, ProductoFunctor functor) {
        Producto modificado = functor.aplicar(producto);
        return productoRepository.save(modificado);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto buscarProductoPorId(Integer idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoNotFoundException("Producto con ID " + idProducto + " no encontrado."));
    }

    @Override
    @Transactional
    public void guardarProducto(Producto producto) {
        try {
            productoRepository.save(producto);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Conflicto de concurrencia detectado.");
        }
    }

    @Override
    @Transactional
    public void eliminarProducto(Producto producto) {
        try {
            productoRepository.delete(producto);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Conflicto de concurrencia al eliminar el producto.");
        }
    }

    @Transactional
    public Producto actualizarProducto(Integer id, Producto datos) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto con ID " + id + " no encontrado."));

        productoExistente.setNombre(datos.getNombre());
        productoExistente.setDescripcion(datos.getDescripcion());
        productoExistente.setPrecio(datos.getPrecio());
        productoExistente.setImagen(datos.getImagen());

        try {
            return productoRepository.save(productoExistente);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Otro usuario modificó este producto antes que tú.");
        }
    }
}
