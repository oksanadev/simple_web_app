package oks.dev.com.simplewebapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebActivity extends Activity {
    WebView wvMain;
    View viewProgress;
    Handler handler = new Handler();
    Runnable callbackShowWebView;
    AlertDialog alertError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        viewProgress = findViewById(R.id.vProgress);
        wvMain = (WebView) findViewById(R.id.wvMain);

        wvMain.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (callbackShowWebView != null) {
                    handler.removeCallbacks(callbackShowWebView);
                }
                showWebView(false);
                showPageError();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

        wvMain.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    if (alertError == null) {
                        callbackShowWebView = new Runnable() {
                            @Override
                            public void run() {
                                showWebView(true);
                                callbackShowWebView = null;
                            }
                        };
                        handler.postDelayed(callbackShowWebView, 100);
                    }
                }
            }
        });
        wvMain.getSettings().setJavaScriptEnabled(true);
        wvMain.getSettings().setJavaScriptCanOpenWindowsAutomatically(
                true);

        wvMain.getSettings().setLoadWithOverviewMode(true);
        wvMain.getSettings().setUseWideViewPort(true);
        wvMain.getSettings().setBuiltInZoomControls(true);

        wvMain.getSettings().setBuiltInZoomControls(true);
        wvMain.getSettings().setDisplayZoomControls(false);

        wvMain.loadUrl(BuildConfig.BASE_URL);
    }

    public void showWebView(boolean isShowWebView) {
        viewProgress.setVisibility(isShowWebView ? View.GONE : View.VISIBLE);
    }

    public void showPageError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.errorTitle);
        builder.setMessage(R.string.error_msg);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertError = null;
                wvMain.reload();
            }
        });
        builder.setCancelable(false);
        alertError = builder.create();
        alertError.show();
    }

    @Override
    public void onBackPressed() {
        if (wvMain.canGoBack()) {
            wvMain.goBack();
        } else {
            super.onBackPressed();

        }
    }
}
