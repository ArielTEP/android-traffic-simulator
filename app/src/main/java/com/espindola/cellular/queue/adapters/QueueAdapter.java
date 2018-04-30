package com.espindola.cellular.queue.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.espindola.cellular.queue.R;
import com.espindola.cellular.queue.tasks.UserTask;
import com.espindola.cellular.queue.listeners.OnTaskClickListener;

import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder>{

    private List<UserTask> taskList;
    private OnTaskClickListener onTaskClickListener;

    public QueueAdapter(OnTaskClickListener onTaskClickListener) {
        this.onTaskClickListener = onTaskClickListener;
    }

    public void setTaskList(List<UserTask> taskList) {
        this.taskList = taskList;
    }

    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_task,parent, false);

        return new QueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QueueViewHolder holder, int position) {
        UserTask userTask = taskList.get(position);
        holder.setUserClickListener(userTask, onTaskClickListener);
        holder.titleText.setText(userTask.getName());
        String subtitle = String.valueOf(userTask.getProgress()) + "/" + userTask.getTTL();
        holder.subtitleText.setText(subtitle);
        holder.progressBar.setProgress(userTask.getProgress());
        holder.progressBar.setMax(userTask.getTTL());
    }

    @Override
    public int getItemCount() {
        return (taskList ==null)? 0 : taskList.size();
    }


    public class QueueViewHolder extends RecyclerView.ViewHolder{

        ProgressBar progressBar;
        TextView titleText;
        TextView subtitleText;

        private View view;

        QueueViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_card_view);
            titleText = itemView.findViewById(R.id.tv_card_title);
            subtitleText = itemView.findViewById(R.id.tv_card_subtitle);
            this.view = itemView;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public void setUserClickListener(final UserTask userTask, final OnTaskClickListener listener){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(userTask, view);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onLongClick(userTask);
                    return false;
                }
            });
        }

    }
}
