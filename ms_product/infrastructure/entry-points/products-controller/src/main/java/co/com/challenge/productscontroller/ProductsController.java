package co.com.challenge.productscontroller;


import co.com.challenge.model.utils.interfaces.JwtInterface;
import co.com.challenge.model.utils.interfaces.ProductsDataUseCaseInterface;
import co.com.challenge.model.utils.responses.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsDataUseCaseInterface productsDataUseCaseInterface;
    private final JwtInterface jwtInterface;

    @RequestMapping("/all-products")
    public ResponseEntity<ResponseUtil> getAllProducts(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        var token = request.getHeader("Authorization");
        if (token == null || !jwtInterface.validateToken(token)) {
            return new ResponseEntity<>(new ResponseUtil(401, "Unauthorized: Missing or invalid token", null), HttpStatus.UNAUTHORIZED);
        }

        try {
            Map<String, Object> result = productsDataUseCaseInterface.getAllProducts(title, minPrice, maxPrice, category, page, size);
            return new ResponseEntity<>(new ResponseUtil(200, "OK", result), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(new ResponseUtil(400, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResponseUtil(500, "Internal Server Error", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/product-by-id")
    public ResponseEntity<ResponseUtil> getProductById(@RequestParam(value = "id") String id, HttpServletRequest request) {

        var token = request.getHeader("Authorization");
        if (token == null || !jwtInterface.validateToken(token)) {
            return new ResponseEntity<>(new ResponseUtil(401, "Unauthorized: Missing or invalid token", null), HttpStatus.UNAUTHORIZED);
        }

        var product = productsDataUseCaseInterface.getProductById(id);
        if (product == null) {
            return new ResponseEntity<>(new ResponseUtil(404, "Product not found", null),
                    org.springframework.http.HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseUtil(200, "OK",
                product), org.springframework.http.HttpStatus.OK);
    }

    @RequestMapping("/recommendation-by-product-id")
    public ResponseEntity<ResponseUtil> getRecommendationByProductId(@RequestParam(value = "id") String id, HttpServletRequest request) {

        var token = request.getHeader("Authorization");
        if (token == null || !jwtInterface.validateToken(token)) {
            return new ResponseEntity<>(new ResponseUtil(401, "Unauthorized: Missing or invalid token", null), HttpStatus.UNAUTHORIZED);
        }

        var product = productsDataUseCaseInterface.getRecomendationByProductId(id);
        if (product == null || product.isEmpty()) {
            return new ResponseEntity<>(new ResponseUtil(404, "Product not found", null),
                    org.springframework.http.HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseUtil(200, "OK",
                product), org.springframework.http.HttpStatus.OK);
    }
}
