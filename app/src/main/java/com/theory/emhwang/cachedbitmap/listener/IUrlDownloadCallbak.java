package com.theory.emhwang.cachedbitmap.listener;

public interface IUrlDownloadCallbak {
    void onSuccess(final Object result, final int position);
    void onFail(final String code, final String msg);
}
