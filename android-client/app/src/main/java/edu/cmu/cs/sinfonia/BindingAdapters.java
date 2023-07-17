package edu.cmu.cs.sinfonia;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import edu.cmu.cs.openrtist.BR;

public class BindingAdapters {
    @BindingAdapter({"items", "layout"})
    public static <E> void setItems(LinearLayout view, Iterable<E> oldList, int oldLayoutId, Iterable<E> newList, int newLayoutId) {
        if (oldList == newList && oldLayoutId == newLayoutId)
            return;

        view.removeAllViews();

        if (newList == null)
            return;

        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        for (E item : newList) {
            ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, newLayoutId, view, false);
            binding.setVariable(BR.uuid, newList);
            binding.setVariable(BR.tier1url, item);
            binding.executePendingBindings();
            view.addView(binding.getRoot());
        }
    }
}
