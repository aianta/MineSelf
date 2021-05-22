package ca.mineself.fragments;


import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.fragment.app.Fragment;



import ca.mineself.R;
import ca.mineself.listeners.AspectListener;
import ca.mineself.model.Aspect;

public class AspectView extends Fragment {

    Aspect aspect;

    TextView aspectValueLabel;
    TextView aspectNameLabel;
    TextView lastUpdateLabel;

    //Aspect Update Buttons
    Button subSmallBtn;
    Button subMedBtn;
    Button subLargeBtn;
    Button addSmallBtn;
    Button addMedBtn;
    Button addLargeBtn;
    Button noChangeBtn;

    AspectListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AspectListener){
            listener = (AspectListener)context;
        }else{
            Log.e(getClass().getSimpleName(), "Error getting parent activity aspect listener!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        return inflater.inflate(R.layout.aspect_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        aspect = getArguments().getParcelable("aspect");

        //Map UI
        aspectNameLabel = getView().findViewById(R.id.aspectName);
        aspectNameLabel.setText(aspect.name);

        aspectValueLabel = getView().findViewById(R.id.aspectValue);
        aspectValueLabel.setText(Long.toString(aspect.value));

        lastUpdateLabel = getView().findViewById(R.id.lastUpdatedLabel);

        subSmallBtn = getView().findViewById(R.id.subSmallBtn);
        subMedBtn = getView().findViewById(R.id.subMedBtn);
        subLargeBtn = getView().findViewById(R.id.subLargeBtn);
        addSmallBtn = getView().findViewById(R.id.addSmallBtn);
        addMedBtn = getView().findViewById(R.id.addMedBtn);
        addLargeBtn = getView().findViewById(R.id.addLargeBtn);
        noChangeBtn = getView().findViewById(R.id.noChangeBtn);

        subSmallBtn.setOnClickListener(this::openAspectUpdate);
        subMedBtn.setOnClickListener(this::openAspectUpdate);
        subLargeBtn.setOnClickListener(this::openAspectUpdate);
        addSmallBtn.setOnClickListener(this::openAspectUpdate);
        addMedBtn.setOnClickListener(this::openAspectUpdate);
        addLargeBtn.setOnClickListener(this::openAspectUpdate);
        noChangeBtn.setOnClickListener(this::openAspectUpdate);
    }

    public void openAspectUpdate(View view){
        long delta = 0;
        switch (view.getId()){
            case R.id.subSmallBtn:
                delta = Long.parseLong(subSmallBtn.getText().toString());
                break;
            case R.id.subMedBtn:
                delta = Long.parseLong(subMedBtn.getText().toString());
                break;
            case R.id.subLargeBtn:
                delta = Long.parseLong(subLargeBtn.getText().toString());
                break;
            case R.id.addSmallBtn:
                delta = Long.parseLong(addSmallBtn.getText().toString());
                break;
            case R.id.addMedBtn:
                delta = Long.parseLong(addMedBtn.getText().toString());
                break;
            case R.id.addLargeBtn:
                delta = Long.parseLong(addLargeBtn.getText().toString());
                break;
            default:
                delta = 0;

        }
        listener.switchToAspectUpdate(delta);
    }


}
