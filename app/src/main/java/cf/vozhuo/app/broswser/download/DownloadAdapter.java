package cf.vozhuo.app.broswser.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.tab.RecyclerAdapter;

import static android.content.ContentValues.TAG;


public class DownloadAdapter extends RecyclerAdapter<DownloadEntity> {

    private List<DownloadEntity> mDownload;

    public DownloadAdapter(Context context) {
        super(context);
        mDownload = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void bindView(DownloadEntity data, int position, RecyclerView.ViewHolder holder) {
        ((DownloadHolder)holder).bind(data, position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadHolder(mInflater.inflate(R.layout.download_list_item,
                parent, false));
    }

    class DownloadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_download_size)
        TextView tv_download_size;
        @BindView(R.id.tv_download_name)
        TextView tv_download_name;
        @BindView(R.id.seekBar)
        AppCompatSeekBar seekBar;

        DownloadEntity data;
        int position;

        public DownloadHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
        }

        void bind(DownloadEntity data, int position) {
            this.data = data;
            this.position = position;

            tv_download_name.setText(data.getName());
            tv_download_size.setText(data.getSize());
        }


    }
}
