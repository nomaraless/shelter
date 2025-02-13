package org.shelter.repository;

import org.shelter.model.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    List<Adoption> findByUserId(Long userId);
}
