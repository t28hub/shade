/*
 * Copyright (c) 2016 Tatsuya Maki
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.t28.shade.example.widget;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.t28.shade.example.BR;
import io.t28.shade.example.R;
import io.t28.shade.example.model.Setting;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {
    private final List<Setting> settings;

    public SettingAdapter() {
        this.settings = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, @ViewType int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_setting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Setting setting = getItem(position);
        holder.getBinding().setVariable(BR.setting, setting);
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return ViewType.SETTING;
    }

    public void setSettings(@NonNull List<Setting> settings) {
        this.settings.clear();
        this.settings.addAll(settings);
        notifyDataSetChanged();
    }

    @NonNull
    private Setting getItem(int position) {
        return settings.get(position);
    }

    @IntDef({ViewType.SETTING})
    @interface ViewType {
        int SETTING = 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        ViewHolder(@NonNull View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        ViewDataBinding getBinding() {
            return binding;
        }
    }
}
