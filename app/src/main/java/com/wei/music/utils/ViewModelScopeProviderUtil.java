package com.wei.music.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import autodispose2.ScopeProvider;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.CompletableSubject;

public class ViewModelScopeProviderUtil {

    public static ScopeProvider create(ViewModel viewModel) {
        CompletableSubject clearedSubject = CompletableSubject.create();

        // 使用反射或其他方式监听 ViewModel 的清除
        // 这里使用一个简单的包装器
        return new ScopeProvider() {
            @NonNull
            @Override
            public CompletableSource requestScope() {
                return clearedSubject;
            }
        };
    }

    // 另一种实现：使用 ViewModel 的 onCleared 回调
    public static class ScopedViewModel extends ViewModel {
        private final CompletableSubject scopeCompletable = CompletableSubject.create();
        private final CompositeDisposable disposables = new CompositeDisposable();

        public ScopeProvider getScopeProvider() {
            return () -> scopeCompletable;
        }

        public void autoDispose(Disposable disposable) {
            disposables.add(disposable);
        }

        @Override
        protected void onCleared() {
            super.onCleared();
            scopeCompletable.onComplete();
            disposables.dispose();
        }
    }
}