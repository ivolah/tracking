package it.cleverad.tracking.persistence.repository;

import it.cleverad.tracking.persistence.model.Cpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CplRepository extends JpaRepository<Cpl, Long>, JpaSpecificationExecutor<Cpl> {


}
