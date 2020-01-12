package ru.somecompany.loadmodule.cards.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.somecompany.loadmodule.cards.forms.CardForm;
import ru.somecompany.loadmodule.cards.models.Card;
import ru.somecompany.loadmodule.cards.repository.CardSearchRepository;
import ru.somecompany.loadmodule.cards.service.CardsService;

import java.util.List;

@Controller
public class CardsController {

    private int pageSize = 10;

    @Autowired
    private CardsService cardsService;

    @Autowired
    private CardSearchRepository cardSearch;


    @RequestMapping(value = "/cards", method = RequestMethod.GET)
    public ModelAndView cards(@RequestParam(name = "page", required = false, defaultValue = "0") String page, 
                              @RequestParam(name="size", required=false, defaultValue="10") String size,
                              @RequestParam(name="search", required=false, defaultValue="") String search) {

        ModelAndView modelAndView = new ModelAndView();

        int p = (!page.equals("0")) ? Integer.valueOf(page) : 1;
        int s = Integer.valueOf(size); //items on page

        Long cardsCount = 0L;

        if(!search.isEmpty()){
            int startAt = s * (p  - 1);
            List<Card> cards = cardSearch.search(search, startAt, s);
            cardsCount = (long) cardSearch.getResultSize(search);
            modelAndView.addObject("cards", cards);

        }else{
            List<Card> cards = cardsService.getAllCardsByPage(p - 1, s);
            cardsCount = cardsService.getCardsCount();
            modelAndView.addObject("cards", cards);
        }

        int pagesCount = (int) Math.ceil((double) cardsCount / (double) s);
        modelAndView.addObject("pages", pagesCount == 0 ? 1 : pagesCount);
        modelAndView.addObject("current_page", p);
        modelAndView.addObject("current_size", s);
        modelAndView.addObject("current_search", search);
        modelAndView.setViewName("cards");
        return modelAndView;
    }

    @RequestMapping(value = "/cards/add", method = RequestMethod.GET)
    public ModelAndView addCardForm() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("card", new Card());
        modelAndView.setViewName("edit_card");
        return modelAndView;

    }

    @RequestMapping(value = "/cards/add", method = RequestMethod.POST)
    public String addCard(CardForm form) {

        Card card = new Card();
        card.setName(form.getName());
        card.setDescription(form.getDescription());
        card.setPan(form.getPan());
        card.setExpiryDate(form.getExpiryDate());
        card.setCvv(form.getCvv());
        card.setPin(form.getPin());
        card.setTrack2(form.getTrack2());
        cardsService.save(card);
        return "redirect:/cards";

    }

    @RequestMapping(value = "/cards/edit", method = RequestMethod.GET)
    public ModelAndView editCard(@RequestParam(name="id") String id) {
        Long cardId = Long.valueOf(id);
        Card card = cardsService.getById(cardId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("card", card);
        modelAndView.setViewName("edit_card");
        return modelAndView;

    }

    @RequestMapping(value = "/cards/edit", method = RequestMethod.POST)
    public String editCard(CardForm form) {

        Card card = cardsService.getById(form.getId());
        card.setName(form.getName());
        card.setDescription(form.getDescription());
        card.setPan(form.getPan());
        card.setExpiryDate(form.getExpiryDate());
        card.setCvv(form.getCvv());
        card.setPin(form.getPin());
        card.setTrack2(form.getTrack2());
        cardsService.save(card);
        return "redirect:/cards/edit/?id=" + card.getId();

    }

    @RequestMapping(value = "/cards/delete", method = RequestMethod.GET)
    public String deleteCard(@RequestParam(name = "id") String id) {

        Long cardId = Long.valueOf(id);
        Card card = cardsService.getById(cardId);
        cardsService.delete(card);
        return "redirect:/cards";

    }


}
