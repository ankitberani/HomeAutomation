package com.wekex.apps.homeautomation.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.squareup.picasso.Picasso;
import com.triggertrap.seekarc.SeekArc;
import com.wekex.apps.homeautomation.Interfaces.SelectableListener;
import com.wekex.apps.homeautomation.R;
import com.wekex.apps.homeautomation.model.SceneListModel;
import com.wekex.apps.homeautomation.model.scene_model;
import com.wekex.apps.homeautomation.utils.Constants;
import com.wekex.apps.homeautomation.utils.GradienSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class SceneListAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    Activity activity;
    List<SceneListModel> users;
    ArrayList<SceneListModel> arrayList; //used for the search bar
    LayoutInflater inflater;
    SparseBooleanArray checkedStates;
    Context context;
    AlertDialog dialog;
    View dialogView;
    Button btnRGB, btnSwitch;
    Switch toggleSwitch;
    SelectableListener listener;

    int position;
    ArrayList<String> tempItems = new ArrayList<>();
    JSONArray items = new JSONArray();
    boolean toggle;

    /* RGB CONTROLS */
    RelativeLayout rl_rgb, rl_white;
    ColorPicker picker;

    private int colorpic;
    GradienSeekBar alphaSlider;
    SeekBar _brightness;

    int red = 0;
    int green = 0;
    int blue = 0;
    int white = 0;
    int warm_white = 0;

    TextView tv_bright_perc;
    TextView brigtnessTV;
    SeekArc seekArc;
    SeekBar seekbar_ww;

    int brightNess;
    SeekBar seekbar_clr_picker;

    public SceneListAdapter(Activity activity) {
        this.activity = activity;
    }

    public SceneListAdapter(Activity activity, List<SceneListModel> users, Context context, SelectableListener listener) {
        this.activity = activity;
        this.users = users;
        inflater = activity.getLayoutInflater();
        arrayList = new ArrayList<>();
        this.arrayList.addAll(users);
        checkedStates = new SparseBooleanArray(this.users.size());
        this.context = context;
        this.listener = listener;
        initViews();
    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {

            view = inflater.inflate(R.layout.item_scene_list, viewGroup, false);

            holder = new ViewHolder();

            holder.tvUserName = view.findViewById(R.id.tv_title_scene);
            holder.cb = view.findViewById(R.id.iv_check_box);
            holder.iconView = view.findViewById(R.id.imageViewSceneList);

            view.setTag(holder);

        } else
            holder = (ViewHolder) view.getTag();

        SceneListModel model = users.get(i);

        holder.cb.setTag(i);
        holder.cb.setChecked(this.checkedStates.get(i));
        holder.cb.setOnCheckedChangeListener(this);

        holder.tvUserName.setText(model.getName().equals("") ? "NA" : model.getName());

        if (model.getdType() == 1) {
            Picasso.get()
                    .load(R.drawable.icon_plug)
                    .fit()
                    .centerCrop()
                    .into(holder.iconView);
        } else if (model.getdType() == 5 || model.getdType() == 6 || model.getdType() == 18 || model.getdType() == 19 || model.getdType() == 20 || model.getdType() == 9) {
            Picasso.get()
                    .load(R.drawable.switch_icon)
                    .fit()
                    .centerCrop()
                    .into(holder.iconView);
        }

//        1 = plug
//        5 6 18 19 20 9 = switch

        return view;
    }

    public void setChecked(int position, boolean isChecked) {
        checkedStates.put(position, isChecked);
    }

    public boolean isChecked(int position) {
        return checkedStates.get(position);
    }

    public JSONArray getSelected() {
//        List<String> items = new LinkedList<>();
        JSONArray items = new JSONArray();
        for (int i = 0; i < users.size(); i++) {
            if (isChecked(i)) {

                if (users.get(i).getdType() == 2) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("dno", users.get(i).getId());
                        jsonObject.put("d1", new JSONObject());

                        JSONObject jsonObject1 = jsonObject.getJSONObject("d1");
                        jsonObject1.put("status", users.get(i).getStatus());
                        jsonObject1.put("r", red);
                        jsonObject1.put("g", green);
                        jsonObject1.put("b", blue);
                        jsonObject1.put("w", white);
                        jsonObject1.put("ww", warm_white);
                        jsonObject1.put("br", brightNess);

                        items.put(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return items;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int pos = (Integer) compoundButton.getTag();
        setChecked(pos, b);

        if (users.get(pos).getdType() == 2 || users.get(pos).getdType() == 4) {
            if (b) {
                position = pos;
                dialog.show();
            }
            if (!b) {
                for (int i = 0; i <= items.length(); i++) {
                    try {
                        if (items.getString(i).indexOf(users.get(pos).getId()) > 0) {
                            items.remove(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (users.get(pos).getdType() == 6 || users.get(pos).getdType() == 5) {
            if (b) {
                toggle = false;
                LayoutInflater factory = LayoutInflater.from(context);
                final View dialogView = factory.inflate(R.layout.dialog_switch_controls, null);
                AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                dialog.setView(dialogView);

                toggleSwitch = dialogView.findViewById(R.id.toggle_on_off_switch_controls);
                btnSwitch = dialogView.findViewById(R.id.btn_done_switch_controls);

                toggleSwitch.setOnCheckedChangeListener((compoundButton1, b1) -> toggle = b1);

                btnSwitch.setOnClickListener(v -> {
                    dialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("dno", users.get(pos).getId());
                        jsonObject.put("d1", new JSONObject());

                        JSONObject jsonObject1 = jsonObject.getJSONObject("d1");
                        jsonObject1.put("status", toggle);

                        items.put(jsonObject.toString());
                        tempItems.add(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    listener.selectedData(items.toString());
                });

                dialog.show();
            }
            if (!b) {
                for (int i = 0; i <= items.length(); i++) {
                    try {
                        if (items.getString(i).indexOf(users.get(pos).getId()) > 0) {
                            items.remove(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class ViewHolder {
        TextView tvUserName, tvCost;
        ImageView iconView;
        CheckBox cb;
    }

    // filter name in Search Bar
    public void filter(String characterText) {
        characterText = characterText.toLowerCase(Locale.getDefault());
        users.clear();
        if (characterText.length() == 0) {
            users.addAll(arrayList);
        } else {
            for (SceneListModel productt : arrayList) {
                if (productt.getName().toLowerCase(Locale.getDefault()).contains(characterText)) {
                    users.add(productt);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void initViews() {
        LayoutInflater factory = LayoutInflater.from(context);
        dialogView = factory.inflate(R.layout.dialog_rgb_controls, null);
        dialog = new AlertDialog.Builder(context).create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.setView(dialogView);

        seekArc = dialogView.findViewById(R.id.seekArcWhite);
        brigtnessTV = dialogView.findViewById(R.id.brightnessTV);
        rl_white = dialogView.findViewById(R.id.rl_whitelayout);
        rl_rgb = dialogView.findViewById(R.id.rl_rgblayout);
        btnRGB = dialogView.findViewById(R.id.btn_done_rgb_controls);

        seekbar_ww = dialogView.findViewById(R.id.seekbar_ww);
        tv_bright_perc = dialogView.findViewById(R.id.tv_bright_perc);
        //seekArc.setProgress(50);

        _brightness = dialogView.findViewById(R.id.brightness);
        _brightness.incrementProgressBy(1);
        _brightness.setMax(100);
        //_brightness.setProgress(50);

        picker = dialogView.findViewById(R.id.picker);
        picker.setShowOldCenterColor(false);

        alphaSlider = dialogView.findViewById(R.id.alphaSlider);
        alphaSlider.setColor(colorpic);

        btnRGB.setOnClickListener(v -> {
            dialog.cancel();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dno", users.get(position).getId());
                jsonObject.put("d1", new JSONObject());

                JSONObject jsonObject1 = jsonObject.getJSONObject("d1");
                jsonObject1.put("status", users.get(position).getStatus());
                jsonObject1.put("r", red);
                jsonObject1.put("g", green);
                jsonObject1.put("b", blue);
                jsonObject1.put("w", white);
                jsonObject1.put("ww", warm_white);
                jsonObject1.put("br", brightNess);

                items.put(jsonObject.toString());
                tempItems.add(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.selectedData(items.toString());
        });

        picker.setOnColorSelectedListener(color -> {
            Log.e("DIALOG", "onColorSelected: color " + color);
            colorpic = color;
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);

            alphaSlider.setColor(color);
            setRBG();

            white = 0;
            warm_white = 0;
        });

        alphaSlider.setOnALphaChangeListener(new GradienSeekBar.OnAlphaChangeListener() {
            @Override
            public void onAlphaColorChnage(int color) {
                Log.e("DIALOG", "onAlphaColorChnage called");
                Log.e("DIALOG", "r " + Color.red(color) + " g " + Color.green(color) + " b " + Color.blue(color) + "onAlphaColorChnage: " + color);
                red = Color.red(color);
                green = Color.green(color);
                blue = Color.blue(color);

                warm_white = 0;
                white = 0;
                setRBG();
            }

            @Override
            public void onAlphaColorChnaged(boolean alpha) {
            }
        });

        _brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    progress = 1;
                brightNess = progress;

                brigtnessTV.setText("Brightness " + progress + "%");

                if (progress < 10) {
                    _brightness.setProgress(10);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                white = 0;
                warm_white = 0;
                brightNess = seekBar.getProgress();
            }
        });

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                Log.e("DIALOG", "onProgressChanged: " + i + " At " + Constants.red + " " + Constants.green + " " + Constants.blue);
                red = 0;
                green = 0;
                blue = 0;
                white = i;
                warm_white = seekbar_ww.getProgress();

                double per = ((double) i / 100) * 100;
                Log.e("DIALOG", "Percentage " + (Double) (per / 100));
                brightNess = i;
                tv_bright_perc.setText((String.format("%.0f", per) + "%"));
                if (i < 10) {
                    seekArc.setProgress(10);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                brightNess = seekArc.getProgress();
            }
        });

        seekbar_ww.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("DIALOG", "Seekbar Change " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Log.e("DIALOG", "ONStop WW " + seekBar.getProgress() + " W " + (255 - seekBar.getProgress()));
                red = 0;
                green = 0;
                blue = 0;
                warm_white = seekbar_ww.getProgress();
                white = (255 - seekBar.getProgress());

            }
        });

        seekbar_clr_picker = dialogView.findViewById(R.id.seekbar_clr_picker);
        seekbar_clr_picker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setColorToPicker(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void setRBG() {
        TextView r, b, g;
        r = dialogView.findViewById(R.id.red);
        b = dialogView.findViewById(R.id.blue);
        g = dialogView.findViewById(R.id.green);

        r.setText("R " + red);
        b.setText("B " + blue);
        g.setText("G " + green);

        r.setBackgroundColor(android.graphics.Color.argb(255, red, 0, 0));
        b.setBackgroundColor(android.graphics.Color.argb(255, 0, 0, blue));
        g.setBackgroundColor(android.graphics.Color.argb(255, 0, green, 0));
    }

    public int setColor(View v) {
        ColorDrawable viewColor = (ColorDrawable) v.getBackground();
        return viewColor.getColor();
    }

    public void setColorToPicker(int progress) {

        int redValue, greenValue, blueValue;

        Log.e("TAGGG", "Progress " + progress);
        if (progress <= 18) {
            redValue = 254;
            greenValue = 242;
            blueValue = 54;
        } else if (progress == 19) {
            redValue = 254;
            greenValue = 244;
            blueValue = 81;
        } else if (progress == 20) {
            redValue = 254;
            greenValue = 247;
            blueValue = 99;
        } else if (progress == 21) {
            redValue = 254;
            greenValue = 249;
            blueValue = 112;
        } else if (progress == 22) {
            redValue = 254;
            greenValue = 251;
            blueValue = 123;
        } else if (progress == 23) {
            redValue = 254;
            greenValue = 253;
            blueValue = 132;
        } else if (progress == 24) {
            redValue = 254;
            greenValue = 254;
            blueValue = 139;
        } else if (progress == 25) {
            redValue = 252;
            greenValue = 254;
            blueValue = 145;
        } else if (progress == 26) {
            redValue = 251;
            greenValue = 254;
            blueValue = 149;
        } else if (progress == 27) {
            redValue = 250;
            greenValue = 254;
            blueValue = 153;
        } else if (progress == 28) {
            redValue = 248;
            greenValue = 254;
            blueValue = 157;
        } else if (progress == 29) {
            redValue = 247;
            greenValue = 254;
            blueValue = 161;
        } else if (progress == 30) {
            redValue = 246;
            greenValue = 254;
            blueValue = 164;
        } else if (progress == 31) {
            redValue = 245;
            greenValue = 254;
            blueValue = 167;
        } else if (progress == 32) {
            redValue = 244;
            greenValue = 254;
            blueValue = 169;
        } else if (progress == 33) {
            redValue = 244;
            greenValue = 254;
            blueValue = 172;
        } else if (progress == 34) {
            redValue = 243;
            greenValue = 254;
            blueValue = 174;
        } else if (progress == 35) {
            redValue = 242;
            greenValue = 254;
            blueValue = 176;
        } else if (progress == 36) {
            redValue = 241;
            greenValue = 254;
            blueValue = 178;
        } else if (progress == 37) {
            redValue = 241;
            greenValue = 254;
            blueValue = 180;
        } else if (progress == 38) {
            redValue = 240;
            greenValue = 253;
            blueValue = 182;
        } else if (progress == 39) {
            redValue = 240;
            greenValue = 253;
            blueValue = 184;
        } else if (progress == 40) {
            redValue = 239;
            greenValue = 253;
            blueValue = 185;
        } else if (progress == 41) {
            redValue = 239;
            greenValue = 253;
            blueValue = 187;
        } else if (progress == 42) {
            redValue = 238;
            greenValue = 253;
            blueValue = 188;
        } else if (progress == 43) {
            redValue = 238;
            greenValue = 253;
            blueValue = 190;
        } else if (progress == 44) {
            redValue = 237;
            greenValue = 253;
            blueValue = 191;
        } else if (progress == 45) {
            redValue = 237;
            greenValue = 253;
            blueValue = 192;
        } else if (progress == 46) {
            redValue = 237;
            greenValue = 253;
            blueValue = 193;
        } else if (progress == 47) {
            redValue = 236;
            greenValue = 253;
            blueValue = 194;
        } else if (progress == 48) {
            redValue = 236;
            greenValue = 253;
            blueValue = 195;
        } else if (progress == 49) {
            redValue = 235;
            greenValue = 253;
            blueValue = 197;
        } else if (progress == 50) {
            redValue = 235;
            greenValue = 253;
            blueValue = 198;
        } else if (progress >= 51 && progress <= 60) {
            redValue = 233;
            greenValue = 254;
            blueValue = 204;
        } else if (progress >= 61 && progress <= 70) {
            redValue = 232;
            greenValue = 254;
            blueValue = 209;
        } else if (progress >= 71 && progress <= 80) {
            redValue = 231;
            greenValue = 254;
            blueValue = 213;
        } else if (progress >= 81 && progress <= 90) {
            redValue = 230;
            greenValue = 254;
            blueValue = 217;
        } else {
            redValue = 229;
            greenValue = 254;
            blueValue = 220;
        }

        Constants.white = 0;
        Constants.warm_white = 0;

        Constants.red = redValue;
        Constants.green = greenValue;
        Constants.blue = blueValue;

        picker.setColor(Color.rgb(redValue, greenValue, blueValue));
        setRBG();
    }

}
