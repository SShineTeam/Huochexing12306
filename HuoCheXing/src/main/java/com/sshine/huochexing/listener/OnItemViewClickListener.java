package com.sshine.huochexing.listener;

import android.view.View;

public interface OnItemViewClickListener<T> {
	void onItemClick(View v, int pos, T entity, Object... params);
}
