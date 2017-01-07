package org.example.username.my15puzzle;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by hiroaki on 2016/12/24.
 */

public class GiveUpDialog extends DialogFragment {
    private static final String ICON = "icon";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";

    public interface GiveUpDialogListener {

        void onDialogPositiveClick(GiveUpDialog dialog);

        void onDialogNegativeClick(GiveUpDialog dialog);
    }

    GiveUpDialogListener mListener;

    public static GiveUpDialog newInstance(int icon, String title, String message) {
        // ダイアログフラグメントを生成します
        GiveUpDialog fragment = new GiveUpDialog();
        // ダイアログフラグメントに渡すデータはBundleオブジェクト内に格納します
        Bundle args = new Bundle();
        // バンドルにダイアログフラグメントに渡す値をセットします。
        args.putInt(ICON, icon);
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        // ダイアログフラグメントにバンドルをセットします。
        fragment.setArguments(args);
        return fragment;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // ダイアログフラグメントに渡されたデータをバンドルから取り出します。
        Bundle args = getArguments();
        int icon = args.getInt(ICON);
        String title = args.getString(TITLE);
        String message = args.getString(MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setIcon(icon)
                .setTitle(title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // アクティビティに対しpositiveボタンが押されたイベントを送り返します
                        mListener.onDialogPositiveClick(GiveUpDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // アクティビティに対しnegativeボタンが押されたイベントを送り返します
                        mListener.onDialogNegativeClick(GiveUpDialog.this);
                    }
                });
        // 最後にビルダーでAlertDialogオブジェクトを作成して戻り値とします
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GiveUpDialogListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

}
