package com.marcelopena.minhasfinancas.model.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.marcelopena.minhasfinancas.model.entity.Usuario;

@Service
public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);
}
