package co.com.challenge.usecase.productsdata;

import co.com.challenge.model.itemmodel.ItemModel;
import co.com.challenge.model.reviewsmodel.ReviewsModel;
import co.com.challenge.model.utils.interfaces.ProductsDataUseCaseInterface;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ProductsDataUseCase implements ProductsDataUseCaseInterface {

    private static final Log log = LogFactory.getLog(ProductsDataUseCase.class);
    private final Logger logger = Logger.getLogger(ProductsDataUseCase.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private Map<String, ItemModel> items;
    private List<ReviewsModel> reviews;

    @Override
    public Map<String, Object> getProductById(String id) {
        loadProducts();
        var item = new ItemModel();
        String recommendation;
        if (items.containsKey(id)) {
            logger.info("Product with id: " + id);
            item = items.get(id);
            var bestReview = reviews.stream()
                    .filter(r -> id.equals(r.getItem_id()))
                    .max(java.util.Comparator.comparingDouble(ReviewsModel::getRating));

            recommendation = bestReview
                    .map(ReviewsModel::getComment)
                    .orElse("");
            return Map.of(
                    "item", item,
                    "best_recommendation", recommendation);
        }
        logger.info("Product with id: " + id + " not found");

        return null;
    }

    @Override
    public List<ReviewsModel> getRecomendationByProductId(String id) {
        loadProducts();

        logger.info("Getting recommendations for product id: " + id);
        return reviews.stream()
                .filter(review -> review.getItem_id().equals(id))
                .toList();
    }

    @Override
    public Map<String, Object> getAllProducts(String title, Double minPrice, Double maxPrice, String category, int page, int size) {
        loadProducts();

        if (size <= 0) {
            throw new IllegalArgumentException("El parámetro 'size' debe ser mayor que 0");
        }

        List<ItemModel> allItems = new ArrayList<>(items.values());

        logger.info("Filtering products with parameters - title: " + title + ", minPrice: " + minPrice +
                ", maxPrice: " + maxPrice + ", category: " + category + ", page: " + page + ", size: " + size);
        var combined = Stream.<java.util.function.Predicate<ItemModel>>of(
                (title != null && !title.isBlank())
                        ? item -> item.getTitle() != null && item.getTitle().toLowerCase(Locale.ROOT).contains(title.toLowerCase(Locale.ROOT))
                        : item -> true,
                (category != null && !category.isBlank())
                        ? item -> item.getCategory() != null && item.getCategory().equalsIgnoreCase(category)
                        : item -> true,
                (minPrice != null)
                        ? item -> item.getPrice() >= minPrice
                        : item -> true,
                (maxPrice != null)
                        ? item -> item.getPrice() <= maxPrice
                        : item -> true
        ).reduce(x -> true, java.util.function.Predicate::and);

        log.info("Combined filter created");
        List<ItemModel> filtered = allItems.stream()
                .filter(combined)
                .toList();

        int totalItems = filtered.size();
        int totalPages = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / size);

        if (page < 1) page = 1;
        if (page > totalPages) {
            throw new IllegalArgumentException("Página " + page + " fuera de rango. totalPages: " + totalPages);
        }

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);
        List<ItemModel> pagedItems = fromIndex >= totalItems ? List.of() : filtered.subList(fromIndex, toIndex);
        log.info("Pagination applied: fromIndex=" + fromIndex + ", toIndex=" + toIndex);
        return Map.of(
                "totalItems", totalItems,
                "page", page,
                "totalPages", totalPages,
                "size", size,
                "data", pagedItems
        );
    }


    public synchronized void loadProducts() {
        if (items != null && reviews != null) return;
        logger.info("Loading products and reviews data...");
        try {
            items = mapper.readValue(
                            new ClassPathResource("data/items.json").getInputStream(),
                            new TypeReference<List<ItemModel>>() {
                            })
                    .stream().collect(Collectors.toMap(ItemModel::getId, it -> it));


            reviews = readCsvReviews();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<ReviewsModel> readCsvReviews() {
        try {
            logger.info("Reading reviews from CSV file...");
            var resource = new ClassPathResource("data/reviews.csv");
            var reviewsList = new ArrayList<ReviewsModel>();
            try (var reader = resource.getInputStream();
                 var br = new java.io.BufferedReader(new java.io.InputStreamReader(reader))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Skip header line
                    }
                    String[] values = line.split(",");
                    if (values.length >= 4) {
                        ReviewsModel review = new ReviewsModel(
                                values[0],
                                values[1],
                                values[2],
                                Double.valueOf(values[3]),
                                values[4],
                                values[5],
                                values[6],
                                values[7],
                                values[8]
                        );
                        reviewsList.add(review);
                    }
                }
            }
            return reviewsList;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
