package com.mapo.personapp.person;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbPersonDataProvider implements PersonDataProvider {

    private final PersonRepository personRepository;

    public DbPersonDataProvider(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> all() {
        return personRepository.findAll();
    }

    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person findById(Long id) {
        return personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @Override
    public List<Person> findByColor(String color) {
        return personRepository.findByColor(color);
    }

    @Override
    public PersonDataProviderType getProviderType() {
        return PersonDataProviderType.DB;
    }
}
