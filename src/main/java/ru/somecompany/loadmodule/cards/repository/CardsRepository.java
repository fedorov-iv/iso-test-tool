package ru.somecompany.loadmodule.cards.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.cards.models.Card;

import java.util.List;

@Repository
public interface CardsRepository extends CrudRepository<Card, Long> {

    @Query(getAllCardsByPage)
    List<Card> getAllCardsByPage(Pageable pageable);
    final String getAllCardsByPage = "from Card order by id";
}
