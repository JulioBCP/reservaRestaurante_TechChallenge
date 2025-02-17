package br.com.fiap.restaurantes.repository;

import br.com.fiap.restaurantes.entities.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    @Query("SELECT a FROM Avaliacao a ORDER BY a.dataAvaliacao DESC")
    Page<Avaliacao> listarAvaliacoes(Pageable pageable);
}
