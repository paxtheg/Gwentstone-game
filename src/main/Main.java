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

                player1.getHero().setHealth(30);
                player2.getHero().setHealth(30);

                int turn = inputData.getGames().get(i).getStartGame().getStartingPlayer();
                for (int j = 0; j < inputData.getGames().get(i).getActions().size(); j++) {
                    int idx = inputData.getGames().get(i).getActions().
                            get(j).getPlayerIdx();

                    switch (inputData.getGames().get(i).getActions().get(j).getCommand()) {
                        case "endPlayerTurn":
                            if (turn == 1) {
                                turn = 2;
                            } else if (turn == 2) {
                                turn = 1;
                            }
                            break;
                        case "getPlayerTurn":
                            output.addObject().put("command", "getPlayerTurn").
                                    put("output", turn);
                            break;
                        case "getPlayerDeck":
                            ArrayList<CardInput> currentDeck = new ArrayList<>();
                            if (idx == 1) {
                                currentDeck = player1.getDeck();
                            } else if (idx == 2) {
                                currentDeck = player2.getDeck();
                            }
                            ObjectNode node = output.addObject();
                            node.put("command", "getPlayerDeck").
                                    put("playerIdx", idx);
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
                            if (idx == 1) {
                                CardInput currentHero = player1.getHero();

                                node = output.addObject();
                                ObjectNode node2 = objectMapper.createObjectNode();
                                node.put("command", "getPlayerHero").
                                        put("playerIdx", idx).set("output", node2);
                                node2.put("mana", currentHero.getMana());
                                node2.put("health", currentHero.getHealth());
                                node2.put("description", currentHero.getDescription());
                                ArrayNode colorNode = node2.putArray("colors");
                                for (String string : currentHero.getColors()) {
                                    colorNode.add(string);
                                }
                                node2.put("name", currentHero.getName());

                            } else if (idx == 2) {
                                CardInput currentHero = player2.getHero();

                                node = output.addObject();
                                ObjectNode node2 = objectMapper.createObjectNode();
                                node.put("command", "getPlayerHero").
                                        put("playerIdx", idx).set("output", node2);
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
                        default:
                            break;

                    }
                }
            }




        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
