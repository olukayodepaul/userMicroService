package darts.ng.io.usersMicroservice.darts_app.controller;

import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.service.blacklist_service.WhitelistService;
import darts.ng.io.usersMicroservice.darts_app.service.blacklist_service.AddBlacklistEntryService;
import darts.ng.io.usersMicroservice.darts_app.service.blacklist_service.GetBlacklistEntryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class BlackListServiceController {


    private final AddBlacklistEntryService addBlacklistEntryService;
    private final GetBlacklistEntryService getBlackListEntry;
    private final WhitelistService whitelistServiceImpl;

    public BlackListServiceController(
            AddBlacklistEntryService addBlacklistEntryService,
            GetBlacklistEntryService getBlackListEntry,
            WhitelistService whitelistServiceImpl
    )
    {
        this.addBlacklistEntryService = addBlacklistEntryService;
        this.getBlackListEntry = getBlackListEntry;
        this.whitelistServiceImpl = whitelistServiceImpl;
    }

    //when you blacklisted an account, send notification to all the service that a user is blacklisted.
    //black all the user token. since user can create a new token from authService, the condition hold.
    @PostMapping("/blacklist")
    public ResponseEntity<AddBlacklistEntryResModel> addBlackListEntry(
            @RequestBody AddBlacklistEntryReqModel bodyRequest,
            HttpServletRequest headerRequest,
            @RequestHeader("Authorization") String token
    ){
        return addBlacklistEntryService.addBlacklistEntry(bodyRequest, headerRequest, token);
    }

    //change the implementation to user uuid from the header
    @GetMapping("/blacklist/{user_id}")
    public ResponseEntity<UserBlackListPaginationResModel> getBlackListEntry(
            @PathVariable Integer user_id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("Authorization") String token
    ){
        return getBlackListEntry.getBlackListEntry(user_id, offset, limit, token);
    }

    //refactor this implementation to use the uuid from the header
    @DeleteMapping("/{user_id}/blacklist")
    public ResponseEntity<Void> whitelistUser(
            @RequestBody WhitelistReqModel request,
            HttpServletRequest headerRequest,
            @RequestHeader("Authorization") String token){
        return whitelistServiceImpl.whitelistUser(request, headerRequest, token);
    }


}


