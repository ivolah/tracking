package it.cleverad.tracking.persistence.repository;


import it.cleverad.tracking.persistence.model.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrackingRepository extends JpaRepository<Tracking, Long>, JpaSpecificationExecutor<Tracking> {


}
