package org.example.admissionsecretary;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервисный класс, реализующий бизнес-логику работы с абитуриентами.
 * <p>
 * Обеспечивает добавление заявлений, автоматическое ранжирование абитуриентов
 * по системе приоритетов (целевой набор → льготники → общий конкурс),
 * отправку email-уведомлений и формирование статистики по спискам.
 * </p>
 */
public class ApplicantService {

    private final ApplicantDAO applicantDAO;
    private final AdmissionListDAO listDAO;

    // Конструктор для реального использования
    public ApplicantService() {
        this.applicantDAO = new ApplicantDAO();
        this.listDAO = new AdmissionListDAO();
    }

    // Конструктор для тестов
    public ApplicantService(ApplicantDAO applicantDAO, AdmissionListDAO listDAO) {
        this.applicantDAO = applicantDAO;
        this.listDAO = listDAO;
    }

    /**
     * Добавляет нового абитуриента в список, отправляет email-уведомление
     * и запускает автоматическое пересчёт статусов всех абитуриентов списка.
     *
     * @param a объект абитуриента с заполненными данными
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void addApplicant(Applicant a) throws SQLException {
        applicantDAO.add(a);

        String speciality = listDAO.getAll()
                .stream()
                .filter(l -> l.getId() == a.getListId())
                .map(AdmissionList::getSpecialityName)
                .findFirst()
                .orElse("Неизвестно");

        EmailService.sendApplicationConfirmation(a.getEmail(), a.getFullName(), speciality);

        updateStatuses(a.getListId());
    }

    /**
     * Возвращает ранжированный список абитуриентов для указанного списка специальности.
     * Сортировка выполняется на стороне базы данных:
     * сначала целевики, затем льготники, затем остальные — по убыванию суммарного балла.
     *
     * @param listId идентификатор списка специальности
     * @return отсортированный список абитуриентов
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public List<Applicant> getRankedApplicants(int listId) throws SQLException {
        return applicantDAO.getByListId(listId);
    }

    /**
     * Пересчитывает статусы всех абитуриентов указанного списка специальности
     * в соответствии с системой приоритетов и квотами мест.
     * <p>
     * Алгоритм ранжирования:
     * <ol>
     *   <li>Все абитуриенты получают статус «Не проходит».</li>
     *   <li>Целевики распределяются по целевой квоте (по убыванию балла).
     *       Не прошедшие по квоте переходят в общий конкурс.</li>
     *   <li>Льготники распределяются по льготной квоте (по убыванию балла).
     *       Не прошедшие по квоте переходят в общий конкурс.</li>
     *   <li>Незаполненные места из целевой и льготной квот добавляются
     *       к местам общего конкурса.</li>
     *   <li>Оставшиеся места распределяются по общему конкурсу
     *       (все остальные + overflow) по убыванию суммарного балла.</li>
     * </ol>
     * </p>
     *
     * @param listId идентификатор списка специальности
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updateStatuses(int listId) throws SQLException {
        AdmissionList list = listDAO.getAll()
                .stream()
                .filter(l -> l.getId() == listId)
                .findFirst()
                .orElse(null);

        if (list == null) return;

        List<Applicant> applicants = applicantDAO.getByListId(listId);

        int targetPlaces   = list.getTargetPlaces();
        int priorityPlaces = list.getPriorityPlaces();
        int totalPlaces    = list.getTotalPlaces();

        // Шаг 1: всем присваиваем "Не проходит"
        for (Applicant a : applicants) {
            applicantDAO.updateStatus(a.getId(), "Не проходит");
        }

        // Шаг 2: целевая квота
        List<Applicant> targets = applicants.stream()
                .filter(Applicant::isTarget)
                .sorted((a, b) -> b.getTotalScore() - a.getTotalScore())
                .collect(java.util.stream.Collectors.toList());

        int targetAccepted = 0;
        List<Applicant> targetOverflow = new java.util.ArrayList<>();
        for (Applicant a : targets) {
            if (targetAccepted < targetPlaces) {
                applicantDAO.updateStatus(a.getId(), "Проходит");
                targetAccepted++;
            } else {
                targetOverflow.add(a);
            }
        }

        // Шаг 3: льготная квота
        List<Applicant> priorities = applicants.stream()
                .filter(Applicant::isPriority)
                .sorted((a, b) -> b.getTotalScore() - a.getTotalScore())
                .collect(java.util.stream.Collectors.toList());

        int priorityAccepted = 0;
        List<Applicant> priorityOverflow = new java.util.ArrayList<>();
        for (Applicant a : priorities) {
            if (priorityAccepted < priorityPlaces) {
                applicantDAO.updateStatus(a.getId(), "Проходит");
                priorityAccepted++;
            } else {
                priorityOverflow.add(a);
            }
        }

        // Шаг 4: общий конкурс + свободные места из квот
        int freeTarget    = targetPlaces - targetAccepted;
        int freePriority  = priorityPlaces - priorityAccepted;
        int generalPlaces = (totalPlaces - targetPlaces - priorityPlaces)
                + freeTarget + freePriority;

        List<Applicant> general = applicants.stream()
                .filter(a -> !a.isTarget() && !a.isPriority())
                .collect(java.util.stream.Collectors.toList());
        general.addAll(targetOverflow);
        general.addAll(priorityOverflow);
        general.sort((a, b) -> b.getTotalScore() - a.getTotalScore());

        int generalAccepted = 0;
        for (Applicant a : general) {
            if (generalAccepted < generalPlaces) {
                applicantDAO.updateStatus(a.getId(), "Проходит");
                generalAccepted++;
            }
        }
    }

    /**
     * Формирует строку со статистикой по указанному списку специальности.
     * Возвращает общее количество заявок, количество проходящих,
     * не проходящих и ожидающих абитуриентов.
     *
     * @param listId идентификатор списка специальности
     * @return строка со статистикой в формате:
     *         "Всего заявок: N\nПроходит: N\nНе проходит: N\nОжидает: N"
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public String getStatistics(int listId) throws SQLException {
        List<Applicant> applicants = applicantDAO.getByListId(listId);
        long total    = applicants.size();
        long accepted = applicants.stream().filter(a -> "Проходит".equals(a.getStatus())).count();
        long rejected = applicants.stream().filter(a -> "Не проходит".equals(a.getStatus())).count();
        long waiting  = applicants.stream().filter(a -> "Ожидает".equals(a.getStatus())).count();

        return String.format(
                "Всего заявок: %d\nПроходит: %d\nНе проходит: %d\nОжидает: %d",
                total, accepted, rejected, waiting
        );
    }
}