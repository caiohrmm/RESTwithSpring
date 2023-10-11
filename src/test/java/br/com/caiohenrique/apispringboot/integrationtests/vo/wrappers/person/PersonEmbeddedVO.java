package br.com.caiohenrique.apispringboot.integrationtests.vo.wrappers.person;

import br.com.caiohenrique.apispringboot.integrationtests.vo.entities.PersonVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PersonEmbeddedVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("personVOList")
    private List<PersonVO> persons;

    public List<PersonVO> getPersons() {
        return persons;
    }

    public PersonEmbeddedVO() {}

    public void setPersons(List<PersonVO> persons) {
        this.persons = persons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEmbeddedVO that = (PersonEmbeddedVO) o;
        return Objects.equals(persons, that.persons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons);
    }
}
