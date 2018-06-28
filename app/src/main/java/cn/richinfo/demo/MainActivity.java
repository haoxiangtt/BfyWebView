package cn.richinfo.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DownloadListener{
    private WebView mWebView;
    private EditText mEtUrl;
    private Button mBtLoad;
    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtUrl = (EditText)findViewById(R.id.et_url);
        mBtLoad = (Button)findViewById(R.id.bt_load);
        mProgressbar = (ProgressBar)findViewById(R.id.progressbar);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressbar.setVisibility(View.GONE);
            }
        });
        mWebView.setDownloadListener(this);
        WebSettings settings = mWebView.getSettings();
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + ";light139AndroidVer");
//        mWebView.loadUrl("javascript:alert('hello world')");
        mBtLoad.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_load) {
            String url = mEtUrl.getText().toString();
            if (!TextUtils.isEmpty(url)) {
                if (!url.startsWith("http://")
                        && !url.startsWith("https://")
                        && !url.startsWith("javascript:")
                        && !url.startsWith("file://")
                        && !url.startsWith("ftp://")) {
                    url = "http://" + url;
                }
                mWebView.loadUrl(url);
            }
        }
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
