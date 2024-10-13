package darts.ng.io.usersMicroservice.darts_app.service.blacklist_service;

import darts.ng.io.usersMicroservice.darts_app.entity.UserBlackListPaginationResModel;
import darts.ng.io.usersMicroservice.darts_app.entity.UserBlackListedResponseModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UserBlackListedDbModel;
import darts.ng.io.usersMicroservice.darts_app.entity.dao.UsersDatabaseModel;
import darts.ng.io.usersMicroservice.darts_app.repository.UserBlackListedRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserDatabaseRepo;
import darts.ng.io.usersMicroservice.darts_app.repository.UserRedisCacheRepo;
import darts.ng.io.usersMicroservice.security.FilterService;
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
    private final UserDatabaseRepo userDatabaseRepo;
    private final FilterService jwtService;
    private final ValidationUtils validationUtils;
    private final UserRedisCacheRepo cacheService;

    public GetBlacklistEntryServiceImpl(
            UserBlackListedRepo userBlackListedRepo,
            FilterService jwtService,
            ValidationUtils validationUtils,
            UserDatabaseRepo userDatabaseRepo,
            UserRedisCacheRepo cacheService
    ) {
        this.userBlackListedRepo = userBlackListedRepo;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
        this.userDatabaseRepo = userDatabaseRepo;
        this.cacheService = cacheService;
    }

    /**
     * Retrieves blacklist entries for a user.
     *
     * @param userId User ID
     * @param offset Pagination offset
     * @param limit  Pagination limit (default: 10)
     * @param token  JWT token
     * @return Blacklist entries with pagination metadata
     */
    public ResponseEntity<UserBlackListPaginationResModel> getBlackListEntry(
            Integer userId,
            int offset,
            Integer limit,
            String token
    ){

        validationUtils.tokenValidateRequest(token);
        cacheService.isTokenBlacklisted(jwtService.extractUUID(token), jwtService.extractTokenFromHeader(token));

        UsersDatabaseModel existingUser = userDatabaseRepo.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new CustomRuntimeException(
                        new ErrorHandler(false, AppConfig.KAY_ERROR, AppConfig.INVALID_EMAIL),
                        HttpStatus.NOT_FOUND
                ));

        validationUtils.bruteForceProtection(existingUser.getEmail(), AppConfig.GET_BLACK_LIST_LIMIT);

        limit = getValidLimit(limit);

        Pageable pageable = getPageable(offset, limit);
        Optional<Page<UserBlackListedDbModel>> result = userBlackListedRepo.findByUuid(jwtService.extractUUID(token), pageable);

        if (result.isPresent()) {

            Page<UserBlackListedDbModel> page = result.get();
            List<UserBlackListedResponseModel> responseList = mapBlacklistEntries(page.getContent());

            UserBlackListPaginationResModel.PageMetadata pageMetadata = buildPageMetadata(page, offset, limit);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new UserBlackListPaginationResModel(responseList, pageMetadata, getNextOffset(offset, limit, page), limit));
        }

        throw new CustomRuntimeException(
                new ErrorHandler(false, "Error saving record into database", "Kindly visit the support team"),
                HttpStatus.BAD_REQUEST
        );

    }

    private int getValidLimit(Integer limit) {
        return limit == null ? 10 : Math.max(1, Math.min(limit, 100));
    }

    private Pageable getPageable(int offset, int limit) {
        return PageRequest.of(offset / limit, limit);
    }

    private List<UserBlackListedResponseModel> mapBlacklistEntries(List<UserBlackListedDbModel> dbModels) {
        return dbModels.stream()
                .map(dbModel -> new UserBlackListedResponseModel(
                        dbModel.getUuid(),
                        dbModel.getIp_address(),
                        dbModel.getReason(),
                        dbModel.getIs_active(),
                        dbModel.getCreated_at(),
                        dbModel.getExpiry_at()))
                .collect(Collectors.toList());
    }

    private UserBlackListPaginationResModel.PageMetadata buildPageMetadata(Page<UserBlackListedDbModel> page, int offset, int limit) {
        Integer previousOffset = page.hasPrevious() ? (page.getNumber() - 1) * page.getSize() : null;
        return UserBlackListPaginationResModel.PageMetadata.builder()
                .page_info(UserBlackListPaginationResModel.PageMetadata.PageInfo.builder()
                        .self(new UserBlackListPaginationResModel.PageMetadata.SelfLink(page.getNumber() + 1, getSelfLink(page.getContent().get(0).getUuid().toString(), offset, limit)))
                        .first(1)
                        .next(page.hasNext() ? page.getNumber() + 2 : null)
                        .previous(previousOffset != null ? page.getNumber() : null)
                        .last(page.getTotalPages())
                        .build())
                .previous_offset(previousOffset)
                .build();
    }

    private String getSelfLink(String userId, int offset, int limit) {
        return userId + "?offset=" + offset + "&limit=" + limit;
    }

    private Integer getNextOffset(int offset, int limit, Page page) {
        return page.hasNext() ? (offset + limit) : 0;
    }
}



