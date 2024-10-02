package darts.ng.io.usersMicroservice.darts_app.controller;

import darts.ng.io.usersMicroservice.darts_app.entity.*;
import darts.ng.io.usersMicroservice.darts_app.service.blacklist_service.WhitelistServiceImpl;
import darts.ng.io.usersMicroservice.darts_app.service.blacklist_service.AddBlacklistEntryServiceImpl;
import darts.ng.io.usersMicroservice.darts_app.service.blacklist_service.GetBlacklistEntryServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class BlackListServiceController {


    private final AddBlacklistEntryServiceImpl addBlacklistEntryService;
    private final GetBlacklistEntryServiceImpl getBlackListEntry;
    private final WhitelistServiceImpl whitelistServiceImpl;

    public BlackListServiceController(
            AddBlacklistEntryServiceImpl addBlacklistEntryService,
            GetBlacklistEntryServiceImpl getBlackListEntry,
            WhitelistServiceImpl whitelistServiceImpl
    )
    {
        this.addBlacklistEntryService = addBlacklistEntryService;
        this.getBlackListEntry = getBlackListEntry;
        this.whitelistServiceImpl = whitelistServiceImpl;
    }

    @PostMapping("/blacklist")
    public ResponseEntity<AddBlacklistEntryResModel> addBlackListEntry(
            @RequestBody AddBlacklistEntryReqModel bodyRequest,
            HttpServletRequest headerRequest,
            @RequestHeader("Authorization") String token
    ){
        return addBlacklistEntryService.addBlacklistEntry(bodyRequest, headerRequest, token);
    }

    @GetMapping("/blacklist/{user_id}")
    public ResponseEntity<UserBlackListPaginationResModel> getBlackListEntry(
            @PathVariable Integer user_id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("Authorization") String token
    ){
        return getBlackListEntry.getBlackListEntry(user_id, offset, limit, token);
    }

    @DeleteMapping("/{user_id}/blacklist")
    public ResponseEntity<Void> whitelistUser(@PathVariable Integer user_id, @RequestBody WhitelistReqModel request){
        return whitelistServiceImpl.whitelistUser(user_id, request);
    }


}


