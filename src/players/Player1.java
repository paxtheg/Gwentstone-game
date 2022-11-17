package players;

import fileio.CardInput;
import fileio.DecksInput;
import fileio.Input;

import java.util.ArrayList;

public final class Player1 {
    private DecksInput deckArray;
    private int deckIdx;
    private ArrayList<CardInput> deck;

    private ArrayList<CardInput> hand;
    private CardInput hero;


    public Player1(final Input input, final int gameIdx) {
        this.deckArray = input.getPlayerOneDecks(); // saves in Player1 the decks array
        this.deckIdx = input.getGames().get(gameIdx).getStartGame().getPlayerOneDeckIdx();  // saves in Player1 the index of the deck
                                                                                        // that is going to be used
        this.deck = deckArray.getDecks().get(deckIdx);   //saves in Player1 the card array, based by the deck that is going to be used
        this.hero = input.getGames().get(gameIdx).getStartGame().getPlayerOneHero(); //saves in Player1 the hero

        this.hand = new ArrayList<>(deck.size());
        // ultima carte : cards.get(cards.size() - 1);
    }

    public DecksInput getDeckArray() {
        return deckArray;
    }

    public void setDeckArray(final DecksInput deckArray) {
        this.deckArray = deckArray;
    }

    public ArrayList<CardInput> getDeck() {
        return deck;
    }

    public void setDeck(final ArrayList<CardInput> deck) {
        this.deck = deck;
    }

    public ArrayList<CardInput> getHand() {
        return hand;
    }

    public void setHand(final ArrayList<CardInput> hand) {
        this.hand = hand;
    }

    public int getDeckIdx() {
        return deckIdx;
    }

    public void setDeckIdx(final int deckIdx) {
        this.deckIdx = deckIdx;
    }

    public CardInput getHero() {
        return hero;
    }

    public void setHero(final CardInput hero) {
        this.hero = hero;
    }
}
