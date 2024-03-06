package com.handson.chatbot.service;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@Service
public class IMDbService {
    // This PRODUCT_PATTERN is used compile the regex pattern to get the movie name and rating
    public static final Pattern MOVIE250_PATTERN = Pattern.compile("tabindex=\"0\"><h3 class=\"ipc-title__text\">(\\d+\\.[^<]+)<\\/h3>.*?aria-label=\"IMDb rating: (\\d+\\.\\d+)\"");

    public String searchTopMovies(String keyword) throws IOException {
        return parseTopMoviesHtml(getTopMoviesHtml(keyword));
    }

    // This parseProductHtml method is used to parse the HTML of the movie page using regex to get the movie name and rating
    private String parseTopMoviesHtml(String html) {
        String res = "";
        Matcher matcher = MOVIE250_PATTERN.matcher(html);
        // This while loop is used to find the movie name and rating and append it to the res string
        while (matcher.find()) {
            res += matcher.group(1) + "\nRating: " + matcher.group(2) + "\n";
        }
        return res;
    }

    // This getTopMoviesHtml method is used to get the HTML of the top 250 movies page from imdb
    private String getTopMoviesHtml(String keyword) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://www.imdb.com/chart/top/")
                .method("GET", null)
                .addHeader("authority", "www.imdb.com")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("accept-language", "en-US,en;q=0.9")
                .addHeader("cache-control", "max-age=0")
                .addHeader("cookie", "session-id=142-1182356-4192552; session-id-time=2082787201l; ad-oo=0; ubid-main=133-3401308-8830412; uu=eyJpZCI6InV1Yzk4NjMzZmRjZjI2NDg0YWEyNjUiLCJwcmVmZXJlbmNlcyI6eyJmaW5kX2luY2x1ZGVfYWR1bHQiOmZhbHNlfX0=; session-token=S/CQA+17uEbNASKusNKaaus72IHJ9mfIDPEl+mqn4JkjX/CvupgdzMh1W1mlHTc7P3p+THg0Fg73PrT22WS9vUau6XdAzVG7ByhBrf3twX/J5LJE3UC1qyEnvK186G2ANwME23vQ7oP6ACKA1qC/2E42jPqo8N6+d1cicBDAqZ4Xxu/hPCngaW2QC8W3rgDluTrCM9jVLCu32yk84ksCquSssCwpBSNUHhrh49vm3ix1G7nhNYVMy50RHsnv+rBSM6q3EdgcXOzXrcMGlA6337lUR7IIYRix7fsJuH2/Rzi5yv4ylqoLnjKvHNdQ7pYTS47+6DKJeTnjl+qyC14N3cWkT54o/k/e; csm-hit=tb:8C2G9PPHZD8XN0RR0764+s-8C2G9PPHZD8XN0RR0764|1709559862642&t:1709559862642&adb:adblk_no; ci=e30; session-id=142-1182356-4192552; session-id-time=2082787201l; session-token=S/CQA+17uEbNASKusNKaaus72IHJ9mfIDPEl+mqn4JkjX/CvupgdzMh1W1mlHTc7P3p+THg0Fg73PrT22WS9vUau6XdAzVG7ByhBrf3twX/J5LJE3UC1qyEnvK186G2ANwME23vQ7oP6ACKA1qC/2E42jPqo8N6+d1cicBDAqZ4Xxu/hPCngaW2QC8W3rgDluTrCM9jVLCu32yk84ksCquSssCwpBSNUHhrh49vm3ix1G7nhNYVMy50RHsnv+rBSM6q3EdgcXOzXrcMGlA6337lUR7IIYRix7fsJuH2/Rzi5yv4ylqoLnjKvHNdQ7pYTS47+6DKJeTnjl+qyC14N3cWkT54o/k/e")
                .addHeader("referer", "https://www.google.com/")
                .addHeader("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Linux\"")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-user", "?1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    // This PRODUCT_PATTERN is used compile the regex pattern to get the movie name and rating
    public static final Pattern MOVIE_SEARCH_PATTERN = Pattern.compile("\"titleNameText\":([^,]+),\"titleReleaseText\":\"([^\"]+)\".*?\"topCredits\":\\[(.*?)\\]");

