package org.example.admissionsecretary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Модульные тесты для класса ApplicantService.
 * Проверяют корректность бизнес-логики ранжирования абитуриентов
 * без обращения к реальной базе данных.
 */
@ExtendWith(MockitoExtension.class)
public class ApplicantServiceTest {

    @Mock
    private ApplicantDAO applicantDAO;

    @Mock
    private AdmissionListDAO listDAO;

    private ApplicantService service;
    private AdmissionList testList;

    @BeforeEach
    void setUp() {
        service = new ApplicantService(applicantDAO, listDAO);

        testList = new AdmissionList();
        testList.setId(1);
        testList.setName("Тест");
        testList.setSpecialityName("Информатика и ВТ");
        testList.setTotalPlaces(3);
        testList.setTargetPlaces(1);
        testList.setPriorityPlaces(1);
    }

    /**
     * Тест 1: Целевик проходит по целевой квоте несмотря на низкий балл.
     * Ожидание: целевик со 100 баллами получает статус "Проходит".
     */
    @Test
    void targetApplicantShouldPassWithinQuota() throws SQLException {
        Applicant target  = createApplicant(1, true, false, 100);
        Applicant regular = createApplicant(2, false, false, 290);

        when(listDAO.getAll()).thenReturn(List.of(testList));
        when(applicantDAO.getByListId(1)).thenReturn(Arrays.asList(target, regular));

        service.updateStatuses(1);

        verify(applicantDAO).updateStatus(1, "Проходит");
        verify(applicantDAO).updateStatus(2, "Проходит"); // общий конкурс — 1 место осталось
    }

    /**
     * Тест 2: Обычный абитуриент с высоким баллом не вытесняет целевика и льготника
     * если все места заняты квотами.
     */
    @Test
    void regularApplicantShouldNotPassIfQuotasFull() throws SQLException {
        Applicant target   = createApplicant(1, true, false, 100);
        Applicant priority = createApplicant(2, false, true, 150);
        Applicant regular  = createApplicant(3, false, false, 290);

        testList.setTotalPlaces(2);
        testList.setTargetPlaces(1);
        testList.setPriorityPlaces(1);

        when(listDAO.getAll()).thenReturn(List.of(testList));
        when(applicantDAO.getByListId(1)).thenReturn(Arrays.asList(target, priority, regular));

        service.updateStatuses(1);

        verify(applicantDAO).updateStatus(1, "Проходит");
        verify(applicantDAO).updateStatus(2, "Проходит");
        verify(applicantDAO).updateStatus(3, "Не проходит");
    }

    /**
     * Тест 3: Свободные места целевой квоты переходят в общий конкурс.
     * Если целевиков нет — их место занимают обычные абитуриенты.
     */
    @Test
    void freeTargetPlacesShouldGoToGeneralCompetition() throws SQLException {
        Applicant regular1 = createApplicant(1, false, false, 250);
        Applicant regular2 = createApplicant(2, false, false, 200);
        Applicant regular3 = createApplicant(3, false, false, 150);

        testList.setTotalPlaces(3);
        testList.setTargetPlaces(1);
        testList.setPriorityPlaces(0);

        when(listDAO.getAll()).thenReturn(List.of(testList));
        when(applicantDAO.getByListId(1)).thenReturn(Arrays.asList(regular1, regular2, regular3));

        service.updateStatuses(1);

        verify(applicantDAO).updateStatus(1, "Проходит");
        verify(applicantDAO).updateStatus(2, "Проходит");
        verify(applicantDAO).updateStatus(3, "Не проходит");
    }

    /**
     * Тест 4: Целевик не прошедший по квоте идёт в общий конкурс.
     */
    @Test
    void targetOverflowShouldGoToGeneralCompetition() throws SQLException {
        Applicant target1 = createApplicant(1, true, false, 280);
        Applicant target2 = createApplicant(2, true, false, 200);
        Applicant regular = createApplicant(3, false, false, 100);

        testList.setTotalPlaces(3);
        testList.setTargetPlaces(1);
        testList.setPriorityPlaces(0);

        when(listDAO.getAll()).thenReturn(List.of(testList));
        when(applicantDAO.getByListId(1)).thenReturn(Arrays.asList(target1, target2, regular));

        service.updateStatuses(1);

        verify(applicantDAO).updateStatus(1, "Проходит"); // целевая квота
        verify(applicantDAO).updateStatus(2, "Проходит"); // общий конкурс
        verify(applicantDAO).updateStatus(3, "Проходит"); // общий конкурс
    }

    /**
     * Вспомогательный метод для создания тестового абитуриента.
     *
     * @param id уникальный идентификатор
     * @param isTarget признак целевого набора
     * @param isPriority признак льготной категории
     * @param totalScore суммарный балл
     * @return объект абитуриента
     */
    private Applicant createApplicant(int id, boolean isTarget,
                                      boolean isPriority, int totalScore) {
        Applicant a = new Applicant();
        a.setId(id);
        a.setFullName("Тест " + id);
        a.setEmail("test" + id + "@mail.ru");
        a.setListId(1);
        a.setTarget(isTarget);
        a.setPriority(isPriority);
        a.setTotalScore(totalScore);
        a.setStatus("Ожидает");
        return a;
    }
}