package io.github.koss.brew.util.arch

import io.reactivex.subjects.PublishSubject

/**
 * Simple EventBus which can be used at application scope
 */
class EventBus {

    private val bus = PublishSubject.create<Any>()

    fun send(obj: Any) {
        bus.onNext(obj)
    }

    fun observable() = bus.share()
}