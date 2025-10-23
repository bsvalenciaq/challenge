package co.com.challenge.model.utils.interfaces;

import co.com.challenge.model.reviewsmodel.ReviewsModel;

import java.util.List;
import java.util.Map;

public interface ProductsDataUseCaseInterface {

    Object getProductById(String id);

    List<ReviewsModel> getRecomendationByProductId(String id);

    Map<String, Object> getAllProducts(String title, Double minPrice, Double maxPrice, String category, int page, int size);
}
