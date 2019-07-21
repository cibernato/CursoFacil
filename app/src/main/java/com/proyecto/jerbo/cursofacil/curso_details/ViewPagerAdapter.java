package com.proyecto.jerbo.cursofacil.curso_details;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.proyecto.jerbo.cursofacil.R;

import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP;
import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE;
import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_START;

public class ViewPagerAdapter extends PagerAdapter {
    Activity activity;
    String [] fotos;
    LayoutInflater layoutInflater;
    SubsamplingScaleImageView imageView;

    public ViewPagerAdapter (Activity activity, String[] fotos){
        this.activity=activity;
        this.fotos=fotos;

    }

    @Override
    public int getCount() {
        return fotos.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view ==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_pager_item,container,false);
        imageView = view.findViewById(R.id.view_pager_image);
//        ImageView imageView2 = view.findViewById(R.id.view_pager_image);
        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
        int height = dis.heightPixels;
        int width = dis.widthPixels;
        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);
        try {
            ImageSource a = ImageSource.uri(fotos[position]);
            imageView.setImage(a);


        }catch (Exception e){
        }
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
    public void recycle(){
        imageView.recycle();
    }

}
