package com.service.inspection.mapper;

import com.service.inspection.dto.inspection.CategoryWithFile;
import com.service.inspection.entities.Category;
import com.service.inspection.entities.Inspection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CategoryMapperImpl.class, PhotoMapperImpl.class})
class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @MockBean
    private PhotoMapper photoMapper;

    @Test
    void whenMapToCategory_givenNameAndInspection_shouldReturnCategory() {
        // Arrange
        String name = "Safety";
        Inspection inspection = new Inspection(); // Assuming Inspection class has an empty constructor or is otherwise instantiated

        // Act
        Category result = categoryMapper.mapToCategory(name, inspection);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getInspection()).isEqualTo(inspection);
    }

    @Test
    void whenMapToCategory_givenNullNameAndInspection_shouldReturnNull() {
        // Act
        Category result = categoryMapper.mapToCategory((String) null, null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void whenMapToCategory_givenCategoryAndName_shouldUpdateCategory() {
        // Arrange
        Category category = new Category();
        String name = "Quality";

        // Act
        categoryMapper.mapToCategory(category, name);

        // Assert
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    void whenMapToCategory_givenCategoryAndNullName_shouldNotUpdateCategory() {
        // Arrange
        Category category = new Category();
        category.setName("OriginalName");

        // Act
        categoryMapper.mapToCategory(category, null);

        // Assert
        assertThat(category.getName()).isEqualTo("OriginalName");
    }

    @Test
    void whenMapToCategoryWithFile_givenListOfCategories_shouldReturnListOfCategoryWithFile() {
        // Arrange
        List<Category> categories = Arrays.asList(new Category(), new Category()); // Assuming Category class has an empty constructor or is otherwise instantiated

        // Act
        List<CategoryWithFile> result = categoryMapper.mapToCategoryWithFile(categories);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(categories.size());
        // Additional assertions would depend on the implementation of categoryToCategoryWithFile and photoListToPhotoDtoSet methods
    }

    @Test
    void whenMapToCategoryWithFile_givenNullListOfCategories_shouldReturnNull() {
        // Act
        List<CategoryWithFile> result = categoryMapper.mapToCategoryWithFile(null);

        // Assert
        assertThat(result).isNull();
    }
}
