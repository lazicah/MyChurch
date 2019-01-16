package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.data.Books;


import java.io.File;
import java.io.FileOutputStream;

import java.util.ArrayList;

/**
 * Created by root on 6/19/18.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {
    ArrayList<Books> singletons;
    Activity activity;
    int total_types;
    private LayoutInflater inflater;


    public BookAdapter(Activity activity, ArrayList<Books> singletons) {
        this.singletons = singletons;
        this.activity = activity;
        total_types = singletons.size();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        //Books singleton = (Books) this.singletons.get(position);

        //Uri uri = Uri.parse(singleton.getFilepath());
        //File image = new File(singleton.getFilepath());
        //generateImageFromPdf(uri,image.getName().substring(0, (image.getName().length() - 4)));

//        String fileName = singleton.getFilename() + ".png";
  //      String completePath = activity.getFilesDir().toString();
//
  //      File file = new File(completePath, ".LatterHouse");
    //    File mydir = new File(file, fileName);
      //  Uri imageUri = Uri.fromFile(mydir);








        //MyViewHolder holder1 = ((MyViewHolder) holder);
      //  holder1.title.setText(singleton.getFilename());
        //holder1.image.setImageResource(R.drawable.pdf);




    }


    @Override
    public int getItemCount() {
        return singletons.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, pubDate, url;
        ImageView image;


        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image_view);
            title = (TextView) itemView.findViewById(R.id.title);


            ((CardView) itemView.findViewById(R.id.card_view1)).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {

                    Log.d("pathh",singletons.get(getAdapterPosition()).getFilepath());

                    String uir = singletons.get(getAdapterPosition()).getFilepath();

                    //Intent intent = new Intent(view.getContext(), PdfActivity.class);
                    //intent.putExtra("path",uir );
                    //view.getContext().startActivity(intent);

                }
            });


        }
    }


    //PdfiumAndroid (https://github.com/barteksc/PdfiumAndroid)
//https://github.com/barteksc/AndroidPdfViewer/issues/49
    void generateImageFromPdf(Uri pdfUri, String filename) {
//        String newName = filename + ".png";
  //      int pageNumber = 0;
    //    PdfiumCore pdfiumCore = new PdfiumCore(activity);
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
      //      ParcelFileDescriptor fd = activity.getContentResolver().openFileDescriptor(pdfUri, "r");
        //    PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
          //  pdfiumCore.openPage(pdfDocument, pageNumber);
          //  int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
          //  int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
           // Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            //pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            //saveImage(bmp, activity, newName);
            //pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {
            //todo with exception
        }
    }


    private void saveImage(Bitmap bmp, Activity activity, String filename) {
        String FOLDER = activity.getFilesDir().toString();
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER, ".LatterHouse");
            if(!folder.exists())
                folder.mkdirs();
            File file = new File(folder, filename);
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            //todo with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
    }
}
