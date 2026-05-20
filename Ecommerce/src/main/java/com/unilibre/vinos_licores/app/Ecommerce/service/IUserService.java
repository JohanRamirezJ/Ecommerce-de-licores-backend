package com.unilibre.vinos_licores.app.Ecommerce.service;

import com.unilibre.vinos_licores.app.Ecommerce.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User register(String nombre, String email, String role, String password);

    List<User> findAll();

    Optional<User> findById(Integer id);

    void delete(Integer id);
}