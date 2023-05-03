package it.cleverad.tracking.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.tracking.persistence.model.Cpm;
import it.cleverad.tracking.persistence.repository.CpmRepository;
import it.cleverad.tracking.service.Refferal;
import it.cleverad.tracking.service.RefferalService;
import it.cleverad.tracking.web.dto.CpmDTO;
import it.cleverad.tracking.web.exception.ElementCleveradException;
import it.cleverad.tracking.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CpmBusiness {

    @Autowired
    private CpmRepository repository;

    @Autowired
    private Mapper mapper;

    @Autowired
    private RefferalService refferalService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CpmDTO create(BaseCreateRequest request) {
        Cpm map = mapper.map(request, Cpm.class);
        map.setRead(false);
        map.setDate(LocalDateTime.now());
        if (request.getRefferal() != null) {
            Refferal refferal = refferalService.decodificaRefferal(request.getRefferal());
            map.setImageId(refferal.getCampaignId());
            map.setMediaId(refferal.getMediaId());
        }
        return CpmDTO.from(repository.save(map));
    }

    // GET BY ID
    public CpmDTO findById(Long id) {
        Cpm channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpm", id));
        return CpmDTO.from(channel);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<CpmDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpm> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpmDTO::from);
    }

    // UPDATE
    public CpmDTO update(Long id, Filter filter) {
        Cpm channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpm", id));
        CpmDTO campaignDTOfrom = CpmDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Cpm mappedEntity = mapper.map(channel, Cpm.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return CpmDTO.from(repository.save(mappedEntity));
    }

    public void setRead(long id) {
        Cpm cpm = repository.findById(id).get();
        cpm.setRead(true);
        repository.save(cpm);
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Cpm> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getRefferal() != null) {
                predicates.add(cb.equal(root.get("refferal"), request.getRefferal()));
            }
            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
            }
            if (request.getIp() != null) {
                predicates.add(cb.equal(root.get("ip"), request.getIp()));
            }
            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getDateFrom().atStartOfDay()));
            }
            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getDateTo().plus(1, ChronoUnit.DAYS).atStartOfDay()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private String refferal;
        private String ip;
        private String agent;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private Long campaignId;
        private Long imageId;
        private Long mediaId;

        private String refferal;
        private String ip;
        private String agent;

        private Boolean read;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
    }

}

