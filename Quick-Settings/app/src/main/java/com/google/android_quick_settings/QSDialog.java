// Copyright 2016 Google Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android_quick_settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class QSDialog
    extends DialogFragment {

    public static final String TILE_STATE_KEY = "tileState";

    private Context _context;
    private QSDialogListener _listener;

    /**
     * An inner class used to pass context into the dialog.
     */
    public static class Builder {

        private Context _context;
        private QSDialogListener _listener;

        public Builder(Context context){
            this._context = context;
        }

        public Builder setClickListener(QSDialogListener listener) {
            if (listener instanceof QSDialogListener) {
                this._listener = listener;
            }
            return this;
        }

        public QSDialog create() {
            QSDialog dialog = new QSDialog()
                .setContext(this._context)
                .setClickListener(this._listener);
            return dialog;
        }
    }

    /**
     * A public interface for communication between the
     * dialog and the QSDialogService.
     */
    public interface QSDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedState){

        // Read the saved state data passed in.
        boolean isTileActive = false;
        if (savedState.containsKey(TILE_STATE_KEY)) {
            isTileActive = savedState.getBoolean(TILE_STATE_KEY);
        }

        int actionButtonText = isTileActive ?
                R.string.qs_dialog_active : R.string.qs_dialog_inactive;

        AlertDialog.Builder alertBuilder =
                new AlertDialog.Builder(this._context);

        alertBuilder
                .setView(R.layout.dialog_quicksettings)

                // OnAttach doesn't get called on the dialog;
                // we have to apply our click event handlers here.
                .setNegativeButton(R.string.qs_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("QS", "Dialog cancel");
                                _listener.onDialogNegativeClick(QSDialog.this);
                            }
                        })
                .setPositiveButton(actionButtonText,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d("QS", "Dialog action taken");
                            dialog.dismiss();

                            _listener.onDialogPositiveClick(QSDialog.this);
                        }
                    });

        return  alertBuilder.create();
    }

    private QSDialog setClickListener(QSDialogListener listener) {
        this._listener = listener;
        return this;
    }

    private QSDialog setContext(Context context){
        this._context = context;
        return this;
    }
}
