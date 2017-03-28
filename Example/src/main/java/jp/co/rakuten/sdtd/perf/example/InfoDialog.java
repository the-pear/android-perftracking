package jp.co.rakuten.sdtd.perf.example;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * InfoDialog to display alert dialog
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */

public class InfoDialog extends DialogFragment {

    public static InfoDialog newInstance(String message) {
        InfoDialog frag = new InfoDialog();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");
        return new AlertDialog.Builder(getActivity())
                .setTitle("Information")
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                ).create();
    }

}
