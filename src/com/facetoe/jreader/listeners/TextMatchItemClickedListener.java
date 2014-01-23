package com.facetoe.jreader.listeners;

import com.facetoe.jreader.githubapi.GitHubAPI;
import com.facetoe.jreader.githubapi.GitHubAPIException;
import com.facetoe.jreader.githubapi.ObjectQuery;
import com.facetoe.jreader.githubapi.ObjectResponse;
import com.facetoe.jreader.helpers.Utilities;
import com.facetoe.jreader.ui.JReader;
import com.facetoe.jreader.ui.OnTextMatchItemClickedListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * JReader
 * Created by facetoe on 23/01/14.
 */
public class TextMatchItemClickedListener implements OnTextMatchItemClickedListener {
    private final Logger log = Logger.getLogger(this.getClass());

    JReader jreader;
    String searchTerm;
    public TextMatchItemClickedListener(JReader jreader, String searchTerm) {
        this.jreader = jreader;
        this.searchTerm = searchTerm;
    }

    @Override
    public void textMatchItemClicked(final String objectUrl) {
            updateStatus("Requesting object...");
            new SwingWorker() {
                URL url;
                String title;

                @Override
                protected Object doInBackground() throws Exception {
                    ObjectResponse response = (ObjectResponse) GitHubAPI.sendRequest(new ObjectQuery(objectUrl));
                    url = new URL(response.getHtml_url()
                            .replace("https://github.com", "https://raw.github.com")
                            .replace("/blob", ""));
                    title = Utilities.extractFileName(url.getFile());
                    return null;
                }

                @Override
                protected void done() {
                    updateStatus("Downloading " + title);
                    jreader.createAndShowNewSourcePanel(url, title);
                    updateStatus("");
                }
            }.execute();
    }

    private void updateStatus(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jreader.updateStatus(message);
            }
        });
    }
}