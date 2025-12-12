package com.mapo.personapp.person;

import org.apache.catalina.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {
    private final PersonService personService;

    PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/persons")
    List<Person> all() {
        return personService.all();
    }

    @GetMapping("/persons/{id}")
    Person ById(@PathVariable Long id) throws PersonNotFoundException {
        return personService.findById(id);
    }

    @GetMapping("/persons/color/{color}")
    List<Person> byColor(@PathVariable String color) {
        return personService.findByColor(color);
    }

    @PostMapping("/persons")
    Person newEmployee(@RequestBody Person newPerson) {
        return personService.save(newPerson);
    }
}
