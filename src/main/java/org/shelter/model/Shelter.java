package org.shelter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shelters")
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название приюта
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "type")
    private String type;

    // Адрес, расписание, контакты
    @Column(name = "address")
    private String address;

    @Column(name = "working_hours")
    private String workingHours;

    @Column(name = "contacts")
    private String contacts;

    // Путь к схеме проезда (или URL картинки)
    @Column(name = "map_url")
    private String mapUrl;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getWorkingHours() {
        return workingHours;
    }
    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
    public String getContacts() {
        return contacts;
    }
    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    public String getMapUrl() {
        return mapUrl;
    }
    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }
}

