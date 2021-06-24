package com.marcelopena.minhasfinancas.model.service;

import com.marcelopena.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
}
