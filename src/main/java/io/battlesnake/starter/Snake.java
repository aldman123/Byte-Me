package io.battlesnake.starter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;

/**
 * Snake server that deals with requests from the snake engine.
 * Just boiler plate code.  See the readme to get started.
 * It follows the spec here: https://github.com/battlesnakeio/docs/tree/master/apis/snake
 */
public class Snake {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Handler HANDLER = new Handler();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);
    public static long startTime;

	private static MoveSelector moveDecider;
	private static long minDeltaTime, maxDeltaTime;

    /**
     * Main entry point.
     *
     * @param args are ignored.
     */
    public static void main(String[] args) {
		minDeltaTime = Long.MAX_VALUE;
		maxDeltaTime = 0;
		moveDecider = new MoveSelector();
        String port = System.getProperty("PORT");
        if (port != null) {
            LOG.info("Found system provided port: {}", port);
        } else {
            LOG.info("Using default port: {}", port);
            port = "8080";
        }
        port(Integer.parseInt(port));
        get("/", (req, res) -> "Battlesnake documentation can be found at " +
            "<a href=\"https://docs.battlesnake.io\">https://docs.battlesnake.io</a>.");
        post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/ping", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);
    }

    /**
     * Handler class for dealing with the routes set up in the main method.
     */
    public static class Handler {

        /**
         * For the ping request
         */
        private static final Map<String, String> EMPTY = new HashMap<>();

        /**
         * Generic processor that prints out the request and response from the methods.
         *
         * @param req
         * @param res
         * @return
         */
        public Map<String, String> process(Request req, Response res) {
			System.out.println('\n' + "Recieved request!" + '\n');
			startTime = System.currentTimeMillis();
            try {
                JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
                String uri = req.uri();
                LOG.info("{} called with: {}", uri, req.body());
                Map<String, String> snakeResponse;
                if (uri.equals("/start")) {
                    snakeResponse = start(parsedRequest);
                } else if (uri.equals("/ping")) {
                    snakeResponse = ping();
                } else if (uri.equals("/move")) {
                    snakeResponse = move(parsedRequest);
                } else if (uri.equals("/end")) {
                    snakeResponse = end(parsedRequest);
					System.out.println('\n' + "Max Time Elapsed: " + maxDeltaTime
									 + '\n' + "Min Time Elapsed: " + minDeltaTime + '\n');
                } else {
                    throw new IllegalAccessError("Strange call made to the snake: " + uri);
                }
                LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));
            long deltaTime = System.currentTimeMillis() - startTime;
			if (deltaTime > maxDeltaTime) {
				maxDeltaTime = deltaTime;
			} else if (deltaTime < minDeltaTime) {
				minDeltaTime = deltaTime;
			}
			System.out.println('\n'+"Elapsed time: " + (deltaTime) + '\n');
				return snakeResponse;
            } catch (Exception e) {
                LOG.warn("Something went wrong!", e);
                return null;
            }
        }

        /**
         * /ping is called by the play application during the tournament or on play.battlesnake.io to make sure your
         * snake is still alive.
         *
         * @param pingRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return an empty response.
         */
        public Map<String, String> ping() {
            return EMPTY;
        }

        /**
         * /start is called by the engine when a game is first run.
         *
         * @param startRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return a response back to the engine containing the snake setup values.
         */
        public Map<String, String> start(JsonNode startRequest) {
            Map<String, String> response = new HashMap<>();
            response.put("color", "#767E85");
            return response;
        }

        /**
         * /move is called by the engine for each turn the snake has.
         *
         * @param moveRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return a response back to the engine containing snake movement values.
         */
        public Map<String, String> move(JsonNode moveRequest) {
			       System.out.println('\n' + "Move number: " + moveRequest.get("turn") + '\n');
			       Map<String, String> response = new HashMap<>();
			       if (moveDecider == null) {
				           moveDecider = new MoveSelector();
			       }
             if (startTime == 0) {
               startTime = System.currentTimeMillis();
             }
             moveRequest = (JsonNode) ((ObjectNode) moveRequest).put("startTime", startTime);
			       response.put("move", moveDecider.selectMove(moveRequest));

			       //For test cases only
			       if (moveRequest.get("desiredOutcome") != null) {
				           response.put("desiredOutcome", moveRequest.get("desiredOutcome").asText());
			       }
			       return response;
        }

        /**
         * /end is called by the engine when a game is complete.
         *
         * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> end(JsonNode endRequest) {
            Map<String, String> response = new HashMap<>();
            return response;
        }
    }

}
