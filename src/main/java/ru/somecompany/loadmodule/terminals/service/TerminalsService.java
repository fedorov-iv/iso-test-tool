package ru.somecompany.loadmodule.terminals.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.terminals.models.Terminal;
import ru.somecompany.loadmodule.terminals.repository.TerminalsRepository;

import java.util.List;

@Service("terminalsService")
public class TerminalsService {
    private TerminalsRepository terminalsRepository;

    @Autowired
    public TerminalsService(TerminalsRepository terminalsRepository){
        this.terminalsRepository = terminalsRepository;
    }

    public Iterable<Terminal> getAllTerminals(){
        return terminalsRepository.findAll();

    }

    public List<Terminal> getAllTerminalsByPage(int page, int size){
        return terminalsRepository.getAllTerminalsByPage(PageRequest.of(page, size));
    }

    public Terminal getById(Long id){
        return terminalsRepository.findById(id).get();

    }

    public Long getTerminalsCount(){
        return terminalsRepository.count();
    }

    public void save(Terminal terminal){
        terminalsRepository.save(terminal);
    }

    public void delete(Terminal terminal){
        terminalsRepository.delete(terminal);
    }
}
