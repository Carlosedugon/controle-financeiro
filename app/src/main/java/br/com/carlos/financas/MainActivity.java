package br.com.carlos.financas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Minhas Finanças — app de controle financeiro.
 * Toda a interface fica em assets/index.html (funciona offline).
 * Os dados ficam no armazenamento local do WebView (localStorage) deste aparelho.
 */
public class MainActivity extends Activity {

    private static final int REQ_ABRIR = 1;
    private static final int REQ_SALVAR = 2;

    private WebView web;
    private ValueCallback<Uri[]> fileCallback;
    private String conteudoParaSalvar;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0F766E"));

        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setTextZoom(100);

        web.addJavascriptInterface(new Ponte(), "Android");
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                try {
                    startActivityForResult(Intent.createChooser(i, "Escolher backup"), REQ_ABRIR);
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }
                return true;
            }
        });

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl("file:///android_asset/index.html");
        }
    }

    /** Ponte chamada pelo JavaScript para exportar o backup. */
    private class Ponte {
        @JavascriptInterface
        public void saveFile(final String nome, final String conteudo) {
            runOnUiThread(() -> {
                conteudoParaSalvar = conteudo;
                Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("application/json");
                i.putExtra(Intent.EXTRA_TITLE, nome);
                try {
                    startActivityForResult(i, REQ_SALVAR);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Não foi possível abrir o seletor de arquivos", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ABRIR) {
            Uri[] r = null;
            if (resultCode == RESULT_OK && data != null && data.getData() != null) r = new Uri[]{data.getData()};
            if (fileCallback != null) fileCallback.onReceiveValue(r);
            fileCallback = null;
        } else if (requestCode == REQ_SALVAR) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null && conteudoParaSalvar != null) {
                try (OutputStream os = getContentResolver().openOutputStream(data.getData())) {
                    os.write(conteudoParaSalvar.getBytes(StandardCharsets.UTF_8));
                    Toast.makeText(this, "Backup salvo", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao salvar o backup", Toast.LENGTH_LONG).show();
                }
            }
            conteudoParaSalvar = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        // Deixa o app voltar de telas internas / fechar formulários antes de sair
        web.evaluateJavascript("(window.appBack && window.appBack()) ? '1' : '0'", valor -> {
            if (!"\"1\"".equals(valor)) MainActivity.super.onBackPressed();
        });
    }
}
