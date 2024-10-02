package darts.ng.io.usersMicroservice.darts_app.entity;

import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserBlackListedDbModel;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlackListPaginationResModel {

    private List<UserBlackListedResponseModel> content; // List of blacklisted users
    private PageMetadata page; // Pagination metadata
    private Integer next_offset; // Next offset for pagination
    private Integer limit; // Limit for pagination

    @Data
    @Builder
    public static class PageMetadata {
        private PageInfo page_info; // Page information
        private Integer previous_offset; // Previous offset value

        @Data
        @Builder
        public static class PageInfo {
            private SelfLink self; // Self link information
            private Integer first; // First page number
            private Integer next; // Next page number
            private Integer previous; // Previous page number
            private Integer last; // Last page number
        }

        @Data
        @Builder
        public static class SelfLink {
            public Integer number; // Page number
            public String link; // Link for self-reference

            // Public constructor
            public SelfLink(Integer number, String link) {
                this.number = number;
                this.link = link;
            }
        }
    }

    @Data
    @Builder
    public static class Links {
        private String self; // Self link
        private String first; // First link
        private String next; // Next link
        private String previous; // Previous link
        private String last; // Last link
    }
}
