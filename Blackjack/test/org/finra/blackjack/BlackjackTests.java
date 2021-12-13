package org.finra.blackjack;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// This class contains two basic Junit tests.
// I did not have time to add more tests, so I am submitting just these for now.
// I can add more tests if requested, when time allows.
public class BlackjackTests extends TestCase {

    public void testDealerHitsBecauseTheyAreCurrentlyBeatingLessThan25PercentOfPlayers() {

        Blackjack blackjack = new Blackjack();

        List<Set<Integer>> hands = new ArrayList<>();
        // player 1 hand (total 18)
        Set<Integer> hand = new HashSet<>();
        // Ace
        hand.add(1);
        // 7
        hand.add(7);
        // King
        hand.add(13);
        hands.add(hand);
        // dealer's hand (total 17)
        hand = new HashSet<>();
        // 8
        hand.add(8);
        // 9
        hand.add(9);
        hands.add(hand);

        blackjack.setCurrentHands(hands);

        Assert.assertTrue(blackjack.shouldDealerHit());
    }

    public void testDealerStandsBecauseTheyAreCurrentlyBeatingMoreThan75PercentOfPlayers() {

        Blackjack blackjack = new Blackjack();

        List<Set<Integer>> hands = new ArrayList<>();
        // player 1 hand (total 18)
        Set<Integer> hand = new HashSet<>();
        // Ace
        hand.add(1);
        // 7
        hand.add(7);
        // King
        hand.add(13);
        hands.add(hand);
        // dealer's hand (total 19)
        hand = new HashSet<>();
        // 10
        hand.add(10);
        // 9
        hand.add(9);
        hands.add(hand);

        blackjack.setCurrentHands(hands);

        Assert.assertFalse(blackjack.shouldDealerHit());
    }
}