    public String searchMovie(String keyword) throws IOException {
        return parseMovieHtml(getMovieSearchHtml(keyword));
    }
    public String searchMovieJson(String keyword) throws IOException {
        String html = getMovieSearchHtml(keyword);
        return parseMovieJson(html);
    }
    // Function to parse HTML and return movie details in JSON format
    private String parseMovieJson(String html) {
        StringBuilder responseBuilder = new StringBuilder();

        Matcher matcher = MOVIE_SEARCH_PATTERN.matcher(html);

        while (matcher.find()) {
            responseBuilder.append("Movie Name: ").append(matcher.group(1)).append("\n");
            responseBuilder.append("Year: ").append(matcher.group(2)).append("\n");

            String[] topCredits = matcher.group(3).split(",");
            responseBuilder.append("Top Credits:\n");
            for (String credit : topCredits) {
                responseBuilder.append("- ").append(credit.trim()).append("\n");
            }
            responseBuilder.append("\n");
        }

        return responseBuilder.toString();
    }

    // This parseProductHtml method is used to parse the HTML of the movie page using regex to get the movie name and rating
    private String parseMovieHtml(String html) {
        String res = "";
        Matcher matcher = MOVIE_SEARCH_PATTERN.matcher(html);
        // This while loop is used to find the movie name and rating and append it to the res string
        while (matcher.find()) {
            res += "Movie name: " + matcher.group(1) + " ,Year:" + matcher.group(2) + " ,Top Credits: " + matcher.group(3) + "\n";
        }
        return res;
    }


    // This getTopMoviesHtml method is used to get the HTML of the top 250 movies page from imdb
    private String getMovieSearchHtml(String keyword) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://www.imdb.com/find/?q=" + keyword + "&ref_=nv_sr_sm")
                .method("GET", null)
                .addHeader("authority", "www.imdb.com")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("accept-language", "en-US,en;q=0.9")
                .addHeader("cache-control", "max-age=0")
                .addHeader("cookie", "session-id=142-1182356-4192552; session-id-time=2082787201l; ad-oo=0; ubid-main=133-3401308-8830412; uu=eyJpZCI6InV1Yzk4NjMzZmRjZjI2NDg0YWEyNjUiLCJwcmVmZXJlbmNlcyI6eyJmaW5kX2luY2x1ZGVfYWR1bHQiOmZhbHNlfX0=; session-token=XBtnJAZfyaGIkaotjE5a01RuQeSzQxV7n77JN3tgoIWEUHrWNTqhfCJv4iOfV83cB3vYieV0Wk9C8DiwyiIo2dXxhbvh5AxUYlVT44cMk3uhYUt/h00gTOJsfIanAVsx8+2DJ0JVcp+wyTShdR40bGbbM6dwOei3ESV/xRnbZYc/1geLfyj81eGUgXycYXqEp+kqoVEMC3CfxGIugsbFwyVv/Auq8EVWnm1Yem0CGhrtwMwhytqFhXpqJ5LHWo7bZOanluUUFnRrk5nfLJ+zQHiGIITQlC8w1pOrRsEIoneHp6kRM3O0N5e5tnlqAEWT0fm4LrAw8rcCooOo6JS+/6yg+awWVeR9; ci=e30; csm-hit=tb:s-59G7P67NZ9RYP1J4N9YY|1709634357305&t:1709634357635&adb:adblk_no; session-id=142-1182356-4192552; session-id-time=2082787201l; session-token=XBtnJAZfyaGIkaotjE5a01RuQeSzQxV7n77JN3tgoIWEUHrWNTqhfCJv4iOfV83cB3vYieV0Wk9C8DiwyiIo2dXxhbvh5AxUYlVT44cMk3uhYUt/h00gTOJsfIanAVsx8+2DJ0JVcp+wyTShdR40bGbbM6dwOei3ESV/xRnbZYc/1geLfyj81eGUgXycYXqEp+kqoVEMC3CfxGIugsbFwyVv/Auq8EVWnm1Yem0CGhrtwMwhytqFhXpqJ5LHWo7bZOanluUUFnRrk5nfLJ+zQHiGIITQlC8w1pOrRsEIoneHp6kRM3O0N5e5tnlqAEWT0fm4LrAw8rcCooOo6JS+/6yg+awWVeR9")
                .addHeader("referer", "https://www.imdb.com/")
                .addHeader("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Linux\"")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-user", "?1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}