package darts.ng.io.usersMicroservice.darts_app.service.blacklist_service;

import darts.ng.io.usersMicroservice.darts_app.entity.UserBlackListPaginationResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.UserBlackListedResponseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserBlackListedDbModel;
import darts.ng.io.usersMicroservice.darts_app.repository.UserBlackListedRepo;
import darts.ng.io.usersMicroservice.utilities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GetBlacklistEntryServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(GetBlacklistEntryServiceImpl.class);
    private final UserBlackListedRepo userBlackListedRepo;

    public GetBlacklistEntryServiceImpl(UserBlackListedRepo userBlackListedRepo) {
        this.userBlackListedRepo = userBlackListedRepo;
    }

    public ResponseEntity<UserBlackListPaginationResModel> getBlackListEntry(Integer user_id, int offset, Integer limit, String token) {

        if(limit==null){
            limit = 10;
        }

        if (limit < 1 || limit > 100) {
            throw new CustomRuntimeException(
                    new ErrorHandler(false, "Limit error", "A limit on the number of objects returned. Limit can range between 1 and 100, and the default is 10."),
                    HttpStatus.BAD_REQUEST
            );
        }

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Optional<Page<UserBlackListedDbModel>> result = userBlackListedRepo.findByUserId(user_id, pageable);

        if (result.isPresent()) {
            Page<UserBlackListedDbModel> page = result.get();

            List<UserBlackListedResponseModel> responseList = page.getContent().stream()
                    .map(blackListedDbModel -> new UserBlackListedResponseModel(
                            blackListedDbModel.getUserId(), // Map the user_id
                            blackListedDbModel.getIp_address(),
                            blackListedDbModel.getReason(),
                            blackListedDbModel.getIs_active(),
                            blackListedDbModel.getCreated_at(),
                            blackListedDbModel.getExpiry_at()
                    ))
                    .collect(Collectors.toList());

            // Calculate the previous offset
            Integer previousOffset = page.hasPrevious() ? (page.getNumber() - 1) * page.getSize() : null;

            UserBlackListPaginationResModel.PageMetadata pageMetadata = UserBlackListPaginationResModel.PageMetadata.builder()
                    .page_info(UserBlackListPaginationResModel.PageMetadata.PageInfo.builder()
                            .self(new UserBlackListPaginationResModel.PageMetadata.SelfLink(
                                    page.getNumber() + 1, // Page number (1-based)
                                    getSelfLink(user_id.toString(), offset, limit) // Generate the self link
                            ))
                            .first(1) // First page
                            .next(page.hasNext() ? page.getNumber() + 2 : null) // Next page
                            .previous(previousOffset != null ? page.getNumber() : null) // Previous page number
                            .last(page.getTotalPages()) // Last page
                            .build())
                    .previous_offset(previousOffset) // Include previous offset
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(
                    new UserBlackListPaginationResModel(
                            responseList, // Return the mapped response list
                            pageMetadata,
                            getNextOffset(offset, limit, page),
                            limit
                    ));
        }

        throw new CustomRuntimeException(
                new ErrorHandler(false, "error saving record into database", "Kindly visit the support team"),
                HttpStatus.BAD_REQUEST
        );
    }

    // Extracted methods
    private String getSelfLink(String user_id, int offset, int limit) {
        return  user_id + "?offset=" + offset + "&limit=" + limit;
    }

    private Integer getNextOffset(int offset, int limit, Page page) {
        return page.hasNext() ? (offset + limit) : 0;
    }

}
