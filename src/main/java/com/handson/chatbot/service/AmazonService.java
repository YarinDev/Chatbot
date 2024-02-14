package com.handson.chatbot.service;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AmazonService {

    public static final Pattern PRODUCT_PATTERN = Pattern.compile("<span class=\\\"a-size-medium a-color-base a-text-normal\\\"([^<]+)<\\/span>.*<span class=\\\"a-icon-alt\\\">([^<]+)<\\/span>.*<span class=\\\"a-offscreen\\\">([^<]+)<\\/span>");

    public String searchProducts(String keyword) throws IOException {
        return parseProductHtml(getProductHtml(keyword));
    }

    private String parseProductHtml(String html) {
        String res = "";
        Matcher matcher = PRODUCT_PATTERN.matcher(html);
        while (matcher.find()) {
            res += matcher.group(1) + " - " + matcher.group(2) + ", price:" + matcher.group(3) + "<br>\n";
        }
        return res;
    }

    private String getProductHtml(String keyword) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://www.amazon.com/s?k=" + keyword + "&crid=ART35D7G2AUX&sprefix=ipod%2Caps%2C274&ref=nb_sb_noss_1")
                .method("GET", null)
                .addHeader("authority", "www.amazon.com")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("accept-language", "en-US,en;q=0.9")
                .addHeader("cache-control", "max-age=0")
                .addHeader("cookie", "aws-target-data=%7B%22support%22%3A%221%22%7D; aws-target-visitor-id=1707653935651-910511.44_0; aws-ubid-main=120-4478705-0043316; remember-account=true; aws-account-alias=995553441267; regStatus=registered; aws-mkto-trk=id%3A112-TZM-766%26token%3A_mch-aws.amazon.com-1707653935132-97127; aws_lang=en; AMCVS_7742037254C95E840A4C98A6%40AdobeOrg=1; AMCV_7742037254C95E840A4C98A6%40AdobeOrg=1585540135%7CMCIDTS%7C19767%7CMCMID%7C77038904326875201934567423480424331959%7CMCAAMLH-1708425233%7C7%7CMCAAMB-1708425233%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1707827633s%7CNONE%7CMCAID%7CNONE%7CvVersion%7C4.4.0; s_campaign=ps%7Cfce796e8-4ceb-48e0-9767-89f7873fac3d; s_cc=true; s_eVar60=fce796e8-4ceb-48e0-9767-89f7873fac3d; aws-userInfo=%7B%22arn%22%3A%22arn%3Aaws%3Aiam%3A%3A995553441267%3Auser%2Fyaring%22%2C%22alias%22%3A%22995553441267%22%2C%22username%22%3A%22yaring%22%2C%22keybase%22%3A%22P45doJ8pfSAjjGpDmBhFli%2BA1qpZ7szOtrNAXVH%2BQTk%5Cu003d%22%2C%22issuer%22%3A%22http%3A%2F%2Fsignin.aws.amazon.com%2Fsignin%22%2C%22signinType%22%3A%22PUBLIC%22%7D; aws-userInfo-signed=eyJ0eXAiOiJKV1MiLCJrZXlSZWdpb24iOiJ1cy1lYXN0LTIiLCJhbGciOiJFUzM4NCIsImtpZCI6IjIzMjI3NDlhLTM1MWQtNDc4NS1hOGUzLTMwMTMwNWQ3NmMzMCJ9.eyJzdWIiOiI5OTU1NTM0NDEyNjciLCJzaWduaW5UeXBlIjoiUFVCTElDIiwiaXNzIjoiaHR0cDpcL1wvc2lnbmluLmF3cy5hbWF6b24uY29tXC9zaWduaW4iLCJrZXliYXNlIjoiUDQ1ZG9KOHBmU0FqakdwRG1CaEZsaStBMXFwWjdzek90ck5BWFZIK1FUaz0iLCJhcm4iOiJhcm46YXdzOmlhbTo6OTk1NTUzNDQxMjY3OnVzZXJcL3lhcmluZyIsInVzZXJuYW1lIjoieWFyaW5nIn0.IoHy2vplVNVBX_xk2t6pkxFOFIa_o5dg1m4lwxaMgb0JeTXLER6WBD4ttJIzeyqi0XdhbAofaOdvci_kd6M7XAadj1tGvYC1d-iAmWD8TscVFxBR8kr68xHnLePrczm1; noflush_awsccs_sid=de8380d473f975029420a1dad5437b085e343d8e47c2706a76604c2c31a525d7; session-id=142-8683627-9632948; session-id-time=2082787201l; i18n-prefs=USD; skin=noskin; ubid-main=130-8103113-1968134; session-token=gYmW3bdUedN6kj7pKe6VGb6yuIG/aUH17jeKfH9YHg6lV07z6BpfQat24QZg2Qfvlq7VBvPclRjx4ZO1x6HwEyOpotUk58dLgR2E+jX871wXV7+hVU4CD3+CYI06OeJW7+em/L4nctSGIyCIuI6kHo2qf+5TVQw/dB+WdXtnNnROSR9+7JIVybjj64+sNC4W9alpd7gqgcD+rDvt4xCS0JjgcyBsyv8Ud6sZSMDkhopTfDGLh+x+flamtv5mjf0dck7sYXdYXmmPe2fB7TFWLOa4JmGd+UTNWNMsXtlRuyyc7gLKvTLu8pfUhUolYvhRdLUDotXgDqhfUPgqj0ZIFiqBvOh49qdO; JSESSIONID=E09D6E37793387F527AE905AE6218D11; csm-hit=tb:KR5X73PWSJWV4W97EG8Z+s-D3DDDESE9NZWFBTHMYTQ|1707913763447&t:1707913763447&adb:adblk_no")
                .addHeader("device-memory", "8")
                .addHeader("downlink", "8.1")
                .addHeader("dpr", "1")
                .addHeader("ect", "4g")
                .addHeader("referer", "https://www.amazon.com/")
                .addHeader("rtt", "0")
                .addHeader("sec-ch-device-memory", "8")
                .addHeader("sec-ch-dpr", "1")
                .addHeader("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Linux\"")
                .addHeader("sec-ch-viewport-width", "988")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-user", "?1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                .addHeader("viewport-width", "988")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();

    }
}