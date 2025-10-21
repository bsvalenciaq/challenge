package co.com.challenge.model.reviewsmodel;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReviewsModel {

    private String review_id;
    private String item_id;
    private String user_name;
    private Double rating;
    private String title;
    private String comment;
    private String created_at;
    private String helpful_votes;
    private String verified_purchase;

}
