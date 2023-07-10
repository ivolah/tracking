package it.cleverad.tracking.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.tracking.persistence.model.Blacklist;
import it.cleverad.tracking.persistence.repository.BlacklistRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class BlacklistBusiness {

    @Autowired
    private Mapper mapper;
    @Autowired
    BlacklistRepository repository;

    /**
     * ============================================================================================================
     **/

    // CREATE
    //  public CpcDTO create(BaseCreateRequest request) {
//        log.trace(" :: CPC :: " + request.toString());
//        Cpc map = mapper.map(request, Cpc.class);
//        map.setDate(LocalDateTime.now());
//        map.setRead(false);
//
//        Filter ss = new Filter();
//        ss.setIp(request.getIp());
//        if (blacklistRepository.findAll(ss, PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements() > 0) {
//            log.warn("BLACKLISTED CPC {}", request.getIp());
//            map.setBlacklisted(true);
//        }
//
//        return CpcDTO.from(repository.save(map));
    //  }

    // GET BY ID
    public boolean findByIp(String ip) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
        Filter ff = new Filter();
        ff.setIp(ip);
        Page<Blacklist> page = repository.findAll(getSpecification(ff), pageable);
        if (page.isEmpty())
            return false;
        else
            return true;
    }


    /**
     * ============================================================================================================
     **/
    private Specification<Blacklist> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getIp() != null) {
                predicates.add(cb.equal(root.get("ip"), request.getIp()));
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
        private String ip;
        private String note;
        private LocalDateTime insert_date = LocalDateTime.now();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String ip;
        private String note;
        private LocalDateTime insert_date = LocalDateTime.now();
    }

}