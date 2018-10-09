package i.am.shiro.chesto.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.function.IntConsumer;

import java.util.Collections;
import java.util.List;

import i.am.shiro.chesto.R;

public final class SearchInputAdapter extends RecyclerView.Adapter<SearchInputAdapter.ViewHolder> {

    private List<String> data;

    private IntConsumer onItemRemoveClickListener;

    public SearchInputAdapter() {
        data = Collections.emptyList();
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemRemoveClickListener(IntConsumer onItemRemoveClickListener) {
        this.onItemRemoveClickListener = onItemRemoveClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_input_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.labelView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView labelView;

        private final View removeView;

        private ViewHolder(View itemView) {
            super(itemView);
            labelView = itemView.findViewById(R.id.textLabel);
            removeView = itemView.findViewById(R.id.imageRemove);
            removeView.setOnClickListener(v -> onRemoveClick());
        }

        private void onRemoveClick() {
            onItemRemoveClickListener.accept(getAdapterPosition());
        }
    }
}
