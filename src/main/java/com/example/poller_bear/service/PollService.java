package com.example.poller_bear.service;

import com.example.poller_bear.dto.*;
import com.example.poller_bear.exception.BadRequestException;
import com.example.poller_bear.exception.ResourceNotFoundException;
import com.example.poller_bear.model.*;
import com.example.poller_bear.repository.PollRepository;
import com.example.poller_bear.repository.UserRepository;
import com.example.poller_bear.repository.VoteRepository;
import com.example.poller_bear.security.AccountUserDetails;
import com.example.poller_bear.util.ModelResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PollService {

    @Autowired
    PollRepository pollRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Getting paginated response of polls for user's poll feed
     *
     * @param user
     * @param page
     * @param size
     * @return
     */
    public PagedResponse<?> getAllPolls(AccountUserDetails user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> polls = pollRepository.findAll(pageable);

        if (polls.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), polls.getNumber(), polls.getSize(),
                    polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
        }

        List<Long> pollIds = polls.map(Poll::getId).getContent();
        Map<Long, Long> choiceToVoteCountMap = getChoiceToVoteCountMap(pollIds);
        Map<Long, Long> votesByUserMap = getVotesByUserMap(pollIds, user.getId());
        Map<Long, AccountUser> pollCreatorMap = getPollCreatorMap(polls.getContent());

        List<PollResponse> pollResponses = polls.map(poll -> {

            AccountUser creator = pollCreatorMap.getOrDefault(poll.getCreatedBy(), null);
            Long currentUserSelectedChoiceForThisPoll = votesByUserMap != null ?
                    votesByUserMap.getOrDefault(poll.getId(), null) : null;

            return ModelResponseMapper.mapPollToPollResponse(
                    poll,
                    choiceToVoteCountMap,
                    creator,
                    currentUserSelectedChoiceForThisPoll
            );
        }).getContent();

        return new PagedResponse<>(pollResponses, polls.getNumber(), polls.getSize(),
                polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
    }

    /**
     * retrieving single poll by poll id
     *
     * @param user
     * @param pollId
     * @return
     */
    public PollResponse getPollById(AccountUserDetails user, Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("poll", "poll id", pollId));

        Map<Long, Long> choiceToVoteCountMap = getChoiceToVoteCountMap(Collections.singletonList(pollId));

        AccountUser creator = userRepository.findById(poll.getCreatedBy()).orElse(null);

        Vote voteByCurrentUserForThisPoll = user != null ?
                voteRepository.findByUserIdAndPollId(pollId, user.getId())
                : null;
        Long currentUserSelectedChoiceForThisPoll = voteByCurrentUserForThisPoll != null ?
                voteByCurrentUserForThisPoll.getChoice().getId()
                : null;

        return ModelResponseMapper.mapPollToPollResponse(
                poll,
                choiceToVoteCountMap,
                creator,
                currentUserSelectedChoiceForThisPoll
        );
    }


    /**
     * Creating poll for current user
     *
     * @param user
     * @param pollRequest
     * @return PollResponse
     */
    public PollResponse createPollByCurrentUser(AccountUserDetails user, PollRequest pollRequest) {
        Poll poll = new Poll();

        poll.setTopic(pollRequest.getTopic());

//        List<Choice> choices = pollRequest.getChoices()
//                .stream()
//                .map(choiceRequest -> {
//                    Choice choice = new Choice();
//                    choice.setText(choiceRequest.getText());
//                    return choice;
//                })
//                .collect(Collectors.toList());
//        poll.setChoices(choices);

        pollRequest.getChoices()
                .forEach(choiceRequest -> {
                    Choice choice = new Choice(choiceRequest.getText());
                    choice.setPoll(poll);
                    poll.getChoices().add(choice);
                });

        PollDuration duration = pollRequest.getDuration();
        Instant now = Instant.now();
        Instant expirationTime = now.plus(Duration.ofDays(duration.getDays()))
                .plus(Duration.ofHours(duration.getHours()));
        poll.setExpirationTime(expirationTime);


        Poll newPoll = pollRepository.save(poll);

        List<ChoiceResponse> choiceResponses = poll.getChoices()
                .stream()
                .map(choice -> new ChoiceResponse(choice.getId(), choice.getText(), 0L))
                .collect(Collectors.toList());

        UserSummary creator = new UserSummary(user.getId(), user.getName(), user.getUsername());
        return new PollResponse(
                newPoll.getId(),
                newPoll.getTopic(),
                choiceResponses,
                newPoll.getCreatedAt(),
                newPoll.getExpirationTime(),
                false,
                creator,
                null,
                0L);
    }

    /**
     * Getting paginated response of polls created by user with particular username
     *
     * @param username
     * @param authUser
     * @param page
     * @param size
     * @return
     */
    public PagedResponse<?> getAllPollsCreatedByUser(String username,
                                                     AccountUserDetails authUser,
                                                     int page,
                                                     int size) {

        AccountUser creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> pagedPoll = pollRepository.findByCreatedBy(creator.getId(), pageable);

        if (pagedPoll.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(),
                    pagedPoll.getNumber(), pagedPoll.getSize(),
                    pagedPoll.getTotalElements(), pagedPoll.getTotalPages(),
                    pagedPoll.isLast());
        }

        List<Long> pollIds = pagedPoll
                                .map(Poll::getId)
                                .getContent();

        Map<Long, Long> choiceVoteCountMap = getChoiceToVoteCountMap(pollIds);
        Map<Long, Long> votesByUserMap = getVotesByUserMap(pollIds, authUser.getId());

        List<PollResponse> pollResponses = pagedPoll
                .map(poll -> {
                    Long currentUserSelectedChoiceForThisPoll = votesByUserMap
                            .getOrDefault(poll.getId(), null);

                    return ModelResponseMapper.mapPollToPollResponse(
                            poll,
                            choiceVoteCountMap,
                            creator,
                            currentUserSelectedChoiceForThisPoll
                    );
                }).getContent();

        return new PagedResponse<>(
                pollResponses,
                pagedPoll.getNumber(),
                pagedPoll.getSize(),
                pagedPoll.getTotalElements(),
                pagedPoll.getTotalPages(),
                pagedPoll.isLast()
        );
    }

    /**
     * Getting paginated response of polls voted by user with particular username
     *
     * @param username
     * @param authUser
     * @param page
     * @param size
     * @return
     */
    public PagedResponse<?> getAllPollsVotedByUser(String username,
                                                     AccountUserDetails authUser,
                                                     int page,
                                                     int size) {

        AccountUser voter = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user", "username", username));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Long> pagedUserVotedPollIds = voteRepository.findVotedPollIdsByUserId(voter.getId(), pageable);

        if (pagedUserVotedPollIds.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(),
                    pagedUserVotedPollIds.getNumber(), pagedUserVotedPollIds.getSize(),
                    pagedUserVotedPollIds.getTotalElements(), pagedUserVotedPollIds.getTotalPages(),
                    pagedUserVotedPollIds.isLast());
        }

        List<Long> pollIds = pagedUserVotedPollIds.getContent();

        Page<Poll> pagedPolls = pollRepository.findByIdIn(pollIds, pageable);

        Map<Long, Long> choiceVoteCountMap = getChoiceToVoteCountMap(pollIds);
        Map<Long, Long> votesByUserMap = getVotesByUserMap(pollIds, authUser.getId());
        Map<Long, AccountUser> creatorMap = getPollCreatorMap(pagedPolls.getContent());

        List<PollResponse> pollResponses = pagedPolls
                .map(poll -> {
                    Long currentUserSelectedChoiceForThisPoll = votesByUserMap
                            .getOrDefault(poll.getId(), null);

                    return ModelResponseMapper.mapPollToPollResponse(
                            poll,
                            choiceVoteCountMap,
                            creatorMap.getOrDefault(poll.getCreatedBy(), null),
                            currentUserSelectedChoiceForThisPoll
                    );
                }).getContent();

        return new PagedResponse<>(
                pollResponses,
                pagedPolls.getNumber(),
                pagedPolls.getSize(),
                pagedPolls.getTotalElements(),
                pagedPolls.getTotalPages(),
                pagedPolls.isLast()
        );
    }


    /**
     * returns User of the creator of the polls Map<poll id, user>
     *
     * @param polls
     * @return Map<Long, AccountUser>
     */
    private Map<Long, AccountUser> getPollCreatorMap(List<Poll> polls) {
        List<Long> creatorIds = polls
                .stream()
                .map(Poll::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<AccountUser> users = userRepository.findByIdIn(creatorIds);
        Map<Long, AccountUser> pollCreatorMap = users
                .stream()
                .collect(Collectors.toMap(AccountUser::getId, Function.identity()));
        return pollCreatorMap;
    }

    /**
     * returns Map<poll id, choice id> which tells
     * current user chose which choice of the poll of poll id
     *
     * @param pollIds
     * @param userId
     * @return Map<Long, Long>
     */
    private Map<Long, Long> getVotesByUserMap(List<Long> pollIds, Long userId) {
        List<Vote> votesByUser = voteRepository.findByUserIdAndPollIdIn(pollIds, userId);

        if (votesByUser == null || votesByUser.size() == 0) return null;

        Map<Long, Long> votesByUserMap = votesByUser
                .stream()
                .collect(Collectors
                        .toMap(vote -> vote.getPoll().getId(),
                                vote -> vote.getChoice().getId()));
        return votesByUserMap;
    }

    /**
     * returns Map<choice id, vote count> which tells
     * which choice got how many votes
     *
     * @param pollIds
     * @return Map<Long, Long>
     */
    private Map<Long, Long> getChoiceToVoteCountMap(List<Long> pollIds) {
        List<ChoiceVoteCount> choiceVoteCounts = voteRepository.countByPollIdInGroupByChoiceId(pollIds);
        Map<Long, Long> choiceToVoteCount = choiceVoteCounts
                .stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
        return choiceToVoteCount;
    }

    @Transactional
    public PollResponse castVoteForPollAndGetUpdatedPoll(AccountUserDetails voterDetails,
                                                         Long pollId,
                                                         VoteRequest voteRequest) {

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("poll", "poll id", pollId));

        // Check expiration
        if (poll.getExpirationTime().isBefore(Instant.now())) {
            throw new BadRequestException("This poll is expired");
        }

        Choice selectedChoice = poll.getChoices()
                .stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("choice", "choice id", voteRequest.getChoiceId()));

        AccountUser voter = userRepository.findById(voterDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("user", "user id", voterDetails.getId()));

        Vote vote = new Vote();

        vote.setPoll(poll);
        vote.setChoice(selectedChoice);
        vote.setUser(voter);

        try {
            voteRepository.save(vote);
        } catch (Exception exception) {
            throw new BadRequestException("user already casted vote for this poll");
        }

        // send updated poll response

        AccountUser pollCreator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new BadRequestException("Creator of the poll not found"));

        Map<Long, Long> choiceToVoteCountMap = getChoiceToVoteCountMap(Collections.singletonList(poll.getId()));

        PollResponse pollResponse = ModelResponseMapper.mapPollToPollResponse(
                poll,
                choiceToVoteCountMap,
                pollCreator,
                selectedChoice.getId()
        );

        return pollResponse;
    }
}
