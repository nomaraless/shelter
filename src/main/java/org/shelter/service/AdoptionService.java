package org.shelter.service;


import org.shelter.model.Adoption;
import org.shelter.model.User;
import org.shelter.repository.AdoptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AdoptionService {

    private static final Logger logger = LoggerFactory.getLogger(AdoptionService.class);
    private final AdoptionRepository adoptionRepository;

    public AdoptionService(AdoptionRepository adoptionRepository) {
        this.adoptionRepository = adoptionRepository;
    }

    /**
     * Регистрирует новое усыновление.
     * Устанавливает дату усыновления, статус испытательного срока (например, "IN_TRIAL")
     * и рассчитывает дату окончания испытательного срока (например, через 30 дней).
     */
    public Adoption registerAdoption(User user, Long animalId) {
        Adoption adoption = new Adoption();
        adoption.setUser(user);
        // Здесь предполагается, что Animal будет установлен позже
        // adoption.setAnimal(animal);
        adoption.setAdoptionDate(LocalDate.now());
        adoption.setTrialStatus("IN_TRIAL");
        adoption.setTrialEndDate(LocalDate.now().plusDays(30));
        adoptionRepository.save(adoption);
        logger.info("Зарегистрировано усыновление для пользователя id {}.", user.getId());
        return adoption;
    }

    /**
     * Обновляет статус испытательного срока.
     */
    public void updateTrialStatus(Adoption adoption, String newStatus, int additionalDays) {
        adoption.setTrialStatus(newStatus);
        if (additionalDays > 0) {
            adoption.setTrialEndDate(adoption.getTrialEndDate().plusDays(additionalDays));
        }
        adoptionRepository.save(adoption);
        logger.info("Статус испытательного срока для усыновления id {} обновлён на {}.", adoption.getId(), newStatus);
    }
}
