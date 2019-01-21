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

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.givedirect.givedirectpos.dagger.module.BaseActivityModule;
import io.givedirect.givedirectpos.view.util.TextUtils;

public abstract class BaseActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    @Named(BaseActivityModule.ACTIVITY_FRAGMENT_MANAGER)
    protected FragmentManager fragmentManager;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public final AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    protected final void replaceFragment(@IdRes int containerViewId,
                                         @NonNull Fragment fragment,
                                         boolean executeImmediately) {
        replaceFragmentInternal(containerViewId, fragment, executeImmediately, null);
    }

    protected final void replaceFragment(@IdRes int containerViewId,
                                         @NonNull Fragment fragment,
                                         boolean executeImmediately,
                                         @NonNull String stackId) {
        replaceFragmentInternal(containerViewId, fragment, executeImmediately, stackId);
    }

    private void replaceFragmentInternal(@IdRes int containerViewId,
                                         @NonNull Fragment fragment,
                                         boolean executeImmediately,
                                         @Nullable String stackId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(containerViewId, fragment);

        if (!TextUtils.isEmpty(stackId)) {
            transaction = transaction.addToBackStack(stackId);
        }

        transaction.commit();

        if (executeImmediately) {
            fragmentManager.executePendingTransactions();
        }
    }
}
