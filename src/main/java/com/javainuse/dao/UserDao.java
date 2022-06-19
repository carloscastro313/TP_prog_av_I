package com.javainuse.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.model.Usuario;

@Repository
public interface UserDao extends CrudRepository<Usuario, Integer> {
	
	Usuario findByUsername(String username);
	
}