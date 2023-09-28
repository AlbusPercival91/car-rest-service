package ua.foxminded.car.microservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import ua.foxminded.car.microservice.entities.Category;
import ua.foxminded.car.microservice.service.CategoryService;
import ua.foxminded.university.dao.validation.InfoConstants;

@WebMvcTest({ CategoryController.class })
@ActiveProfiles("test-container")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void testCreateCategory_Success_ShouldGiveStatusIsOk() throws Exception {
        Category category = new Category("testCategory");
        category.setCategoryId(123);

        when(categoryService.createCategory(any(Category.class))).thenReturn(category.getCategoryId());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(category)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(category.getCategoryId()));
    }

    @Test
    void testCreateCategory_Failure_ShouldGiveStatusIsConflict() throws Exception {
        Category category = new Category("testCategory");

        when(categoryService.createCategory(any(Category.class)))
                .thenThrow(new EntityExistsException(InfoConstants.CATEGORY_EXISTS));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(category)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void testDeleteCategory_Success_ShouldGiveStatusIsNoContent() throws Exception {
        int categoryId = 123;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/{categoryId}", categoryId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testDeleteCategory_Failure_ShouldGiveStatusIsNotFound() throws Exception {
        int categoryId = 123;

        when(categoryService.deleteCategoryById(categoryId))
                .thenThrow(new EntityNotFoundException(InfoConstants.CATEGORY_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/{categoryId}", categoryId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testUpdateCategory_Success_ShouldGiveStatusIsOk() throws Exception {
        int categoryId = 123;
        Category updatedCategory = new Category("testCategory");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedCategory)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testUpdateCategory_Failure_ShouldGiveStatusIsNotFound() throws Exception {
        int categoryId = 123;
        Category updatedCategory = new Category("testCategory");

        when(categoryService.updateCategoryById(eq(categoryId), any(Category.class)))
                .thenThrow(new EntityNotFoundException(InfoConstants.CATEGORY_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedCategory)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testGetCategory_Success_ShouldGiveStatusIsOk() throws Exception {
        int categoryId = 123;

        when(categoryService.findCategoryById(categoryId)).thenReturn(Optional.of(new Category()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/{categoryId}", categoryId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testListCategories_Success_ShouldGiveStatusIsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testSearchCategories_Success_ShouldGiveStatusIsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/search"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @CsvSource({ "testCategory, 0, 10, make, asc" })
    void testSearchCategories_Failure_ShouldGiveStatusIsNotFound(String categoryName, int page, int size, String sortBy,
            String sortOrder) throws Exception {
        UUID carId = UUID.randomUUID();

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);

        when(categoryService.searchCategories(categoryName, carId, pageRequest))
                .thenThrow(new EntityNotFoundException(InfoConstants.CAR_NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/search").param("categoryName", categoryName)
                .param("carId", carId.toString()).param("page", String.valueOf(page))
                .param("size", String.valueOf(size)).param("sortBy", sortBy).param("sortOrder", sortOrder))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
