package com.mapo.personapp.person;

import java.util.List;

public interface PersonDataProvider {
    List<Person> all();
    Person save(Person person);
    Person findById(Long id);
    List<Person> findByColor(String color);
    PersonDataProviderType getProviderType();
}

