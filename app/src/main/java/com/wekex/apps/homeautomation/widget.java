package com.wekex.apps.homeautomation;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.wekex.apps.homeautomation.utils.Constants;


public class widget extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
    }

    public void quadswitch(View view) {
        ImageView imageView = (ImageView)view;
       LinearLayout linearLayout = findViewById(R.id.quadholder);
       if (linearLayout.getVisibility()==View.GONE){
           linearLayout.setVisibility(View.VISIBLE);
           imageView.setImageResource(R.drawable.ic_up);
       }else {
           linearLayout.setVisibility(View.GONE);
           imageView.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
       }
    }

    public void dualswitch(View view) {
        ImageView imageView = (ImageView)view;
        LinearLayout linearLayout = findViewById(R.id.dualholder);
        if (linearLayout.getVisibility()==View.GONE){
            linearLayout.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_up);
        }else {
            linearLayout.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        }
    }

    public void rgbdialog(View view) {
        TextView rgbswitch = findViewById(R.id.rgbswitch);
        ColorDrawable viewColor = (ColorDrawable) rgbswitch.getBackground();
        int colorId = viewColor.getColor();
        final String[] dia_name = new String[1];
        AlertDialog.Builder colorPickAlert = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View dialoglayout = inflater.inflate(R.layout.rbg_control, null);

        colorPickAlert.setView(dialoglayout);

        ColorPicker picker = dialoglayout.findViewById(R.id.picker);
        TextView onoff_colorpicker = dialoglayout.findViewById(R.id.onoff_colorpicker);
        onoff_colorpicker.setTag(false); //Initialize value of Bulb i.e ON or OFF



        ImageView rbg_on_bulb = dialoglayout.findViewById(R.id.rbg_on_bulb);
        ImageView rbg_off_bulb = dialoglayout.findViewById(R.id.rbg_off_bulb);

        LinearLayout rbg_on_layout = dialoglayout.findViewById(R.id.rbg_on_layout);
        LinearLayout rbg_off_layout = dialoglayout.findViewById(R.id.rbg_off_layout);

        if ((Boolean)onoff_colorpicker.getTag()){
            rbg_off_layout.setVisibility(View.INVISIBLE);
            rbg_on_layout.setVisibility(View.VISIBLE);
        }else {
            rbg_on_layout.setVisibility(View.INVISIBLE);
            rbg_off_layout.setVisibility(View.VISIBLE);
        }

        rbg_on_bulb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onoff_colorpicker.setTag(false);
                onoff_colorpicker.setText("OFF");
                rbg_on_layout.setVisibility(View.INVISIBLE);
                rbg_off_layout.setVisibility(View.VISIBLE);
            }
        });
        rbg_off_bulb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onoff_colorpicker.setTag(true);
                onoff_colorpicker.setText("ON");
                rbg_off_layout.setVisibility(View.INVISIBLE);
                rbg_on_layout.setVisibility(View.VISIBLE);
            }
        });

        picker.setOldCenterColor(colorId);
        picker.setShowOldCenterColor(false);
        SVBar svBar = (SVBar) dialoglayout.findViewById(R.id.svbar);

        OpacityBar opacityBar = (OpacityBar) dialoglayout.findViewById(R.id.opacitybar);

        Button colorSelectedBtn = (Button)dialoglayout.findViewById(R.id.button1);

        final AlertDialog colorDialog = colorPickAlert.show();

        colorSelectedBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                rgbswitch.setBackgroundColor(picker.getColor());
                rgbswitch.setTag(picker.getColor());
                colorDialog.dismiss();
            }
        });

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener()
        {
            @Override
            public void onColorChanged(int color)
            {
                rgbswitch.setBackgroundColor(picker.getColor());
                Constants.red = Color.red(color);
                Constants.green = Color.green(color);
                Constants.blue = Color.blue(color);
               // DtypeViews.publishRBGcolor(widget.this,(Boolean)onoff_colorpicker.getTag());
            }
        });
    }

    public void homeactivity(View view) {
        startActivity(new Intent(this,HomeActivity.class));
    }
}
