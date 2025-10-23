package co.com.challenge.productscontroller;

import co.com.challenge.model.utils.interfaces.JwtInterface;
import co.com.challenge.model.utils.interfaces.ProductsDataUseCaseInterface;
import co.com.challenge.model.utils.responses.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    @Mock
    private HttpServletRequest request;

    @Mock
    private JwtInterface jwtInterface;

    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGFsbGVuZ2UiLCJlbWFpbCI6ImNoYWxsZW5nZUBjb3JyZW8uY29tIiwiaWF0IjoxNzYxMTc2NjY1LCJleHAiOjE3NjEyNjMwNjV9.BwGlzZSlBLAQJl1-X0_XvWPvlq8leaD_9rubKbxXJhs";

    @Test
    void getAllProductsSuccess() {
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtInterface.validateToken(token)).thenReturn(true);

        Map<String, Object> result = Map.of(
                "total", 1,
                "items", List.of(Map.of("id", "1", "title", "phone"))
        );

        when(useCase.getAllProducts(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(result);

        ResponseEntity<ResponseUtil> response = controller.getAllProducts("phone", null, null, null, 1, 10, request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("OK", response.getBody().getMessage());
        assertEquals(result, response.getBody().getData());
    }

    @Test
    void getAllProductsBadRequestOnIllegalArgument() {
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtInterface.validateToken(token)).thenReturn(true);
        when(useCase.getAllProducts(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Parámetros inválidos"));

        ResponseEntity<ResponseUtil> response = controller.getAllProducts(null, -10.0, null, null, 1, 10, request);

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Parámetros inválidos", response.getBody().getMessage());
    }

    @Test
    void getProductByIdFound() {
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtInterface.validateToken(token)).thenReturn(true);
        Map<String, Object> product = Map.of("id", "123", "title", "test");
        when(useCase.getProductById(eq("123"))).thenReturn(product);

        ResponseEntity<ResponseUtil> response = controller.getProductById("123", request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(product, response.getBody().getData());
    }

    @Test
    void getProductByIdNotFound() {
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtInterface.validateToken(token)).thenReturn(true);
        when(useCase.getProductById(eq("missing"))).thenReturn(null);

        ResponseEntity<ResponseUtil> response = controller.getProductById("missing", request);

        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Product not found", response.getBody().getMessage());
    }


    @Test
    void getRecommendationByProductIdNotFound() {
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtInterface.validateToken(token)).thenReturn(true);
        when(useCase.getRecomendationByProductId(eq("missing"))).thenReturn(null);

        ResponseEntity<ResponseUtil> response = controller.getRecommendationByProductId("missing", request);

        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Product not found", response.getBody().getMessage());
    }
}