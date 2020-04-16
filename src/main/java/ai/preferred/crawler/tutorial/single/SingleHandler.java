/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.preferred.crawler.tutorial.single;

import ai.preferred.crawler.tutorial.EntityCSVStorage;
import ai.preferred.venom.Handler;
import ai.preferred.venom.Session;
import ai.preferred.venom.Worker;
import ai.preferred.venom.job.Scheduler;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.response.VResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;


/**
 * @author Ween Jiann Lee
 */
public class SingleHandler implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleHandler.class);

    private static int count = 2;

    @Override
    public void handle(Request request, VResponse response, Scheduler scheduler, Session session, Worker worker) {
        LOGGER.info("processing: {}", request.getUrl());

        // JSoup
        final Document document = response.getJsoup();

        Elements elements = document.select("#genre_flyout > div > div:nth-child(2) > a:nth-child(" + count + ")");
        for (Element e : elements) {
            Element element = e.selectFirst("a");
            final String url = element.attr("href");
            scheduler.add(new VRequest(url), this);

            try (PrintStream out = new PrintStream(new FileOutputStream("data/categories.csv", true))) {
                out.println(url);
            } catch (FileNotFoundException error) {
               LOGGER.error("File not found");
            }
        }
        count++;
    }
}
