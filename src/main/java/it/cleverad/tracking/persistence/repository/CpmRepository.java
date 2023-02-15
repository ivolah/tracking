package it.cleverad.tracking.persistence.repository;

import it.cleverad.tracking.persistence.model.Cpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CpmRepository extends JpaRepository<Cpm, Long>, JpaSpecificationExecutor<Cpm> {


}
