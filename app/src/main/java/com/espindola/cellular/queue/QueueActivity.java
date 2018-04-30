package com.espindola.cellular.queue;

import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.espindola.cellular.queue.adapters.QueueAdapter;
import com.espindola.cellular.queue.listeners.OnTaskClickListener;
import com.espindola.cellular.queue.listeners.UserTaskListener;
import com.espindola.cellular.queue.tasks.UserTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueActivity extends AppCompatActivity
        implements
        UserTaskListener,
        View.OnClickListener,
        OnTaskClickListener {

    public String TAG = getClass().getSimpleName();
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private ProgressBar progressBar;
    private TextView textView;
    private Button button;
    private RecyclerView recyclerView;

    private Handler handler;
    private QueueAdapter adapter;

    private final List<UserTask> threadPool = Collections.synchronizedList(new ArrayList<UserTask>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCollapsingToolbar();

        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.tv_title);
        button = findViewById(R.id.btn_start);
        recyclerView = findViewById(R.id.recycler_view);

        button.setOnClickListener(this);
        handler = new QueueHandler(this);
        adapter = new QueueAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        for (UserTask task : threadPool) {
            task.interrupt();
        }
        threadPool.clear();
        // this avoids memory leak
        recyclerView.setAdapter(null);
        recyclerView = null;
        Toast.makeText(this, "Goodbye!", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }


    @Override
    public void completeTask(final UserTask task) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int position = threadPool.indexOf(task);
                if (position < 0)
                {
                    Log.d(TAG, "completeTask: Task was not found!");
                    return;
                }
                threadPool.remove(position);
                recyclerView.removeViewAt(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position,threadPool.size());
                Toast.makeText(getBaseContext(), task.getName()+" completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void updateTask(UserTask task) {

        final int position = threadPool.indexOf(task);
        if (position < 0)
        {
            Log.d(TAG, "updateTask: Task was not found!");
            return;
        }

        threadPool.set(position, task);
        adapter.notifyDataSetChanged();

        Log.d(TAG, "QueueUpdateProgress: "+"pid:"+task.getId()+" " );
//        progressBar.setProgress(task.getProgress());
//        textView.setText(String.valueOf(task.getProgress()));
    }

    @Override
    public void onClick(View view) {

        if (coresConstrain())
        {
            Toast.makeText(getBaseContext(),
                    " No more threads available: "+NUMBER_OF_CORES, Toast.LENGTH_SHORT).show();
            return;
        }

        UserTask userTask = new UserTask();
        userTask.setHandler(handler);
        userTask.setListener(this);

        threadPool.add(userTask);
        adapter.setTaskList(threadPool);
        adapter.notifyDataSetChanged();

        userTask.start();
        Log.d(TAG, "onClick: New thread created: " + userTask.toString());
    }

    @Override
    public void onClick(UserTask userTask, View view) {
        Toast.makeText(this, userTask.getName(), Toast.LENGTH_SHORT).show();
        view.setFocusable(true);
    }

    @Override
    public void onLongClick(UserTask userTask) {

    }

    /*
    * @pid: look-up id
    * This method search linearly over the thread list
    * (unused)
    * */
    private int getIndex(long pid){
        int index = -1;
        synchronized (threadPool){
            for (UserTask u : threadPool){
                if (u.getId() == pid){
                    index = threadPool.indexOf(u);
                    break;
                }
            }
        }
        return index;
    }

    private boolean coresConstrain(){
        int threadPoolSize = threadPool.size();
        return threadPoolSize >= NUMBER_OF_CORES;
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
