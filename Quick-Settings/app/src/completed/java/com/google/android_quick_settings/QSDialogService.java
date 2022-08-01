// Copyright 2022 Google Inc.
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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import androidx.fragment.app.DialogFragment;

@SuppressLint("Override")
public class QSDialogService
        extends TileService {

    private boolean isTileActive;

    @Override
    public void onClick(){

        // Get the tile's current state.
        Tile tile = getQsTile();
        isTileActive = (tile.getState() == Tile.STATE_ACTIVE);

        QSDialog.Builder dialogBuilder =
                new QSDialog.Builder(getApplicationContext());

        QSDialog dialog = dialogBuilder
                .setClickListener(new QSDialog.QSDialogListener() {
                  @Override
                  public void onDialogPositiveClick(DialogFragment dialog) {
                    Log.d("QS", "Positive registed");

                    // The user wants to change the tile state.
                    isTileActive = !isTileActive;
                    updateTile();
                  }

                  @Override
                  public void onDialogNegativeClick(DialogFragment dialog) {
                    Log.d("QS", "Negative registered");

                    // The user is cancelled the dialog box.
                    // We can't do anything to the dialog box here,
                    // but we can do any cleanup work.
                  }
                })
                .create();

        // Pass the tile's current state to the dialog.
        Bundle args = new Bundle();
        args.putBoolean(QSDialog.TILE_STATE_KEY, isTileActive);

        this.showDialog(dialog.onCreateDialog(args));
    }

    private void updateTile() {
        Tile tile = super.getQsTile();
        int activeState = isTileActive ?
                Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;

        tile.setState(activeState);
        tile.updateTile();
    }
}
