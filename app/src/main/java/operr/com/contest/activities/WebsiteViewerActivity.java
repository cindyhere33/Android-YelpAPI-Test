package operr.com.contest.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.operr.contest.R;

/**
 * Created by Sindhura on 5/31/2017.
 */

public class WebsiteViewerActivity extends Activity {

    String url = "";
    ProgressBar progressBar;

    //Display the yelp website and reviews for the selected business

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        if (getIntent().hasExtra(getResources().getString(R.string.WEBSITE_URL))) {
            url = getIntent().getExtras().getString(getResources().getString(R.string.WEBSITE_URL));
        }
        if (url == null || url.length() == 0) return;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.bringToFront();

        WebView webView = ((WebView) findViewById(R.id.webView));
        webView.setWebViewClient(new MyBrowser());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }

                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
