package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.DecksInput;
import fileio.Input;
import players.Player1;
import players.Player2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import static checker.CheckerConstants.*;


/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    //final private int i = 2;
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        //TODO add here the entry point to your implementation





        //i is the game number, j is the command/action number

            for (int i = 0; i < inputData.getGames().size(); i++) {

                Player1 player1 = new Player1(inputData, i);
                Player2 player2 = new Player2(inputData, i);

                Collections.shuffle((player1.getDeck()), new Random(inputData.getGames().
                        get(i).getStartGame().getShuffleSeed()));
                Collections.shuffle((player2.getDeck()), new Random(inputData.getGames().
                        get(i).getStartGame().getShuffleSeed()));

                player1.getHand().add(player1.getDeck().get(0));
                player2.getHand().add(player2.getDeck().get(0));

                player1.getDeck().remove(0);
                player2.getDeck().remove(0);

                player1.getHero().setHealth(THIRTY); //sets the hero
                                    // health to 30 because it is not
                player2.getHero().setHealth(THIRTY);  // initialized by the A.I.

                int playerManaCount = TWO;
                //is used to count the mana
                //that the players get

                ArrayList<ArrayList<CardInput>> table = new ArrayList<>(FOUR);
                for (int y = 0; y < FOUR; y++) {
                    table.add(new ArrayList<>(FIVE));
                }

                int turn = inputData.getGames().get(i).getStartGame().getStartingPlayer();
                int turnCount = 0; //checks if a round has ended

                for (int j = 0; j < inputData.getGames().get(i).getActions().size(); j++) {
                    int playerIdx = inputData.getGames().get(i).getActions().
                            get(j).getPlayerIdx();
                    int handIdx = inputData.getGames().get(i).getActions().
                            get(j).getHandIdx();
                    int affectedRow = inputData.getGames().get(i).getActions().
                            get(j).getAffectedRow();

                    switch (inputData.getGames().get(i).getActions().get(j).getCommand()) {
                        case "endPlayerTurn":
                            turnCount++;
                            if (turnCount == TWO) {
                                turnCount = 0;

                                player1.setMana(player1.getMana() + playerManaCount);
                                player2.setMana(player2.getMana() + playerManaCount);

                                if (playerManaCount < TEN) {
                                    playerManaCount++;
                                }

                                if (!player1.getDeck().isEmpty()) {
                                    player1.getHand().add(player1.getDeck().get(0));
                                    player1.getDeck().remove(0);
                                }
                                if (!player2.getDeck().isEmpty()) {
                                    player2.getHand().add(player2.getDeck().get(0));
                                    player2.getDeck().remove(0);
                                }
                            }
                            if (turn == ONE) {
                                turn = TWO;
                            } else if (turn == TWO) {
                                turn = ONE;
                            }
                            break;
                        case "getPlayerTurn":
                            output.addObject().put("command", "getPlayerTurn").
                                    put("output", turn);
                            break;
                        case "getPlayerDeck":
                            ArrayList<CardInput> currentDeck = new ArrayList<>();
                            if (playerIdx == ONE) {
                                currentDeck = player1.getDeck();
                            } else if (playerIdx == TWO) {
                                currentDeck = player2.getDeck();
                            }
                            ObjectNode node = output.addObject();
                            node.put("command", "getPlayerDeck").
                                    put("playerIdx", playerIdx);
                            ArrayNode arrayNode = node.putArray("output");
                            for (CardInput cardInput : currentDeck) {
                                ObjectNode node2 = objectMapper.createObjectNode();
                                node2.put("mana", cardInput.getMana());
                                if (!cardInput.getName().equals("Winterfell")
                                        && !cardInput.getName().equals("Heart Hound")
                                        && !cardInput.getName().equals("Firestorm")) {
                                    node2.put("attackDamage", cardInput.getAttackDamage());
                                    node2.put("health", cardInput.getHealth());
                                }
                                node2.put("description", cardInput.getDescription());
                                ArrayNode colorNode = node2.putArray("colors");
                                for (String string : cardInput.getColors()) {
                                    colorNode.add(string);
                                }
                                node2.put("name", cardInput.getName());
                                arrayNode.add(node2);
                            }
                            break;
                        case "getPlayerHero":
                            if (playerIdx == ONE) {
                                CardInput currentHero = player1.getHero();

                                node = output.addObject();
                                ObjectNode node2 = objectMapper.createObjectNode();
                                node.put("command", "getPlayerHero").
                                        put("playerIdx", playerIdx).set("output", node2);
                                node2.put("mana", currentHero.getMana());
                                node2.put("health", currentHero.getHealth());
                                node2.put("description", currentHero.getDescription());
                                ArrayNode colorNode = node2.putArray("colors");
                                for (String string : currentHero.getColors()) {
                                    colorNode.add(string);
                                }
                                node2.put("name", currentHero.getName());

                            } else if (playerIdx == TWO) {
                                CardInput currentHero = player2.getHero();

                                node = output.addObject();
                                ObjectNode node2 = objectMapper.createObjectNode();
                                node.put("command", "getPlayerHero").
                                        put("playerIdx", playerIdx).set("output", node2);
                                node2.put("mana", currentHero.getMana());
                                node2.put("health", currentHero.getHealth());
                                node2.put("description", currentHero.getDescription());
                                ArrayNode colorNode = node2.putArray("colors");
                                for (String string : currentHero.getColors()) {
                                    colorNode.add(string);
                                }
                                node2.put("name", currentHero.getName());
                            }
                            break;
                        case "getCardsInHand":
                            ArrayList<CardInput> currentHand = new ArrayList<>();
                            if (playerIdx == ONE) {
                                currentHand = player1.getHand();
                            } else if (playerIdx == TWO) {
                                currentHand = player2.getHand();
                            }
                            node = output.addObject();
                            node.put("command", "getCardsInHand").
                                    put("playerIdx", playerIdx);
                            arrayNode = node.putArray("output");
                            for (CardInput cardInput : currentHand) {
                                ObjectNode node2 = objectMapper.createObjectNode();
                                node2.put("mana", cardInput.getMana());
                                if (!cardInput.getName().equals("Winterfell")
                                        && !cardInput.getName().equals("Heart Hound")
                                        && !cardInput.getName().equals("Firestorm")) {
                                    node2.put("attackDamage", cardInput.getAttackDamage());
                                    node2.put("health", cardInput.getHealth());
                                }
                                node2.put("description", cardInput.getDescription());
                                ArrayNode colorNode = node2.putArray("colors");
                                for (String string : cardInput.getColors()) {
                                    colorNode.add(string);
                                }
                                node2.put("name", cardInput.getName());
                                arrayNode.add(node2);
                            }
                            break;
                        case "placeCard":
                            if (turn == ONE) {
                                currentHand = player1.getHand();
                                if (handIdx >= currentHand.size()) {
                                    break;
                                }
                                if (currentHand.get(handIdx).getName().equals("The Ripper")
                                        || currentHand.get(handIdx).getName().equals("Miraj")
                                        || currentHand.get(handIdx).getName().equals("Goliath")
                                        || currentHand.get(handIdx).getName().equals("Warden")) {
                                    if (currentHand.get(handIdx).getMana() <= player1.getMana()) {
                                        if (table.get(TWO).size() < FIVE) {
                                            player1.setMana(player1.getMana() - currentHand.
                                                    get(handIdx).getMana());
                                            table.get(TWO).add(currentHand.get(handIdx));
                                            currentHand.remove(handIdx);
                                            break;
                                        } else {
                                            output.addObject().put("command", "placeCard").
                                                    put("handIdx", handIdx).
                                                    put("error",
                                                            "Cannot place card on table since "
                                                                    + "row is full.");
                                            break;
                                        }
                                    } else {
                                        output.addObject().put("command", "placeCard").
                                                put("handIdx", handIdx).
                                                put("error",
                                                        "Not enough mana to place card on table.");
                                        break;
                                    }
                                }
                                if (currentHand.get(handIdx).getName().equals("Sentinel")
                                        || currentHand.get(handIdx).getName().
                                        equals("Berserker")
                                        || currentHand.get(handIdx).getName().
                                        equals("The Cursed One")
                                        || currentHand.get(handIdx).getName().
                                        equals("Disciple")) {
                                    if (currentHand.get(handIdx).getMana() <= player1.getMana()) {
                                        if (table.get(THREE).size() < FIVE) {
                                            player1.setMana(player1.getMana() - currentHand.
                                                    get(handIdx).getMana());
                                            table.get(THREE).add(currentHand.get(handIdx));
                                            currentHand.remove(handIdx);
                                            break;
                                        } else {
                                            output.addObject().put("command", "placeCard").
                                                    put("handIdx", handIdx).
                                                    put("error",
                                                            "Cannot place card on table since "
                                                                    + "row is full.");
                                            break;
                                        }
                                    } else {
                                        output.addObject().put("command", "placeCard").
                                                put("handIdx", handIdx).
                                                put("error",
                                                        "Not enough mana to place card on table.");
                                        break;
                                    }

                                }
                                if (currentHand.get(handIdx).getName().equals("Winterfell")
                                        || currentHand.get(handIdx).getName().equals("Heart Hound")
                                        || currentHand.get(handIdx).getName().equals("Firestorm")) {
                                    output.addObject().put("command", "placeCard").
                                            put("handIdx", handIdx).
                                            put("error",
                                                    "Cannot place environment card on table.");
                                    break;
                                }
                            } else if (turn == TWO) {
                                currentHand = player2.getHand();
                                if (handIdx >= currentHand.size()) {
                                    break;
                                }
                                if (currentHand.get(handIdx).getName().equals("The Ripper")
                                        || currentHand.get(handIdx).getName().equals("Miraj")
                                        || currentHand.get(handIdx).getName().equals("Goliath")
                                        || currentHand.get(handIdx).getName().equals("Warden")) {
                                    if (currentHand.get(handIdx).getMana() <= player2.getMana()) {
                                        if (table.get(ONE).size() < FIVE) {
                                            player2.setMana(player2.getMana() - currentHand.
                                                    get(handIdx).getMana());
                                            table.get(ONE).add(currentHand.get(handIdx));
                                            currentHand.remove(handIdx);
                                            break;
                                        } else {
                                            output.addObject().put("command", "placeCard").
                                                    put("handIdx", handIdx).
                                                    put("error",
                                                            "Cannot place card on table since "
                                                                    + "row is full.");
                                            break;
                                        }
                                    } else {
                                        output.addObject().put("command", "placeCard").
                                                put("handIdx", handIdx).
                                                put("error",
                                                        "Not enough mana to place card on table.");
                                        break;
                                    }
                                }
                                if (currentHand.get(handIdx).getName().equals("Sentinel")
                                        || currentHand.get(handIdx).getName().equals("Berserker")
                                        || currentHand.get(handIdx).getName().
                                        equals("The Cursed One")
                                        || currentHand.get(handIdx).getName().equals("Disciple")) {
                                    if (currentHand.get(handIdx).getMana() <= player2.getMana()) {
                                        if (table.get(0).size() < FIVE) {
                                            player2.setMana(player2.getMana() - currentHand.
                                                    get(handIdx).getMana());
                                            table.get(0).add(currentHand.get(handIdx));
                                            currentHand.remove(handIdx);
                                            break;
                                        } else {
                                            output.addObject().put("command", "placeCard").
                                                    put("handIdx", handIdx).
                                                    put("error",
                                                            "Cannot place card on table since "
                                                                    + "row is full.");
                                            break;
                                        }
                                    } else {
                                        output.addObject().put("command", "placeCard").
                                                put("handIdx", handIdx).
                                                put("error",
                                                        "Not enough mana to place card on table.");
                                        break;
                                    }
                                }
                                if (currentHand.get(handIdx).getName().equals("Winterfell")
                                        || currentHand.get(handIdx).getName().equals("Heart Hound")
                                        || currentHand.get(handIdx).getName().equals("Firestorm")) {
                                    output.addObject().put("command", "placeCard").
                                            put("handIdx", handIdx).
                                            put("error",
                                                    "Cannot place environment card on table.");
                                    break;
                                }
                            }
                            System.out.println("A ajuns");
                            break;
                        case "getCardsOnTable":
                            node = output.addObject();
                            node.put("command", "getCardsOnTable");
                            arrayNode = node.putArray("output");
                            for (ArrayList<CardInput> row : table) {
                                ArrayNode arrayNode1 = objectMapper.createArrayNode();
                                for (CardInput cardInput : row) {
                                    ObjectNode node2 = objectMapper.createObjectNode();
                                    node2.put("mana", cardInput.getMana());
                                    if (!cardInput.getName().equals("Winterfell")
                                            && !cardInput.getName().equals("Heart Hound")
                                            && !cardInput.getName().equals("Firestorm")) {
                                        node2.put("attackDamage", cardInput.getAttackDamage());
                                        node2.put("health", cardInput.getHealth());
                                    }
                                    node2.put("description", cardInput.getDescription());
                                    ArrayNode colorNode = node2.putArray("colors");
                                    for (String string : cardInput.getColors()) {
                                        colorNode.add(string);
                                    }
                                    node2.put("name", cardInput.getName());
                                    arrayNode1.add(node2);
                                }
                                arrayNode.add(arrayNode1);
                            }
                            break;
                        case "getPlayerMana":
                            if (playerIdx == ONE) {
                                output.addObject().put("command", "getPlayerMana").
                                        put("playerIdx", playerIdx).
                                        put("output", player1.getMana());
                            } else if (playerIdx == TWO) {
                                output.addObject().put("command", "getPlayerMana").
                                        put("playerIdx", playerIdx).
                                        put("output", player2.getMana());
                            }
                            break;

                        default:
                            break;

                    }
                }
            }




        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
