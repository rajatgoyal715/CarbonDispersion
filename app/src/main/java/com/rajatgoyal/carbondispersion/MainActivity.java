package com.rajatgoyal.carbondispersion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    SparseIntArray array;

    TextView accuracyView;
    ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Please click on the below image to compare with the above standard image", Toast.LENGTH_LONG).show();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        accuracyView = (TextView) findViewById(R.id.accuracy);
        accuracyView.setVisibility(View.GONE);

        progBar = (ProgressBar) findViewById(R.id.progBar);
        progBar.setVisibility(View.GONE);

        array = new SparseIntArray(10);
        fillArray();

        recyclerView.setAdapter(new ItemAdapter());
    }

    public void fillArray() {
        array.put(1, R.drawable.new_1);
        array.put(2, R.drawable.new_2);
        array.put(3, R.drawable.new_3);
        array.put(4, R.drawable.new_4);
        array.put(5, R.drawable.new_5);
        array.put(6, R.drawable.new_6);
        array.put(7, R.drawable.new_7);
        array.put(8, R.drawable.new_8);
        array.put(9, R.drawable.new_9);
        array.put(10, R.drawable.new_10);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ItemHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.image_num);
            imageView = (ImageView) itemView.findViewById(R.id.carbon_image);
        }
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(getLayoutInflater().inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            holder.imageView.setBackgroundResource(array.get(position+1));
            holder.textView.setText((position+1)+"");

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new CompareTask(){
                        @Override
                        protected void onPostExecute(Integer integer) {
                            accuracyView.setText(integer + "%");
                            progBar.setVisibility(View.GONE);
                            accuracyView.setVisibility(View.VISIBLE);

                        }
                    }.execute(position);


                    progBar.setVisibility(View.VISIBLE);
                    accuracyView.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }
    }

    public class CompareTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int position = params[0];
            return compareImage(position);
        }

        public int compareImage(int position) {
            Bitmap stdImage = BitmapFactory.decodeResource(getResources(), R.drawable.new_10);
            Bitmap currImage = BitmapFactory.decodeResource(getResources(), array.get(position+1));

            int accuracy = compare(stdImage, currImage);
            return accuracy;
        }

        public int compare(Bitmap stdImage, Bitmap currImage) {
            int h = stdImage.getHeight();
            int w = stdImage.getWidth();
            int diff = 0;

            for (int i=0;i<w;i++) {
                for (int j=0;j<h;j++) {
                    int pixel1 = stdImage.getPixel(i, j);
                    int pixel2 = currImage.getPixel(i, j);

                    int diff1 = Math.abs(Color.red(pixel1) - Color.red(pixel2));
                    int diff2 = Math.abs(Color.green(pixel1) - Color.green(pixel2));
                    int diff3 = Math.abs(Color.blue(pixel1) - Color.blue(pixel2));

                    diff += (diff1 + diff2 + diff3)/3;
                }
            }

            int defect = (diff * 100) / (255 * w * h);
            return 100 - defect;
        }
    }


}
