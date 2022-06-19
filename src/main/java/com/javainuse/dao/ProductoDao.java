package com.javainuse.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.javainuse.model.Producto;

public interface ProductoDao extends CrudRepository<Producto,Integer> {

	@Query("select p from Producto p where p.quantity >= :cantidad")
	public Iterable<Producto> buscarPorMayorCantidad(int cantidad);
	
	@Query("select p from Producto p where p.price <= :precio")
	public Iterable<Producto> buscarPorMenorPrecio(Long precio);
}
