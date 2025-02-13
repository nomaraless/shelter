package org.shelter.repository;

import org.shelter.model.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    Optional<Shelter> findByType(String type);
}
