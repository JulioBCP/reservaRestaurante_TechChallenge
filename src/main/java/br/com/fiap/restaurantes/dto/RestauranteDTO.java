package br.com.fiap.restaurantes.dto;

import br.com.fiap.restaurantes.entities.Reserva;
import br.com.fiap.restaurantes.entities.TipoCozinha;

import java.util.List;

public record RestauranteDTO(
        Long id,
        String nome,
        String endereco,
        TipoCozinha tipoCozinha,
		String horaInicio,
		String horaFinal,
    	int numMesas,
		int mesasDisponiveis
) {}
