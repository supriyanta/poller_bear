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
import java.util.*;
import java.util.List;
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
     * Getting paginated response of polls for user
     * @param user
     * @param page
     * @param size
     * @return
     */
    public PagedResponse<?> getAllPolls(AccountUserDetails user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> polls = pollRepository.findAll(pageable);

        if(polls.getNumberOfElements() == 0) {
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
     * @param user
     * @param pollId
     * @return
     */
    public PollResponse getPollById(AccountUserDetails user, Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException());
        // TODO : fill ResourceNotFound Exception constructor arguments

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
     * returns User of the creator of the polls Map<poll id, user>
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
     *      current user chose which choice of the poll of poll id
     * @param pollIds
     * @param userId
     * @return Map<Long, Long>
     */
    private Map<Long, Long> getVotesByUserMap(List<Long> pollIds, Long userId) {
        List<Vote> votesByUser = voteRepository.findByUserIdAndPollIdIn(pollIds, userId);

        if(votesByUser == null || votesByUser.size() == 0) return null;

        Map<Long, Long> votesByUserMap = votesByUser
                .stream()
                .collect(Collectors
                        .toMap(vote -> vote.getPoll().getId(),
                                vote -> vote.getChoice().getId()));
        return votesByUserMap;
    }

    /**
     * returns Map<choice id, vote count> which tells
     *      which choice got how many votes
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
                .orElseThrow(() -> new ResourceNotFoundException());

        // Check expiration
        if(poll.getExpirationTime().isBefore(Instant.now())) {
            throw new BadRequestException();
        }

        Choice selectedChoice = poll.getChoices()
                .stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException());

        AccountUser voter = userRepository.findById(voterDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException());

        Vote vote = new Vote();

        vote.setPoll(poll);
        vote.setChoice(selectedChoice);
        vote.setUser(voter);

        try {
            voteRepository.save(vote);
        } catch (Exception exception) {
            throw new BadRequestException();
        }

        // send updated poll response

        AccountUser pollCreator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException());

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
