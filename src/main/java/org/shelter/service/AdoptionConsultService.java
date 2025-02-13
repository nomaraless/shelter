package org.shelter.service;

import org.shelter.model.Animal;
import org.shelter.model.Shelter;
import org.shelter.repository.AnimalRepository;
import org.shelter.repository.ShelterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdoptionConsultService {

    private static final Logger logger = LoggerFactory.getLogger(AdoptionConsultService.class);

    private final ShelterRepository shelterRepository;
    private final AnimalRepository animalRepository;
    private final MessageService messageService;

    public AdoptionConsultService(ShelterRepository shelterRepository,
                                       AnimalRepository animalRepository,
                                       MessageService messageService) {
        this.shelterRepository = shelterRepository;
        this.animalRepository = animalRepository;
        this.messageService = messageService;
    }

    public void handleAdoptionConsultation(String chatId, String shelterType) {
        StringBuilder sb = new StringBuilder();
        sb.append("Как взять животное из приюта:\n")
                .append("1. Ознакомьтесь со списком животных для усыновления:\n");
        Optional<Shelter> shelterOpt = shelterRepository.findByType(shelterType);
        if (shelterOpt.isPresent()) {
            Shelter shelter = shelterOpt.get();
            List<Animal> animals = animalRepository.findByShelterIdAndType(shelter.getId(), shelterType);
            if (animals.isEmpty()) {
                sb.append("В данный момент животных нет.\n");
            } else {
                String animalList = animals.stream()
                        .map(a -> String.format("- %s (%s, %d лет)", a.getName(), a.getType(), a.getAge()))
                        .collect(Collectors.joining("\n"));
                sb.append(animalList).append("\n");
            }
        }
        sb.append("2. Правила знакомства с животным:\n")
                .append("- Встреча происходит в специально отведённом месте приюта\n")
                .append("- Соблюдайте тишину и спокойствие\n")
                .append("3. Необходимые документы:\n")
                .append("- Паспорт, СНИЛС, ИНН, справка о месте жительства\n")
                .append("4. Рекомендации по транспортировке и обустройству дома:\n")
                .append("- Используйте специальный переносной контейнер\n")
                .append("- Подготовьте безопасное место для животного\n")
                .append("5. Советы кинолога:\n")
                .append("- При первой встрече с животным сохраняйте спокойствие и уверенность\n")
                .append("- Дайте животному время адаптироваться\n")
                .append("6. Возможные причины отказа:\n")
                .append("- Несоответствие условий проживания\n")
                .append("- Неполный пакет документов\n")
                .append("- Недостаточная подготовка к уходу за животным\n")
                .append("Если у вас остались вопросы, вы можете вызвать волонтёра для консультации.");
        messageService.sendTextMessage(chatId, sb.toString());
    }
}
