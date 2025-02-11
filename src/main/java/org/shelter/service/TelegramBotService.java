package org.shelter.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.shelter.enums.BotCommand;
import org.shelter.enums.Role;
import org.shelter.enums.UserStage;
import org.shelter.model.Animal;
import org.shelter.model.DailyReport;
import org.shelter.model.Shelter;
import org.shelter.model.User;
import org.shelter.repository.AnimalRepository;
import org.shelter.repository.ShelterRepository;
import org.shelter.repository.UserRepository;
import org.shelter.util.InlineKeyboardUtil;
import org.shelter.util.PhoneValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для работы с Telegram-ботом с использованием библиотеки Pengrad.
 * Реализует long polling с помощью интерфейса UpdatesListener.
 * Обрабатывает входящие обновления, команды и callback-запросы, взаимодействует с базой данных через репозитории,
 * а также управляет состоянием диалога пользователя.
 */
@Service
public class TelegramBotService implements UpdatesListener {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private TelegramBot telegramBot;

    // Зависимости
    private final UserRepository userRepository;
    private final ShelterRepository shelterRepository;
    private final AnimalRepository animalRepository;
    private final UserStateService userStateService;
    private final ReportService reportService;

    // Пример volunteerChatId
    private final String volunteerChatId = "@Volunteer";

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param options           объект настроек бота (DefaultBotOptions)
     * @param userRepository    репозиторий для пользователей
     * @param shelterRepository репозиторий для приютов
     * @param animalRepository  репозиторий для животных
     * @param userStateService  сервис управления состоянием пользователя
     * @param reportService     сервис для работы с отчетами
     * @param botUsername       имя бота, полученное из настроек
     * @param botToken          токен бота, полученный из настроек
     */
    public TelegramBotService(DefaultBotOptions options, UserRepository userRepository,
                              ShelterRepository shelterRepository,
                              AnimalRepository animalRepository,
                              UserStateService userStateService,
                              ReportService reportService, String botUsername, String botToken) {
        this.userRepository = userRepository;
        this.shelterRepository = shelterRepository;
        this.animalRepository = animalRepository;
        this.userStateService = userStateService;
        this.reportService = reportService;
    }

    /**
     * Инициализация бота после создания бина.
     * Создает объект TelegramBot и устанавливает слушатель обновлений.
     */
    @PostConstruct
    public void init() {
        telegramBot = new TelegramBot(botToken);
        telegramBot.setUpdatesListener(this);
        logger.info("Telegram Bot initialized. Bot username: {}", botUsername);
    }

