package com.mapo.personapp.person;

public class PersonNotFoundException  extends RuntimeException{
        PersonNotFoundException(Long id) {
            super("Could not find person " + id);
        }
}
