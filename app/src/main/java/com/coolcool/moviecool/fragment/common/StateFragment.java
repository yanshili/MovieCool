package com.coolcool.moviecool.fragment.common;


import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class StateFragment extends Fragment {
    public static final String TAG="StateFragment";
    public static final String FRAGMENT_STATE="com.coolcool.moviecool.fragment.common.StateFragment";
    public static final String FRAGMENT_HIDDEN="com.coolcool.moviecool.custom.FRAGMENT_HIDDEN";

    Bundle savedState;


    public StateFragment() {
        // Required empty public constructor
        if (getArguments()==null)
            setArguments(new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getParentFragment()==null)
//        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!restoreStateFromArguments()){
            onFirstLaunch();
        }
    }

    protected void onFirstLaunch(){}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveStateToArguments();

    }

    private void saveStateToArguments(){
        if (getView()!=null)
            savedState =saveState();
        if (savedState !=null){
            Bundle bundle=getArguments();
            savedState.putBoolean(FRAGMENT_HIDDEN,hidden);
            bundle.putBundle(FRAGMENT_STATE, savedState);
        }
    }

    private Bundle saveState() {
        Bundle state=new Bundle();

        onSaveState(state);
        return state;
    }

    /**
     * 保存状态
     * @param outState  将要保存的状态保存在outState内
     */
    protected void onSaveState(Bundle outState) {

    }

    private boolean restoreStateFromArguments(){
        Bundle bundle=getArguments();
        savedState =bundle.getBundle(FRAGMENT_STATE);
        if (savedState !=null){
            hidden=savedState.getBoolean(FRAGMENT_HIDDEN);
            if (hidden){
                getFragmentManager().beginTransaction().hide(this).commit();
            }else {
                getFragmentManager().beginTransaction().show(this).commit();
            }

            onRestoreState(savedState);
            return true;
        }
        return false;
    }

    /**
     * 恢复状态
     * @param savedInstanceState     要恢复的状态
     */
    protected void onRestoreState(Bundle savedInstanceState){

    }

    boolean hidden=false;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden=hidden;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        hidden=!isVisibleToUser;
    }
}
