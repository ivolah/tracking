package it.cleverad.tracking.persistence.repository;

import it.cleverad.tracking.persistence.model.Cps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CpsRepository extends JpaRepository<Cps, Long>, JpaSpecificationExecutor<Cps> {

}
