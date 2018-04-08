package com.android.example.whowroteitloader;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
  EditText mBookInput;
  TextView mAuthorText, mTitleText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mBookInput = (EditText) findViewById(R.id.bookInput);
    mTitleText = (TextView) findViewById(R.id.titleText);
    mAuthorText = (TextView) findViewById(R.id.authorText);

    if(getSupportLoaderManager().getLoader(0)!= null){
      getSupportLoaderManager().initLoader(0, null, this);
    }
  }

  public void searchBooks(View view) {
    String queryString = mBookInput.getText().toString();

    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

    if(networkInfo != null && networkInfo.isConnected() && queryString.length()!=0){
      mAuthorText.setText("");
      mTitleText.setText(R.string.loading);
      Bundle queryBundle = new Bundle();
      queryBundle.putString("queryString", queryString);
      getSupportLoaderManager().restartLoader(0, queryBundle, this);
    }

    else{
      if(queryString.length() == 0){
        mAuthorText.setText("");
        mTitleText.setText("Please enter a search term");
      } else{
        mAuthorText.setText("");
        mTitleText.setText("Please check your network connection and try again.");
      }
    }
  }

  @Override
  public Loader<String> onCreateLoader(int id, Bundle args) {
    return new BookLoader(this, args.getString("queryString"));
  }

  @Override
  public void onLoadFinished(Loader<String> loader, String data) {
    try{
      JSONObject jsonObject = new JSONObject(data);
      JSONArray itemsArray = jsonObject.getJSONArray("items");

      //Iterate through the results
      for (int i = 0; i < itemsArray.length(); i++) {
        JSONObject book = itemsArray.getJSONObject(i); //Get current item
        String title = null, authors = null;
        JSONObject volumeInfo = book.getJSONObject("volumeInfo");

        try{
          title = volumeInfo.getString("title");
          authors = volumeInfo.getString("authors");
        } catch (Exception e){
          e.printStackTrace();
        }

        //If both title and author exist, update the TextViews and return
        if(title != null && authors != null){
          mTitleText.setText(title);
          mAuthorText.setText(authors);
          return;
        }
      }

      mTitleText.setText(R.string.no_results);
      mAuthorText.setText("");

    } catch (JSONException e) {
      mTitleText.setText(R.string.no_results);
      mAuthorText.setText("");
      e.printStackTrace();
    }
  }

  @Override
  public void onLoaderReset(Loader<String> loader) {

  }
}
