package it.cleverad.tracking.persistence.repository;


import it.cleverad.tracking.persistence.model.Blacklist;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long>, JpaSpecificationExecutor<Blacklist> {

}