    /**
     * Метод, вызываемый при получении списка обновлений.
     * Обрабатывает каждое обновление и возвращает значение, подтверждающее обработку всех обновлений.
     *
     * @param updates список полученных обновлений
     * @return UpdatesListener.CONFIRMED_UPDATES_ALL
     */
    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            logger.info("Получено обновление: {}", update);
            try {
                processUpdate(update);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /**
     * Основной метод обработки одного обновления.
     *
     * @param update входящее обновление
     * @throws InterruptedException, если поток прерван
     */
    public void processUpdate(Update update) throws InterruptedException {
        if (update.message() != null) {
            String chatId = String.valueOf(update.message().chat().id());

            // Если сообщение содержит фото и ожидается фото отчета
            if (update.message().photo() != null){
                if (userStateService.getStage(chatId) == UserStage.AWAIT_REPORT_PHOTO) {
                    processReportPhoto(chatId, update);
                    return;
                }
            }

            // Если сообщение содержит текст
            if (update.message().text() != null) {
                String messageText = update.message().text();
                UserStage stage = userStateService.getStage(chatId);
                if (stage == UserStage.AWAIT_PHONE) {
                    processPhoneInput(chatId, messageText);
                    return;
                } else if (stage == UserStage.AWAIT_REPORT_TEXT) {
                    processReportText(chatId, messageText);
                    return;
                }
                if (messageText.equalsIgnoreCase("/start")) {
                    sendWelcomeMessage(getOrCreateUser(chatId), chatId);
                    return;
                }
                sendTextMessage(chatId, "Пожалуйста, выберите вариант из меню.");
            }
        }
        // Обработка callback-запросов
        else if (update.callbackQuery() != null) {
            String callbackData = update.callbackQuery().data();
            String chatId = String.valueOf(update.callbackQuery().message().chat().id());

            BotCommand command = BotCommand.fromCallbackData(callbackData);
            if (command == null) {
                sendTextMessage(chatId, "Неверная команда. Выберите вариант из меню.");
                return;
            }
            Optional<User> optionalUser = userRepository.findByChatId(chatId);
            if (!optionalUser.isPresent()) {
                sendTextMessage(chatId, "Пользователь не найден. Начните с /start.");
                return;
            }
            User user = optionalUser.get();
            userStateService.clearStage(chatId);

            switch (command) {
                case INFO_SHELTER:
                    handleStage1(user, chatId);
                    break;
                case HOW_TO_ADOPT:
                    handleStage2(user, chatId);
                    break;
                case SEND_REPORT:
                    sendTextMessage(chatId, "Пришлите, пожалуйста, фото питомца.");
                    userStateService.setStage(chatId, UserStage.AWAIT_REPORT_PHOTO);
                    DailyReport report = new DailyReport();
                    report.setUserId(user);
                    reportService.saveReport(report);
                    break;
                case CALL_VOLUNTEER:
                    handleCallVolunteer(user, chatId);
                    break;
                default:
                    sendTextMessage(chatId, "Неверная команда. Выберите вариант из меню.");
                    break;
            }
        }
    }

    /**
     * Обрабатывает ввод номера телефона пользователем.
     *
     * @param chatId      идентификатор чата
     * @param messageText введенный текст
     */
    private void processPhoneInput(String chatId, String messageText) {
        if (PhoneValidator.isValid(messageText)) {
            Optional<User> optionalUser = userRepository.findByChatId(chatId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setPhone(messageText);
                userRepository.save(user);
                sendTextMessage(chatId, "Ваш номер телефона принят.");
                userStateService.clearStage(chatId);
                sendMainMenu(chatId);
            }
        } else {
            sendTextMessage(chatId, "Неверный формат номера телефона. Попробуйте ещё раз. Формат: +7-9XX-XXX-XXXX");
        }
    }

    /**
     * Отправляет главное меню с inline-клавиатурой.
     *
     * @param chatId идентификатор чата
     */
    private void sendMainMenu(String chatId) {
        SendMessage request = new SendMessage(chatId, "Выберите вариант из меню:")
                .replyMarkup(InlineKeyboardUtil.getMainMenuKeyboard());
        telegramBot.execute(request);
    }

    /**
     * Обрабатывает получение фото для отчёта.
     *
     * @param chatId идентификатор чата
     * @param update входящее обновление
     */
    private void processReportPhoto(String chatId, Update update) {
        Optional<User> optionalUser = userRepository.findByChatId(chatId);
        if (!optionalUser.isPresent()) {
            sendTextMessage(chatId, "Ошибка: пользователь не найден.");
            return;
        }
        User user = optionalUser.get();
        DailyReport report = reportService.getLatestReportForUser(user);
        if (report == null) {
            sendTextMessage(chatId, "Ошибка: предварительный отчёт не найден.");
            return;
        }
        List<PhotoSize> photos = List.of(update.message().photo());
        String fileId = photos.stream()
                .reduce((first, second) -> first.fileSize() > second.fileSize() ? first : second)
                .map(PhotoSize::fileId)
                .orElse(null);
        if (fileId != null) {
            report.setPhotoPath(fileId);
            report.setHasPhoto(true);
            reportService.saveReport(report);
            sendTextMessage(chatId, "Фото получено. Теперь пришлите, пожалуйста, текст отчёта.");
            userStateService.setStage(chatId, UserStage.AWAIT_REPORT_TEXT);
        } else {
            sendTextMessage(chatId, "Не удалось извлечь фото. Попробуйте отправить снова.");
        }
    }

    /**
     * Обрабатывает получение текстовой части отчёта.
     *
     * @param chatId      идентификатор чата
     * @param messageText текст отчёта
     */
    private void processReportText(String chatId, String messageText) {
        Optional<User> optionalUser = userRepository.findByChatId(chatId);
        if (!optionalUser.isPresent()) {
            sendTextMessage(chatId, "Ошибка: пользователь не найден.");
            return;
        }
        User user = optionalUser.get();
        DailyReport report = reportService.getLatestReportForUser(user);
        if (report == null) {
            sendTextMessage(chatId, "Ошибка: предварительный отчёт не найден.");
            return;
        }
        report.setTextReport(messageText);
        report.setHasText(true);
        reportService.saveReport(report);
        sendTextMessage(chatId, "Отчёт получен. Спасибо!");
        userStateService.clearStage(chatId);
        sendMainMenu(chatId);
    }

    /**
     * Отправляет приветственное сообщение и главное меню.
     *
     * @param user   объект пользователя
     * @param chatId идентификатор чата
     */
    private void sendWelcomeMessage(User user, String chatId) {
        String welcomeText = (user.getName() == null)
                ? "Привет! Я бот-помощник для приютов. Вы можете получить информацию о приюте, узнать, как взять животное, прислать отчёт или вызвать волонтёра.\nПожалуйста, пришлите номер телефона в формате: +7-9XX-XXX-XXXX-X"
                : "С возвращением! Выберите интересующий вас раздел.";
        sendTextMessage(chatId, welcomeText);
        if (user.getPhone() == null) {
            userStateService.setStage(chatId, UserStage.AWAIT_PHONE);
        }
        sendMainMenu(chatId);
    }

    /**
     * Обрабатывает этап 1 – вывод информации о приюте.
     *
     * @param user   объект пользователя
     * @param chatId идентификатор чата
     */
    private void handleStage1(User user, String chatId) {
        Optional<Shelter> shelterOpt = shelterRepository.findAll().stream().findFirst();
        if (!shelterOpt.isPresent()) {
            sendTextMessage(chatId, "Информация о приюте отсутствует. Обратитесь к волонтёру.");
            return;
        }
        Shelter shelter = shelterOpt.get();
        String info = String.format(
                "Информация о приюте \"%s\":\nАдрес: %s\nРежим работы: %s\nКонтакты охраны: %s\n\nРекомендации по технике безопасности:\n- Соблюдайте правила пропуска\n- На территории приюта запрещено шуметь\n- Следуйте инструкциям персонала",
                shelter.getName(), shelter.getAddress(), shelter.getWorkingHours(), shelter.getContacts()
        );
        sendTextMessage(chatId, info);
        if (shelter.getMapUrl() != null && !shelter.getMapUrl().isEmpty()) {
            try {
                SendPhoto photoMessage = new SendPhoto(chatId, shelter.getMapUrl())
                        .caption("Схема проезда к приюту");
                telegramBot.execute(photoMessage);
            } catch (Exception e) {
                logger.error("Ошибка отправки схемы проезда: ", e);
            }
        }
        sendTextMessage(chatId, "Если у вас возникли вопросы, вы можете оставить контакт для связи.");
        sendMainMenu(chatId);
    }

    /**
     * Обрабатывает этап 2 – консультацию для потенциального хозяина.
     *
     * @param user   объект пользователя
     * @param chatId идентификатор чата
     */
    private void handleStage2(User user, String chatId) {
        StringBuilder sb = new StringBuilder();
        sb.append("Как взять животное из приюта:\n")
                .append("1. Ознакомьтесь со списком животных для усыновления:\n");
        Optional<Shelter> shelterOpt = shelterRepository.findAll().stream().findFirst();
        if (shelterOpt.isPresent()) {
            List<Animal> animals = animalRepository.findByShelterId(shelterOpt.get().getId());
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
                .append("- При первой встрече с собакой сохраняйте спокойствие и уверенность\n")
                .append("- Дайте животному время адаптироваться\n")
                .append("6. Возможные причины отказа:\n")
                .append("- Несоответствие условий проживания\n")
                .append("- Неполный пакет документов\n")
                .append("- Недостаточная подготовка к уходу за животным\n")
                .append("Если у вас остались вопросы, вы можете вызвать волонтёра для консультации.");
        sendTextMessage(chatId, sb.toString());
        sendMainMenu(chatId);
    }

    /**
     * Обрабатывает вызов волонтёра.
     *
     * @param user   объект пользователя
     * @param chatId идентификатор чата
     */
    private void handleCallVolunteer(User user, String chatId) {
        sendTextMessage(chatId, "Пожалуйста, подождите, мы свяжемся с волонтёром (" + volunteerChatId + ").");
        logger.info("Пользователь с chatId {} вызвал волонтёра.", chatId);
        sendDirectMessage(volunteerChatId, "Пользователь с chatId " + chatId + " нуждается в помощи.");
    }

    /**
     * Отправляет текстовое сообщение в указанный чат.
     *
     * @param chatId идентификатор чата
     * @param text   текст сообщения
     */
    public void sendTextMessage(String chatId, String text) {
        SendMessage request = new SendMessage(chatId, text);
        try {
            telegramBot.execute(request);
            logger.info("Сообщение отправлено на chatId {}: {}", chatId, text);
        } catch (Exception e) {
            logger.error("Ошибка отправки сообщения на chatId {}: {}", chatId, e.getMessage(), e);
        }
    }

    /**
     * Отправляет уведомление (текстовое сообщение) в указанный чат.
     *
     * @param chatId идентификатор чата
     * @param text   текст уведомления
     */
    public void sendDirectMessage(String chatId, String text) {
        sendTextMessage(chatId, text);
    }

    /**
     * Находит пользователя по chatId или создает нового с ролью USER.
     *
     * @param chatId идентификатор чата
     * @return объект пользователя
     */
    private User getOrCreateUser(String chatId) {
        return userRepository.findByChatId(chatId).orElseGet(() -> {
            User newUser = new User();
            newUser.setChatId(chatId);
            newUser.setRole(Role.USER);
            return userRepository.save(newUser);
        });
    }

    /**
     * Возвращает идентификатор чата волонтёра.
     *
     * @return volunteerChatId
     */
    public String getVolunteerChatId() {
        return volunteerChatId;
    }
}
