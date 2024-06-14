package com.service.inspection.mapper;

import com.service.inspection.dto.IdentifiableDto;
import com.service.inspection.dto.NamedDto;
import com.service.inspection.entities.Identifiable;
import com.service.inspection.entities.Named;
import org.springframework.beans.factory.annotation.Autowired;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CommonMapperImpl.class})
class CommonMapperTest {

    @Autowired
    private CommonMapper commonMapper;

    @Test
    void whenMapToIdentifiableDto_givenIdentifiable_shouldReturnIdentifiableDto() {
        // Arrange
        Identifiable identifiable = new Identifiable();
        identifiable.setId(1L);

        // Act
        IdentifiableDto result = commonMapper.mapToIdentifiableDto(identifiable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void whenMapToIdentifiableDto_givenNullIdentifiable_shouldReturnNull() {
        // Act
        IdentifiableDto result = commonMapper.mapToIdentifiableDto(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToNamedDto_givenNamed_shouldReturnNamedDto() {
        // Arrange
        Named named = new Named();
        named.setId(1L);
        named.setName("NamedEntity");

        // Act
        NamedDto result = commonMapper.mapToNamedDto(named);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("NamedEntity");
    }

    @Test
    void whenMapToNamedDto_givenNullNamed_shouldReturnNull() {
        NamedDto result = commonMapper.mapToNamedDto((Named) null);

        assertThat(result).isNull();
    }

    @Test
    void whenMapToNamedDto_givenCollectionOfNamed_shouldReturnCollectionOfNamedDto() {
        Named named = new Named();
        named.setId(1L);
        named.setName("FirstEntity");

        Named named2 = new Named();
        named2.setId(2L);
        named2.setName("SecondEntity");

        // Arrange
        Collection<Named> namedCollection = Arrays.asList(named, named2);

        // Act
        Collection<NamedDto> result = commonMapper.mapToNamedDto(namedCollection);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").containsExactly(1L, 2L);
        assertThat(result).extracting("name").containsExactly("FirstEntity", "SecondEntity");
    }

    @Test
    void whenMapToNamedDto_givenNullCollectionOfNamed_shouldReturnNull() {
        // Act
        Collection<NamedDto> result = commonMapper.mapToNamedDto((Collection<Named>) null);

        // Assert
        assertThat(result).isNull();
    }
}
