package ca.mineself;

import android.view.View;
import android.widget.EditText;

import ca.mineself.adapters.ProfileListAdapter;
import ca.mineself.model.Profile;

/**
 * Singleton for shared data structures
 */
public class MineSelf {

    private static MineSelf instance;

    //Application Adapters
    private ProfileListAdapter profileList = new ProfileListAdapter();

    /**
     * Utility Class for edit texts to make them auto clear their placeholders when
     * tapped. Replaces the placeholders if nothing is entered.
     */
    public static class AutoClearingEditText implements View.OnFocusChangeListener {
        String placeholder;
        EditText editText;

        public AutoClearingEditText(EditText editText){
            this.editText = editText;
        }

        @Override
        public void onFocusChange(View view, boolean focused) {
            if(focused){
                this.placeholder = this.editText.getText().toString();
                this.editText.setText("");
            }else{
                if(editText.getText().toString().isEmpty()){
                    this.editText.setText(placeholder);
                }
            }
        }
    }

    public static MineSelf getInstance(){
        if (instance == null){
            instance = new MineSelf();
        }
        return instance;
    }

    public ProfileListAdapter getProfileList() {
        return profileList;
    }
}
