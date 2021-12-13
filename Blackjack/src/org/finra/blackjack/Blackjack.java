package org.finra.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Blackjack {

    // a deck of cards
    private List<Integer> deck;
    // an ordering of the four suites
    private List<String> suiteOrder;
    // a list of all players' current hands; the last entry in this list
    // represents the dealer's current hand
    private List<Set<Integer>> currentHands;

    public static void main(String[] args) {

        Blackjack blackjack = new Blackjack();
        blackjack.play();
    }

    // this program assumes the following:
    // deck contains 52 cards, represented by integers 1 to 52
    // 1-13 are clubs (1 is Ace, 11 is Jack, 12 is Queen, 13 is King)
    // 14-26 are diamonds (14 is Ace, 11 is Jack, 12 is Queen, 13 is King)
    // 27-39 are hearts (27 is Ace, 11 is Jack, 12 is Queen, 13 is King)
    // 40-52 are spades (40 is Ace, 11 is Jack, 12 is Queen, 13 is King)
    protected void play() {

        System.out.println("Enter number of players:");
        Scanner scanner = new Scanner(System.in);
        int playerCount = Integer.parseInt(scanner.nextLine());

        // get the four suites in some random order
        suiteOrder = getSuiteOrder();
        // get a new deck
        deck = new ArrayList<>(52);
        for(int i = 1; i <= 52; i++) {
            deck.add(i);
        }
        // shuffle the deck
        Collections.shuffle(deck);

        // dealer's hand is maintained last in this list
        currentHands = new ArrayList<>(playerCount + 1);

        // deal one card to each player and then one card to the dealer
        for (int i = 0; i < playerCount + 1; i++) {
            dealCard(i);
            if (i == playerCount) {
                System.out.println("\nDealing to computer, card: face down.");
            } else {
                System.out.print("\nDealing to player " + (i + 1) + ", " + displayHand(currentHands.get(i)));
            }
        }

        // 1) iterate through each player's hand and:
        // a) deal a card
        // b) check if player is busted (they will not be on their first iteration since they've only been
        // dealt one card so far (above), so this 2nd card dealt can only take the hand's total to a maximum of 21)
        // c) if busted, move on to the next player; if not, ask player if they want to hit or stand.
        // d) if they hit, repeat from (a) above. If they stand, move on to the next player (step (1) above)
        //
        // 2) for the dealer's hand
        // a) deal a card
        // b) check if dealer is busted (they will not be on their first iteration since they've only been
        // dealt one card so far (above), so this 2nd card dealt can only take the hand's total to a maximum of 21)
        // c) if busted, move on; if not, make a decision to either hit or stand.
        // d) if dealer hits, repeat from (a) above. If they stand, move on
        for (int i = 0; i < playerCount + 1; i++) {

            while (true) {

                dealCard(i);
                if(i < playerCount) {
                    System.out.println("\nDealing to player " + (i + 1) + ", " + displayHand(currentHands.get(i)));
                }
                else {
                    System.out.println("\nDealing to computer, " + displayHand(currentHands.get(i)));
                }

                int handTotal = getMostAdvantageousHandTotal(currentHands.get(i));

                // is hand over 21?
                if (handTotal > 21) {
                    System.out.print(" Busted, over 21");
                    break;
                // should player hit or stand?
                } else if(i < playerCount) {
                    String hitOrStand = null;
                    while(!"stand".equalsIgnoreCase(hitOrStand)
                            && !("hit").equalsIgnoreCase(hitOrStand)) {
                        System.out.print(" Hit or Stand?");
                        hitOrStand = scanner.nextLine();
                    }
                    if ("stand".equalsIgnoreCase(hitOrStand)) {
                        break;
                    }
                }
                // should dealer hit or stand?
                else {
                    if (!shouldDealerHit()) {
                        System.out.print(" Dealer stands.");
                        break;
                    }
                    else {
                        System.out.print(" Dealer hits.");
                    }
                }
            }
        }
        printScores();
    }

    // dealer decides to hit or stand as follows:
    // a) if dealer's current hand is 19 or more, stand.
    // b) if dealer is currently beating 75% or more of the players in the game, stand.
    // c) if dealer's current hand is 14 or less, hit.
    // d) if dealer is currently beating 25% or less of the players in the game, hit.
    // e) if dealer is currently beating more than 25% but less than 50% of the players in the game, then:
    // - hit if dealer's current hand is 17 or less.
    // - stand if dealer's current hand is 18.
    // f) if dealer is currently beating 50% or more of the players in the game,
    // but less than 75% of the players in the game, then:
    // - hit if dealer's current hand is 16 or less.
    // - stand if dealer's current hand is 17 or 18.
    protected boolean shouldDealerHit() {

        int playerCount = currentHands.size() - 1;

        int dealersMostAdvantageousHand = getMostAdvantageousHandTotal(currentHands.get(currentHands.size() - 1));

        int numberOfPlayersDealerCurrentlyBeats = 0;
        // count the number of players that the dealer is currently beating
        // dealer beats players that are busted or have equal or lower hands than the dealer
        for(int i = 0; i < playerCount; i++) {

            if(getMostAdvantageousHandTotal(currentHands.get(i)) > 21
                    || dealersMostAdvantageousHand >= getMostAdvantageousHandTotal(currentHands.get(i)) ) {
                numberOfPlayersDealerCurrentlyBeats++;
            }
        }

        float ratioOfPlayersDealerCurrentlyBeats = (float)numberOfPlayersDealerCurrentlyBeats / playerCount;
        int highestCardValueWithoutBusting = 21 - dealersMostAdvantageousHand;

        if(highestCardValueWithoutBusting <= 2 || ratioOfPlayersDealerCurrentlyBeats >= 0.75) {
            return false;
        }
        if(highestCardValueWithoutBusting >= 7 || ratioOfPlayersDealerCurrentlyBeats <= 0.25) {
            return true;
        }
        if(ratioOfPlayersDealerCurrentlyBeats < 0.5) {
            return highestCardValueWithoutBusting >= 4;
        }
        else {
            return highestCardValueWithoutBusting >= 5;
        }
    }

    private void printScores() {

        int dealerTotal = getMostAdvantageousHandTotal(currentHands.get(currentHands.size() - 1));

        for(int i = 0; i < currentHands.size() - 1; i++) {

            int playerTotal = getMostAdvantageousHandTotal(currentHands.get(i));
            if(playerTotal > 21) {
                if(dealerTotal > 21) {
                    System.out.println("\nScoring player " + (i + 1) + " busted. Dealer also busted. No one wins.");
                }
                else {
                    System.out.println("\nScoring player " + (i + 1) + " busted. " +
                            "Dealer has " + dealerTotal + ". Dealer wins.");
                }
            }
            else {
                if(dealerTotal > 21) {
                    System.out.println("\nScoring player " + (i + 1) + " has " + playerTotal
                            + ". Dealer busted. Player " + (i + 1) + " wins.");
                }
                else {
                    System.out.println("\nScoring player " + (i + 1) + " has " + playerTotal
                            + ", dealer has " + dealerTotal + ".");
                    if(dealerTotal >= playerTotal) {
                        System.out.print(" Dealer wins.");
                    }
                    else {
                        System.out.print(" Player " + (i + 1) + " wins.");
                    }
                }

            }
        }
    }

    private int dealCard(int playerIndex) {

        int dealtCard = deck.remove(0);

        Set<Integer> hand;
        if(currentHands.size() < playerIndex + 1) {
            hand = new LinkedHashSet<>();
            currentHands.add(playerIndex, hand);
        }
        else {
            hand = currentHands.get(playerIndex);
        }
        hand.add(dealtCard);
        return dealtCard;
    }

    // tries to return the highest total for the given hand, that is less
    // than or equal to 21, using the value of any aces in the hand as a 1 or 11,
    // such that the total does not exceed 21 but is as close to 21 as possible.
    // if the above is not possible, then this method will return a total > 21
    private int getMostAdvantageousHandTotal(Set<Integer> hand) {

        int total = 0;
        int numberOfAcesInHand = 0;
        if(hand != null && !hand.isEmpty()) {
            for (Integer card : hand) {
                int remainder = card % 13;
                // card is an Ace; count it for later
                if (remainder == 1) {
                    numberOfAcesInHand++;
                }
                // card is a Jack, Queen or King; add 10 to the current total
                else if(remainder == 11 || remainder == 12 || remainder == 0) {
                    total = total + 10;
                }
                // card is numbered, add the numeric value to the current total
                else {
                    total = total + remainder;
                }
            }
            // now, go through the aces in the hand, if any, and add 11 to the
            // current total if that will not make the total > 21; if it will
            // make the total > 21, then add 1 to the current total
            for(int i = 0; i < numberOfAcesInHand; i++) {
                if(total + 11 <= 21) {
                    total = total + 11;
                } else {
                    total = total + 1;
                }
            }
        }
        return total;
    }

    // returns a String containing the card number (or Ace or face name)
    // and suite name of each card in the given hand; cards are comma-delimited
    private String displayHand(Set<Integer> hand) {

        if(hand == null || hand.isEmpty()) {
            return null;
        }
        else {
            StringBuilder message = new StringBuilder();
            if(hand.size() == 1) {
                message.append("card: ");
            }
            else {
                message.append("cards: ");
            }
            for(Integer card : hand) {
                message.append(getCardNumberAndSuite(card)).append(",");
            }
            return message.substring(0,message.length() - 1) + ".";
        }
    }

    // returns a String containing the card number (or Ace or face name)
    // and suite name of the given card (card is represented as an Integer)
    private String getCardNumberAndSuite(Integer dealtCard) {

        StringBuilder card = new StringBuilder();
        int remainder = dealtCard % 13;
        if(remainder == 1) card.append("Ace ");
        else if(remainder == 11) card.append("Jack ");
        else if(remainder == 12) card.append("Queen ");
        else if(remainder == 0) card.append("King ");
        else card.append(remainder).append(" ");

        card.append(suiteOrder.get(((dealtCard - 1) / 13)));

        return card.toString();
    }

    private List<String> getSuiteOrder() {

        List<String> suiteOrder = new ArrayList<>();
        suiteOrder.add("Clubs");
        suiteOrder.add("Diamonds");
        suiteOrder.add("Hearts");
        suiteOrder.add("Spades");
        Collections.shuffle(suiteOrder);
        return suiteOrder;
    }

    public void setCurrentHands(List<Set<Integer>> hands) {
        this.currentHands = hands;
    }
}