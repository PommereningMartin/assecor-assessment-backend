package com.mapo.personapp.person;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue
    private Long id;

    private String firstname;
    private String lastname;
    private String zipcode;
    private String city;
    private String color;

    public Person(String firstname, String lastname, String zipcode, String city, String color) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.zipcode = zipcode;
        this.city = city;
        this.color = color;
    }
}
