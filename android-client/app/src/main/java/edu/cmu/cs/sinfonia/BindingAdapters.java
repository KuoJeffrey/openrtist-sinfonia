package edu.cmu.cs.sinfonia;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import edu.cmu.cs.openrtist.BR;

public class BindingAdapters {
    @BindingAdapter({"items", "layout"})
    public static <E> void setItems(
            LinearLayout view,
            Iterable<E> oldList, int oldLayoutId,
            Iterable<E> newList, int newLayoutId
    ) {
        if (oldList == newList && oldLayoutId == newLayoutId)
            return;

        view.removeAllViews();

        if (newList == null)
            return;

        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        for (E item : newList) {
            ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, newLayoutId, view, false);
//            binding.setVariable(BR.collection, newList);
            binding.setVariable(BR.item, item);
            binding.executePendingBindings();
            view.addView(binding.getRoot());
        }
    }

//    @BindingAdapter(value = {"items", "layout", "fragment"}, requireAll = false)
//    public static <E> void setItems(
//            LinearLayout view,
//            ArrayList<E> oldList, int oldLayoutId, Fragment oldFragment,
//            ArrayList<E> newList, int newLayoutId, Fragment newFragment
//    ) {
//        if (oldList == newList && oldLayoutId == newLayoutId)
//            return;
//
////        ItemChangeListener<E> listener = ListenerUtil.getListener(view, R.id.item_change_listener);
////        // If the layout changes, any existing listener must be replaced.
////        if (listener != null && oldList != null && oldLayoutId != newLayoutId) {
////            listener.setList(null);
////            listener = null;
////            // Stop tracking the old listener.
////            ListenerUtil.trackListener(view, null, R.id.item_change_listener);
////        }
////
////        // Avoid adding a listener when there is no new list or layout.
////        if (newList == null || newLayoutId == 0)
////            return;
////
////        if (listener == null) {
////            listener = new ItemChangeListener<>(view, newLayoutId, newFragment);
////            ListenerUtil.trackListener(view, listener, R.id.item_change_listener);
////        }
////
////        // Either the list changed, or this is an entirely new listener because the layout changed.
////        listener.setList(newList);
//    }
}
