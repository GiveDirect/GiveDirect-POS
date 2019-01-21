package io.givedirect.givedirectpos.view.common;

/*
 * Copyright 2018 Vandolf Estrellado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Modifications copyright (C) 2018 Duchess Technologies

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;
import io.givedirect.givedirectpos.R;
import io.givedirect.givedirectpos.dagger.module.BaseFragmentModule;

public abstract class BaseFragment extends AppCompatDialogFragment
        implements HasSupportFragmentInjector {

    @Inject
    protected Context activityContext;

    @Inject
    @Named(BaseFragmentModule.CHILD_FRAGMENT_MANAGER)
    protected FragmentManager childFragmentManager;

    @Inject
    DispatchingAndroidInjector<Fragment> childFragmentInjector;

    @Nullable
    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        // This is called even for API levels below 23.
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Quick fix for the dialog width not taking up most of the screen width.
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.DefaultDialogFragment);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //noinspection ConstantConditions
        unbinder = ButterKnife.bind(this, getView());
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public final AndroidInjector<Fragment> supportFragmentInjector() {
        return childFragmentInjector;
    }

    protected final void addChildFragment(@IdRes int containerViewId, Fragment fragment) {
        childFragmentManager.beginTransaction()
                .add(containerViewId, fragment)
                .commit();
    }
}
