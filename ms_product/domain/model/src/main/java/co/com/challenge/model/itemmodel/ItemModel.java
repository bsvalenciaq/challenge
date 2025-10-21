package co.com.challenge.model.itemmodel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemModel {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("price")
    private double price;

    @JsonProperty("currency_id")
    private String currencyId;

    @JsonProperty("available_quantity")
    private int availableQuantity;

    @JsonProperty("sold_quantity")
    private int soldQuantity;

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("category")
    private String category;

    @JsonProperty("free_shipping")
    private boolean freeShipping;

    @JsonProperty("rating")
    private double rating; // 0.0 a 5.0

    @JsonProperty("reviews")
    private int reviews;

    @JsonProperty("description")
    private String description;
}
