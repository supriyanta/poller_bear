package com.example.poller_bear.util;

import com.example.poller_bear.dto.ChoiceResponse;
import com.example.poller_bear.dto.PollResponse;
import com.example.poller_bear.dto.UserSummary;
import com.example.poller_bear.model.AccountUser;
import com.example.poller_bear.model.Poll;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelResponseMapper {

    /**
     *
     * @param poll
     * @param choiceVoteCountMap map of (choice id, vote count)
     * @param creator creator of the current poll
     * @param currentUserSelectedChoiceId Creator's selected choice for his current poll
     * @return PollResponse
     */
    public static PollResponse mapPollToPollResponse(Poll poll,
                                                     Map<Long, Long> choiceVoteCountMap,
                                                     AccountUser creator,
                                                     Long currentUserSelectedChoiceId) {

        PollResponse pollResponse = new PollResponse();

        pollResponse.setId(poll.getId());
        pollResponse.setTopic(poll.getTopic());
        pollResponse.setCreatedAt(poll.getCreatedAt());
        pollResponse.setExpiredAt(poll.getExpirationTime());
        pollResponse.setIsExpired(poll.getExpirationTime().isBefore(Instant.now()));
        pollResponse.setCreatedBy(new UserSummary(creator.getId(),creator.getName(), creator.getUsername()));
        pollResponse.setSelectedChoice(currentUserSelectedChoiceId);

        List<ChoiceResponse> choiceResponseList = poll.getChoices()
                            .stream()
                            .map(choice -> {
                                ChoiceResponse choiceResponse = new ChoiceResponse();
                                choiceResponse.setId(choice.getId());
                                choiceResponse.setText(choice.getText());
                                choiceResponse.setVoteCount(
                                        choiceVoteCountMap.getOrDefault(choice.getId(), 0L)
                                );
                                return choiceResponse;
                            }).collect(Collectors.toList());

        pollResponse.setChoices(choiceResponseList);

        Long totalVoteCounts = choiceResponseList
                                .stream()
                                .mapToLong(ChoiceResponse::getVoteCount).sum();
        pollResponse.setTotalVotes(totalVoteCounts);

        return pollResponse;
    }
}
