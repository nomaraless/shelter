package org.shelter.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "adoptions")
public class Adoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Пользователь, который усыновил животное
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Животное, которое усыновлено
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    // Дата заключения договора об усыновлении
    @Column(name = "adoption_date")
    private LocalDate adoptionDate;

    // Статус испытательного срока: IN_TRIAL, PASSED, EXTENDED, FAILED
    @Column(name = "trial_status")
    private String trialStatus;

    // Дата окончания испытательного срока (если применяется)
    @Column(name = "trial_end_date")
    private LocalDate trialEndDate;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Animal getAnimal() {
        return animal;
    }
    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
    public LocalDate getAdoptionDate() {
        return adoptionDate;
    }
    public void setAdoptionDate(LocalDate adoptionDate) {
        this.adoptionDate = adoptionDate;
    }
    public String getTrialStatus() {
        return trialStatus;
    }
    public void setTrialStatus(String trialStatus) {
        this.trialStatus = trialStatus;
    }
    public LocalDate getTrialEndDate() {
        return trialEndDate;
    }
    public void setTrialEndDate(LocalDate trialEndDate) {
        this.trialEndDate = trialEndDate;
    }
}
