package com.example.poller_bear.controller;

import com.example.poller_bear.constants.AppConstants;
import com.example.poller_bear.dto.PagedResponse;
import com.example.poller_bear.dto.ResponseDto;
import com.example.poller_bear.dto.UserProfileResponse;
import com.example.poller_bear.exception.ResourceNotFoundException;
import com.example.poller_bear.model.AccountUser;
import com.example.poller_bear.repository.PollRepository;
import com.example.poller_bear.repository.UserRepository;
import com.example.poller_bear.repository.VoteRepository;
import com.example.poller_bear.security.AccountUserDetails;
import com.example.poller_bear.security.AuthenticatedUser;
import com.example.poller_bear.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PollRepository pollRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    PollService pollService;

    @GetMapping("/{username}")
    public ResponseDto<?> getUserProfileByUsername(@PathVariable("username") String username) {
        AccountUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));

        UserProfileResponse userProfileResponse = new UserProfileResponse();

        userProfileResponse.setId(user.getId());
        userProfileResponse.setJoinedAt(user.getCreatedAt());
        userProfileResponse.setName(user.getName());
        userProfileResponse.setUsername(user.getUsername());

        Long pollCount = pollRepository.countByCreatedBy(user.getId());
        Long voteCount = voteRepository.countByUserId(user.getId());

        userProfileResponse.setPollCount(pollCount);
        userProfileResponse.setVoteCount(voteCount);

        return new ResponseDto<>(HttpStatus.OK, "success", userProfileResponse);
    }

    @GetMapping("/{username}/polls")
    public ResponseDto<?> getAllPollsCreatedByUser(@PathVariable String username,
                                                   @AuthenticatedUser AccountUserDetails user,
                                                   @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NO) int page,
                                                   @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {


        PagedResponse<?> pagedResponse = pollService.getAllPollsCreatedByUser(username, user, page, size);
        return new ResponseDto<>(HttpStatus.OK, "success", pagedResponse);
    }


    @GetMapping("/{username}/votes")
    public ResponseDto<?> getAllPollsVotedByUser(@PathVariable String username,
                                                   @AuthenticatedUser AccountUserDetails user,
                                                   @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NO) int page,
                                                   @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {


        PagedResponse<?> pagedResponse = pollService.getAllPollsVotedByUser(username, user, page, size);
        return new ResponseDto<>(HttpStatus.OK, "success", pagedResponse);
    }
}
