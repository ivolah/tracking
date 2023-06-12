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
        log.trace(" :: CPC :: " + request.toString());
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
        mapper.map(filter, channel);
        return CpcDTO.from(repository.save(channel));
    }

    public CpcDTO updateCountry(long id, String isoCode) {
        Cpc channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Cpc", id));
        Filter filter = new Filter();
        filter.setCountry(isoCode);
        mapper.map(filter, channel);
        return CpcDTO.from(repository.save(channel));
    }

    public void setRead(long id) {
        Cpc cpc = repository.findById(id).get();
        cpc.setRead(true);
        repository.save(cpc);
    }


    public Page<CpcDTO> getToTest2HourBefore() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("refferal")));
        Filter request = new Filter();
        //request.setRead(false);
        LocalDateTime oraSpaccata = LocalDateTime.now();
        //.withMinute(0).withSecond(0).withNano(0);        request.setDatetimeFrom(oraSpaccata.minusHours(3));
        request.setDatetimeFrom(oraSpaccata.minusHours(2));
        request.setDatetimeTo(oraSpaccata);
        Page<Cpc> page = repository.findAll(getSpecification(request), pageable);
        log.trace(" >>> TEST CPC 3 HOUR BEFORE :: {}", page.getTotalElements());
        return page.map(CpcDTO::from);
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Cpc> getSpecification(Filter request) {
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
        private String htmlReferral;
        private String info;
        private String country;
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
        private String country;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
        private LocalDateTime datetimeFrom;
        private LocalDateTime datetimeTo;
    }

}