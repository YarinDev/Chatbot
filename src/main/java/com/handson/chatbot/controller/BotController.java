package com.handson.chatbot.controller;

import com.handson.chatbot.service.IMDbService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@Api(tags = "IMDB Service")
@Controller
@RequestMapping("/bot")
public class BotController {

    @Autowired
    private IMDbService IMDbService;

    @ApiOperation(value = "Get top 250 movies from IMDb")
    @RequestMapping(value = "/IMDB/top250", method = RequestMethod.GET)
    public ResponseEntity<?> getTopMovies() throws IOException {
        String topMovies = IMDbService.searchTopMovies(null);
        return new ResponseEntity<>(topMovies, HttpStatus.OK);
    }

    @ApiOperation(value = "Search movies on IMDb")
    @RequestMapping(value = "/IMDB/search", method = RequestMethod.GET)
    public ResponseEntity<?> searchMovie(@RequestParam String keyword) throws IOException {
        String movieDetails = IMDbService.searchMovie(keyword);
        return new ResponseEntity<>(movieDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = {RequestMethod.POST})
    public ResponseEntity<?> getBotResponse(@RequestBody BotQuery query) throws IOException {
        HashMap<String, String> params = query.getQueryResult().getParameters();
        String res = "Not found";
        res = IMDbService.searchMovieJson(params.get("movie"));

        return new ResponseEntity<>(BotResponse.of(res), HttpStatus.OK);
    }

    static class BotQuery {
        QueryResult queryResult;

        public QueryResult getQueryResult() {
            return queryResult;
        }
    }

    static class QueryResult {
        HashMap<String, String> parameters;

        public HashMap<String, String> getParameters() {
            return parameters;
        }
    }

    static class BotResponse {
        String fulfillmentText;
        String source = "BOT";

        public String getFulfillmentText() {
            return fulfillmentText;
        }

        public String getSource() {
            return source;
        }

        public static BotResponse of(String fulfillmentText) {
            BotResponse res = new BotResponse();
            res.fulfillmentText = fulfillmentText;
            return res;
        }
    }
}
