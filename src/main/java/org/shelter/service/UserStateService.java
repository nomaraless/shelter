package org.shelter.service;


import org.shelter.enums.UserStage;
import org.shelter.model.UserState;
import org.shelter.repository.UserStateRepository;
import org.springframework.stereotype.Service;

@Service
public class UserStateService {

    private final UserStateRepository userStateRepository;

    public UserStateService(UserStateRepository userStateRepository) {
        this.userStateRepository = userStateRepository;
    }

    /**
     * Устанавливает состояние для указанного chatId.
     */
    public void setStage(String chatId, UserStage stage) {
        UserState state = userStateRepository.findById(chatId)
                .orElse(new UserState(chatId, stage));
        state.setStage(stage);
        userStateRepository.save(state);
    }

    /**
     * Возвращает состояние для указанного chatId. Если не найдено – возвращает UserStage.NONE.
     */
    public UserStage getStage(String chatId) {
        return userStateRepository.findById(chatId)
                .map(UserState::getStage)
                .orElse(UserStage.NONE);
    }

    /**
     * Удаляет состояние для указанного chatId.
     */
    public void clearStage(String chatId) {
        userStateRepository.deleteById(chatId);
    }
}
