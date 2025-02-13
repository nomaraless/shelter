package org.shelter.repository;

import org.shelter.model.UserState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStateRepository extends JpaRepository<UserState, String> {
}
