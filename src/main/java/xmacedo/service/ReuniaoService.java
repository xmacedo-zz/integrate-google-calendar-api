package xmacedo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xmacedo.model.Reuniao;
import xmacedo.repository.ReuniaoRepository;

@Service
public class ReuniaoService {

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    public Reuniao findOne(String id) {
        return reuniaoRepository.findOne(id);
    }

    public Page<Reuniao> findAll(Pageable pages) {
        return reuniaoRepository.findAll(pages);
    }

    public Reuniao insert(Reuniao reuniao) {
        googleCalendarService.adicionarReuniao(reuniao);
        return reuniaoRepository.save(reuniao);
    }
}
