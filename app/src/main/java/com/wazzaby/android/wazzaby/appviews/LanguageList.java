package com.wazzaby.android.wazzaby.appviews;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.adapter.LanguageListAdapter;
import com.wazzaby.android.wazzaby.adapter.categorieProbAdapter;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Categorie_prob;
import java.util.ArrayList;
import java.util.List;

public class LanguageList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LanguageListAdapter allUsersAdapter;
    private List<Categorie_prob> data = new ArrayList<>();
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private Resources res;
    private DatabaseHandler database;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_list);

        recyclerView =  findViewById(R.id.my_recycler_view);
        toolbar = findViewById(R.id.toolbar);
        coordinatorLayout =  findViewById(R.id.coordinatorLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        res = getResources();
        database = new DatabaseHandler(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(res.getString(R.string.language_list));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SaveLanguage();
        allUsersAdapter = new LanguageListAdapter(this, data);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allUsersAdapter);
    }

    public void SaveLanguage() {
        data.add(new Categorie_prob(0, "Français",""));
        data.add(new Categorie_prob(0, "English",""));
        data.add(new Categorie_prob(0, "Español",""));
        data.add(new Categorie_prob(0, "日本の",""));
        data.add(new Categorie_prob(0, "العربية",""));
        data.add(new Categorie_prob(0, "Deutsch",""));
        data.add(new Categorie_prob(0, "русский",""));
        data.add(new Categorie_prob(0, "Italiano",""));
        data.add(new Categorie_prob(0, "Português",""));
    }
}
