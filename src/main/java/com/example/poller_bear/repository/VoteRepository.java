package com.example.poller_bear.repository;

import com.example.poller_bear.model.Choice;
import com.example.poller_bear.model.ChoiceVoteCount;
import com.example.poller_bear.model.Poll;
import com.example.poller_bear.model.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT NEW com.example.poller_bear.model.ChoiceVoteCount(v.choice.id, COUNT(v.id)) FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdGroupByChoiceId(@Param("pollId") Long pollId);

    @Query("SELECT NEW com.example.poller_bear.model.ChoiceVoteCount(v.choice.id, COUNT(v.id)) FROM Vote v WHERE v.poll.id IN :pollIds GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds") List<Long> pollIds);

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.poll.id = :pollId")
    Vote findByUserIdAndPollId(@Param("pollId") Long pollId,
                               @Param("userId") Long userId);

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.poll.id IN :pollIds")
    List<Vote> findByUserIdAndPollIdIn(@Param("pollIds") List<Long> pollIds,
                                       @Param("userId") Long userId);
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT v.poll.id FROM Vote v WHERE v.user.id = :userId")
    Page<Long> findVotedPollIdsByUserId(@Param("userId") Long userId,
                                        Pageable pageable);
}
