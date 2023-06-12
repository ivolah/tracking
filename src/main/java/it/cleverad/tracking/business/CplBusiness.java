package it.cleverad.tracking.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.tracking.persistence.model.Cpl;
import it.cleverad.tracking.persistence.repository.CplRepository;
import it.cleverad.tracking.web.dto.CplDTO;
import it.cleverad.tracking.web.exception.ElementCleveradException;
import it.cleverad.tracking.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class CplBusiness {
    @Autowired
    private CplRepository repository;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public CplDTO create(BaseCreateRequest request) {
        log.info(">>> CPL >>> " + request.toString());
        Cpl map = mapper.map(request, Cpl.class);
        map.setDate(LocalDateTime.now());
        map.setRead(false);
        return CplDTO.from(repository.save(map));
    }

    // GET BY ID
    public CplDTO findById(Long id) {
        Cpl channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpl", id));
        return CplDTO.from(channel);
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
    public Page<CplDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        return page.map(CplDTO::from);
    }

    // UPDATE
    public CplDTO update(Long id, Filter filter) {
        Cpl channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpl", id));
        mapper.map(filter, channel);
        return CplDTO.from(repository.save(channel));
    }

    public void setRead(long id) {
        Cpl media = repository.findById(id).get();
        media.setRead(true);
        repository.save(media);
    }

    public Page<CplDTO> getToTest2HourBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        LocalDateTime oraSpaccata = LocalDateTime.now();
        //.withMinute(0).withSecond(0).withNano(0);
        request.setDatetimeFrom(oraSpaccata.minusHours(2));
        request.setDatetimeTo(oraSpaccata);
        Page<Cpl> page = repository.findAll(getSpecification(request), pageable);
        log.trace(" >>> TEST CPL 3 HOUR BEFORE :: {}", page.getTotalElements());
        return page.map(CplDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Cpl> getSpecification(Filter request) {
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

            if (request.getDatetimeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getDatetimeFrom()));
            }
            if (request.getDatetimeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getDatetimeTo()));
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
    @ToString
    public static class BaseCreateRequest {
        private String refferal;
        private String ip;
        private String agent;
        private String data;
        private String info;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String refferal;
        private String cid;
        private String ip;
        private String agent;
        private String data;
        private Boolean read;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
        private LocalDateTime datetimeFrom;
        private LocalDateTime datetimeTo;
    }

}