package com.android.example.whowroteitloader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class BookLoader extends AsyncTaskLoader<String> {

  private String mQueryString;

  public BookLoader(Context context, String queryString) {
    super(context);
    mQueryString = queryString;
  }

  @Override
  protected void onStartLoading() {
    forceLoad(); // Starts the loadInBackground method
  }

  @Override
  public String loadInBackground() {
    return NetworkUtils.getBookInfo(mQueryString);
  }
}
