package com.raoni.chamaja.repository;

import com.raoni.chamaja.model.Chamado;
import com.raoni.chamaja.model.MensagemChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MensagemChatRepository extends JpaRepository<MensagemChat,Long> {


    List<MensagemChat> findByChamadoOrderByDataEnvioAsc(Chamado chamado);

}
