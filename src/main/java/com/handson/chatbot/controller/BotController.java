package com.handson.chatbot.controller;

import com.handson.chatbot.service.AmazonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = "IMDB Service")
@Controller
@RequestMapping("/bot")
public class BotController {

    @Autowired
    private AmazonService amazonService;

    @ApiOperation(value = "Get top 250 movies from IMDb")
    @RequestMapping(value = "/IMDB/top250", method = RequestMethod.GET)
    public ResponseEntity<?> getTopMovies() throws IOException {
        String topMovies = amazonService.searchTopMovies(null);
        return new ResponseEntity<>(topMovies, HttpStatus.OK);
    }

    @ApiOperation(value = "Search movies on IMDb")
    @RequestMapping(value = "/IMDB/search", method = RequestMethod.GET)
    public ResponseEntity<?> searchMovie(@RequestParam String keyword) throws IOException {
        String movieDetails = amazonService.searchMovie(keyword);
        return new ResponseEntity<>(movieDetails, HttpStatus.OK);
    }
}
