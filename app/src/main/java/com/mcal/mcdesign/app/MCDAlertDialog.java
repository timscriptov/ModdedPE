/*
 * Copyright (C) 2018-2019 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.mcdesign.app;

import androidx.appcompat.app.AlertDialog;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCDAlertDialog extends AlertDialog
{
    protected MCDAlertDialog(android.content.Context context) {
        super(context, android.R.style.Theme_Translucent);
    }

    public static class Builder extends AlertDialog.Builder {
        public Builder(android.content.Context context) {
            super(context);
        }

        public Builder(android.content.Context context, int themeResId) {
            super(context, themeResId);
        }
    }
}
