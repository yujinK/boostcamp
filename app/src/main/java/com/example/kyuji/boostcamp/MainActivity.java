package com.example.kyuji.boostcamp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int TIMEOUT_VALUE = 1000;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Button btnSearch;
    EditText editKeyword;

    List<Movie> movieList = new ArrayList<>();
    MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
    }

    public void setup() {
        btnSearch = findViewById(R.id.btn_search);
        editKeyword = findViewById(R.id.edit_keyword);

        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        movieAdapter = new MovieAdapter(movieList);
        mRecyclerView.setAdapter(movieAdapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new SearchMoviesTask().execute(editKeyword.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    class SearchMoviesTask extends AsyncTask <String, Void, String> {
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("잠시만 기다려주세요");
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return search();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            movieListParser(s);
            movieAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(movieAdapter);

            if (movieList.size() == 0) {
                Toast.makeText(MainActivity.this, String.format("'%s' 검색결과가 없습니다", editKeyword.getText().toString()), Toast.LENGTH_SHORT).show();
            }

            asyncDialog.dismiss();
        }
    }

    public String search() {
        try {
            String keyword = URLEncoder.encode(editKeyword.getText().toString(), "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + keyword;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setConnectTimeout(TIMEOUT_VALUE);
            con.setReadTimeout(TIMEOUT_VALUE);
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", getString(R.string.CLIENT_ID));
            con.setRequestProperty("X-Naver-Client-Secret", getString(R.string.CLIENT_SECRET));
            int responseCode = con.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();

        } catch(Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public void movieListParser(String jsonString) {
        try {
            JSONArray arr = new JSONObject(jsonString).getJSONArray("items");
            movieList.clear();

            if (arr.length() == 0) return;

            for (int i=0; i<arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Movie movie = new Movie(obj.getString("title"),
                        obj.getString("link"),
                        obj.getString("image"),
                        obj.getString("pubDate"),
                        obj.getString("director"),
                        obj.getString("actor"),
                        Float.parseFloat(obj.getString("userRating"))/2);

                movieList.add(movie);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
