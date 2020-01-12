package ru.somecompany.loadmodule.cards.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.cards.models.Card;
import ru.somecompany.loadmodule.cards.repository.CardsRepository;

import java.util.List;

@Service("cardsService")
public class CardsService {

    private CardsRepository cardsRepository;

    @Autowired
    public CardsService(CardsRepository cardsRepository){
        this.cardsRepository = cardsRepository;
    }

    public Iterable<Card> getAllCards(){
        return cardsRepository.findAll();

    }

    public List<Card> getAllCardsByPage(int page, int size){
        return cardsRepository.getAllCardsByPage(PageRequest.of(page, size));
    }

    public Card getById(Long id){
        return cardsRepository.findById(id).get();

    }

    public Long getCardsCount(){
        return cardsRepository.count();
    }

    public void save(Card card){
        cardsRepository.save(card);
    }

    public void delete(Card card){
        cardsRepository.delete(card);
    }
}
