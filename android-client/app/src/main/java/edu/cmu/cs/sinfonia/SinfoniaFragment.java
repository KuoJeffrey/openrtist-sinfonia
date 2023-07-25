package edu.cmu.cs.sinfonia;

import static edu.cmu.cs.openrtist.ServerListActivity.sinfoniaService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import edu.cmu.cs.openrtist.R;
import edu.cmu.cs.openrtist.databinding.SinfoniaFragmentBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SinfoniaFragment extends Fragment {
    public static final String WIREGUARD_PACKAGE = "com.wireguard.android.debug";
    private static final String TAG = "OpenRTiST/SinfoniaFragment";
    private SinfoniaFragmentBinding binding;
    private final static String KEY_LOCAL_UUID = "local_uuid";
    private final static String KEY_LOCAL_URL = "local_url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = SinfoniaFragmentBinding.inflate(inflater, container, false);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        addOnClickListener(view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (binding == null) return;
        binding.setFragment(this);
        if (savedInstanceState != null) {
            binding.setUuid(savedInstanceState.getString(KEY_LOCAL_UUID));
            binding.setTier1url(savedInstanceState.getString(KEY_LOCAL_URL));
        } else {
            binding.setUuid("737b5001-d27a-413f-9806-abf9bfce6746");
            binding.setTier1url("https://cmu.findcloudlet.org");
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (binding != null) {
            outState.putString(KEY_LOCAL_UUID, binding.getUuid());
            outState.putString(KEY_LOCAL_URL, binding.getTier1url());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void onFinished() {
        // Hide the keyboard; it rarely goes away on its own.
        Activity activity = getActivity();
        if (activity == null) return;
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS
            );
        }
        getParentFragmentManager().popBackStackImmediate();
    }

    public void onLaunchClicked(View view) {
        final Activity activity = requireActivity();
        Intent intent = new Intent(SinfoniaService.ACTION_START)
                .setPackage(SinfoniaService.PACKAGE_NAME)
                .putExtra("url", binding.getTier1url())
                .putExtra("applicationName", "openrtist")
                .putExtra("uuid", binding.getUuid())
                .putStringArrayListExtra(
                        "application",
                        new ArrayList<>(Collections.singletonList(activity.getPackageName()))
                );
        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                activity.startForegroundService(intent);
//            else
//                activity.startService(intent);
            Log.d(TAG, String.format("onLaunchClicked: $%s", SinfoniaService.PACKAGE_NAME));
            sinfoniaService.deploy(intent);
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(e.getMessage())
                    .setTitle(R.string.about_title)
                    .create()
                    .show();
        }
        onFinished();
    }

    private void addOnClickListener(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                MaterialButton launchButton = view.findViewById(R.id.deployment_button);
                if (launchButton != null) {
                    launchButton.setOnClickListener(view -> onLaunchClicked(view));
                }
            }
        });
    }
}