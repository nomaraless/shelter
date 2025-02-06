package org.shelter.repository;

import org.shelter.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findByShelterId(Long shelterId);
}
