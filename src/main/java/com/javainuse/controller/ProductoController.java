package com.javainuse.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.dao.ProductoDao;
import com.javainuse.model.Producto;
import com.javainuse.model.ProductoDTO;

@RestController
public class ProductoController {

	@Autowired
	private ProductoDao productoDao;
	
	@PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
	@GetMapping({"/producto","/producto/{id}"})
	public ResponseEntity<?> getProductos(@PathVariable(required = false) Integer id){
		if (id == null) {
			return new ResponseEntity<Iterable<Producto>>(this.productoDao.findAll(),HttpStatus.ACCEPTED);
		}else {
			try {				
				return new ResponseEntity<Optional<Producto>>(this.productoDao.findById(id.intValue()),HttpStatus.ACCEPTED);
			} catch (Exception e) {
				return new ResponseEntity<String>("Error " + e.getMessage(), HttpStatus.BAD_REQUEST);
			}
		}
	}
	
	@PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
	@GetMapping("/producto/cantidad/{quantity}")
	public ResponseEntity<?> getProductosMayorCantidad(@PathVariable Integer quantity){
		try {
			return new ResponseEntity<Iterable<Producto>>(this.productoDao.buscarPorMayorCantidad(quantity.intValue()),HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<String>("Error " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
	@GetMapping("/producto/precio/{price}")
	public ResponseEntity<?> getProductosMenorPrecio(@PathVariable Long price){
		try {
			return new ResponseEntity<Iterable<Producto>>(this.productoDao.buscarPorMenorPrecio(price),HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<String>("Error " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/producto")
	public ResponseEntity<?> createProducto(@RequestBody Producto producto){
		try {
			this.productoDao.save(producto);
			
			return new ResponseEntity<Producto>(producto,HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<String>("Error " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/producto/{id}")
	public ResponseEntity<?> deleteProducto(@PathVariable Integer id){
		try {
			if(this.productoDao.existsById(id)) {
				this.productoDao.deleteById(id);
				
				return new ResponseEntity<String>("Se a eliminado el producto",HttpStatus.ACCEPTED);
			}else {
				return new ResponseEntity<String>("No existe el producto",HttpStatus.BAD_REQUEST);
			}
			
		} catch (Exception e) {
			return new ResponseEntity<String>("Error " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/producto/{id}")
	public ResponseEntity<?> updateProducto(@RequestBody ProductoDTO producto, @PathVariable Integer id){
		try {
			final Optional<Producto> getProducto = this.productoDao.findById(id);
			
			if (!getProducto.isEmpty()) {
				Producto productoUpdate = getProducto.get();
				
				if(!producto.getName().isEmpty()) {
					productoUpdate.setName(producto.getName().get());
				}
				
				if(!producto.getQuantity().isEmpty()) {
					productoUpdate.setQuantity(producto.getQuantity().get());
				}
				
				if(!producto.getPrice().isEmpty()) {
					productoUpdate.setPrice(producto.getPrice().get());
				}
				
				this.productoDao.save(productoUpdate);
				return new ResponseEntity<Producto>(productoUpdate,HttpStatus.OK);
			}else {
				return new ResponseEntity<String>("No existe el producto",HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>("Error " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
