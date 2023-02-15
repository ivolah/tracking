package it.cleverad.tracking.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.tracking.persistence.model.Tracking;
import it.cleverad.tracking.persistence.repository.TrackingRepository;
import it.cleverad.tracking.service.Refferal;
import it.cleverad.tracking.service.RefferalService;
import it.cleverad.tracking.web.dto.TargetDTO;
import it.cleverad.tracking.web.dto.TrackingDTO;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class TrackingBusiness {

    @Autowired
    private TrackingRepository repository;

    @Autowired
    private Mapper mapper;

//    @Autowired
//    private MediaBusiness mediaBusiness;
//    @Autowired
//    private CampaignBusiness campaignBusiness;
//    @Autowired
//    private CampaignAffiliateBusiness campaignAffiliateBusiness;

    @Autowired
    private RefferalService refferalService;

    /**
     * ============================================================================================================
     */

    // GET BY ID
    public TargetDTO getTarget(BaseCreateRequest request) {

        Refferal refferal = refferalService.decodificaRefferal(request.getRefferalId());
        TargetDTO targetDTO = new TargetDTO();
        log.trace("REFFERAL :: {} - {}", request.getRefferalId(), refferal.toString());

//        MediaDTO mediaDTO = mediaBusiness.findById(refferal.getMediaId());
//        targetDTO.setTarget(mediaDTO.getTarget());
//
//        Long cID = mediaDTO.getCampaignId();
//        if (cID != null) {
//            targetDTO.setCookieTime(campaignBusiness.findById(cID).getCookieValue());
//        } else {
//            targetDTO.setCookieTime("60");
//        }
//
//        campaignAffiliateBusiness.searchByAffiliateIdAndCampaignId(refferal.getAffiliateId(), refferal.getCampaignId()).stream().findFirst().ifPresent(campaignAffiliateDTO ->
//                targetDTO.setFollowThorugh(campaignAffiliateDTO.getFollowThrough())
//        );

        return targetDTO;
    }

    // CREATE
    public TrackingDTO create(BaseCreateRequest request) {
        Tracking map = mapper.map(request, Tracking.class);
        map.setCreationDate(LocalDateTime.now());
        map.setRead(false);
        return TrackingDTO.from(repository.save(map));
    }

    // GET BY ID
    public TrackingDTO findById(Long id) {
        Tracking media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Tracking", id));
        return TrackingDTO.from(media);
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

    // UPDATE
    public TrackingDTO update(Long id, Filter filter) {
        Tracking media = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Tracking", id));
        TrackingDTO mediaDTOfrom = TrackingDTO.from(media);
        mapper.map(filter, mediaDTOfrom);

        Tracking mappedEntity = mapper.map(media, Tracking.class);
        mapper.map(mediaDTOfrom, mappedEntity);
        return TrackingDTO.from(repository.save(mappedEntity));
    }

    public void setRead(long id) {

        Tracking media = repository.findById(id).get();
        media.setRead(true);
        repository.save(media);

    }

    // SEARCH PAGINATED
    public Page<TrackingDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Tracking> page = repository.findAll(getSpecification(request), pageable);
        return page.map(TrackingDTO::from);
    }

    public Page<TrackingDTO> getUnread() {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("id")));
        Filter request = new Filter();
        request.setRead(false);
        Page<Tracking> page = repository.findAll(getSpecification(request), pageable);
        log.info("UNREAD {}", page.getTotalElements());
        return page.map(TrackingDTO::from);
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Tracking> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            if (request.getRead() != null) {
                predicates.add(cb.equal(root.get("read"), request.getRead()));
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
        private String refferalId;
        private String ip;
        private String agent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String refferalId;
        private String ip;
        private String agent;
        private Boolean read;
        private LocalDateTime creationDate;
    }

}
