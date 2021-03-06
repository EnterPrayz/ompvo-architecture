package com.fifed.architecture.app.mvp.managers_ui.core;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.fifed.architecture.R;
import com.fifed.architecture.app.activities.interfaces.ActivityStateInterface;
import com.fifed.architecture.app.fragments.core.BaseFragment;
import com.fifed.architecture.app.fragments.transiotions.FragmentViewTransitionsRequest;
import com.fifed.architecture.app.fragments.utils.FragmentAnimUtils;
import com.fifed.architecture.app.mvp.managers_ui.interfaces.core.ManagerUI;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fedir on 21.10.2017.
 */

public abstract class BaseManagerUI implements ManagerUI {
    private DrawerLayout drawer;
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private ViewGroup toolbarContainer;
    private boolean useFragmentAnim = true;

    public BaseManagerUI(AppCompatActivity activity) {
        this.activity = new WeakReference<>(activity).get();
        activity.setContentView(getActivityRootLayout());
        initUI();
        initToolbar();
        initToolbarContainer();
    }

    protected AppCompatActivity getActivity() {
        return activity;
    }

    protected abstract void initUI();

    protected abstract int getIdFragmentsContainer();

    protected void setDrawer(DrawerLayout drawer) {
        this.drawer = drawer;
    }

    protected void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    protected int getToolbarContainerID() {
        return 0;
    }

    private void initToolbarContainer() {
        if (toolbar != null) {
            toolbarContainer = (ViewGroup) toolbar.findViewById(getToolbarContainerID());
        }
    }

    protected void addFragmentToContainer(BaseFragment fragment, boolean toBackStack, @Nullable Bundle bundle) {
        addFragmentToContainer(fragment, toBackStack, bundle, null);
    }

    protected void addFragmentToContainer(BaseFragment fragment, boolean toBackStack, @Nullable Bundle bundle, @Nullable FragmentViewTransitionsRequest request) {
        fragment.setArguments(bundle);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fragment.getClass() == getFirstInStackFragmentClass()) {
            if (useFragmentAnim) {
                FragmentAnimUtils.revertAnim();
            }
            BaseFragment dashboardFragment = (BaseFragment) getActivity().getSupportFragmentManager().findFragmentByTag(getFirstInStackFragmentClass().getSimpleName());
            if (dashboardFragment != null) {
                dashboardFragment.reloadedAsNewFragment(true);
                dashboardFragment.onReloadFromPassiveState(bundle);
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                if (dashboardFragment.isAdded()) {
                    dashboardFragment.onReloadFragmentDataWithOutChangeState(bundle);
                }
                return;
            }

        }
        BaseFragment sameFragment = (BaseFragment) fm.findFragmentByTag(fragment.getClass().getSimpleName());
        if (sameFragment != null && sameFragment.isAdded()) {
            sameFragment.onReloadFragmentDataWithOutChangeState(bundle);
            return;
        }
        boolean containsInBackStack = false;
        if (sameFragment != null) {
            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                if (sameFragment.getClass().getSimpleName().equals(fm.getBackStackEntryAt(i).getName())) {
                    containsInBackStack = true;
                    break;
                }
            }
        }
        if (sameFragment != null && containsInBackStack) {
            if (useFragmentAnim) {
                FragmentAnimUtils.revertAnim();
            }
            sameFragment.reloadedAsNewFragment(true);
            sameFragment.onReloadFromPassiveState(bundle);
            fm.popBackStackImmediate(sameFragment.getClass().getSimpleName(), 0);
        } else {
            FragmentTransaction transaction = fm.beginTransaction();
            if (useFragmentAnim) {
                transaction.setCustomAnimations(
                        getFragmentAnimationEnter(), getFragmentAnimationExit(),
                        getFragmentAnimationPopEnter(), getFragmentAnimationPopExit());
            }
            if (toBackStack) {
                transaction.addToBackStack(fragment.getClass().getSimpleName())
                        .replace(getIdFragmentsContainer(), fragment, fragment.getClass().getSimpleName());
            } else {
                transaction
                        .replace(getIdFragmentsContainer(), fragment, fragment.getClass().getSimpleName());
            }
            if (FragmentViewTransitionsRequest.isLolipop() && request != null && request.getRequests().size() > 0) {
                for (View v : request.getRequests()) {
                    transaction.addSharedElement(v, ViewCompat.getTransitionName(v));
                }
            }
            transaction.commitAllowingStateLoss();
        }
    }


    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public ViewGroup getToolbarContainer() {
        return toolbarContainer;
    }

    @Override
    public void initToolbar() {

    }

    protected abstract Class<?> getFirstInStackFragmentClass();


    @Override
    public DrawerLayout getDrawer() {
        return drawer;
    }


    @Override
    public void onDestroyActivity() {

    }

    protected List<Fragment> getCurrentAddedFragments() {
        List<Fragment> allFragments = getActivity().getSupportFragmentManager().getFragments();
        List<Fragment> addedFragments = new ArrayList<>();
        if (allFragments != null) {
            for (Fragment fragment : allFragments) {
                if (fragment != null && fragment.isAdded()) {
                    addedFragments.add(fragment);
                }
            }
        }
        return addedFragments;
    }

    protected boolean isActivityRotated() {
        return ((ActivityStateInterface) getActivity()).isActivityRotated();
    }

    public void setEnabledFragmentAnimations(boolean enabled) {
        useFragmentAnim = enabled;
    }

    @AnimRes
    protected int getFragmentAnimationEnter() {
        return R.anim.fragment_animation_enter;
    }

    @AnimRes
    protected int getFragmentAnimationExit() {
        return R.anim.fragment_animation_exit;
    }

    @AnimRes
    protected int getFragmentAnimationPopEnter() {
        return R.anim.fragment_animation_pop_enter;
    }

    @AnimRes
    protected int getFragmentAnimationPopExit() {
        return R.anim.fragment_animation_pop_exit;
    }

}

