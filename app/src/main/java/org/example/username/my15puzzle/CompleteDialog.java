package org.example.username.my15puzzle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by hiroaki on 2016/12/16.
 */

public class CompleteDialog extends DialogFragment {

    public interface CompleteDialogListener {
        void onDialogPositiveClick();
    }
    CompleteDialogListener mListener;

    public static CompleteDialog newInstance() {
        CompleteDialog fragment = new CompleteDialog();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.complete,container);
        Button ok = (Button) content.findViewById(R.id.ok_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDialogPositiveClick();
            }
        });
        return content;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (CompleteDialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
