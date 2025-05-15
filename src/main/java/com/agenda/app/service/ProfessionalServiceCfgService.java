package com.agenda.app.service;

import com.agenda.app.dto.ProfessionalServiceCfgRequest;
import com.agenda.app.dto.ProfessionalServiceCfgResponse;
import com.agenda.app.mapper.ProfessionalServiceCfgMapper;
import com.agenda.app.model.Item;
import com.agenda.app.model.Professional;
import com.agenda.app.model.ProfessionalServiceCfg;
import com.agenda.app.repository.ItemRepository;
import com.agenda.app.repository.ProfessionalRepository;
import com.agenda.app.repository.ProfessionalServiceCfgRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfessionalServiceCfgService {

    private final ProfessionalServiceCfgRepository repo;
    private final ProfessionalRepository professionalRepo;
    private final ItemRepository serviceRepo;
    private final ProfessionalServiceCfgMapper mapper;

    @Transactional
    public ProfessionalServiceCfgResponse create(ProfessionalServiceCfgRequest dto) {

        if (repo.existsByProfessionalIdAndServiceId(dto.professionalId(), dto.serviceId())) {
            throw new IllegalArgumentException("Config already exists");
        }

        Professional prof = professionalRepo.findById(dto.professionalId())
                .orElseThrow(() -> new IllegalArgumentException("Professional not found"));
        Item serv = serviceRepo.findById(dto.serviceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        ProfessionalServiceCfg cfg = new ProfessionalServiceCfg();
        cfg.setProfessional(prof);
        cfg.setService(serv);
        cfg.setCustomPrice(dto.customPrice());
        cfg.setCustomDurationMinutes(dto.customDurationMinutes());
        cfg.setCommissionPct(dto.commissionPct());
        cfg.setCommissionFixed(dto.commissionFixed());

        repo.save(cfg);
        return mapper.toResponse(cfg);
    }

    /* update / delete seguem a mesma ideia… */
}
