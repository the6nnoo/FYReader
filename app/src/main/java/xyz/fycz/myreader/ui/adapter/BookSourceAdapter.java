package xyz.fycz.myreader.ui.adapter;


import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xyz.fycz.myreader.base.adapter.BaseListAdapter;
import xyz.fycz.myreader.base.adapter.IViewHolder;
import xyz.fycz.myreader.greendao.GreenDaoManager;
import xyz.fycz.myreader.greendao.entity.ReplaceRuleBean;
import xyz.fycz.myreader.greendao.entity.rule.BookSource;
import xyz.fycz.myreader.ui.adapter.helper.ItemTouchCallback;
import xyz.fycz.myreader.ui.adapter.holder.BookSourceHolder;

/**
 * @author fengyue
 * @date 2020/8/12 20:02
 */

public class BookSourceAdapter extends BaseSourceAdapter {
    private final FragmentActivity activity;
    private final OnSwipeListener onSwipeListener;
    private boolean mEditState;
    private final ItemTouchCallback.OnItemTouchListener itemTouchListener = new ItemTouchCallback.OnItemTouchListener() {
        @Override
        public void onSwiped(int adapterPosition) {

        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            Collections.swap(mList, srcPosition, targetPosition);
            notifyItemMoved(srcPosition, targetPosition);
            notifyItemChanged(srcPosition);
            notifyItemChanged(targetPosition);
            AsyncTask.execute(() -> {
                for (int i = 1; i <= mList.size(); i++) {
                    mList.get(i - 1).setOrderNum(i);
                }
                GreenDaoManager.getDaoSession().getBookSourceDao().insertOrReplaceInTx(mList);
            });
            return true;
        }
    };

    public BookSourceAdapter(FragmentActivity activity, OnSwipeListener onSwipeListener) {
        this.activity = activity;
        this.onSwipeListener = onSwipeListener;
    }

    @Override
    protected IViewHolder<BookSource> createViewHolder(int viewType) {
        return new BookSourceHolder(activity, this, onSwipeListener);
    }

    public ItemTouchCallback.OnItemTouchListener getItemTouchListener() {
        return itemTouchListener;
    }

    public boolean ismEditState() {
        return mEditState;
    }

    public void setmEditState(boolean mEditState) {
        this.mEditState = mEditState;
        setCheckedAll(false);
    }

    public void removeItem(int pos) {
        mList.remove(pos);
        notifyItemRemoved(pos);
        if (pos != mList.size())
            notifyItemRangeChanged(pos, mList.size() - pos);
    }

    public void toTop(int which, BookSource bean) {
        mList.remove(bean);
        notifyItemInserted(0);
        mList.add(0, bean);
        notifyItemRemoved(which);
        notifyItemRangeChanged(0, which + 1);
    }

    public interface OnSwipeListener {
        void onDel(int which, BookSource source);

        void onTop(int which, BookSource source);
    }
}
