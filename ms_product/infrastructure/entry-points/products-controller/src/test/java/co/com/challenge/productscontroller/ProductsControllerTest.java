package co.com.challenge.productscontroller;

import co.com.challenge.model.reviewsmodel.ReviewsModel;
import co.com.challenge.model.utils.interfaces.ProductsDataUseCaseInterface;
import co.com.challenge.model.utils.responses.ResponseUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    private ProductsDataUseCaseInterface useCase;

    @InjectMocks
    private ProductsController controller;

    @Test
    void getAllProductsSuccess() {
        Map<String, Object> result = Map.of(
                "total", 1,
                "items", List.of(Map.of("id", "1", "title", "phone"))
        );

        when(useCase.getAllProducts(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(result);

        ResponseEntity<ResponseUtil> response = controller.getAllProducts("phone", null, null, null, 1, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("OK", response.getBody().getMessage());
        assertEquals(result, response.getBody().getData());
    }

    @Test
    void getAllProductsBadRequestOnIllegalArgument() {
        when(useCase.getAllProducts(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Parámetros inválidos"));

        ResponseEntity<ResponseUtil> response = controller.getAllProducts(null, -10.0, null, null, 1, 10);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Parámetros inválidos", response.getBody().getMessage());
    }

    @Test
    void getProductByIdFound() {
        Map<String, Object> product = Map.of("id", "123", "title", "test");
        when(useCase.getProductById(eq("123"))).thenReturn(product);

        ResponseEntity<ResponseUtil> response = controller.getProductById("123");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(product, response.getBody().getData());
    }

    @Test
    void getProductByIdNotFound() {
        when(useCase.getProductById(eq("missing"))).thenReturn(null);

        ResponseEntity<ResponseUtil> response = controller.getProductById("missing");

        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Product not found", response.getBody().getMessage());
    }

// @Test
// void getRecommendationByProductIdFound() {
//     List<ReviewsModel> recs = List.of((ReviewsModel) Map.of("id", "r1"));
//     when(useCase.getRecomendationByProductId(eq("123"))).thenReturn(recs);

//     ResponseEntity<ResponseUtil> response = controller.getRecommendationByProductId("123");

//     assertEquals(200, response.getStatusCodeValue());
//     assertNotNull(response.getBody());
//     assertEquals(recs, response.getBody().getData());
// }

    @Test
    void getRecommendationByProductIdNotFound() {
        when(useCase.getRecomendationByProductId(eq("missing"))).thenReturn(null);

        ResponseEntity<ResponseUtil> response = controller.getRecommendationByProductId("missing");

        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Product not found", response.getBody().getMessage());
    }
}