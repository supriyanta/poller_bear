package com.example.poller_bear.controller;

import com.example.poller_bear.constants.AppConstants;
import com.example.poller_bear.dto.*;
import com.example.poller_bear.security.AccountUserDetails;
import com.example.poller_bear.security.AuthenticatedUser;
import com.example.poller_bear.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/poll")
public class PollController {

    @Autowired
    PollService pollService;

//    // delete after test
//    @Secured("ROLE_USER")
//    @GetMapping("/check")
//    public String check() {
//        return "Checked";
//    }

    @GetMapping
    public ResponseDto<?> getAllPolls(@AuthenticatedUser AccountUserDetails user,
                                      @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NO) int page,
                                      @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        PagedResponse pollResponses = pollService.getAllPolls(user, page, size);
        return new ResponseDto<>(HttpStatus.OK, "success", pollResponses);
    }

    @GetMapping("/{pollId}")
    public ResponseDto<?> getPollById(@AuthenticatedUser AccountUserDetails user,
                                      @PathVariable("pollId") Long pollId) {

        PollResponse pollResponse = pollService.getPollById(user, pollId);
        return new ResponseDto<>(HttpStatus.OK, "success", pollResponse);
    }

    @PostMapping
    public ResponseDto<?> createPoll(@AuthenticatedUser AccountUserDetails user,
                                     @Valid @RequestBody PollRequest pollRequest) {


        PollResponse pollResponse = pollService.createPollByCurrentUser(user, pollRequest);
        return new ResponseDto<>(HttpStatus.CREATED, "success", pollResponse);
    }

    @PostMapping("/{pollId}/vote")
    public ResponseDto<?> castVoteForPoll(@AuthenticatedUser AccountUserDetails user,
                                          @PathVariable("pollId") Long pollId,
                                          @Valid @RequestBody VoteRequest voteRequest) {

        PollResponse pollResponse = pollService.castVoteForPollAndGetUpdatedPoll(user, pollId, voteRequest);
        return new ResponseDto<>(HttpStatus.ACCEPTED, "success", pollResponse);
    }

}
