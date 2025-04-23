package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    private UUID id;

    @Column(name = "first_name")
    @NotEmpty
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    private String lastName;

    @Column(name = "firebase_id")
    private String firebaseId;

    @OneToOne(mappedBy = "personalForUser")
    private Organization personalOrganization;

    public User() {
        this.id = UUID.randomUUID();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public Organization getPersonalOrganization() {
        return personalOrganization;
    }

    public void setPersonalOrganization(Organization personalOrganization) {
        this.personalOrganization = personalOrganization;
    }
}
