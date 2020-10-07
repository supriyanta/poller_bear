package com.example.poller_bear.service;

import com.example.poller_bear.dto.PagedResponse;
import com.example.poller_bear.dto.PollResponse;
import com.example.poller_bear.dto.UserSummary;
import com.example.poller_bear.exception.ResourceNotFoundException;
import com.example.poller_bear.model.AccountUser;
import com.example.poller_bear.model.ChoiceVoteCount;
import com.example.poller_bear.model.Poll;
import com.example.poller_bear.model.Vote;
import com.example.poller_bear.repository.PollRepository;
import com.example.poller_bear.repository.UserRepository;
import com.example.poller_bear.repository.VoteRepository;
import com.example.poller_bear.security.AccountUserDetails;
import com.example.poller_bear.security.AuthenticatedUser;
import com.example.poller_bear.util.ModelResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
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
}
