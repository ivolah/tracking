package it.cleverad.tracking.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.tracking.persistence.model.Cpc;
import it.cleverad.tracking.persistence.repository.CpcRepository;
import it.cleverad.tracking.web.dto.CpcDTO;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class CpcBusiness {

    @Autowired
    private CpcRepository repository;

    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CpcDTO create(BaseCreateRequest request) {
        Cpc map = mapper.map(request, Cpc.class);
        map.setDate(LocalDateTime.now());
        map.setRead(false);
        return CpcDTO.from(repository.save(map));
    }

    // GET BY ID
    public CpcDTO findById(Long id) {
        Cpc channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpc", id));
        return CpcDTO.from(channel);
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
    public Page<CpcDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CpcDTO::from);
    }

    // UPDATE
    public CpcDTO update(Long id, Filter filter) {
        Cpc channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpc", id));
        CpcDTO campaignDTOfrom = CpcDTO.from(channel);

        mapper.map(filter, campaignDTOfrom);

        Cpc mappedEntity = mapper.map(channel, Cpc.class);
        mapper.map(campaignDTOfrom, mappedEntity);

        return CpcDTO.from(repository.save(mappedEntity));
    }

    public Page<CpcDTO> getUnread() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.trace("UNREAD {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }

    public Page<CpcDTO> getUnreadLastHour() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setRead(false);
        LocalDateTime now = LocalDateTime.now();
        request.setDateFrom(now.minusHours(1).toInstant(ZoneOffset.of("+02:00")));
        request.setDateTo(now.toInstant(ZoneOffset.of("+02:00")));
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.trace("UNREAD {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }

    public void setRead(long id) {
        Cpc cpc = repository.findById(id).get();
        cpc.setRead(true);
        repository.save(cpc);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Cpc> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
            }
            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), LocalDateTime.ofInstant(request.getDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), LocalDateTime.ofInstant(request.getDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private String refferal;
        private String ip;
        private String agent;
        private Boolean read;
        private Instant dateFrom;
        private Instant dateTo;
    }

}
