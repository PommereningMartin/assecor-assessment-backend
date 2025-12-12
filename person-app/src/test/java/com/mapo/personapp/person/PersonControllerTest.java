package com.mapo.personapp.person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private MockMvc mockMvc;

    private Person testPerson1;
    private Person testPerson2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();

        testPerson1 = new Person("John", "Doe", "12345", "Berlin", "blau");
        testPerson1.setId(1L);

        testPerson2 = new Person("Jane", "Smith", "54321", "Munich", "rot");
        testPerson2.setId(2L);
    }

    @Test
    void all_ShouldReturnListOfPersons() throws Exception {
        List<Person> persons = Arrays.asList(testPerson1, testPerson2);
        when(personService.all()).thenReturn(persons);

        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstname").value("John"))
                .andExpect(jsonPath("$[0].lastname").value("Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstname").value("Jane"));

        verify(personService).all();
    }

    @Test
    void all_ShouldReturnEmptyListWhenNoPersons() throws Exception {
        when(personService.all()).thenReturn(List.of());

        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(personService).all();
    }

    @Test
    void byId_ShouldReturnPersonWhenFound() throws Exception {
        when(personService.findById(1L)).thenReturn(testPerson1);

        mockMvc.perform(get("/persons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.zipcode").value("12345"))
                .andExpect(jsonPath("$.city").value("Berlin"))
                .andExpect(jsonPath("$.color").value("blau"));

        verify(personService).findById(1L);
    }

    @Test
    void byColor_ShouldReturnPersonsWithMatchingColor() throws Exception {
        List<Person> bluPersons = List.of(testPerson1);
        when(personService.findByColor("blau")).thenReturn(bluPersons);

        mockMvc.perform(get("/persons/color/blau"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].color").value("blau"))
                .andExpect(jsonPath("$[0].firstname").value("John"));

        verify(personService).findByColor("blau");
    }

    @Test
    void byColor_ShouldReturnEmptyListWhenNoMatch() throws Exception {
        when(personService.findByColor("grün")).thenReturn(List.of());

        mockMvc.perform(get("/persons/color/grün"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(personService).findByColor("grün");
    }

    @Test
    void newEmployee_ShouldCreateAndReturnPerson() throws Exception {
        Person savedPerson = new Person("Alice", "Johnson", "11111", "Hamburg", "gelb");
        savedPerson.setId(3L);

        when(personService.save(any(Person.class))).thenReturn(savedPerson);

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstname\":\"Alice\",\"lastname\":\"Johnson\",\"zipcode\":\"11111\",\"city\":\"Hamburg\",\"color\":\"gelb\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstname").value("Alice"))
                .andExpect(jsonPath("$.lastname").value("Johnson"))
                .andExpect(jsonPath("$.zipcode").value("11111"))
                .andExpect(jsonPath("$.city").value("Hamburg"))
                .andExpect(jsonPath("$.color").value("gelb"));

        verify(personService).save(any(Person.class));
    }
}
